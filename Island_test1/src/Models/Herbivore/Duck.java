package Models.Herbivore;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Island.Cell;
import Models.Utils.EatingChancesSource;
import Simulation.Simulation;

import java.util.concurrent.ThreadLocalRandom;

public class Duck extends Herbivore {
    public Duck() {
        super(1.0, 200, 3, 5.0, 1.0, "🦆");
    }

    @Override
    public void eat(IslandObject food, Cell currentCell, Simulation simulation) {
        double initialSaturation = getCurrentSaturation();  // Saturation level before eating

        if (food instanceof Models.Herbivore.Caterpillar) {
            // Chance of eating the caterpillar
            int chance = EatingChancesSource.getEatingChance(this.getClass(), food.getClass());
            int roll = ThreadLocalRandom.current().nextInt(100);  // Random number from 0 to 99

            if (roll < chance) {
                // Increase satiety when eating a caterpillar
                increaseSaturation(food.getWeight());
                currentCell.removeObject(food);  // Removing caterpillar from cell
                System.out.println(getUnicodeSymbol() + " (ID: " + getId() + ") eats a Caterpillar and gains " +
                        String.format("%.1f", food.getWeight()) + " saturation (" +
                        formatSaturation(initialSaturation, getCurrentSaturation()) + ").");

                // Updating statistics on the caterpillar
                if (food instanceof Animal) {
                    Animal eatenAnimal = (Animal) food;
                    eatenAnimal.getOnDeathCallback().accept(eatenAnimal, true); // Помечаем как съеденное
                }
            } else {
                // If we fail to eat the caterpillar, we lose our satiety
                decreaseSaturation(getHungerRate());
                System.out.println(getUnicodeSymbol() + " (ID: " + getId() + ") tried to eat a Caterpillar but failed (" +
                        formatSaturation(initialSaturation, getCurrentSaturation()) + ").");
            }
        } else {
            // Herbivores behaviour if no caterpillar is found
            super.eat(food, currentCell, simulation);  // Calling the base method to eat a plant
        }

        // We check whether the animal has died of hunger
        checkIfDead(currentCell, simulation);  // Pass the simulation object to update statistics
    }


}
