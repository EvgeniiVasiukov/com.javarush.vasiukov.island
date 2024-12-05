package Models.Island;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Herbivore.*;
import Models.Plant;
import Models.Predator.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Island {
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final Map<Class<? extends IslandObject>, Integer> objectCountMap; // Карта для отслеживания объектов

    public Island(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        this.objectCountMap = new HashMap<>(); // Инициализация карты
        initializeCells();
    }

    private void initializeCells() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }
    }

    public Cell getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return cells[x][y];
        }
        throw new IndexOutOfBoundsException("Coordinates out of island bounds.");
    }

    public synchronized boolean addObjectToCell(IslandObject object, int x, int y) {
        // Проверяем, не превышен ли лимит на количество объектов данного типа на острове
        if (object instanceof Animal) {
            int totalAnimalCount = getObjectCount(object.getClass());
            if (totalAnimalCount >= object.getMaxCountOnLocation()) {
                // Не добавляем животное, если превышен лимит на острове
                System.out.println(object.getClass().getSimpleName() + " cannot be added: max animal count reached on the island.");
                return false;
            }
        }

        // Проверяем, можно ли добавить объект в клетку
        if (getCell(x, y).addObject(object)) {
            incrementObjectCount(object.getClass()); // Увеличиваем количество объектов данного типа на острове
            return true;
        }
        return false;
    }

    // Увеличение количества объектов данного типа на острове
    private void incrementObjectCount(Class<? extends IslandObject> type) {
        objectCountMap.put(type, getObjectCount(type) + 1);
    }

    // Уменьшение количества объектов данного типа на острове
    private void decrementObjectCount(Class<? extends IslandObject> type) {
        int count = getObjectCount(type);
        if (count > 0) {
            objectCountMap.put(type, count - 1);
        }
    }

    private int getObjectCount(Class<? extends IslandObject> clazz) {
        int count = 0;
        for (Cell cell : getAllCells()) {
            count += cell.countObjectsOfType(clazz);  // Суммируем количество объектов на каждой клетке
        }
        return count;
    }

    public void processMovementForAllAnimals() {
        for (Cell cell : getAllCells()) {
            List<IslandObject> objects = new ArrayList<>(cell.getObjects()); // Копируем список объектов
            for (IslandObject obj : objects) {
                if (obj instanceof Animal) {
                    Animal animal = (Animal) obj;

                    // Если животное ещё не перемещалось, перемещаем его
                    if (!animal.hasMoved()) {
                        moveAnimal(animal, cell);
                        animal.setHasMoved(true); // Помечаем, что животное переместилось
                    }
                }
            }
        }

        // Сброс флагов после завершения шага
        for (Cell cell : getAllCells()) {
            List<IslandObject> objects = cell.getObjects();
            for (IslandObject obj : objects) {
                if (obj instanceof Animal) {
                    Animal animal = (Animal) obj;
                    animal.setHasMoved(false); // Сбрасываем флаг для следующего шага
                }
            }
        }
    }
    public void processEating() {
        for (Cell cell : getAllCells()) {
            for (Animal animal : cell.getObjectsOfType(Animal.class)) {
                IslandObject food = findFood(cell, animal); // Найти подходящую еду
                animal.eat(food, cell); // Животное ест еду или теряет насыщение
                animal.checkIfDead(cell);
            }
        }
    }


    // Метод для поиска еды в клетке
    public IslandObject findFood(Cell cell, Animal animal) {
        if (animal instanceof Herbivore) {
            var plants = cell.getObjectsOfType(Plant.class);
            return plants.isEmpty() ? null : plants.get(0); // Травоядные едят растения
        } else if (animal instanceof Predator) {
            var herbivores = cell.getObjectsOfType(Herbivore.class);
            return herbivores.isEmpty() ? null : herbivores.get(0); // Хищники едят травоядных
        } else if (animal instanceof Duck) {
            var caterpillars = cell.getObjectsOfType(Caterpillar.class);
            if (!caterpillars.isEmpty()) {
                return caterpillars.get(0); // Утки сначала едят гусениц
            }
            var plants = cell.getObjectsOfType(Plant.class);
            return plants.isEmpty() ? null : plants.get(0); // Если гусениц нет, едят растения
        }
        return null; // Если еда не найдена
    }




    private void moveAnimal(Animal animal, Cell currentCell) {
        // Получаем скорость животного
        int travelSpeed = animal.getTravelSpeed();

        // Получаем доступные клетки для перемещения
        List<Cell> neighbors = getNeighboringCells(currentCell.getX(), currentCell.getY(), travelSpeed);

        if (neighbors.isEmpty()) {
            System.out.println(animal.getUnicodeSymbol() + " (ID: " + animal.getId() + ") has no available cells to move.");
            return;
        }

        // Выбираем случайную клетку
        Cell targetCell = neighbors.get(ThreadLocalRandom.current().nextInt(neighbors.size()));

        // Проверяем, может ли животное переместиться в целевую клетку
        if (targetCell.canAddObject(animal)) {
            currentCell.removeObject(animal);  // Убираем животное с текущей клетки
            targetCell.addObject(animal);      // Добавляем животное в новую клетку

            // Обновляем позицию животного
            animal.setPosition(targetCell.getX(), targetCell.getY());

            System.out.println(animal.getUnicodeSymbol() + " (ID: " + animal.getId() + ") moved from (" +
                    currentCell.getX() + ", " + currentCell.getY() + ") to (" +
                    targetCell.getX() + ", " + targetCell.getY() + ").");
        } else {
            System.out.println(animal.getUnicodeSymbol() + " (ID: " + animal.getId() + ") cannot move to target cell: cell is full.");
        }
    }

    public List<Cell> getNeighboringCells(int x, int y, int travelSpeed) {
        List<Cell> neighbors = new ArrayList<>();
        for (int dx = -travelSpeed; dx <= travelSpeed; dx++) {
            for (int dy = -travelSpeed; dy <= travelSpeed; dy++) {
                if (dx == 0 && dy == 0) continue; // Пропускаем текущую клетку
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
        for (Cell cell : getAllCells()) {
            cell.removeDeadObjects();
        }
    }
}
