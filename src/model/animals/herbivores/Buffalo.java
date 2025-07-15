package model.animals.herbivores;

import model.animals.Animal;
import model.animals.Herbivore;
import utils.SimulationSettings.AnimalConfig;

public class Buffalo extends Herbivore {
    public Buffalo() {
        super(AnimalConfig.BUFFALO);
    }

    @Override
    protected Animal createOffspring() {
        return new Buffalo();
    }

    @Override
    public String getEmoji() {
        return "🐃";
    }
}