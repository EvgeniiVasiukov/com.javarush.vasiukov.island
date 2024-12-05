package Models.AnimalGeneration;


import Models.Abstraction.Animal;
import Models.Predator.*;
import Models.Herbivore.*;

public enum AnimalType {
    WOLF(Wolf.class),
    BEAR(Bear.class),
    DUCK(Duck.class),
    MOUSE(Mouse.class),
    COW(Cow.class),
    RABBIT(Rabbit.class),
    SHEEP(Sheep.class),
    HORSE(Horse.class),
    GOAT(Goat.class),
    CATERPILLAR(Caterpillar.class),
    HAMSTER(Hamster.class),
    FOX(Fox.class),
    EAGLE(Eagle.class),
    DEER(Deer.class),
    SNAKE(Snake.class);

    private final Class<? extends Animal> animalClass;

    AnimalType(Class<? extends Animal> animalClass) {
        this.animalClass = animalClass;
    }

    public Class<? extends Animal> getAnimalClass() {
        return animalClass;
    }
}

