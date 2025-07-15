package model.animals.herbivores;

import model.animals.Animal;
import model.animals.Herbivore;
import utils.SimulationSettings.AnimalConfig;

public class Caterpillar extends Herbivore {
    public Caterpillar() {
        super(AnimalConfig.CATERPILLAR);
    }

    @Override
    public String getEmoji() {
        return "🐛";
    }

    @Override
    protected Animal createOffspring() {
        return new Caterpillar();
    }
}