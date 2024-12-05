package Models.Predator;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Herbivore.Herbivore;
import Models.Island.Cell;
import Models.Utils.EatingChancesSource;
import Simulation.Simulation;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Predator extends Animal {
    public Predator(double weight, int maxCountOnLocation, int travelSpeed, double maxSaturation, double hungerRate, String unicodeSymbol) {
        super(weight, maxCountOnLocation, travelSpeed, maxSaturation, hungerRate, unicodeSymbol);
    }

    @Override
    public void eat(IslandObject food, Cell currentCell, Simulation simulation) {
        double initialSaturation = this.getCurrentSaturation();  // Saturation level before eating

        // Проверяем, что еда является травоядным
        if (food instanceof Herbivore) {
            int chance = EatingChancesSource.getEatingChance(this.getClass(), food.getClass());  // Get a chance to eat
            int roll = ThreadLocalRandom.current().nextInt(100);  // Random number from 0 to 99

            // If the chance to eat has passed
            if (roll < chance) {
                this.increaseSaturation(food.getWeight());  // Increasing predator saturation
                currentCell.removeObject(food);  // Remove the eaten herbivore from the cell

                // We log that a predator ate a herbivore
                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") eats " + food.getClass().getSimpleName() +
                        " and gains " + String.format("%.1f", food.getWeight()) + " saturation (" +
                        formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");

                // Updating statistics on animals eaten
                if (food instanceof Animal) {
                    Animal eatenAnimal = (Animal) food;
                    eatenAnimal.getOnDeathCallback().accept(eatenAnimal, true);  // Помечаем как съеденное
                }

            } else {
                // If the meal fails, the predator loses its satiation.
                this.decreaseSaturation(this.getHungerRate());
                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") tried to eat " + food.getClass().getSimpleName() +
                        " but failed (" + formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
            }
        } else {
            // If the food is not herbivorous, the predator loses its satiation
            this.decreaseSaturation(this.getHungerRate());
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") could not find food and lost " +
                    String.format("%.1f", this.getHungerRate()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        }

        // Check if the animal has died of hunger and update the statistics via Simulation
        checkIfDead(currentCell, simulation);  // Pass the simulation object to update statistics
    }



}
