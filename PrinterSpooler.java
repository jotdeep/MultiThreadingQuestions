import java.sql.Time;
import java.util.concurrent.*;


    /*1. Bounded Deque with LinkedBlockingDeque
Task: Simulate a printer spooler where “high-priority” jobs go to the front, “normal” jobs go to the back.

Requirements:

Use a LinkedBlockingDeque<Job> with a fixed capacity.

Producers submit jobs at either end (addFirst vs addLast), blocking when full (with timeout).

A pool of worker threads takes from the front, processes, then logs completion.

Challenges:

Handling full-deque timeouts.

Fairness between high/normal priority producers.

Clean shutdown of workers when no more jobs will arrive.*/

public class PrinterSpooler {
    static class Job {
        final String name;
        Job(String name) { this.name = name; }
        void process() {
            System.out.println(Thread.currentThread().getName() + " processing " + name);
        }
    }

    private final BlockingDeque<Job> deque;
    private final ExecutorService workers;

    public PrinterSpooler(int capacity, int workerCount) {
        this.deque    = new LinkedBlockingDeque<>(capacity);
        this.workers  = Executors.newFixedThreadPool(workerCount);
        startWorkers(workerCount);
    }

    private void startWorkers(int n) {
        for (int i = 0; i < n; i++) {
            workers.submit(() -> {
                try {
                    while (true) {
                        Job job = deque.takeFirst();     // blocks if empty
                        if (job.name.equals("POISON")) break;
                        job.process();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    public boolean submitHighPriority(Job job, long timeout, TimeUnit unit)
            throws InterruptedException {
        return deque.offerFirst(job, timeout, unit);
    }

    public boolean submitNormal(Job job, long timeout, TimeUnit unit)
            throws InterruptedException {
        return deque.offerLast(job, timeout, unit);
    }

    /** Call when no more real jobs will arrive. */
    public void shutdown() throws InterruptedException {
        // Send one poison pill per worker
        for (int i = 0; i < ((ThreadPoolExecutor)workers).getCorePoolSize(); i++) {
            deque.putFirst(new Job("POISON"));
        }
        workers.shutdown();
        workers.awaitTermination(1, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws InterruptedException {
        PrinterSpooler spooler = new PrinterSpooler(50, 3);

        spooler.submitHighPriority(new Job("Urgent-1"), 2, TimeUnit.SECONDS);
        spooler.submitNormal     (new Job("Normal-1"),  2, TimeUnit.SECONDS);
        spooler.shutdown();
        System.out.println("All workers terminated.");
    }
}