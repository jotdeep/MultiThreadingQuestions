    public class MyCyclicBarrier {
        // Create a barrier for 'parties' threads
        int count=0;
        int parties;
        public MyCyclicBarrier(int parties) {
            this.parties=parties;
        }


        public synchronized int await() throws InterruptedException {
            int arrivalIndex = parties - (count);

            count++;
            if(count<parties) {
                while (count < parties) {
                    System.out.println("waiting for other to complete "+ arrivalIndex);

                    wait();
                }
                return  arrivalIndex;
            }
            else {
                System.out.println("Doing some work " + Thread.currentThread().getName());
                Thread.sleep(500);
                System.out.println("Thread  " + Thread.currentThread().getName() + " has completed");
                notifyAll();
                return 0;
            }
        }

        public static void main(String[] args) {

            MyCyclicBarrier myCyclicBarrier = new MyCyclicBarrier(10);

            for (int j = 0; j < 10; j++) {
                Thread thread = new Thread(() ->
                {

                    try {
                       int index= myCyclicBarrier.await();
                        System.out.println("Doing some work " + Thread.currentThread().getName());

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                });

                thread.start();
            }
        }
    }
