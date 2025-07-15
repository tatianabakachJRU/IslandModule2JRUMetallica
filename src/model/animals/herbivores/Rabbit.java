package model.animals.herbivores;

import model.animals.Animal;
import model.animals.Herbivore;
import utils.SimulationSettings.AnimalConfig;

public class Rabbit extends Herbivore {
    public Rabbit() {
        super(AnimalConfig.RABBIT);
    }

    @Override
    protected Animal createOffspring() {
        return new Rabbit();
    }

    @Override
    public String getEmoji() {
        return "🐇";
    }
}