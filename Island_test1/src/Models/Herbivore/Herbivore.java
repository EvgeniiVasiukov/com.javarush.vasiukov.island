package Models.Herbivore;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Island.Cell;
import Models.Island.Island;
import Models.Plant;

import java.util.function.Consumer;

public abstract class Herbivore extends Animal {

    protected Herbivore(double weight, int maxCountOnLocation, int travelSpeed, double maxSaturation, double hungerRate, String unicodeSymbol) {
        super(weight, maxCountOnLocation, travelSpeed, maxSaturation, hungerRate, unicodeSymbol);
    }
    private Consumer<Animal> onDeathCallback; // Обработчик события смерти

    public void setOnDeathCallback(Consumer<Animal> onDeathCallback) {
        this.onDeathCallback = onDeathCallback;
    }

    // Логика питания для травоядного
    @Override
    public void eat(IslandObject food, Cell currentCell) {
        double initialSaturation = this.getCurrentSaturation(); // Уровень насыщения до еды

        if (food instanceof Plant) {
            this.increaseSaturation(food.getWeight());
            currentCell.removeObject(food); // Удаляем съеденное растение
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") eats a plant and gains " +
                    String.format("%.1f", food.getWeight()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        } else {
            this.decreaseSaturation(this.getHungerRate());
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") could not find food and lost " +
                    String.format("%.1f", this.getHungerRate()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        }

        // Проверяем, умерло ли травоядное
        if (this.isDead()) {
            if (onDeathCallback != null) {
                onDeathCallback.accept(this); // Сообщаем о смерти
            }
            currentCell.removeObject(this); // Удаляем животное из клетки
            //System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") has died of hunger.");
        }
    }


    // Логика размножения для травоядного (использует общую логику из класса Animal)
    @Override
    public void reproduce(Cell currentCell, Island island) {
        super.reproduce(currentCell, island); // Используем общую логику из Animal
    }
}
