package model.animals;

import model.Location;
import model.animals.herbivores.Caterpillar;
import statistics.Statistics;
import utils.Randomizer;
import utils.SimulationSettings;

import java.util.List;

/**
 * Абстрактный класс Herbivore представляет собой травоядное животное в симуляции
 */
public abstract class Herbivore extends Animal {

    /**
     * Конструктор класса Herbivore, принимает конфигурацию животного
     */
    public Herbivore(SimulationSettings.AnimalConfig config) {
        super(config); // Вызываем конструктор родительского класса Animal с конфигурацией
    }

    /**
     *  Метод для поедания пищи в указанной локации
     * @param location Текущая локация животного
     */
    @Override
    public void eat(Location location) {
        // Проверяем, является ли это травоядное животным, которое может есть гусениц
        if (this instanceof CaterpillarEater) {
            tryEatCaterpillar(location); // Пробуем есть гусеницу
            return;
        }
        // Если не удалось поесть гусеницу, пробуем поесть растения
        eatPlants(location);
    }

    /**
     * Метод для поедания растений в указанной локации
     */
    protected boolean eatPlants(Location location) {
        // Рассчитываем, сколько пищи нужно травоядному
        double needed = config.foodNeeded - satiety; // Определяем недостаток пищи (потребность - сытость)

        // Если сытости достаточно (needed <= 0), возвращаем false
        if (needed <= 0) return false;

        // Пытаемся поесть растения в локации и получаем количество съеденных растений
        double eaten = location.eatPlants(needed); // Метод в Location, который уменьшает количество растений
        satiety += eaten; // Увеличиваем уровень сытости травоядного на количество съеденной пищи
        return eaten > 0; // Возвращаем true, если что-то было съедено, иначе false
    }

    /**
     * Метод для попытки поедания гусеницы в указанной локации
     */
    protected boolean tryEatCaterpillar(Location location) {
        // Проверяем, является ли это травоядное животное экземпляром CaterpillarEater
        if (!(this instanceof CaterpillarEater eater)) return false; // Если нет, возвращаем false

        // Получаем вероятность успешного поедания гусеницы
        int chance = eater.getCaterpillarEatingChance();
        // Получаем список гусениц в локации
        List<Caterpillar> caterpillars = location.getAnimalsByType(Caterpillar.class);

        // Если гусениц нет, возвращаем false
        if (caterpillars.isEmpty()) return false;

        // Проверяем, удалось ли травоядному поймать гусеницу на основе случайной вероятности
        // Это не является требованием в проекте я дописала его опционально
        if (Randomizer.getProbability(chance)) {
            // Если да, выбираем случайную гусеницу из списка
            Caterpillar caterpillar = Randomizer.randomItem(caterpillars);
            // Рассчитываем количество питательных веществ, которое травоядное получит от гусеницы
            double nutrition = Math.min(caterpillar.getWeight(), config.foodNeeded - satiety); // Минимум между весом гусеницы и тем, что нужно для сытости
            satiety += nutrition; // Увеличиваем уровень сытости травоядного
            caterpillar.die(); // Уничтожаем гусеницу
            Statistics.recordDeathByPredation(caterpillar); // Записываем факт убийства гусеницы в статистику
            return true; // Возвращаем true, указывая на успешное поедание
        }
        return false; // Возвращаем false, если поедание не удалось
    }

    /**
     * Метод для размножения травоядного в указанной локации
     * @param location Текущая локация животного
     */
    @Override
    public void reproduce(Location location) {
        // Получаем список особей того же вида в данной локации
        List<? extends Herbivore> sameSpecies = location.getAnimalsByType(this.getClass()); // Получаем всех травоядных того же типа
        // Проверяем, достаточно ли особей для размножения и вероятность успешного размножения
        // Если животных больше двух одного вида пробуем размножаться
        if (sameSpecies.size() >= 2 && Randomizer.getProbability(config.reproductionChance * 100)) {
            // Если условия выполнены, создаем потомка
            Animal offspring = createOffspring(); // Метод должен быть реализован в подклассах
            location.addAnimal(offspring); // Добавляем потомка в локацию (в эту же локацию)
            Statistics.recordBirth(offspring); // Записываем факт рождения в статистику
        }
    }

    /**
     * Абстрактный метод для создания потомка, который должен быть реализован подклассами
     */
    protected abstract Animal createOffspring();
}