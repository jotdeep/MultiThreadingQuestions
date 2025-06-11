/*
2. Fair FIFO Queue via ArrayBlockingQueue
Task: Build a simulation of a taxi stand: riders queue up and taxis take one at a time.

Requirements:

Use an ArrayBlockingQueue<Rider>(N, true) (fair mode).

Rider threads call offer(rider, timeout)—if they time out, they leave angrily.

Taxi threads call poll(timeout)—if none arrive, they go offline.

Challenges:

Configuring capacity and timeouts to avoid starvation.

Measuring how fairness impacts rider wait times under high load.
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FairFifo {



    ArrayBlockingQueue<String>arrayBlockingQueue=new ArrayBlockingQueue<>(10);

    public void offer(String element) throws InterruptedException {
       boolean res= arrayBlockingQueue.offer(element,1, TimeUnit.MICROSECONDS);
       if(res==true)
       {
           System.out.println("Element "+element+" is successfully added");
       }
       else {
           System.out.println("Element "+element+"was not added" );
       }
    }

    public void remove() throws InterruptedException {
        String res=arrayBlockingQueue.poll(2,TimeUnit.SECONDS);
        if(res==null)
        {
            System.out.println("No customers so going for a quick nap!!!");
        }
        else {
            System.out.println("Your ride is completed now get out of my car");

        }
    }



    public static void main(String[] args) {

        FairFifo fairFifo=new FairFifo();
        Executor executor= Executors.newFixedThreadPool(100);
        for(int i= 0;i<100;i++)
        {
            executor.execute(new Thread(()->
            {
                try {
                    fairFifo.offer("elee");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        Executor executor1= Executors.newFixedThreadPool(100);
        for(int i= 0;i<100;i++)
        {
            executor1.execute(new Thread(()->
            {
                try {
                    fairFifo.remove();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }
}
