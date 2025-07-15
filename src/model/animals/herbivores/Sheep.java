package model.animals.herbivores;

import model.animals.Animal;
import model.animals.Herbivore;
import utils.SimulationSettings.AnimalConfig;

public class Sheep extends Herbivore {
    public Sheep() {
        super(AnimalConfig.SHEEP);
    }

    @Override
    protected Animal createOffspring() {
        return new Sheep();
    }

    @Override
    public String getEmoji() {
        return "🐑";
    }
}