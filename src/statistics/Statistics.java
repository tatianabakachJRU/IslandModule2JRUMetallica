package statistics;

import model.animals.Animal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс Statistics предназначен для отслеживания статистики животных и растений в симуляции
 */
public class Statistics {
    // Потокобезопасная карта, которая будет хранить количество животных по их типам (классам)
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> animalCounts = new ConcurrentHashMap<>();

    // Атомарные счетчики для хранения общего количества растений и различных типов смертей
    private static final AtomicInteger totalPlants = new AtomicInteger(0); // Общее количество растений
    private static final AtomicInteger totalDeathsByHunger = new AtomicInteger(0); // Счетчик смертей от голода
    private static final AtomicInteger totalDeathsByPredation = new AtomicInteger(0); // Счетчик смертей от хищников
    private static final AtomicInteger totalBirths = new AtomicInteger(0); // Счетчик общего числа рождений

    // Метод для записи рождения животного
    public static void recordBirth(Animal animal) {
        // Увеличиваем счетчик для данного типа животного в карте, если его еще нет
        // computeIfAbsent: если ключ (тип животного) отсутствует, создается новый AtomicInteger со значением 0
        animalCounts.computeIfAbsent(animal.getClass(), k -> new AtomicInteger(0)).incrementAndGet();
        // Увеличиваем общий счетчик рождений на 1
        totalBirths.incrementAndGet();
    }

    // Метод для записи смерти животного от голода
    public static void recordDeathByHunger(Animal animal) {
        // Уменьшаем счетчик для данного типа животного, если он существует в карте
        animalCounts.computeIfPresent(animal.getClass(), (k, v) -> {
            v.decrementAndGet(); // Уменьшаем количество животных данного типа на 1
            // Если счетчик становится меньше или равным нулю, удаляем его из карты
            return v.get() <= 0 ? null : v;
        });
        // Увеличиваем общий счетчик смертей от голода на 1
        totalDeathsByHunger.incrementAndGet();
    }

    // Метод для записи смерти животного от хищничества
    public static void recordDeathByPredation(Animal animal) {
        // Аналогично, уменьшаем счетчик для данного типа животного
        animalCounts.computeIfPresent(animal.getClass(), (k, v) -> {
            v.decrementAndGet(); // Уменьшаем количество животных данного типа на 1
            // Удаляем из карты, если счетчик становится нулевым
            return v.get() <= 0 ? null : v;
        });
        // Увеличиваем общий счетчик смертей от хищников на 1
        totalDeathsByPredation.incrementAndGet();
    }

    // Метод для обновления общего количества растений
    public static void updatePlants(int count) {
        // Устанавливаем новое значение общего количества растений, гарантируя, что оно не будет отрицательным
        // Math.max(0, count) обеспечивает, что значение не может быть меньше нуля
        totalPlants.set(Math.max(0, count));
    }

    // Метод для вывода общей статистики на экран
    public static void printStatistics() {
        System.out.println("\n=== Общая статистика ==="); // Заголовок статистики

        // Проходим по всем типам животных и их количеству в карте
        animalCounts.forEach((cls, count) -> {
            try {
                // Создаем экземпляр животного данного типа, чтобы получить его эмодзи
                // Используем рефлексию для создания нового экземпляра
                String emoji = ((Animal)cls.getDeclaredConstructor().newInstance()).getEmoji();
                // Выводим тип животного (эмодзи) и его текущее количество
                System.out.printf("%s: %d  ", emoji, count.get());
            } catch (Exception e) {
                // Если произошла ошибка при создании экземпляра, выводим имя класса и количество
                System.out.printf("%s: %d  ", cls.getSimpleName(), count.get());
            }
        });

        // Выводим статистику по растениям и смертям
        System.out.println("\nРастений: " + totalPlants.get()); // Общее количество растений
        System.out.println("Рождений: " + totalBirths.get()); // Общее количество рождений
        System.out.println("Смертей от голода: " + totalDeathsByHunger.get()); // Смерти от голода
        System.out.println("Смертей от хищников: " + totalDeathsByPredation.get()); // Смерти от хищников
        System.out.println("========================"); // Разделитель
    }
}