package utils;

// Импорты всех классов животных, которые могут быть созданы в симуляции

import model.animals.Animal;
import model.animals.herbivores.*;
import model.animals.predators.*;

import java.util.function.Supplier;

/**
 * Класс, хранящий ВСЕ настройки симуляции острова.
 * Содержит конфигурации для:
 * - Общих параметров симуляции
 * - Растений
 * - Животных (как хищников, так и травоядных)
 * - Вероятностей взаимодействий между животными
 * - Начальных количеств животных
 *
 * Класс объявлен как final и имеет приватный конструктор,
 * что делает его утилитарным классом (нельзя создать экземпляр)
 */
public final class SimulationSettings {
    /**
     * Приватный конструктор для запрета создания экземпляров класса.
     * Все поля и методы статические.
     */
    private SimulationSettings() {}

    // ================== ОБЩИЕ НАСТРОЙКИ СИМУЛЯЦИИ ==================

    /**
     * Ширина острова в клетках.
     * Определяет, сколько локаций будет по горизонтали.
     */
    public static final int ISLAND_WIDTH = 10;

    /**
     * Высота острова в клетках.
     * Определяет, сколько локаций будет по вертикали.
     */
    public static final int ISLAND_HEIGHT = 10;

    /**
     * Продолжительность одного дня симуляции в миллисекундах.
     * 1000 мс = 1 секунда реального времени на 1 день симуляции.
     */
    public static final int DAY_DURATION_MS = 1000;

    /**
     * Интервал (в днях) между выводами статистики в консоль.
     * При значении 5 статистика будет выводиться каждые 5 дней.
     */
    public static final int STATISTICS_PRINT_INTERVAL_DAYS = 5;

    /**
     * Интервал (в днях) между отрисовками острова в консоли.
     * При значении 5 карта будет отрисовываться каждые 5 дней.
     */
    public static final int ISLAND_PRINT_INTERVAL_DAYS = 5;

    /**
     * Общая продолжительность симуляции в минутах.
     * Через указанное время симуляция автоматически остановится.
     */
    public static final int SIMULATION_DURATION_MINUTES = 10;

    /**
     * Начальный уровень сытости животных при создании.
     * Задается как доля от foodNeeded (0.7 = 70% от максимальной сытости).
     */
    public static final double INITIAL_SATIETY_PERCENT = 0.7;

    /**
     * Дневная потеря сытости животных.
     * Задается как доля от foodNeeded (0.3 = теряется 30% от дневной нормы).
     */
    public static final double DAILY_SATIETY_LOSS = 0.3;

    // ================== НАСТРОЙКИ РАСТЕНИЙ ==================

    /**
     * Максимальное количество растений на одной клетке.
     * Когда достигается этот лимит, новые растения перестают расти.
     */
    public static final int MAX_PLANTS_PER_CELL = 200;

    /**
     * Количество растений, которое вырастает ежедневно на каждой клетке.
     * Если места нет, рост прекращается.
     */
    public static final int PLANT_GROWTH_PER_DAY = 10;

    // ================== КОНФИГУРАЦИЯ ЖИВОТНЫХ ==================

    /**
     * Enum, содержащий параметры для каждого вида животных.
     * Каждое значение enum соответствует одному виду животного.
     */
    public enum AnimalConfig {
        // Формат конструктора:
        // Имя_вида(вес, максНаКлетке, скорость, потребностьВЕде, шансРазмножения, фабрика)

        // ========= ХИЩНИКИ =========
        WOLF(50, 30, 3, 8, 0.3, Wolf::new),
        BOA(15, 30, 1, 3, 0.2, Boa::new),
        FOX(8, 30, 2, 2, 0.4, Fox::new),
        BEAR(500, 5, 2, 80, 0.25, Bear::new),
        EAGLE(6, 20, 3, 1, 0.35, Eagle::new),

        // ========= ТРАВОЯДНЫЕ =========
        HORSE(400, 20, 4, 60, 0.2, Horse::new),
        DEER(300, 20, 4, 50, 0.3, Deer::new),
        RABBIT(2, 150, 2, 0.45, 0.5, Rabbit::new),
        MOUSE(0.05, 500, 1, 0.01, 0.6, Mouse::new),
        GOAT(60, 140, 3, 10, 0.4, Goat::new),
        SHEEP(70, 140, 3, 15, 0.4, Sheep::new),
        BOAR(400, 50, 2, 50, 0.35, Boar::new),
        BUFFALO(700, 10, 3, 100, 0.15, Buffalo::new),
        DUCK(1, 200, 4, 0.15, 0.45, Duck::new),
        CATERPILLAR(0.01, 1000, 0, 0, 0.7, Caterpillar::new);

        // ========= ПАРАМЕТРЫ ЖИВОТНЫХ =========

        /**
         * Вес одной особи данного вида в килограммах.
         * Используется при расчетах питания.
         */
        public final double weight;

        /**
         * Максимальное количество особей этого вида на одной клетке.
         * При достижении лимита новые особи не могут появиться на клетке.
         */
        public final int maxPerCell;

        /**
         * Максимальная скорость перемещения (клеток за ход).
         * Определяет, как далеко может переместиться животное за день.
         */
        public final int maxSpeed;

        /**
         * Количество пищи (в кг), необходимое для полного насыщения за день.
         */
        public final double foodNeeded;

        /**
         * Вероятность размножения (от 0.0 до 1.0).
         * Шанс, что при наличии пары появится потомство.
         */
        public final double reproductionChance;

        /**
         * Фабрика для создания экземпляров животных через лямбда-выражение.
         * Использует ссылку на конструктор соответствующего класса.
         */
        public final Supplier<Animal> factory;

        /**
         * Конструктор для значений enum.
         *
         * @param weight Вес особи (кг)
         * @param maxPerCell Макс. количество на клетке
         * @param maxSpeed Макс. скорость (клеток/ход)
         * @param foodNeeded Потребность в еде (кг/день)
         * @param reproductionChance Шанс размножения (0.0-1.0)
         * @param factory Фабричный метод для создания экземпляров
         */
        AnimalConfig(double weight, int maxPerCell, int maxSpeed,
                     double foodNeeded, double reproductionChance,
                     Supplier<Animal> factory) {
            this.weight = weight;
            this.maxPerCell = maxPerCell;
            this.maxSpeed = maxSpeed;
            this.foodNeeded = foodNeeded;
            this.reproductionChance = reproductionChance;
            this.factory = factory;
        }

        /**
         * Создает новый экземпляр животного данного вида.
         * Использует фабричный метод, переданный в конструкторе.
         *
         * @return Новый экземпляр животного
         */
        public Animal createAnimal() {
            return factory.get();
        }

        /**
         * Возвращает начальное количество животных данного вида.
         * Использует значения из вложенного класса InitialCount.
         *
         * @return Начальное количество животных
         */
        public int getInitialCount() {
            return switch (this) {
                case WOLF -> InitialCount.WOLF;
                case BOA -> InitialCount.BOA;
                case FOX -> InitialCount.FOX;
                case BEAR -> InitialCount.BEAR;
                case EAGLE -> InitialCount.EAGLE;
                case HORSE -> InitialCount.HORSE;
                case DEER -> InitialCount.DEER;
                case RABBIT -> InitialCount.RABBIT;
                case MOUSE -> InitialCount.MOUSE;
                case GOAT -> InitialCount.GOAT;
                case SHEEP -> InitialCount.SHEEP;
                case BOAR -> InitialCount.BOAR;
                case BUFFALO -> InitialCount.BUFFALO;
                case DUCK -> InitialCount.DUCK;
                case CATERPILLAR -> InitialCount.CATERPILLAR;
            };
        }
    }

    // ================== ВЕРОЯТНОСТИ ПОЕДАНИЯ ==================

    /**
     * Класс, содержащий вероятности успешной охоты/поедания.
     * Значения в процентах (0-100).
     */
    public static final class EatingChance {
        // ========= ВОЛК =========
        public static final int WOLF_RABBIT = 60; // 60% шанс съесть кролика
        public static final int WOLF_MOUSE = 80;  // 80% шанс съесть мышь
        public static final int WOLF_GOAT = 60;
        public static final int WOLF_SHEEP = 70;
        public static final int WOLF_HORSE = 10;
        public static final int WOLF_DEER = 15;
        public static final int WOLF_BOAR = 15;
        public static final int WOLF_BUFFALO = 10;
        public static final int WOLF_DUCK = 40;

        // ========= УДАВ =========
        public static final int BOA_FOX = 15;
        public static final int BOA_RABBIT = 20;
        public static final int BOA_MOUSE = 40;
        public static final int BOA_DUCK = 10;

        // ========= ЛИСА =========
        public static final int FOX_RABBIT = 70;
        public static final int FOX_MOUSE = 90;
        public static final int FOX_DUCK = 60;
        public static final int FOX_CATERPILLAR = 40;

        // ========= МЕДВЕДЬ =========
        public static final int BEAR_BOA = 80;
        public static final int BEAR_HORSE = 40;
        public static final int BEAR_DEER = 80;
        public static final int BEAR_RABBIT = 80;
        public static final int BEAR_MOUSE = 90;
        public static final int BEAR_GOAT = 70;
        public static final int BEAR_SHEEP = 70;
        public static final int BEAR_BOAR = 50;
        public static final int BEAR_DUCK = 10;

        // ========= ОРЕЛ =========
        public static final int EAGLE_FOX = 10;
        public static final int EAGLE_RABBIT = 90;
        public static final int EAGLE_MOUSE = 90;
        public static final int EAGLE_DUCK = 80;

        // ========= ОСОБЫЕ СЛУЧАИ =========
        public static final int MOUSE_CATERPILLAR = 90;  // Мышь ест гусеницу
        public static final int BOAR_CATERPILLAR = 90;  // Кабан ест гусеницу
        public static final int DUCK_CATERPILLAR = 90;   // Утка ест гусеницу
    }

    // ================== НАЧАЛЬНОЕ КОЛИЧЕСТВО ЖИВОТНЫХ ==================

    /**
     * Класс, содержащий начальные количества животных каждого вида.
     * Эти значения используются при старте симуляции.
     */
    public static final class InitialCount {
        // ========= ХИЩНИКИ =========
        public static final int WOLF = 30;     // Начальное количество волков
        public static final int BOA = 20;      // Начальное количество удавов
        public static final int FOX = 30;      // Начальное количество лис
        public static final int BEAR = 5;      // Начальное количество медведей
        public static final int EAGLE = 15;    // Начальное количество орлов

        // ========= ТРАВОЯДНЫЕ =========
        public static final int HORSE = 20;    // Начальное количество лошадей
        public static final int DEER = 20;     // Начальное количество оленей
        public static final int RABBIT = 100;  // Начальное количество кроликов
        public static final int MOUSE = 300;   // Начальное количество мышей
        public static final int GOAT = 100;    // Начальное количество коз
        public static final int SHEEP = 100;   // Начальное количество овец
        public static final int BOAR = 40;     // Начальное количество кабанов
        public static final int BUFFALO = 10;  // Начальное количество буйволов
        public static final int DUCK = 150;    // Начальное количество уток
        public static final int CATERPILLAR = 500; // Начальное количество гусениц
    }
}