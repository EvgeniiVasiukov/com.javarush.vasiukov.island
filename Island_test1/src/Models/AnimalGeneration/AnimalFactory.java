package Models.AnimalGeneration;

import Models.Abstraction.Animal;
import Models.Predator.*;
import Models.Herbivore.*;

public class AnimalFactory {
    public static Animal createAnimal(AnimalType type) {
        if (type == null) {
            throw new IllegalArgumentException("AnimalType cannot be null.");
        }

        switch (type) {
            case WOLF:
                return new Wolf();
            case BEAR:
                return new Bear();
            case FOX:
                return new Fox();
            case SNAKE:
                return new Snake();
            case EAGLE:
                return new Eagle();
            case RABBIT:
                return new Rabbit();
            case HORSE:
                return new Horse();
            case COW:
                return new Cow();
            case DEER:
                return new Deer();
            case SHEEP:
                return new Sheep();
            case CATERPILLAR:
                return new Caterpillar();
            case HAMSTER:
                return new Hamster();
            case GOAT:
                return new Goat();
            case MOUSE:
                return new Mouse();
            case DUCK:
                return new Duck();
            default:
                throw new UnsupportedOperationException("AnimalType not supported in factory: " + type);
        }
    }
}
