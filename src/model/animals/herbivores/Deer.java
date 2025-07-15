package model.animals.herbivores;

import model.animals.Animal;
import model.animals.Herbivore;
import utils.SimulationSettings.AnimalConfig;

public class Deer extends Herbivore {
    public Deer() {
        super(AnimalConfig.DEER);
    }

    @Override
    protected Animal createOffspring() {
        return new Deer();
    }

    @Override
    public String getEmoji() {
        return "🦌";
    }
}