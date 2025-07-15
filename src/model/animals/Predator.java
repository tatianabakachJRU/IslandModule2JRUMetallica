package model.animals; // Указываем, что класс принадлежит пакету model.animals

import model.Location;
import statistics.Statistics;
import utils.Randomizer;
import utils.SimulationSettings;

import java.util.List;

/**
 * Абстрактный класс Predator представляет собой хищное животное в симуляции
 */
public abstract class Predator extends Animal {
    // Конструктор класса Predator, принимающий конфигурацию животного
    public Predator(SimulationSettings.AnimalConfig config) {
        super(config); // Вызываем конструктор родительского класса Animal с конфигурацией
    }

    /**
     * Метод для поедания жертвы в указанной локации
     * @param location Текущая локация животного
     */
    @Override
    public void eat(Location location) {
        // Получаем список потенциальной пищи (жертв) в данной локации
        List<Animal> preyList = getPotentialFood(location);

        // Если нет доступной пищи, возвращаем false
        if (preyList.isEmpty()) return;

        // Выбираем случайную жертву из списка потенциальной пищи
        // Используем Randomizer для выбора случайной жертвы
        Animal prey = Randomizer.randomItem(preyList);

        // Получаем вероятность поедания для выбранной жертвы
        int chance = getEatingProbability(prey);

        // Проверяем, удалось ли хищнику поймать жертву на основе случайной вероятности
        if (Randomizer.getProbability(chance)) {
            // Рассчитываем количество питательных веществ, которое хищник получит от жертвы
            double nutrition = Math.min(prey.getWeight(), config.foodNeeded - satiety);
            // Обновляем уровень сытости хищника
            satiety += nutrition;
            prey.die(); // Уничтожаем жертву, вызывая метод die()
            // Записываем факт убийства жертвы в статистику
            Statistics.recordDeathByPredation(prey);
        }
    }

    // Абстрактный метод, который должен быть реализован подклассами для получения списка потенциальной пищи
    protected abstract List<Animal> getPotentialFood(Location location);

    // Абстрактный метод для получения вероятности успешного поедания жертвы
    protected abstract int getEatingProbability(Animal prey);

    /**
     * Метод для размножения хищника в указанной локации
     * @param location Текущая локация животного
     */
    @Override
    public void reproduce(Location location) {
        // Получаем список особей того же вида в данной локации
        List<? extends Predator> sameSpecies = location.getAnimalsByType(this.getClass());

        // Проверяем, достаточно ли особей для размножения и вероятность успешного размножения
        if (sameSpecies.size() >= 2 && Randomizer.getProbability(config.reproductionChance * 100)) {
            // Создаем потомка
            Animal offspring = createOffspring();
            // Добавляем потомка в локацию
            location.addAnimal(offspring);
            // Записываем факт рождения в статистику
            Statistics.recordBirth(offspring);
        }
    }

    // Абстрактный метод для создания потомка, который должен быть реализован подклассами
    protected abstract Animal createOffspring();
}