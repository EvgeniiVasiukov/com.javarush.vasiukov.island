package Models.Predator;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Herbivore.Herbivore;
import Models.Island.Cell;
import Models.Island.Island;
import Models.Utils.EatingChancesSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public abstract class Predator extends Animal {

    protected Predator(double weight, int maxCountOnLocation, int travelSpeed, double maxSaturation, double hungerRate, String unicodeSymbol) {
        super(weight, maxCountOnLocation, travelSpeed, maxSaturation, hungerRate, unicodeSymbol);
    }
    private Consumer<Animal> onDeathCallback; // Обработчик события смерти

    public void setOnDeathCallback(Consumer<Animal> onDeathCallback) {
        this.onDeathCallback = onDeathCallback;
    }

    // Логика питания хищника
    @Override
    public void eat(IslandObject food, Cell currentCell) {
        double initialSaturation = this.getCurrentSaturation();

        if (food instanceof Herbivore) {
            int chance = EatingChancesSource.getEatingChance(this.getClass(), food.getClass());
            int roll = ThreadLocalRandom.current().nextInt(100);

            if (roll < chance) {
                this.increaseSaturation(food.getWeight());

                // Проверяем, является ли съеденное животным, и вызываем обратный вызов
                if (food instanceof Animal) {
                    Animal eatenAnimal = (Animal) food;
                    if (eatenAnimal.getOnDeathCallback() != null) {
                        eatenAnimal.getOnDeathCallback().accept(eatenAnimal, true); // Отмечаем, что животное было съедено
                    }
                    currentCell.removeObject(food);
                }

                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") eats " + food.getClass().getSimpleName() +
                        " and gains " + String.format("%.1f", food.getWeight()) + " saturation (" +
                        formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
            } else {
                this.decreaseSaturation(this.getHungerRate());
                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") tried to eat " + food.getClass().getSimpleName() +
                        " but failed (" + formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
            }
        } else {
            this.decreaseSaturation(this.getHungerRate());
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") could not find food and lost " +
                    String.format("%.1f", this.getHungerRate()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        }

        // Проверка на голодную смерть
        if (this.isDead()) {
            if (onDeathCallback != null) {
                onDeathCallback.accept(this);
            }
            currentCell.removeObject(this);
            //System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") has died of hunger.");
        }
    }










    // Логика размножения для хищника (использует общую логику из класса Animal)
    @Override
    public void reproduce(Cell currentCell, Island island) {
        super.reproduce(currentCell, island); // Используем общую логику из Animal
    }

    // Логика движения хищника (использует логику движения из Animal)
    @Override
    public void move(Cell currentCell, Cell targetCell) {
        super.move(currentCell, targetCell); // Используем общую логику движения из Animal
    }
}
