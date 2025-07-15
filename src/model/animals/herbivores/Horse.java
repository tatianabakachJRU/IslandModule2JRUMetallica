package model.animals.herbivores;

import model.animals.Animal;
import model.animals.Herbivore;
import utils.SimulationSettings.AnimalConfig;

public class Horse extends Herbivore {
    public Horse() {
        super(AnimalConfig.HORSE);
    }

    @Override
    protected Animal createOffspring() {
        return new Horse();
    }

    @Override
    public String getEmoji() {
        return "🐎";
    }
}