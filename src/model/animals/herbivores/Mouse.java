package model.animals.herbivores;

import model.animals.Animal;
import model.animals.CaterpillarEater;
import model.animals.Herbivore;
import utils.SimulationSettings;

public class Mouse extends Herbivore implements CaterpillarEater {
    public Mouse() {
        super(SimulationSettings.AnimalConfig.MOUSE);
    }

    @Override
    public int getCaterpillarEatingChance() {
        return SimulationSettings.EatingChance.MOUSE_CATERPILLAR;
    }

    @Override
    protected Animal createOffspring() {
        return new Mouse();
    }

    @Override
    public String getEmoji() {
        return "🐁";
    }
}