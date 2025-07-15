package view;

import model.Island;
import model.Location;

/**
 * Класс для визуализации острова в консоли с использованием:
 * - Псевдографики для создания таблицы
 * - ANSI-цветов для подсветки элементов
 * - Emoji-символов для отображения сущностей
 */
public class IslandRenderer {
    // ANSI-коды для цветового оформления. Чтобы было красиво)))
    private static final String ANSI_RESET = "\u001B[0m";  // Сброс цвета
    private static final String ANSI_YELLOW = "\u001B[33m"; // Для чисел
    private static final String ANSI_GREEN = "\u001B[32m";  // Для растений
    private static final String ANSI_CYAN = "\u001B[36m";   // Для воды и рамки

    /**
     * Основной метод отрисовки острова
     * @param island - объект острова для визуализации
     */
    public static void render(Island island) {
        // Шапка с названием и размерами острова
        System.out.println("\n" + ANSI_CYAN + "╔════════════════════════════════╗");
        System.out.println("║        Island Map " + island.getWidth() + "x" + island.getHeight() + "        ║");
        System.out.println("╚════════════════════════════════╝" + ANSI_RESET);

        // Основная сетка локаций
        for (int y = 0; y < island.getHeight(); y++) {
            System.out.print(ANSI_CYAN + "║" + ANSI_RESET); // Левая граница строки
            for (int x = 0; x < island.getWidth(); x++) {
                Location cell = island.getLocation(x, y);
                String content = formatCellContent(cell); // Форматирование содержимого ячейки
                // Вывод ячейки с фиксированной шириной 12 символов
                System.out.printf(" %-12s " + ANSI_CYAN + "║" + ANSI_RESET, content);
            }
            System.out.println();

            // Горизонтальные разделители между строками (кроме последней)
            if (y < island.getHeight() - 1) {
                System.out.print(ANSI_CYAN + "╟");
                System.out.print("────────────".repeat(island.getWidth()));
                System.out.println(ANSI_RESET);
            }
        }
    }

    /**
     * Форматирование содержимого ячейки с цветовой подсветкой
     * @param cell - объект локации
     * @return отформатированная строка с ANSI-кодами
     */
    private static String formatCellContent(Location cell) {
        String content = cell.toString();
        return content
                // Регулярные выражения для поиска паттернов:
                // 1. Числа перед emoji (например "3🐺")
                .replaceAll("(\\d+)(\\p{So})", ANSI_YELLOW + "$1" + ANSI_RESET + "$2")
                // 2. Растения (🌱)
                .replace("🌱", ANSI_GREEN + "🌱" + ANSI_RESET)
                // 3. Воду (🌊)
                .replace("🌊", ANSI_CYAN + "🌊" + ANSI_RESET);
    }
}