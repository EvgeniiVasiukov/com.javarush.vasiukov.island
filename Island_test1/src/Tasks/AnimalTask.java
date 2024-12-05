package Tasks;

import Models.Abstraction.*;
import Models.Island.*;
import Simulation.Simulation;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class AnimalTask implements Runnable {
    private final Animal animal;
    private final Cell currentCell;
    private final Island island;
    private final Simulation simulation;
    private final CountDownLatch latch;

    // Конструктор с передачей объектов simulation и latch
    public AnimalTask(Animal animal, Cell currentCell, Island island, Simulation simulation, CountDownLatch latch) {
        this.animal = animal;
        this.currentCell = currentCell;
        this.island = island;
        this.simulation = simulation;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            synchronized (currentCell) {
                if (animal.hasMoved()) {
                    return; // animal already moved
                }

                // moving
                Cell targetCell = getRandomNeighbor(currentCell, animal);
                if (targetCell != null) {
                    synchronized (targetCell) { // Thread-safe access to target cell
                        animal.move(currentCell, targetCell);
                    }
                } else {
                    System.out.println(animal.getUnicodeSymbol() + " (ID: " + animal.getId() + ") has no available cells to move.");
                }

                // food search
                IslandObject food = animal.findFood(currentCell);

                // Pass the simulation object to the eat method
                animal.eat(food, currentCell, simulation);

                // We check if the animal has died
                animal.checkIfDead(currentCell, simulation);

                // breeding
                animal.reproduce(currentCell, island);
            }
        } finally {
            latch.countDown(); // Decrement the wait counter after the task is completed
        }
    }

    // Метод для получения случайной соседней клетки
    private Cell getRandomNeighbor(Cell cell, Animal animal) {
        List<Cell> neighbors = island.getNeighboringCells(cell.getX(), cell.getY(), animal.getTravelSpeed());
        neighbors.removeIf(neighbor -> !neighbor.canAddObject(animal));
        if (!neighbors.isEmpty()) {
            return neighbors.get(ThreadLocalRandom.current().nextInt(neighbors.size()));
        }
        return null; // Если подходящих клеток нет
    }
}
