package Simulation;

public class App {
    public static void main(String[] args) {
        // Create and run a simulation
        Simulation simulation = new Simulation(100, 100);
        simulation.initialize();
        simulation.start();
    }
}
