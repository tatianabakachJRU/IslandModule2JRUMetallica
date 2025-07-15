package model; // Указываем, что класс принадлежит пакету model

import model.animals.Animal;
import statistics.Statistics;
import utils.SimulationSettings;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Класс Location представляет собой конкретную локацию на острове, где могут находиться растения и животные
 */
public class Location {
    // Координаты локации на острове
    public final int x, y;

    // Потокобезопасная карта, которая хранит животных по их типам (классам)
    // Ключ - это класс животного, значение - очередь животных этого типа
    private final ConcurrentHashMap<Class<? extends Animal>, BlockingQueue<Animal>> animals = new ConcurrentHashMap<>();

    // Очередь для хранения растений в данной локации
    // Ограничение на максимальное количество растений задается в настройках симуляции
    private final BlockingQueue<Plant> plants = new LinkedBlockingQueue<>(SimulationSettings.MAX_PLANTS_PER_CELL);

    // Конструктор класса, принимающий координаты x и y локации
    public Location(int x, int y) {
        this.x = x; // Инициализация координаты x
        this.y = y; // Инициализация координаты y
        initializePlants(); // Инициализация растений в локации
    }

    /**
     * Метод для инициализации растений в данной локации
     */
    private void initializePlants() {
        // Устанавливаем начальное количество растений в локации, равное половине от ежедневного прироста
        int initialPlants = SimulationSettings.PLANT_GROWTH_PER_DAY / 2;

        // Добавляем начальные растения в очередь
        for (int i = 0; i < initialPlants; i++) {
            plants.add(new Plant()); // Создаем новое растение и добавляем его в очередь
        }
    }

    /**
     * Метод для добавления животного в локацию
     */
    public void addAnimal(Animal animal) {
        Class<? extends Animal> type = animal.getClass(); // Получаем класс (тип) добавляемого животного

        try {
            // Получаем максимальное количество животных данного типа, которое может находиться в одной локации
            int max = SimulationSettings.AnimalConfig.valueOf(type.getSimpleName().toUpperCase()).maxPerCell;

            // Используем compute для получения или создания очереди для данного типа животных
            BlockingQueue<Animal> queue = animals.compute(type, (k, existingQueue) -> {
                // Если очередь отсутствует, создаем новую с максимальным размером
                if (existingQueue == null) {
                    return new LinkedBlockingQueue<>(max);
                }
                // Если очередь уже существует, возвращаем её
                return existingQueue;
            });

            // Проверяем, есть ли место в очереди для нового животного
            if (queue.remainingCapacity() > 0) {
                queue.offer(animal); // Добавляем животное в очередь
                Statistics.recordBirth(animal); // Записываем событие рождения животного в статистику
            }
        } catch (IllegalArgumentException e) {
            // Обработка случая, если тип животного неизвестен (например, не определен в конфигурации)
            System.err.println("Неизвестный тип животного: " + type.getSimpleName());
        }
    }

    // Метод для удаления животного из локации
    public void removeAnimal(Animal animal) {
        Class<? extends Animal> type = animal.getClass(); // Получаем класс (тип) животного
        // Используем computeIfPresent для удаления животного из очереди
        animals.computeIfPresent(type, (k, queue) -> {
            queue.remove(animal); // Удаляем животное из очереди
            // Если очередь пустая после удаления, возвращаем null, чтобы удалить её из карты
            return queue.isEmpty() ? null : queue;
        });
    }

    // Метод для получения списка всех животных в данной локации
    public List<Animal> getAnimals() {
        // Преобразуем все очереди животных в одно общее множество и возвращаем в виде списка
        return animals.values().stream() // Получаем все очереди животных
                .flatMap(Collection::stream) // Объединяем все очереди в один поток объектов Animal
                .collect(Collectors.toList()); // Собираем поток в список и возвращаем
    }

    // Метод для получения списка животных определенного типа
    public <T extends Animal> List<T> getAnimalsByType(Class<T> type) {
        // Получаем очередь животных указанного типа, если она существует, иначе создаем пустую очередь
        return animals.getOrDefault(type, new LinkedBlockingQueue<>()).stream() // Получаем очередь по типу
                .map(type::cast) // Приводим животных к указанному типу
                .collect(Collectors.toList()); // Собираем в список и возвращаем
    }

    // Метод для потребления растений животными
    public double eatPlants(double amount) {
        // Определяем максимальное количество растений, которое можно съесть
        int maxToEat = (int) (amount / Plant.WEIGHT);
        // Определяем фактическое количество съеденных растений, беря минимальное из maxToEat и текущего количества растений
        int eaten = Math.min(maxToEat, plants.size()); // Минимум между запрашиваемым и доступным количеством
        eaten = Math.max(0, eaten); // Гарантируем, что количество не будет отрицательным

        // Удаляем съеденные растения из очереди
        for (int i = 0; i < eaten; i++) {
            plants.poll(); // Удаляем и возвращаем растение из очереди
        }
        // Возвращаем общий вес съеденных растений
        return eaten * Plant.WEIGHT; // Возвращаем вес съеденных растений
    }

    // Метод для роста растений в данной локации
    public void growPlants() {
        // Определяем количество новых растений, которое может вырасти в локации
        int newPlants = Math.min(
                SimulationSettings.PLANT_GROWTH_PER_DAY, // Максимальное количество, которое может вырасти за день
                SimulationSettings.MAX_PLANTS_PER_CELL - plants.size() // Оставшееся место для новых растений
        );
        newPlants = Math.max(0, newPlants); // Гарантируем, что количество не будет отрицательным

        // Добавляем новые растения в очередь
        for (int i = 0; i < newPlants; i++) {
            plants.add(new Plant()); // Создаем новое растение и добавляем его в очередь
        }
    }

    // Метод для получения информации о доминирующем животном в данной локации
    public String getDominantAnimalInfo() {
        if (animals.isEmpty()) return ""; // Если в локации нет животных, возвращаем пустую строку

        // Собираем статистику по всем животным
        Map<Class<? extends Animal>, Long> animalCounts = new HashMap<>(); // Карта для хранения количества животных по типам
        for (BlockingQueue<Animal> queue : animals.values()) { // Проходим по всем очередям животных
            Class<? extends Animal> type = queue.peek().getClass(); // Получаем тип (класс) первого животного в очереди
            long count = queue.size(); // Определяем количество животных данного типа
            animalCounts.put(type, animalCounts.getOrDefault(type, 0L) + count); // Обновляем счетчик для данного типа
        }

        // Находим вид с максимальным количеством
        Map.Entry<Class<? extends Animal>, Long> dominant = null; // Переменная для хранения доминирующего животного
        for (Map.Entry<Class<? extends Animal>, Long> entry : animalCounts.entrySet()) { // Проходим по всем типам животных
            // Если это первое животное или текущее больше, обновляем доминирующее животное
            if (dominant == null || entry.getValue() > dominant.getValue()) {
                dominant = entry; // Обновляем доминирующее животное
            }
        }

        // Если доминирующее животное найдено, возвращаем его количество и эмодзи
        if (dominant != null) {
            try {
                String emoji = dominant.getKey().getDeclaredConstructor().newInstance().getEmoji(); // Получаем эмодзи для доминирующего животного
                return dominant.getValue() + emoji; // Возвращаем строку с количеством и эмодзи
            } catch (Exception e) {
                return dominant.getValue() + "?"; // Если произошла ошибка, возвращаем количество с вопросительным знаком
            }
        }
        return ""; // Если ничего не найдено, возвращаем пустую строку
    }

    // Метод для получения количества растений в данной локации
    public int getPlantCount() {
        return plants.size(); // Возвращаем текущее количество растений
    }

    // Переопределяем метод toString для удобного вывода информации о локации
    @Override
    public String toString() {
        String animalsInfo = getDominantAnimalInfo(); // Получаем информацию о доминирующем животном
        String plantsInfo = getPlantCount() > 0 ? " " + getPlantCount() + "🌱" : ""; // Получаем информацию о количестве растений (с эмодзи)

        // Если в локации нет животных и растений, возвращаем символ воды (🌊)
        return (animalsInfo.isEmpty() && plantsInfo.isEmpty())
                ? "🌊"
                : animalsInfo + plantsInfo; // Возвращаем информацию о животных и растениях
    }
}

/**
 * private final ConcurrentHashMap<Class<? extends Animal>, BlockingQueue<Animal>> animals:
 *
 * Это потокобезопасная карта, которая хранит животных, сгруппированных по их типам (классам).
 * Ключом является класс животного, а значением — очередь животных этого типа. Это позволяет эффективно управлять разными видами животных в одной локации.
 * private final BlockingQueue<Plant> plants:
 *
 * Это очередь, используемая для хранения растений в данной локации. Она имеет ограничение на максимальное количество растений, заданное в настройках симуляции.
 * Использование LinkedBlockingQueue обеспечивает безопасный доступ к растениям из нескольких потоков.
 * BlockingQueue:
 *
 * BlockingQueue предоставляет встроенную потокобезопасность для операций добавления, удаления и проверки наличия элементов. Это означает, что несколько потоков могут безопасно взаимодействовать с очередью без необходимости в дополнительных механизмах синхронизации.
 * Он также поддерживает блокировку потоков при попытке извлечь элемент из пустой очереди, что позволяет избежать проверки наличия элементов перед извлечением.
 * CopyOnWriteArrayList:
 *
 * CopyOnWriteArrayList создаёт новую копию массива каждый раз, когда происходит модификация (например, добавление или удаление элемента). Это делает его более подходящим для сценариев, где количество операций чтения значительно превышает количество операций записи.
 * В случаях частых модификаций (например, добавление и удаление животных) использование CopyOnWriteArrayList может привести к значительным накладным расходам из-за постоянного создания новых копий.
 */