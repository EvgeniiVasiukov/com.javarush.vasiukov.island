package Models.Utils;

import Models.Herbivore.Duck;
import Models.Herbivore.Mouse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BreedingConfig {
    // Breeding chances map
    private static final Map<Class<?>, Integer> breedingChances = new HashMap<>();

    // Map of maximum offsprings number
    private static final Map<Class<?>, Integer> maxOffspringCounts = new HashMap<>();

    static {
        // Putting breeding chances for every animal
        breedingChances.put(Models.Herbivore.Caterpillar.class, 20);
        breedingChances.put(Models.Herbivore.Cow.class, 60);
        breedingChances.put(Models.Herbivore.Deer.class, 40);
        breedingChances.put(Duck.class, 70);
        breedingChances.put(Models.Herbivore.Goat.class, 35);
        breedingChances.put(Models.Herbivore.Hamster.class, 70);
        breedingChances.put(Models.Herbivore.Horse.class, 25);
        breedingChances.put(Mouse.class, 25);
        breedingChances.put(Models.Herbivore.Rabbit.class, 50);
        breedingChances.put(Models.Herbivore.Sheep.class, 30);
        breedingChances.put(Models.Predator.Wolf.class, 40);
        breedingChances.put(Models.Predator.Bear.class, 30);
        breedingChances.put(Models.Predator.Eagle.class, 60);
        breedingChances.put(Models.Predator.Snake.class, 80);
        breedingChances.put(Models.Predator.Fox.class, 40);


        // Putting maximum amount of offsprings for each animal
        maxOffspringCounts.put(Models.Herbivore.Caterpillar.class, 10);
        maxOffspringCounts.put(Models.Herbivore.Cow.class, 2);
        maxOffspringCounts.put(Models.Herbivore.Deer.class, 1);
        maxOffspringCounts.put(Duck.class, 6);
        maxOffspringCounts.put(Models.Herbivore.Goat.class, 2);
        maxOffspringCounts.put(Models.Herbivore.Hamster.class, 4);
        maxOffspringCounts.put(Models.Herbivore.Horse.class, 1);
        maxOffspringCounts.put(Mouse.class, 6);
        maxOffspringCounts.put(Models.Herbivore.Rabbit.class, 7);
        maxOffspringCounts.put(Models.Herbivore.Sheep.class, 2);
        maxOffspringCounts.put(Models.Predator.Wolf.class, 3);
        maxOffspringCounts.put(Models.Predator.Bear.class, 3);
        maxOffspringCounts.put(Models.Predator.Eagle.class, 3);
        maxOffspringCounts.put(Models.Predator.Snake.class, 8);
        maxOffspringCounts.put(Models.Predator.Fox.class, 7);
    }

    // Getting breeding chance
    public static int getBreedingChance(Class<?> animalType) {
        return breedingChances.getOrDefault(animalType, 0);
    }

    // Getting random offsprings, not more than maximum
    public static int getRandomOffspringCount(Class<?> animalType) {
        int maxOffspring = maxOffspringCounts.getOrDefault(animalType, 0);
        if (maxOffspring > 0) {
            return ThreadLocalRandom.current().nextInt(1, maxOffspring + 1);
        }
        return 0; // if there is no entry for animal return 0
    }
}
