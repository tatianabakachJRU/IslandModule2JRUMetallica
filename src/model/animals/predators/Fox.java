package model.animals.predators;

import model.Location;
import model.animals.Animal;
import model.animals.Predator;
import model.animals.herbivores.Caterpillar;
import model.animals.herbivores.Duck;
import model.animals.herbivores.Mouse;
import model.animals.herbivores.Rabbit;
import utils.SimulationSettings;

import java.util.List;
import java.util.stream.Collectors;

public class Fox extends Predator {
    public Fox() {
        super(SimulationSettings.AnimalConfig.FOX);
    }

    @Override
    protected List<Animal> getPotentialFood(Location location) {
        return location.getAnimals().stream()
            .filter(a -> a instanceof Rabbit || a instanceof Mouse 
                || a instanceof Duck || a instanceof Caterpillar)
            .filter(Animal::isAlive)
            .collect(Collectors.toList());
    }

    @Override
    protected int getEatingProbability(Animal prey) {
        if (prey instanceof Rabbit) return SimulationSettings.EatingChance.FOX_RABBIT;
        if (prey instanceof Mouse) return SimulationSettings.EatingChance.FOX_MOUSE;
        if (prey instanceof Duck) return SimulationSettings.EatingChance.FOX_DUCK;
        if (prey instanceof Caterpillar) return SimulationSettings.EatingChance.FOX_CATERPILLAR;
        return 0;
    }

    @Override
    protected Animal createOffspring() {
        return new Fox();
    }

    @Override
    public String getEmoji() {
        return "🦊";
    }
}