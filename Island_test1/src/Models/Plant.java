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
        int chance = ThreadLocalRandom.current().nextInt(100);  // Случайное число от 0 до 99
        if (chance < 10) { // 20% шанс размножения (можно варьировать)
            System.out.println("Plant is reproducing.");

            // Создание нового растения
            Plant newPlant = new Plant();

            // Добавляем новое растение на ту же клетку, где оно размножается
            if (currentCell.canAddObject(newPlant)) {
                currentCell.addObject(newPlant); // Добавляем новое растение на клетку
                System.out.println("New plant added to cell (" + currentCell.getX() + ", " + currentCell.getY() + ").");
            } else {
                System.out.println("Cannot add new plant: cell is full.");
            }
        }
    }
}
