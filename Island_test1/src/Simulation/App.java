package Simulation;

public class App {
    public static void main(String[] args) {
        // Задаем параметры симуляции
        int islandWidth = 5;
        int islandHeight = 5;
        int steps = 20;

        // Создаем и запускаем симуляцию
        Simulation simulation = new Simulation(islandWidth, islandHeight, steps);
        simulation.initialize();
        simulation.start();
    }
}