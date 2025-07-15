package model.animals.herbivores;

import model.animals.Animal;
import model.animals.Herbivore;
import utils.SimulationSettings.AnimalConfig;

public class Goat extends Herbivore {
    public Goat() {
        super(AnimalConfig.GOAT);
    }

    @Override
    protected Animal createOffspring() {
        return new Goat();
    }

    @Override
    public String getEmoji() {
        return "🐐";
    }
}