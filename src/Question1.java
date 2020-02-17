import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Question1 {
    private static int size;
    private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(16);
    private static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(16);
    private static boolean runDestroy;
    private static int count1, count2, count3, count4;
    private static Random r = new Random();
    private static boolean complete1, complete2, complete3, complete4;


    //---------------------------------------------------------------------------------------

    public static class SockMaker implements Runnable {
        private BlockingQueue<String> sockQueue;
        private int max;
        private String name;
        private int x;
        int time;

        public SockMaker(BlockingQueue<String> sockQueue, int max, String name, int x) {
            this.sockQueue = sockQueue;
            this.max = max;
            this.name = name;
            this.x = x;
            time = r.nextInt(999);
        }
        @Override
        public void run() {
            try {
                generateSock(name, max);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void generateSock(String name, int max) throws InterruptedException {
            for (int i = 0; i < max; i++) {
                System.out.printf("%s sock created %d out of %d\n", name, (i + 1), max);
                sockQueue.put(name);
                Thread.sleep(time);

            }

        }
    }

    //---------------------------------------------------------------------------------------
    public static class Match implements Runnable {
        private BlockingQueue<String> sockQueue;
        private BlockingQueue<String> destroyQueue;
        private int max;
        private String name;
        int time;

        public Match(BlockingQueue<String> sockQueue, int max,
                     String name, BlockingQueue<String> destroyQueue) {
            this.sockQueue = sockQueue;
            this.destroyQueue = destroyQueue;
            this.max = max;
            this.name = name;
            time = r.nextInt(999);
        }

        @Override
        public void run() {
            try {
                MatchSocks(name, max, count1, count2, count3, count4);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void MatchSocks(String name, int max,
                                int blue, int green, int orange, int yellow) throws InterruptedException {
            boolean found = false;
            String check = " ";
            int start = 0;
            int odd1 = 0, odd2 = 0, odd3 = 0, odd4 = 0, leftOvers = 0;
            while (start != size) {

                check = sockQueue.take();
                // these if statements follow same style of implementation
                if (blue % 2 != 0 && odd1 == 0 && check == "blue") {
                    odd1++;
                    start++;
                    check = sockQueue.take();
                }
                if (green % 2 != 0 && odd2 == 0 && check == "green") {
                    odd2++;
                    start++;
                    check = sockQueue.take();

                }
                if (orange % 2 != 0 && odd3 == 0 && check == "orange") {
                    odd3++;
                    start++;
                    check = sockQueue.take();

                }
                if (yellow % 2 != 0 && odd4 == 0 && check == "yellow") {
                    odd4++;
                    start++;
                    check = sockQueue.take();

                }
                    leftOvers =odd1 + odd2 + odd3 + odd4;
                for (int i = 0; i < sockQueue.size(); i++) {
                    if (check == sockQueue.toArray()[i]) {
                        Destroy(start, i, check);
                        destroyQueue.add(check);
                        int count  = sockQueue.size() +  leftOvers;
                        System.out.println("\n" + name + " Thread found a pair of  " + check + " f" +
                                "rom overall sock count " +
                                size + ". Items left from current stock " + count);
                        start = start + 2;
                        found = true;
                        break;
                    }
                }
                if (found == true) {
                    found = false;
                } else {
                    sockQueue.add(check);
                }
                Thread.sleep(time);
            }
            if (leftOvers != 0) {
                System.out.println("Approximately " + leftOvers + " did not have a pair");
            }
            runDestroy = false;

        }

        private void Destroy(int i, int j, String Color) {
            sockQueue.remove(sockQueue.toArray()[j]);
            // System.out.println("\n  Destroyed " + i + "\n");
        }

    }

    //---------------------------------------------------------------------------------------


    public static class WasherDestroySock implements Runnable {
        private BlockingQueue<String> destroyQueue;
        private String name;

        public WasherDestroySock(String name, BlockingQueue<String> destroyQueue) {
            this.name = name;
            this.destroyQueue = destroyQueue;
        }

        @Override
        public void run() {
            try {
                Delete();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void Delete() throws InterruptedException {
            while (runDestroy || !destroyQueue.isEmpty()) {
                String remove = destroyQueue.take();
                System.out.println(name +" Thread washed a pair of " + remove + " socks and destroyed them ");
                Thread.sleep(999);
            }
            System.out.println("Done Washing and Destroying socks");
        }
    }

    // ---------------------------------------------------------------------------------------

    public static void main(String[] args) {
        count1 = r.nextInt(4);
        count2 = r.nextInt(4);
        count3 = r.nextInt(4);
        count4 = r.nextInt(4);
        size = count1 + count2 + count3 + count4;
        runDestroy = true;

        try {

            new Thread(new SockMaker(queue, count1, "blue", 1)).start();
            new Thread(new SockMaker(queue, count2, "green", 2)).start();
            new Thread(new SockMaker(queue, count3, "orange", 3)).start();
            new Thread(new SockMaker(queue, count4, "yellow", 4)).start();


            new Thread(new Match(queue, size, "Match", queue2)).start();
            new Thread(new WasherDestroySock("Washer", queue2)).start();


        } catch (Exception X) {

        }


    }
}
