 import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AlternatingPrinter {
    private final int N;
    private int odd = 1;
    private int even = 2;
    private boolean printOdd = true;

    private final Lock lock = new ReentrantLock();
    private final Condition oddCondition  = lock.newCondition();
    private final Condition evenCondition = lock.newCondition();

    public AlternatingPrinter(int N) {
        this.N = N;
    }

    public void printOdd() throws InterruptedException {
        lock.lock();
        try {
            while (odd <= N) {
                while (!printOdd) {
                    oddCondition.await();
                }
                System.out.println(odd);
                odd += 2;
                printOdd = false;
                // wake up the even thread
                evenCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void printEven() throws InterruptedException {
        lock.lock();
        try {
            while (even <= N) {
                while (printOdd) {
                    evenCondition.await();
                }
                System.out.println(even);
                even += 2;
                printOdd = true;
                // wake up the odd thread
                oddCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        int N = 10;
        AlternatingPrinter printer = new AlternatingPrinter(N);

        Thread oddThread = new Thread(() -> {
            try {
                printer.printOdd();
            } catch (InterruptedException ignored) { }
        });

        Thread evenThread = new Thread(() -> {
            try {
                printer.printEven();
            } catch (InterruptedException ignored) { }
        });

        oddThread.start();
        evenThread.start();
    }
}
