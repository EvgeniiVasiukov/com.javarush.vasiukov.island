package Models.Herbivore;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Island.Cell;
import Models.Plant;
import Simulation.Simulation;

public abstract class Herbivore extends Animal {
    public Herbivore(double weight, int maxCountOnLocation, int travelSpeed, double maxSaturation, double hungerRate, String unicodeSymbol) {
        super(weight, maxCountOnLocation, travelSpeed, maxSaturation, hungerRate, unicodeSymbol);
    }

    @Override
    public void eat(IslandObject food, Cell currentCell, Simulation simulation) {
        double initialSaturation = this.getCurrentSaturation(); // Saturation level before eating

        if (food instanceof Plant) {
            increaseSaturation(food.getWeight()); // Increasing saturation
            currentCell.removeObject(food); // Removing plant from the cell

            // Logging that animal ate plant
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") eats a plant and gains " +
                    String.format("%.1f", food.getWeight()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        } else {
            decreaseSaturation(getHungerRate()); // If food is not found saturation is lost

            // Logging that animal could not find food
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") could not find food and lost " +
                    String.format("%.1f", getHungerRate()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        }

        // We check if the animal has died of hunger and update the statistics via Simulation
        checkIfDead(currentCell, simulation);
    }


}
