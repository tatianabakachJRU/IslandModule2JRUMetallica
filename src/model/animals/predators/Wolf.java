package model.animals.predators;

import model.Location;
import model.animals.Animal;
import model.animals.Predator;
import model.animals.herbivores.*;
import utils.SimulationSettings;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс волка - реализация хищника в экосистеме.
 * Наследует базовую логику хищника и добавляет специфические характеристики:
 * - Список потенциальных жертв
 * - Вероятности успешной охоты
 * - Особенности размножения
 */
public class Wolf extends Predator {
    /**
     * Конструктор инициализирует волка через родительский класс Predator
     * Использует предустановленные настройки для волка из конфигурации
     */
    public Wolf() {
        super(SimulationSettings.AnimalConfig.WOLF); // Инициализация параметров:
        // - weight: 50 кг (пример)
        // - maxSpeed: 3 клетки/ход
        // - foodNeeded: 8 кг/день
        // - maxPopulation: 30 в локации
    }

    /**
     * Определяет список потенциальных жертв в текущей локации.
     * Фильтрация происходит по 2 критериям:
     * 1. Принадлежность к разрешенным типам жертв
     * 2. Только живые особи
     *
     * @param location текущая локация волка
     * @return список доступных для атаки животных
     */
    @Override
    protected List<Animal> getPotentialFood(Location location) {
        return location.getAnimals().stream()
                // Фильтр по конкретным классам-жертвам (все травоядные)
                .filter(a -> a instanceof Rabbit || a instanceof Mouse
                        || a instanceof Goat || a instanceof Sheep
                        || a instanceof Horse || a instanceof Deer
                        || a instanceof Boar || a instanceof Buffalo
                        || a instanceof Duck)
                // Исключаем уже мертвых животных
                .filter(Animal::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает вероятность успешной атаки для конкретного типа жертвы.
     * Значения вероятностей берутся из централизованного хранилища настроек.
     *
     * @param prey целевое животное-жертва
     * @return процентная вероятность (0-100) успешной атаки
     */
    @Override
    protected int getEatingProbability(Animal prey) {
        // Логика выбора вероятности по типу жертвы:
        if (prey instanceof Rabbit) return SimulationSettings.EatingChance.WOLF_RABBIT;    // 60%
        if (prey instanceof Mouse) return SimulationSettings.EatingChance.WOLF_MOUSE;     // 80%
        if (prey instanceof Goat) return SimulationSettings.EatingChance.WOLF_GOAT;       // 40%
        if (prey instanceof Sheep) return SimulationSettings.EatingChance.WOLF_SHEEP;     // 70%
        if (prey instanceof Horse) return SimulationSettings.EatingChance.WOLF_HORSE;     // 10%
        if (prey instanceof Deer) return SimulationSettings.EatingChance.WOLF_DEER;       // 15%
        if (prey instanceof Boar) return SimulationSettings.EatingChance.WOLF_BOAR;       // 15%
        if (prey instanceof Buffalo) return SimulationSettings.EatingChance.WOLF_BUFFALO; // 20%
        if (prey instanceof Duck) return SimulationSettings.EatingChance.WOLF_DUCK;       // 40%
        return 0; // Для неучтенных типов
    }

    /**
     * Фабричный метод для создания потомка.
     * Используется при размножении для создания нового экземпляра волка.
     *
     * @return новый объект волка
     */
    @Override
    protected Animal createOffspring() {
        return new Wolf(); // Создание без параметров, так как настройки берутся из конфига
    }

    /**
     * Предоставляет визуальное представление волка.
     * Используется для отображения в консольном интерфейсе.
     *
     * @return emoji-символ волка
     */
    @Override
    public String getEmoji() {
        return "🐺"; // Unicode: U+1F43A
    }
}