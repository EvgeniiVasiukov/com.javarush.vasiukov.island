package Models.Herbivore;

import Models.Abstraction.Animal;
import Models.Abstraction.IslandObject;
import Models.Island.Cell;
import Models.Island.Island;
import Models.Plant;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Duck extends Herbivore {

    public Duck() {
        super(1.5, 50, 4, 6.0, 1.2, "\uD83E\uDD86"); // вес: 1.5, макс. на клетке: 50, скорость: 4, макс. сытость: 6.0
    }
    private Consumer<Animal> onDeathCallback; // Обработчик события смерти

    public void setOnDeathCallback(Consumer<Animal> onDeathCallback) {
        this.onDeathCallback = onDeathCallback;
    }

    @Override
    public void eat(IslandObject food, Cell currentCell) {
        double initialSaturation = this.getCurrentSaturation(); // Уровень насыщения до еды

        if (food instanceof Plant) {
            this.increaseSaturation(food.getWeight());
            currentCell.removeObject(food); // Удаляем съеденное растение
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") eats a plant and gains " +
                    String.format("%.1f", food.getWeight()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        } else if (food instanceof Caterpillar) {
            int roll = ThreadLocalRandom.current().nextInt(100);
            if (roll < 90) { // 90% шанс съесть гусеницу
                this.increaseSaturation(food.getWeight());
                currentCell.removeObject(food); // Удаляем съеденную гусеницу
                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") eats a caterpillar and gains " +
                        String.format("%.1f", food.getWeight()) + " saturation (" +
                        formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
            } else {
                System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") tried to eat the caterpillar but failed (" +
                        formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
            }
        } else {
            this.decreaseSaturation(this.getHungerRate());
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") could not find food and lost " +
                    String.format("%.1f", this.getHungerRate()) + " saturation (" +
                    formatSaturation(initialSaturation, this.getCurrentSaturation()) + ").");
        }

        // Проверяем, умерло ли животное
        if (this.isDead()) {
            if (onDeathCallback != null) {
                onDeathCallback.accept(this); // Сообщаем о смерти
            }
            currentCell.removeObject(this); // Удаляем животное из клетки
            System.out.println(this.getUnicodeSymbol() + " (ID: " + this.getId() + ") has died of hunger.");
        }
    }



    @Override
    public void reproduce(Cell currentCell, Island island) {
        super.reproduce(currentCell, island); // Логика размножения через базовый класс Animal
    }
}
