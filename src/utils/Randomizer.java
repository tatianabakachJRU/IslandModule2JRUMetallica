package utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Обычный Random:
 * При использовании в многопоточной среде один экземпляр Random может вызывать конкуренцию между потоками. Это приводит к:
 * Снижению производительности (потоки блокируют друг друга).
 * Возможным ошибкам в генерации чисел из-за состояния гонки.
 *
 * ThreadLocalRandom:
 * Каждый поток имеет собственный экземпляр генератора случайных чисел. Это полностью исключает конкуренцию, так как потоки не делят общий ресурс.
 */

/**
 * Утилитарный класс для генерации случайных значений.
 * Содержит методы для работы с различными типами случайных данных:
 * - Вероятности (проценты)
 * - Целые числа в диапазоне
 * - Числа с плавающей точкой в диапазоне
 * - Случайные элементы списка
 * - Случайные значения enum
 *
 * Класс объявлен как final и имеет приватный конструктор,
 * что делает его утилитарным классом (нельзя создать экземпляр)
 */
public final class Randomizer {

    /**
     * Приватный конструктор для запрета создания экземпляров класса.
     * Все методы класса статические.
     */
    private Randomizer() {}

    /**
     * Проверяет, сработала ли вероятность.
     * Генерирует случайное число от 0 до 100 и проверяет,
     * попало ли оно в заданный процентный диапазон.
     *
     * @param percent Вероятность в процентах (от 0 до 100)
     * @return true если сгенерированное число меньше percent, иначе false
     *
     * Пример использования:
     * if (Randomizer.getProbability(30)) {
     *     // Код выполнится с вероятностью 30%
     * }
     */
    public static boolean getProbability(double percent) {
        return ThreadLocalRandom.current().nextDouble(100) < percent;
    }

    /**
     * Генерирует случайное целое число в заданном диапазоне [min, max).
     * Диапазон включает min, но не включает max.
     *
     * @param min Минимальное значение (включительно)
     * @param max Максимальное значение (исключительно)
     * @return Случайное число из диапазона [min, max)
     *
     * Пример:
     * Randomizer.nextInt(1, 5) вернет 1, 2, 3 или 4
     */
    public static int nextInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * Генерирует случайное целое число от 0 (включительно) до bound (исключительно).
     *
     * @param bound Верхняя граница (исключительно)
     * @return Случайное число из диапазона [0, bound)
     *
     * Пример:
     * Randomizer.nextInt(3) вернет 0, 1 или 2
     */
    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    /**
     * Выбирает случайный элемент из списка.
     *
     * @param <T> Тип элементов списка
     * @param list Список элементов
     * @return Случайный элемент из списка
     * @throws IllegalArgumentException если список пуст или null
     *
     * Пример:
     * List<String> items = Arrays.asList("A", "B", "C");
     * String randomItem = Randomizer.randomItem(items);
     */
    public static <T> T randomItem(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Список не может быть пустым");
        }
        return list.get(nextInt(list.size()));
    }

    /**
     * Выбирает случайное значение из перечисления (enum).
     *
     * @param <T> Тип перечисления
     * @param enumClass Класс enum
     * @return Случайное значение из указанного enum
     *
     * Пример:
     * enum Color { RED, GREEN, BLUE }
     * Color randomColor = Randomizer.randomEnum(Color.class);
     */
    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        return values[nextInt(values.length)];
    }

    /**
     * Генерирует случайное число с плавающей точкой от 0.0 (включительно) до 1.0 (исключительно).
     *
     * @return Случайное число из диапазона [0.0, 1.0)
     *
     * Пример:
     * double randomValue = Randomizer.nextDouble(); // 0.0 ≤ value < 1.0
     */
    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }
}