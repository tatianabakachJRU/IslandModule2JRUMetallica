package model.animals;

import enums.Direction;
import model.Island;
import model.Location;
import statistics.Statistics;
import utils.Randomizer;
import utils.SimulationSettings;
import utils.SimulationSettings.AnimalConfig;

/**
 * Абстрактный базовый класс для всех животных в симуляции.
 * Определяет общее поведение и свойства всех животных:
 * - Передвижение по острову
 * - Питание и уровень сытости
 * - Размножение
 * - Смерть от голода или хищников
 *
 * Класс является абстрактным - конкретные реализации должны
 * реализовать абстрактные методы для видового поведения.
 */
public abstract class Animal {
    /**
     * Конфигурация животного, содержащая:
     * - Вес
     * - Максимальное количество на клетке
     * - Скорость передвижения
     * - Потребность в пище
     * - Шанс размножения
     */
    protected final AnimalConfig config;

    /**
     * Текущая локация (клетка), где находится животное.
     * Может быть null, если животное не размещено на острове.
     */
    protected Location location;

    /**
     * Текущий уровень сытости животного.
     * Измеряется в килограммах пищи, эквивалентных foodNeeded из конфига.
     * При достижении 0 животное умирает от голода.
     */
    protected double satiety;

    /**
     * Флаг, указывающий живо ли животное.
     * false означает, что животное мертво и должно быть удалено из симуляции.
     */
    protected boolean isAlive = true;

    /**
     * Конструктор животного.
     *
     * @param config Конфигурация с параметрами животного
     */
    public Animal(AnimalConfig config) {
        this.config = config;
        // Устанавливаем начальную сытость как процент от дневной нормы
        this.satiety = config.foodNeeded * SimulationSettings.INITIAL_SATIETY_PERCENT;
    }

    /**
     * Обрабатывает смерть животного.
     * Удаляет животное из текущей локации и регистрирует
     * причину смерти в статистике (голод или хищник).
     */
    public void die() {
        if (isAlive) {
            isAlive = false;
            if (location != null) {
                location.removeAnimal(this);
                if (satiety <= 0) {
                    // Регистрация смерти от голода
                    Statistics.recordDeathByHunger(this);
                } else {
                    // Регистрация смерти от хищника
                    Statistics.recordDeathByPredation(this);
                }
            }
        }
    }

    /**
     * Перемещает животное на случайное расстояние в случайном направлении.
     * Животное двигается с вероятностью 70% (0.7).
     *
     * @param island Ссылка на остров для доступа к локациям
     */
    public void move(Island island) {
        // Проверка условий для движения:
        // - Животное живо
        // - Локация существует
        // - Случайное число меньше 0.7 (70% шанс движения)
        if (!isAlive || location == null || Randomizer.nextDouble() > 0.7) return;

        // Выбираем случайное направление из enum Direction
        Direction direction = Randomizer.randomEnum(Direction.class);
        // Определяем количество шагов (от 1 до maxSpeed)
        int steps = Randomizer.nextInt(1, config.maxSpeed + 1);

        // Рассчитываем новые координаты с проверкой границ острова
        /**
         * Базовые компоненты:
         * location.x - текущая координата X животного
         * location.y - текущая координата Y животного
         * direction.getDx() - смещение по X для выбранного направления
         * direction.getDy() - смещение по Y для выбранного направления
         * steps - количество шагов (от 1 до maxSpeed)
         * island.getWidth() - общее количество ячеек по горизонтали
         * island.getHeight() - общее количество ячеек по вертикали
         *
         * 2. Вычисление потенциальных координат:
         * location.x + direction.getDx() * steps
         * direction.getDx() * steps:
         * Если направление EAST → dx = +1 → движение вправо
         * Если WEST → dx = -1 → движение влево
         * NORTH/SOUTH → dx = 0 (движение только по Y)
         * Умножение на steps: за один ход животное может пройти несколько ячеек
         *
         * 3. Ограничение границами острова (Math.min):
         * Math.min(..., island.getWidth() - 1)
         * island.getWidth() - 1:
         * Если остров имеет 10 столбцов (width=10), максимальный индекс X=9 (т.к. отсчет с 0)
         * Math.min(a, b) выбирает меньшее значение:
         * Гарантирует, что X не превысит правую границу

         * 4. Защита от отрицательных значений (Math.max):
         * Math.max(0, ...)
         * Гарантирует, что координата не станет меньше 0
         * Защита от выхода за левую границу или верхнюю границу (для Y)
         */
        int newX = Math.max(0, Math.min(location.x + direction.getDx() * steps, island.getWidth() - 1));
        int newY = Math.max(0, Math.min(location.y + direction.getDy() * steps, island.getHeight() - 1));

        // Получаем новую локацию и проверяем ее
        Location newLocation = island.getLocation(newX, newY);
        if (newLocation != null && newLocation != location) {
            // Удаляем из старой локации
            location.removeAnimal(this);
            // Добавляем в новую
            newLocation.addAnimal(this);
            // Обновляем ссылку на текущую локацию
            location = newLocation;
        }
    }

    /**
     * Уменьшает сытость животного на дневную норму.
     * Если сытость достигает 0 или ниже, вызывает смерть животного.
     */
    public void decreaseSatiety() {
        // Уменьшаем сытость на процент от дневной нормы
        satiety -= config.foodNeeded * SimulationSettings.DAILY_SATIETY_LOSS;
        // Проверяем, не умерло ли животное от голода
        if (satiety <= 0) die();
    }

    /**
     * Абстрактный метод питания - должен быть реализован в подклассах.
     *
     * @param location Текущая локация животного
     */
    public abstract void eat(Location location);

    /**
     * Абстрактный метод размножения - должен быть реализован в подклассах.
     *
     * @param location Текущая локация животного
     */
    public abstract void reproduce(Location location);

    /**
     * Абстрактный метод получения emoji-представления животного.
     *
     * @return Строка с emoji для данного вида животного
     */
    public abstract String getEmoji();

    // ========== ГЕТТЕРЫ ==========

    /**
     * Проверяет, живо ли животное.
     *
     * @return true если животное живо, false если мертво
     */
    public boolean isAlive() { return isAlive; }

    /**
     * Возвращает вес животного.
     *
     * @return Вес животного в килограммах
     */
    public double getWeight() { return config.weight; }

    /**
     * Возвращает текущую локацию животного.
     *
     * @return Локация, где находится животное (может быть null)
     */
    public Location getLocation() { return location; }

    /**
     * Возвращает строковое представление животного.
     * Формат: "emoji(уровень_сытости)"
     *
     * @return Строковое представление животного
     */
    @Override
    public String toString() {
        return String.format("%s(%.1f)", getEmoji(), satiety);
    }
}