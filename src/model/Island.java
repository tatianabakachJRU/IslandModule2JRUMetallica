package model; // Указываем, что класс принадлежит пакету model

import utils.SimulationSettings;

/**
 *  Класс Island представляет собой остров, на котором расположены локации и животные
 */
public class Island {
    private final int width; // Ширина острова (количество локаций по оси X)
    private final int height; // Высота острова (количество локаций по оси Y)
    private final Location[][] locations; // Двумерный массив для хранения локаций на острове

    // Конструктор класса Island
    public Island() {
        // Инициализируем ширину и высоту острова из SimulationSettings
        this.width = SimulationSettings.ISLAND_WIDTH;
        this.height = SimulationSettings.ISLAND_HEIGHT;

        // Создаем двумерный массив для хранения локаций
        this.locations = new Location[width][height];

        initializeLocations(); // Вызываем метод для инициализации локаций
        spawnInitialAnimals(); // Вызываем метод для спавна/создания начальных животных
    }

    /**
     * Метод для инициализации всех локаций на острове
     */
    private void initializeLocations() {
        // Проходим по всем координатам острова
        for (int x = 0; x < width; x++) { // Цикл по ширине
            for (int y = 0; y < height; y++) { // Цикл по высоте
                // Создаем новую локацию и сохраняем её в массив
                locations[x][y] = new Location(x, y);
            }
        }
    }

    /**
     * Метод для спавна начальных животных в случайных локациях
     */
    private void spawnInitialAnimals() {
        // Проходим по всем конфигурациям животных, определенным в настройках
        for (SimulationSettings.AnimalConfig config : SimulationSettings.AnimalConfig.values()) {
            // Спавним указанное количество животных для каждого типа
            for (int i = 0; i < config.getInitialCount(); i++) {
                // Получаем случайную локацию
                Location loc = getRandomLocation();
                // Создаем животное и добавляем его в эту случайную локацию
                loc.addAnimal(config.createAnimal());
            }
        }
    }

    /**
     * Метод для получения случайной локации на острове
     */
    public Location getRandomLocation() {
        // Генерируем случайные координаты в пределах ширины и высоты острова
        int x = (int) (Math.random() * width); // Случайная координата по оси X
        int y = (int) (Math.random() * height); // Случайная координата по оси Y
        return locations[x][y]; // Возвращаем случайную локацию
    }

    /**
     * Метод для получения локации по заданным координатам
     */
    public Location getLocation(int x, int y) {
        // Проверяем, находятся ли координаты в допустимых пределах
        if (x >= 0 && x < width && y >= 0 && y < height) {
            // Если координаты валидны, возвращаем соответствующую локацию
            return locations[x][y];
        }
        return null; // Если координаты выходят за пределы, возвращаем null
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Location[][] getLocations() {
        return locations;
    }
}