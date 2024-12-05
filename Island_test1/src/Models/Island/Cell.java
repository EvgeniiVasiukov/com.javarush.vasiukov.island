package Models.Island;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Cell {
    private static final Logger logger = Logger.getLogger(Cell.class.getName());
    private final int x;
    private final int y;
    private final List<IslandObject> objectsOnCell;
    private final int maxObjects = 10; // Максимальное количество объектов на клетке

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.objectsOnCell = new ArrayList<>();
    }

    public synchronized List<IslandObject> getObjects() {
        return new ArrayList<>(objectsOnCell);
    }

    public synchronized <T extends IslandObject> List<T> getObjectsOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (IslandObject obj : objectsOnCell) {
            if (type.isInstance(obj)) {
                result.add(type.cast(obj));
            }
        }
        return result;
    }

    public synchronized <T extends IslandObject> int countObjectsOfType(Class<T> type) {
        return (int) objectsOnCell.stream().filter(type::isInstance).count();
    }

    public synchronized void reproduceObjects(Island island) {
        for (IslandObject obj : new ArrayList<>(objectsOnCell)) {
            if (obj instanceof Animal) {
                ((Animal) obj).reproduce(this, island);
            }
        }
    }

    public synchronized boolean addObject(IslandObject object) {
        if (!canAddObject(object)) {
            return false;
        }
        objectsOnCell.add(object);
        return true;
    }

    public synchronized boolean canAddObject(IslandObject object) {
        if (objectsOnCell.size() >= maxObjects) {
            return false;
        }
        return countObjectsOfType(object.getClass()) < object.getMaxCountOnLocation();
    }

    public synchronized void removeObject(IslandObject object) {
        objectsOnCell.remove(object);
    }

    public synchronized boolean hasSpaceForObject() {
        return objectsOnCell.size() < maxObjects;
    }

    public synchronized void removeDeadObjects() {
        objectsOnCell.removeIf(obj -> {
            if (obj instanceof Animal && ((Animal) obj).isDead()) {
                logger.info(() -> ((Animal) obj).getUnicodeSymbol() + " (ID: " + obj.getId() + ") died on cell (" + x + ", " + y + ").");
                return true;
            }
            return false;
        });
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
