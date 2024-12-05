package Models.Abstraction;

import Models.Island.Cell;
import Models.Island.Island;

import java.util.UUID;

public abstract class IslandObject {
    private double weight;
    private int maxCountOnLocation;
    private int x;
    private int y;
    private String id; // Unique ID for each object

    public IslandObject(double weight, int maxCountOnLocation) {
        this.weight = weight;
        this.maxCountOnLocation = maxCountOnLocation;
        this.id = UUID.randomUUID().toString(); // Unique ID generation
    }

    public String getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public int getMaxCountOnLocation() {
        return maxCountOnLocation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void reproduce(Cell currentCell, Island island);
}
