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
        // Шансы для волка
        Map<Class<? extends Herbivore>, Integer> wolfChances = new HashMap<>();
        wolfChances.put(Rabbit.class, 60);
        wolfChances.put(Sheep.class, 50);
        wolfChances.put(Goat.class, 40);
        wolfChances.put(Mouse.class, 30);
        wolfChances.put(Hamster.class, 70);
        wolfChances.put(Caterpillar.class, 90);
        wolfChances.put(Duck.class, 50);
        wolfChances.put(Cow.class, 80);
        wolfChances.put(Deer.class, 30);
        eatingChances.put(Wolf.class, wolfChances);

        // Шансы для лисы
        Map<Class<? extends Herbivore>, Integer> foxChances = new HashMap<>();
        foxChances.put(Rabbit.class, 50);
        foxChances.put(Mouse.class, 70);
        foxChances.put(Hamster.class, 80);
        foxChances.put(Caterpillar.class, 90);
        foxChances.put(Duck.class, 40);
        eatingChances.put(Fox.class, foxChances);

        // Шансы для медведя
        Map<Class<? extends Herbivore>, Integer> bearChances = new HashMap<>();
        bearChances.put(Rabbit.class, 40);
        bearChances.put(Sheep.class, 60);
        bearChances.put(Goat.class, 50);
        bearChances.put(Mouse.class, 50);
        bearChances.put(Cow.class, 30);
        bearChances.put(Deer.class, 40);
        bearChances.put(Duck.class, 50);
        eatingChances.put(Bear.class, bearChances);

        // Шансы для орла
        Map<Class<? extends Herbivore>, Integer> eagleChances = new HashMap<>();
        eagleChances.put(Mouse.class, 80);
        eagleChances.put(Hamster.class, 70);
        eagleChances.put(Caterpillar.class, 90);
        eagleChances.put(Duck.class, 60);
        eatingChances.put(Eagle.class, eagleChances);

        // Шансы для змеи
        Map<Class<? extends Herbivore>, Integer> snakeChances = new HashMap<>();
        snakeChances.put(Mouse.class, 60);
        snakeChances.put(Hamster.class, 50);
        snakeChances.put(Caterpillar.class, 70);
        eatingChances.put(Snake.class, snakeChances);
    }

    public static int getEatingChance(Class<?> predator, Class<?> prey) {
        if (Herbivore.class.isAssignableFrom(prey)) {
            @SuppressWarnings("unchecked")
            Class<? extends Herbivore> herbivorePrey = (Class<? extends Herbivore>) prey;
            return specificEatingChanceLogic(predator, herbivorePrey);
        }
        return 0; // Если prey не является травоядным
    }

    // Метод для получения шанса, если оба типа уже известны
    public static int specificEatingChanceLogic(Class<?> predator, Class<? extends Herbivore> prey) {
        return eatingChances.getOrDefault(predator, new HashMap<>()).getOrDefault(prey, 0);
    }
}
