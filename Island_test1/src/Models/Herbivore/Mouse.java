package Models.Herbivore;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Island.Cell;
import Models.Island.Island;
import Models.Utils.EatingChancesSource;
import Simulation.Simulation;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Mouse extends Herbivore {

    public Mouse() {
        super(0.05, 500, 3, 0.5, 0.1, "\uD83D\uDC01");
    }

    @Override
    public void eat(IslandObject food, Cell currentCell, Simulation simulation) {
        double initialSaturation = getCurrentSaturation();  // Saturation level before eating

        if (food instanceof Models.Herbivore.Caterpillar) {
            // Шанс поедания гусеницы
            int chance = EatingChancesSource.getEatingChance(this.getClass(), food.getClass());
            int roll = ThreadLocalRandom.current().nextInt(100);  // Random number from 0 to 99

            if (roll < chance) {
                // Increasing saturation after eating a caterpillar
                increaseSaturation(food.getWeight());
                currentCell.removeObject(food);  // Removing caterpillar from the cell
                System.out.println(getUnicodeSymbol() + " (ID: " + getId() + ") eats a Caterpillar and gains " +
                        String.format("%.1f", food.getWeight()) + " saturation (" +
                        formatSaturation(initialSaturation, getCurrentSaturation()) + ").");

                // Обновляем статистику по гусенице
                if (food instanceof Animal) {
                    Animal eatenAnimal = (Animal) food;
                    eatenAnimal.getOnDeathCallback().accept(eatenAnimal, true); // Mark as eaten
                }
            } else {
                // If no caterpillar is eaten decreasing saturation
                decreaseSaturation(getHungerRate());
                System.out.println(getUnicodeSymbol() + " (ID: " + getId() + ") tried to eat a Caterpillar but failed (" +
                        formatSaturation(initialSaturation, getCurrentSaturation()) + ").");
            }
        } else {
            // Herbivore behaviour if no caterpillar is found
            super.eat(food, currentCell, simulation);  // method of parent class while eating the plant
        }

        // Check if the animal died from hunger
        checkIfDead(currentCell, simulation);  // Pass the simulation object to update statistics
    }








    @Override
    public void reproduce(Cell currentCell, Island island) {
        super.reproduce(currentCell, island); // Reproduction logic via base class Animal
    }
}
