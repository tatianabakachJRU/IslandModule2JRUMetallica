package model.animals.predators;

import model.Location;
import model.animals.Animal;
import model.animals.Predator;
import model.animals.herbivores.Duck;
import model.animals.herbivores.Mouse;
import model.animals.herbivores.Rabbit;
import utils.SimulationSettings;

import java.util.List;
import java.util.stream.Collectors;

public class Boa extends Predator {
    public Boa() {
        super(SimulationSettings.AnimalConfig.BOA);
    }

    @Override
    protected List<Animal> getPotentialFood(Location location) {
        return location.getAnimals().stream()
            .filter(a -> a instanceof Fox || a instanceof Rabbit 
                || a instanceof Mouse || a instanceof Duck)
            .filter(Animal::isAlive)
            .collect(Collectors.toList());
    }

    @Override
    protected int getEatingProbability(Animal prey) {
        if (prey instanceof Fox) return SimulationSettings.EatingChance.BOA_FOX;
        if (prey instanceof Rabbit) return SimulationSettings.EatingChance.BOA_RABBIT;
        if (prey instanceof Mouse) return SimulationSettings.EatingChance.BOA_MOUSE;
        if (prey instanceof Duck) return SimulationSettings.EatingChance.BOA_DUCK;
        return 0;
    }

    @Override
    protected Animal createOffspring() {
        return new Boa();
    }

    @Override
    public String getEmoji() {
        return "🐍";
    }
}