package Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadingConfig {
    private final int threadCount;
    private final ExecutorService executorService;

    public ThreadingConfig(int threadCount) {
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    // Геттер для ExecutorService
    public ExecutorService getExecutorService() {
        return executorService;
    }

    // Метод для завершения работы пула потоков
    public void shutdown() {
        executorService.shutdown();
        System.out.println("Thread pool has been shut down.");
    }

    // Геттер для количества потоков (если понадобится)
    public int getThreadCount() {
        return threadCount;
    }

    // В классе ThreadingConfig
    public void shutdownAndAwaitTermination() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Принудительное завершение
                if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.err.println("Thread pool did not terminate.");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Thread pool has been shut down.");
    }

}
