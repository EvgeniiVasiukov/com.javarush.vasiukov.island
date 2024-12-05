package Models.Utils;

import Models.Herbivore.Duck;
import Models.Herbivore.Mouse;

import java.util.HashMap;
import java.util.Map;

public class MovementConfig {
    // Map of movement chances for all animals
    private static final Map<Class<?>, Integer> movementChances = new HashMap<>();

    static {
        // Movement chances for each animal type, percentage
        movementChances.put(Models.Herbivore.Caterpillar.class, 10);
        movementChances.put(Models.Herbivore.Cow.class, 20);
        movementChances.put(Models.Herbivore.Deer.class, 60);
        movementChances.put(Duck.class, 60);
        movementChances.put(Models.Herbivore.Goat.class, 30);
        movementChances.put(Models.Herbivore.Hamster.class, 90);
        movementChances.put(Models.Herbivore.Horse.class, 70);
        movementChances.put(Mouse.class, 85);
        movementChances.put(Models.Herbivore.Rabbit.class, 80);
        movementChances.put(Models.Herbivore.Sheep.class, 50);
        movementChances.put(Models.Predator.Wolf.class, 90);
        movementChances.put(Models.Predator.Bear.class, 50);
        movementChances.put(Models.Predator.Eagle.class, 95);
        movementChances.put(Models.Predator.Snake.class, 30);
        movementChances.put(Models.Predator.Fox.class, 80);
    }

    // method for getting movement chances
    public static int getMovementChance(Class<?> animalType) {
        return movementChances.getOrDefault(animalType, 0); // returns 0 if not stated
    }
}
