package Models.Island;

import Models.Abstraction.IslandObject;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final int x;
    private final int y;
    private final List<IslandObject> objectsOnCell;
    private final int maxObjects = 10; // maximum number of objects in a cell

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.objectsOnCell = new ArrayList<>();
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

    public synchronized boolean addObject(IslandObject object) {
        if (!canAddObject(object)) return false;
        return objectsOnCell.add(object);
    }

    public synchronized boolean canAddObject(IslandObject object) {
        if (objectsOnCell.size() >= maxObjects) {
            return false; // Cell is full
        }
        // We check if the number of objects of this type exceeds the limit
        int typeCount = countObjectsOfType(object.getClass());
        return typeCount < object.getMaxCountOnLocation();
    }

    public synchronized void removeObject(IslandObject object) {
        objectsOnCell.remove(object);
    }

    public synchronized void removeDeadObjects() {
        objectsOnCell.removeIf(obj -> {
            if (obj instanceof Models.Abstraction.Animal && ((Models.Abstraction.Animal) obj).isDead()) {
                System.out.println(obj.getClass().getSimpleName() + " (ID: " + obj.getId() + ") removed from cell (" + x + ", " + y + ").");
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
