package Tasks;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Island.Cell;
import Models.Island.Island;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AnimalTask implements Runnable {
    private final Animal animal;
    private final Cell currentCell;
    private final Island island;

    public AnimalTask(Animal animal, Cell currentCell, Island island) {
        this.animal = animal;
        this.currentCell = currentCell;
        this.island = island;
    }

    @Override
    public void run() {
        try {
            synchronized (currentCell) {
                if (animal.hasMoved()) {
                    return; // Животное уже переместилось
                }

                // Перемещение
                Cell targetCell = getRandomNeighbor(currentCell, animal);
                if (targetCell != null) {
                    synchronized (targetCell) { // Потокобезопасный доступ к целевой клетке
                        animal.move(currentCell, targetCell);
                    }
                } else {
                    System.out.println(animal.getUnicodeSymbol() + " (ID: " + animal.getId() + ") has no available cells to move.");
                }

                // Питание
                synchronized (currentCell) {
                    IslandObject food = island.findFood(currentCell, animal);
                    animal.eat(food, currentCell);
                }

                // Размножение
                synchronized (currentCell) {
                    animal.reproduce(currentCell, island);
                }
            }
        } catch (Exception e) {
            System.err.println("Error while processing task for animal: " + animal.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    // Метод для получения случайной соседней клетки
    private Cell getRandomNeighbor(Cell cell, Animal animal) {
        List<Cell> neighbors = island.getNeighboringCells(cell.getX(), cell.getY(), animal.getTravelSpeed());

        // Фильтруем недоступные клетки
        neighbors.removeIf(neighbor -> !neighbor.canAddObject(animal));

        // Если есть доступные клетки, возвращаем случайную из них
        if (!neighbors.isEmpty()) {
            return neighbors.get(ThreadLocalRandom.current().nextInt(neighbors.size()));
        }

        return null; // Если подходящих клеток нет
    }
}

