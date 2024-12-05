package Models.Utils;

import java.util.HashMap;
import java.util.Map;

import Models.Herbivore.Herbivore;
import Models.Predator.Predator;
import Models.Herbivore.*;
import Models.Herbivore.Duck;
import Models.Herbivore.Mouse;
import Models.Predator.*;

public class EatingChancesSource {
    private static final Map<Class<? extends Predator>, Map<Class<? extends Herbivore>, Integer>> eatingChances = new HashMap<>();

    static {
        // Chances for wolf
        Map<Class<? extends Herbivore>, Integer> wolfChances = new HashMap<>();
        wolfChances.put(Rabbit.class, 60);
        wolfChances.put(Sheep.class, 50);
        wolfChances.put(Goat.class, 70);
        wolfChances.put(Mouse.class, 90);
        wolfChances.put(Hamster.class, 70);
        wolfChances.put(Caterpillar.class, 90);
        wolfChances.put(Duck.class, 50);
        wolfChances.put(Cow.class, 100);
        wolfChances.put(Deer.class, 100);
        eatingChances.put(Wolf.class, wolfChances);

        // Chances for fox
        Map<Class<? extends Herbivore>, Integer> foxChances = new HashMap<>();
        foxChances.put(Rabbit.class, 100);
        foxChances.put(Mouse.class, 70);
        foxChances.put(Hamster.class, 80);
        foxChances.put(Caterpillar.class, 90);
        foxChances.put(Duck.class, 100);
        eatingChances.put(Fox.class, foxChances);

        // Chances for bear
        Map<Class<? extends Herbivore>, Integer> bearChances = new HashMap<>();
        bearChances.put(Rabbit.class, 40);
        bearChances.put(Sheep.class, 80);
        bearChances.put(Goat.class, 90);
        bearChances.put(Mouse.class, 50);
        bearChances.put(Cow.class, 90);
        bearChances.put(Deer.class, 80);
        bearChances.put(Duck.class, 90);
        eatingChances.put(Bear.class, bearChances);

        // Chances for eagle
        Map<Class<? extends Herbivore>, Integer> eagleChances = new HashMap<>();
        eagleChances.put(Mouse.class, 100);
        eagleChances.put(Hamster.class, 100);
        eagleChances.put(Caterpillar.class, 90);
        eagleChances.put(Duck.class, 80);
        eatingChances.put(Eagle.class, eagleChances);

        // Chances for snake
        Map<Class<? extends Herbivore>, Integer> snakeChances = new HashMap<>();
        snakeChances.put(Mouse.class, 100);
        snakeChances.put(Hamster.class, 100);
        snakeChances.put(Caterpillar.class, 100);
        eatingChances.put(Snake.class, snakeChances);
    }

    public static int getEatingChance(Class<?> predator, Class<?> prey) {
        if (Herbivore.class.isAssignableFrom(prey)) {
            @SuppressWarnings("unchecked")
            Class<? extends Herbivore> herbivorePrey = (Class<? extends Herbivore>) prey;
            return specificEatingChanceLogic(predator, herbivorePrey);
        }
        return 0; // If prey is not herbivore
    }

    // Method for getting chance if both types are already known
    public static int specificEatingChanceLogic(Class<?> predator, Class<? extends Herbivore> prey) {
        return eatingChances.getOrDefault(predator, new HashMap<>()).getOrDefault(prey, 0);
    }
}
