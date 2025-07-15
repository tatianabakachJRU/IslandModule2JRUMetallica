package model.animals.predators;

import model.Location;
import model.animals.Animal;
import model.animals.Predator;
import model.animals.herbivores.*;
import utils.SimulationSettings;

import java.util.List;
import java.util.stream.Collectors;

public class Bear extends Predator {
    public Bear() {
        super(SimulationSettings.AnimalConfig.BEAR);
    }

    @Override
    protected List<Animal> getPotentialFood(Location location) {
        return location.getAnimals().stream()
                .filter(a -> a instanceof Boa || a instanceof Horse
                        || a instanceof Deer || a instanceof Rabbit
                        || a instanceof Mouse || a instanceof Goat
                        || a instanceof Sheep || a instanceof Boar
                        || a instanceof Duck)
                .collect(Collectors.toList()); // Удален лишний .filter(Animal::isAlive)
    }

    @Override
    protected int getEatingProbability(Animal prey) {
        if (prey instanceof Boa) return SimulationSettings.EatingChance.BEAR_BOA;
        if (prey instanceof Horse) return SimulationSettings.EatingChance.BEAR_HORSE;
        if (prey instanceof Deer) return SimulationSettings.EatingChance.BEAR_DEER;
        if (prey instanceof Rabbit) return SimulationSettings.EatingChance.BEAR_RABBIT;
        if (prey instanceof Mouse) return SimulationSettings.EatingChance.BEAR_MOUSE;
        if (prey instanceof Goat) return SimulationSettings.EatingChance.BEAR_GOAT;
        if (prey instanceof Sheep) return SimulationSettings.EatingChance.BEAR_SHEEP;
        if (prey instanceof Boar) return SimulationSettings.EatingChance.BEAR_BOAR;
        if (prey instanceof Duck) return SimulationSettings.EatingChance.BEAR_DUCK;
        return 0;
    }

    @Override
    protected Animal createOffspring() {
        return new Bear();
    }

    @Override
    public String getEmoji() {
        return "🐻";
    }
}