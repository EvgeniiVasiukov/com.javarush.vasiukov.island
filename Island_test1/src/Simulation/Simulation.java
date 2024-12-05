package Simulation;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.AnimalGeneration.AnimalFactory;
import Models.AnimalGeneration.AnimalType;
import Models.Island.*;
import Models.Plant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Simulation {
    private final Island island;
    private final int steps;
    private final ExecutorService animalExecutor;
    private final ExecutorService islandExecutor;
    private int deadAnimalsCount = 0; // Счётчик мёртвых животных
    private int eatenAnimalsCount = 0; // Счётчик съеденных животных

    public Simulation(int islandWidth, int islandHeight, int steps) {
        this.island = new Island(islandWidth, islandHeight);
        this.steps = steps;

        // Экзекьюторы
        this.animalExecutor = Executors.newCachedThreadPool();
        this.islandExecutor = Executors.newSingleThreadExecutor();
    }

    public void initialize() {
        island.addObjectToCell(new Plant(), 0, 0);

        island.addObjectToCell(AnimalFactory.createAnimal(AnimalType.WOLF), 0, 2);
        island.addObjectToCell(AnimalFactory.createAnimal(AnimalType.WOLF), 1, 0);
        island.addObjectToCell(AnimalFactory.createAnimal(AnimalType.WOLF), 0, 2);
        island.addObjectToCell(AnimalFactory.createAnimal(AnimalType.COW), 0, 2);

        island.addObjectToCell(AnimalFactory.createAnimal(AnimalType.SHEEP), 1, 0);
        island.addObjectToCell(AnimalFactory.createAnimal(AnimalType.HAMSTER), 1, 0);
        island.addObjectToCell(AnimalFactory.createAnimal(AnimalType.GOAT), 1, 0);



        // Передача обратного вызова каждому животному
        island.getAllCells().forEach(cell ->
                cell.getObjectsOfType(Animal.class).forEach(animal ->
                        animal.setOnDeathCallback((deadAnimal, isEaten) -> incrementDeadAnimals(deadAnimal, isEaten))
                )
        );}

    public void start() {
        for (int day = 1; day <= steps; day++) {
            System.out.println("\nDay " + day + " starts:");

            // Перемещение всех животных и обработка еды
            processAnimalTasks();

            // Засаживание растений и вывод статистики
            processIslandTasks();

            // Проверяем завершение симуляции
            if (isSimulationOver()) {
                System.out.println("All animals have died. Simulation over.");
                break;
            }

            // Вывод статистики после обработки острова
            logStatistics();
        }

        // Завершение работы экзекьюторов
        shutdownExecutors();
    }


    private void processAnimalTasks() {
        CountDownLatch latch = new CountDownLatch((int) island.getAllCells().stream()
                .flatMap(cell -> cell.getObjectsOfType(Animal.class).stream())
                .count());

        for (Cell cell : island.getAllCells()) {
            for (Animal animal : cell.getObjectsOfType(Animal.class)) {
                animalExecutor.submit(() -> {
                    try {
                        Cell targetCell = getRandomNeighbor(cell, animal);
                        animal.move(cell, targetCell);
                        IslandObject food = island.findFood(cell, animal);
                        animal.eat(food, cell);
                        animal.checkIfDead(cell);
                        cell.reproduceObjects(island);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        try {
            latch.await(); // Ждем завершения всех задач
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Animal tasks interrupted", e);
        }
    }





    private void processIslandTasks() {
        islandExecutor.submit(() -> {
            try {
                plantNewPlants();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        resetMovementFlags();
        try {
            islandExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Island processing interrupted", e);
        }
    }

    private Cell getRandomNeighbor(Cell currentCell, Animal animal) {
        List<Cell> neighbors = island.getNeighboringCells(currentCell.getX(), currentCell.getY(), animal.getTravelSpeed());

        // Фильтруем клетки, в которые животное может переместиться
        neighbors.removeIf(neighbor -> !neighbor.canAddObject(animal));

        // Если есть доступные клетки, возвращаем случайную
        if (!neighbors.isEmpty()) {
            return neighbors.get(ThreadLocalRandom.current().nextInt(neighbors.size()));
        }

        return null; // Если подходящих клеток нет
    }

    private void plantNewPlants() {
        island.getAllCells().forEach(cell -> {
            if (Math.random() < 0.05) { // Шанс засадить растение
                cell.addObject(new Plant());
            }
        });
    }

    private void logStatistics() {
        Map<Class<?>, Integer> animalCounts = new HashMap<>();
        int totalPlants = 0;

        // Сбор статистики по клеткам острова
        for (Cell cell : island.getAllCells()) {
            // Считаем животных
            for (Animal animal : cell.getObjectsOfType(Animal.class)) {
                animalCounts.put(animal.getClass(), animalCounts.getOrDefault(animal.getClass(), 0) + 1);
            }
            // Считаем растения
            totalPlants += cell.countObjectsOfType(Plant.class);
        }

        // Общий подсчет животных
        int totalAnimals = animalCounts.values().stream().mapToInt(Integer::intValue).sum();

        // Вывод статистики
        System.out.println("Statistics:");
        System.out.println("  Animals alive: " + totalAnimals);
        animalCounts.forEach((animalClass, count) -> {
            try {
                // Создание временного экземпляра животного, чтобы получить символ Unicode
                Animal tempAnimal = (Animal) animalClass.getDeclaredConstructor().newInstance();
                System.out.println("    " + tempAnimal.getUnicodeSymbol() + " " + animalClass.getSimpleName() + ": " + count);
            } catch (Exception e) {
                System.out.println("    " + animalClass.getSimpleName() + ": " + count);
            }
        });

        System.out.println("  Animals dead (hunger): " + deadAnimalsCount);
        System.out.println("  Animals eaten: " + eatenAnimalsCount);
        System.out.println("  Plants: " + totalPlants);
        System.out.println("Total animal deaths: " + (deadAnimalsCount + eatenAnimalsCount));
    }





    private boolean isSimulationOver() {
        return island.getAllCells().stream()
                .mapToInt(cell -> cell.countObjectsOfType(Animal.class))
                .sum() == 0;
    }

    private void waitForCompletion(ExecutorService executor) {
        executor.shutdown(); // Завершаем приём новых задач
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println("Tasks did not complete in time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Tasks interrupted", e);
        }
    }

    private void shutdownExecutors() {
        try {
            animalExecutor.shutdown();
            if (!animalExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println("Animal tasks did not complete in time.");
            }

            islandExecutor.shutdown();
            if (!islandExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println("Island tasks did not complete in time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Shutdown interrupted", e);
        }
    }


    public void resetMovementFlags() {
        for (Cell cell : island.getAllCells()) {
            for (Animal animal : cell.getObjectsOfType(Animal.class)) {
                animal.setHasMoved(false);
            }
        }
    }

    private void incrementDeadAnimals(Animal animal, boolean isEaten) {
        if (isEaten) {
            eatenAnimalsCount++;
            System.out.println(animal.getUnicodeSymbol() + " (ID: " + animal.getId() + ") was eaten.");
        } else {
            deadAnimalsCount++;
            System.out.println(animal.getUnicodeSymbol() + " (ID: " + animal.getId() + ") died of hunger.");
        }
    }
}
