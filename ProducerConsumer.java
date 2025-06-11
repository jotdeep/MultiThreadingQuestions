    import java.util.concurrent.locks.Lock;
    import java.util.concurrent.locks.ReentrantLock;

    public class ProducerConsumer<T> {
        // Constructs the queue with the given capacity
        int index=-1;
        T arr[];
        int capacity;


        public ProducerConsumer(int capacity) {
            arr= (T[])new Object[capacity];
            this.capacity=capacity;

        }

        // Adds item to the tail; blocks if the queue is full
        public synchronized void put(T item) throws InterruptedException {
            while (index==capacity-1)
            {
                wait();
            }
            index++;
            arr[index]=item;
            System.out.println("Item put in "+item);
            notifyAll();
        }

        // Removes and returns the head; blocks if the queue is empty
        public synchronized T take() throws InterruptedException {
            while (index==-1)
            {
                wait();
            }
            T item=arr[index];
            System.out.println("Item took out "+item);
            index--;
            notifyAll();
            return item;
        }

        public static void main(String[] args)
        {
            ProducerConsumer<Integer> blockingQueue=new ProducerConsumer<>(10);
            Thread t1=new Thread(()->
            {
                for(int i=0;i<100;i++)
                {
                    try {
                        blockingQueue.put(i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            Thread t2=new Thread(()->
            {
                for(int i=0;i<100;i++)
                {
                    try {
                        blockingQueue.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            t1.start();
            t2.start();
        }
    }
