package Models;

import Models.Abstraction.IslandObject;
import Models.Island.Cell;
import Models.Island.Island;

import java.util.concurrent.ThreadLocalRandom;

public class Plant extends IslandObject {
    public Plant() {
        super(1, 200);
    }
    public void reproduce(Cell currentCell, Island island) {
        int chance = ThreadLocalRandom.current().nextInt(100);  // Random number from 0 to 99
        if (chance < 10) { // 10% chance of reproducing
            System.out.println("Plant is reproducing.");

            // Creating new plant
            Plant newPlant = new Plant();

            // Add a new plant to the same cell where it reproduces
            if (currentCell.canAddObject(newPlant)) {
                currentCell.addObject(newPlant); // Adding a new plant to the cell
                System.out.println("New plant added to cell (" + currentCell.getX() + ", " + currentCell.getY() + ").");
            } else {
                System.out.println("Cannot add new plant: cell is full.");
            }
        }
    }
}
