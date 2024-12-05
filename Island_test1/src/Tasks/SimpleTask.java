package Tasks;
/*
* Just for testing purposes
* No real business logic!!!
*/
public class SimpleTask implements Runnable {
    private final int taskId;

    public SimpleTask(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        System.out.println("Task " + taskId + " is running on thread " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000); // Симуляция работы задачи
        } catch (InterruptedException e) {
            System.err.println("Task " + taskId + " was interrupted.");
        }
        System.out.println("Task " + taskId + " completed.");
    }
}
