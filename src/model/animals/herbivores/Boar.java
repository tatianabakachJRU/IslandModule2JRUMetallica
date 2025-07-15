package model.animals.herbivores;

import model.animals.Animal;
import model.animals.CaterpillarEater;
import model.animals.Herbivore;
import utils.SimulationSettings;

public class Boar extends Herbivore implements CaterpillarEater {
    public Boar() {
        super(SimulationSettings.AnimalConfig.BOAR);
    }

    @Override
    public int getCaterpillarEatingChance() {
        return SimulationSettings.EatingChance.BOAR_CATERPILLAR;
    }

    @Override
    protected Animal createOffspring() {
        return new Boar();
    }

    @Override
    public String getEmoji() {
        return "🐗";
    }
}