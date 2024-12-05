package Models.Abstraction;

import Models.Island.*;
import Models.Utils.BreedingConfig;
import Models.Utils.MovementConfig;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public abstract class Animal extends IslandObject {
    private final int travelSpeed;
    private final double maxSaturation;
    private double currentSaturation;
    private final double hungerRate;
    private boolean hasMoved; // Флаг для отслеживания, переместилось ли животное
    private final String id; // Уникальный идентификатор животного
    private final String unicodeSymbol;
    private BiConsumer<Animal, Boolean> onDeathCallback; // Обратный вызов для смерти животного

    public Animal(double weight, int maxCountOnLocation, int travelSpeed, double maxSaturation, double hungerRate, String unicodeSymbol) {
        super(weight, maxCountOnLocation);
        this.travelSpeed = travelSpeed;
        this.maxSaturation = maxSaturation;
        this.currentSaturation = maxSaturation; // Начальный уровень насыщения = максимальному
        this.hungerRate = hungerRate;
        this.hasMoved = false;
        this.unicodeSymbol = unicodeSymbol;
        this.id = UUID.randomUUID().toString();
    }

    // Геттеры
    public int getTravelSpeed() {
        return travelSpeed;
    }

    public double getMaxSaturation() {
        return maxSaturation;
    }

    public double getCurrentSaturation() {
        return currentSaturation;
    }

    public double getHungerRate() {
        return hungerRate;
    }

    public String getId() {
        return id;
    }

    public String getUnicodeSymbol() {
        return unicodeSymbol;
    }

    public BiConsumer<Animal, Boolean> getOnDeathCallback() {
        return onDeathCallback;
    }

    // Сеттер для обратного вызова
    public void setOnDeathCallback(BiConsumer<Animal, Boolean> callback) {
        this.onDeathCallback = callback;
    }

    // Логика для уведомления о смерти
    protected void notifyDeath(boolean isEaten) {
        if (onDeathCallback != null) {
            onDeathCallback.accept(this, isEaten);
        }
    }

    // Проверка, умерло ли животное
    public boolean isDead() {
        return currentSaturation <= 0;
    }

    // Обработка смерти
    public void checkIfDead(Cell currentCell) {
        if (isDead()) {
            currentCell.removeObject(this); // Удаляем животное из клетки
            notifyDeath(false); // Уведомляем о смерти от голода
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") has died of hunger.");
        }
    }

    // Уменьшение насыщения
    public void decreaseSaturation(double amount) {
        currentSaturation -= amount;
        if (currentSaturation < 0) {
            currentSaturation = 0;
        }
    }

    // Увеличение насыщения
    public void increaseSaturation(double amount) {
        currentSaturation += amount;
        if (currentSaturation > maxSaturation) {
            currentSaturation = maxSaturation;
        }
    }

    // Перемещение животного
    public void move(Cell currentCell, Cell targetCell) {
        if (hasMoved) return;

        if (targetCell == null) {
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") cannot move: no target cell available.");
            return;
        }

        int movementChance = MovementConfig.getMovementChance(this.getClass());
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (roll < movementChance) {
            if (targetCell.canAddObject(this)) {
                currentCell.removeObject(this);
                targetCell.addObject(this);
                hasMoved = true;
                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") moved from (" +
                        currentCell.getX() + ", " + currentCell.getY() + ") to (" +
                        targetCell.getX() + ", " + targetCell.getY() + ").");
            } else {
                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") cannot move to target cell: cell is full.");
            }
        } else {
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") decided not to move.");
        }
    }

    // Сбрасываем флаг перемещения
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public void reproduce(Cell currentCell, Island island) {
        long sameSpeciesCount = currentCell.getObjectsOfType(this.getClass()).size();

        if (sameSpeciesCount < 2) return; // Убедимся, что есть как минимум 2 животного одного типа на клетке

        int breedingChance = BreedingConfig.getBreedingChance(this.getClass());
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll >= breedingChance) return;

        int offspringCount = BreedingConfig.getRandomOffspringCount(this.getClass());

        System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") attempts to reproduce and give birth to " + offspringCount + " offspring.");

        for (int i = 0; i < offspringCount; i++) {
            try {
                Animal offspring = this.getClass().getDeclaredConstructor().newInstance();

                // Пытаемся добавить потомка через метод Island
                boolean added = island.addObjectToCell(offspring, currentCell.getX(), currentCell.getY());
                if (added) {
                    // Устанавливаем обратный вызов для потомка
                    offspring.setOnDeathCallback(this.getOnDeathCallback());
                } else {
                    break; // Прекращаем добавление, если невозможно добавить
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    // Форматирование насыщения
    protected String formatSaturation(double initialSaturation, double currentSaturation) {
        String formattedInitial = initialSaturation < 0.05 ? "0" : String.format("%.2f", initialSaturation);
        String formattedCurrent = currentSaturation >= getMaxSaturation() ? "maxSaturation" :
                (currentSaturation < 0.05 ? "0" : String.format("%.2f", currentSaturation));

        return formattedInitial + " -> " + formattedCurrent;
    }

    // Метод для реализации еды
    public abstract void eat(IslandObject food, Cell currentCell);
}
