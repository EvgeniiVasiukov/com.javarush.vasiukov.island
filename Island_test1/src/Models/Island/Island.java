package Models.Island;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Island {
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final Map<Class<? extends IslandObject>, Integer> objectCountMap;

    public Island(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        this.objectCountMap = new HashMap<>();
        initializeCells();
    }

    private void initializeCells() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }
    }
    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    public Cell getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return cells[x][y];
        }
        throw new IndexOutOfBoundsException("Coordinates out of island bounds.");
    }

    public synchronized boolean addObjectToCell(IslandObject object, int x, int y) {
        Cell cell = getCell(x, y);
        if (cell.addObject(object)) {
            incrementObjectCount(object.getClass());
            return true;
        }
        return false;
    }

    private synchronized void incrementObjectCount(Class<? extends IslandObject> type) {
        objectCountMap.put(type, objectCountMap.getOrDefault(type, 0) + 1);
    }


    // Method for obtaining neighboring cells taking into account the speed of movement
    public List<Cell> getNeighboringCells(int x, int y, int travelSpeed) {
        List<Cell> neighbors = new ArrayList<>();

        for (int dx = -travelSpeed; dx <= travelSpeed; dx++) {
            for (int dy = -travelSpeed; dy <= travelSpeed; dy++) {
                if (dx == 0 && dy == 0) continue; // Пропускаем саму клетку
                int neighborX = x + dx;
                int neighborY = y + dy;
                if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                    neighbors.add(getCell(neighborX, neighborY));
                }
            }
        }

        return neighbors;
    }

    public List<Cell> getAllCells() {
        List<Cell> allCells = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                allCells.add(cells[x][y]);
            }
        }
        return allCells;
    }

    public void processDeaths() {
        getAllCells().forEach(Cell::removeDeadObjects);
    }
}
