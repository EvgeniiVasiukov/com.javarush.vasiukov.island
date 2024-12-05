package Models.Abstraction;

import Models.Island.Cell;
import Models.Island.Island;
import Models.Utils.BreedingConfig;
import Simulation.Simulation;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public abstract class Animal extends IslandObject {
    private final int travelSpeed;
    private final double maxSaturation;
    private double currentSaturation;
    private final double hungerRate;
    private boolean hasMoved;
    private final String id;
    private final String unicodeSymbol;
    private BiConsumer<Animal, Boolean> onDeathCallback;
    private boolean dead = false;  // Indication flag that animal is dead

    public Animal(double weight, int maxCountOnLocation, int travelSpeed, double maxSaturation, double hungerRate, String unicodeSymbol) {
        super(weight, maxCountOnLocation);
        this.travelSpeed = travelSpeed;
        this.maxSaturation = maxSaturation;
        this.currentSaturation = maxSaturation;
        this.hungerRate = hungerRate;
        this.hasMoved = false;
        this.unicodeSymbol = unicodeSymbol;
        this.id = UUID.randomUUID().toString();
    }

    public int getTravelSpeed() {
        return travelSpeed;
    }

    public String getId() {
        return id;
    }

    public String getUnicodeSymbol() {
        return unicodeSymbol;
    }
    public double getHungerRate() {
        return hungerRate;
    }
    public double getMaxSaturation() {
        return maxSaturation;
    }
    public double getCurrentSaturation() {
        return currentSaturation;
    }

    public boolean hasMoved() {
        return hasMoved;
    }
    public BiConsumer<Animal, Boolean> getOnDeathCallback() {
        return onDeathCallback;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public void setOnDeathCallback(BiConsumer<Animal, Boolean> callback) {
        this.onDeathCallback = callback;
    }

    protected void notifyDeath(boolean isEaten) {
        if (onDeathCallback != null) {
            onDeathCallback.accept(this, isEaten);
        }
    }

    public boolean isDead() {
        return currentSaturation <= 0;
    }

    public void decreaseSaturation(double amount) {
        currentSaturation = Math.max(0, currentSaturation - amount);
    }

    public void increaseSaturation(double amount) {
        currentSaturation = Math.min(maxSaturation, currentSaturation + amount);
    }

    public void checkIfDead(Cell currentCell, Simulation simulation) {
        if (isDead() && !dead) {  // Check that animal is not indicated as dead
            currentCell.removeObject(this);  // Removing animal from the cell
            simulation.incrementDeadAnimals(this, false);  // Adding to statistics

            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") has died of hunger.");
            this.dead = true;  // Setting dead flag to avoid second death
        }
    }







    public void move(Cell currentCell, Cell targetCell) {
        if (!hasMoved && targetCell != null && targetCell.canAddObject(this)) {
             //Logging for movement information
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") moved from (" +
                   currentCell.getX() + ", " + currentCell.getY() + ") to (" +
                    targetCell.getX() + ", " + targetCell.getY() + ").");

            currentCell.removeObject(this);  // Removing the animal from the current cell
            targetCell.addObject(this);      // Adding animal to the new cell
            setHasMoved(true);                // Noticing that animal has moved
        }
    }



    @Override
    public void reproduce(Cell currentCell, Island island) {
        // Counting animals of the same species on the cell == condition for reproduction
        long sameSpeciesCount = currentCell.getObjectsOfType(this.getClass()).size();

        // Check that there are minimum 2 animals of the same kind
        if (sameSpeciesCount < 2) return;

        // Getting breeding chance for this species of animal
        int breedingChance = BreedingConfig.getBreedingChance(this.getClass());
        int roll = ThreadLocalRandom.current().nextInt(100);  // Random number generation (0 - 99)

        // If the chance to reproduce has not passed, do nothing
        if (roll >= breedingChance) return;

        // Get a random number of offspring for this animal speciesо
        int offspringCount = BreedingConfig.getRandomOffspringCount(this.getClass());
        System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") attempts to reproduce and give birth to " + offspringCount + " offspring.");


        // Create descendants and add them to the cell
        for (int i = 0; i < offspringCount; i++) {
            try {
                Animal offspring = this.getClass().getDeclaredConstructor().newInstance();  // Создаем потомка
                boolean added = island.addObjectToCell(offspring, currentCell.getX(), currentCell.getY());  // Добавляем на клетку

                if (added) {
                    // Set a death callback for the descendant
                    offspring.setOnDeathCallback(this.getOnDeathCallback());
                } else {
                    break;  // If the cell is full, stop creating offsprings
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected String formatSaturation(double initialSaturation, double currentSaturation) {
        String formattedInitial = initialSaturation < 0.05 ? "0" : String.format("%.2f", initialSaturation);
        String formattedCurrent = currentSaturation >= getMaxSaturation() ? "maxSaturation" :
                (currentSaturation < 0.05 ? "0" : String.format("%.2f", currentSaturation));

        return formattedInitial + " -> " + formattedCurrent;
    }
    public IslandObject findFood(Cell currentCell) {
        if (this instanceof Models.Herbivore.Herbivore) {
            // Herbivores eat plants
            return currentCell.getObjectsOfType(Models.Plant.class).stream().findFirst().orElse(null);
        } else if (this instanceof Models.Predator.Predator) {
            // Predators eat Herbivores
            return currentCell.getObjectsOfType(Models.Herbivore.Herbivore.class).stream().findFirst().orElse(null);
        } else if (this instanceof Models.Herbivore.Duck) {
            // Ducks eat Caterpillars if there are no caterpillars than plants
            IslandObject food = currentCell.getObjectsOfType(Models.Herbivore.Caterpillar.class).stream().findFirst().orElse(null);
            if (food == null) {
                food = currentCell.getObjectsOfType(Models.Plant.class).stream().findFirst().orElse(null);
            }
            return food;
        }
        return null; // if food is not found
    }



    public abstract void eat(IslandObject food, Cell currentCell, Simulation simulation);


}
