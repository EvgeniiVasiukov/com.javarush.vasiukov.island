package Simulation;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.AnimalGeneration.AnimalFactory;
import Models.AnimalGeneration.AnimalType;
import Models.Island.Cell;
import Models.Island.Island;
import Models.Plant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Simulation {
    private final Island island;
    private final ExecutorService animalExecutor;
    private final ExecutorService islandExecutor;
    private int deadAnimalsCount = 0;
    private int eatenAnimalsCount = 0;

    public Simulation(int islandWidth, int islandHeight) {
        this.island = new Island(islandWidth, islandHeight);

        this.animalExecutor = Executors.newCachedThreadPool();
        this.islandExecutor = Executors.newSingleThreadExecutor();
    }

    public void initialize() {

        // Создаем 10 животных каждого вида
        createAndPlaceAnimals(AnimalType.SHEEP, 10);
        createAndPlaceAnimals(AnimalType.CATERPILLAR, 10);
        createAndPlaceAnimals(AnimalType.COW, 8);
        createAndPlaceAnimals(AnimalType.DUCK, 10);
        createAndPlaceAnimals(AnimalType.GOAT, 12);
        createAndPlaceAnimals(AnimalType.HAMSTER, 10);
        createAndPlaceAnimals(AnimalType.HORSE, 6);
        createAndPlaceAnimals(AnimalType.MOUSE, 9);
        createAndPlaceAnimals(AnimalType.RABBIT, 8);
        createAndPlaceAnimals(AnimalType.WOLF, 15);
        createAndPlaceAnimals(AnimalType.BEAR, 5);
        createAndPlaceAnimals(AnimalType.FOX, 15);
        createAndPlaceAnimals(AnimalType.EAGLE, 30);
        createAndPlaceAnimals(AnimalType.SNAKE, 15);

        // Устанавливаем обработчик смерти для каждого животного
        island.getAllCells().forEach(cell ->
                cell.getObjectsOfType(Animal.class).forEach(animal ->
                        animal.setOnDeathCallback((deadAnimal, isEaten) -> incrementDeadAnimals(deadAnimal, isEaten))
                )
        );
    }

    private Cell getRandomCell() {
        List<Cell> allCells = island.getAllCells();
        return allCells.get(ThreadLocalRandom.current().nextInt(allCells.size()));  // Выбираем случайную клетку
    }

    // Метод для создания и размещения животных случайным образом
    private void createAndPlaceAnimals(AnimalType animalType, int count) {
        for (int i = 0; i < count; i++) {
            Animal animal = AnimalFactory.createAnimal(animalType);
            // Случайно выбираем клетку для размещения животного
            Cell randomCell = getRandomCell();
            if (randomCell != null) {
                randomCell.addObject(animal);  // Размещаем животное в выбранной клетке
            }
        }
    }

    public void start() {
        int day = 1;
        while (!isSimulationOver()) {
            System.out.println("\nDay " + day + " starts:");

            // moving, eating, breeding tasks
            processAnimalTasks();

            // Removing dead objects and planting new plants
            processIslandTasks();

            logStatistics(day);

            // Increment the day
            day++;
        }

        // ending executors
        shutdownExecutors();
    }

    private void processAnimalTasks() {
        // Create a CountDownLatch for all animals on the island
        CountDownLatch latch = new CountDownLatch((int) island.getAllCells().stream()
                .flatMap(cell -> cell.getObjectsOfType(Animal.class).stream())
                .count());

        // For each animal on the island we create a task
        for (Cell cell : island.getAllCells()) {
            for (Animal animal : cell.getObjectsOfType(Animal.class)) {
                animalExecutor.submit(() -> {
                    try {
                        // moving animal (if it was not done before)
                        Cell targetCell = getRandomNeighbor(cell, animal);  // getting random neighbour cell
                        animal.move(cell, targetCell);  // moving the animal

                        // search for food
                        IslandObject food = animal.findFood(cell);

                        // Pass the simulation object to the eat method
                        animal.eat(food, cell, this);  // "this" passes the current simulation

                        // Проверка, не умерло ли животное
                        animal.checkIfDead(cell, this);  // Pass the simulation object for statistics

                        // Размножение
                        animal.reproduce(cell, island);

                    } finally {
                        latch.countDown();  // Decrement the latch counter when the task is completed
                    }
                });
            }
        }

        try {
            latch.await();  // Waiting for all tasks to be finished
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Animal tasks interrupted", e);
        }
    }

    private void processIslandTasks() {
        // Deleting dead animals
        island.processDeaths();
        resetMovementFlags();

        // planting new plants with the probability of 10% on each cell
        for (Cell cell : island.getAllCells()) {
            if (Math.random() < 0.05) {
                cell.addObject(new Plant());
            }
        }
    }

    public void resetMovementFlags() {
        for (Cell cell : island.getAllCells()) {
            for (Animal animal : cell.getObjectsOfType(Animal.class)) {
                animal.setHasMoved(false);
            }
        }
    }

    private boolean isSimulationOver() {
        return island.getAllCells().stream()
                .flatMap(cell -> cell.getObjectsOfType(Animal.class).stream())
                .count() == 0; // simulation finished, all animals are dead
    }

    private Cell getRandomNeighbor(Cell currentCell, Animal animal) {
        List<Cell> neighbors = island.getNeighboringCells(currentCell.getX(), currentCell.getY(), animal.getTravelSpeed());
        neighbors.removeIf(neighbor -> !neighbor.canAddObject(animal));
        if (!neighbors.isEmpty()) {
            return neighbors.get(ThreadLocalRandom.current().nextInt(neighbors.size()));
        }
        return null; // if there are no suitable cells
    }

    private void logStatistics(int day) {
        Map<Class<?>, Integer> animalCounts = new HashMap<>();
        int totalPlants = 0;

        // Forming a title for statistics
        StringBuilder statisticsLog = new StringBuilder();
        statisticsLog.append("\n=== Day No. " + day + " on the island ===\n");

        // Collecting statistics on animals and plants
        for (Cell cell : island.getAllCells()) {
            for (Animal animal : cell.getObjectsOfType(Animal.class)) {
                animalCounts.put(animal.getClass(), animalCounts.getOrDefault(animal.getClass(), 0) + 1);
            }
            totalPlants += cell.countObjectsOfType(Plant.class);
        }

        int totalAnimals = animalCounts.values().stream().mapToInt(Integer::intValue).sum();

        // Adding statistics on animals
        statisticsLog.append("Animals alive: " + totalAnimals + "\n");
        animalCounts.forEach((animalClass, count) -> {
            try {
                Animal tempAnimal = (Animal) animalClass.getDeclaredConstructor().newInstance();
                statisticsLog.append("  " + tempAnimal.getUnicodeSymbol() + " " + animalClass.getSimpleName() + ": " + count + "\n");
            } catch (Exception e) {
                statisticsLog.append("  " + animalClass.getSimpleName() + ": " + count + "\n");
            }
        });

        // Adding statistics on dead animals and plants
        statisticsLog.append("Animals dead (hunger): " + deadAnimalsCount + "\n");
        statisticsLog.append("Animals eaten: " + eatenAnimalsCount + "\n");
        statisticsLog.append("Plants: " + totalPlants + "\n");
        statisticsLog.append("Total animal deaths: " + (deadAnimalsCount + eatenAnimalsCount) + "\n");
        statisticsLog.append("====================\n");

        // Console output
        System.out.println(statisticsLog.toString());
    }

    public void incrementDeadAnimals(Animal animal, boolean isEaten) {
        // Calculation logic, but without console output
        if (isEaten) {
            eatenAnimalsCount++;
        } else {
            deadAnimalsCount++;
        }
    }

    private void shutdownExecutors() {
        try {
            animalExecutor.shutdown();
            animalExecutor.awaitTermination(10, TimeUnit.SECONDS);

            islandExecutor.shutdown();
            islandExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Executors shutdown interrupted", e);
        }
    }

    private int randomX() {
        return ThreadLocalRandom.current().nextInt(island.getWidth());
    }

    private int randomY() {
        return ThreadLocalRandom.current().nextInt(island.getHeight());
    }
}
