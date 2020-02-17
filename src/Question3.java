import javax.swing.*;
import javax.xml.parsers.SAXParser;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Question3 {
    private static int[] ranks;
    private static int offical;
    private static boolean pause;
    private static boolean terminate;
    public static BlockingQueue<Integer> queueRank;
    public static BlockingQueue<String> queueName;
    public static String RankLeader;
    public static int RankLeaderNum;
    public static Object lock = new Object();
    public static Object lock2 = new Object();
    public static int size;
    private static Random r = new Random();
    public static Thread[] column;
    public static Thread column2 = null;
    public  static int ThreadCount;


    public static class officicals implements Runnable {
        private int rank;
        private String name;
        private BlockingQueue<Integer> queue;
        private BlockingQueue<String> queueID;
        private Object lock3 = new Object();
        private Object lock;
        private String Leader;


        public officicals(Object lock, BlockingQueue<String> queueID, BlockingQueue<Integer> queue,
                          int rank, String name) {
            this.lock = lock;
            this.queueID = queueID;
            this.queue = queue;
            this.rank = rank;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                guess();
            } catch (InterruptedException e) {

                System.out.println("hello, apologies for the Interruption " + name + ". The New Leader "
                        + RankLeader + " with rank of " + RankLeaderNum);
                try {
                    Thread.sleep(12);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }



        private void Test() {
            synchronized (lock2) {
                lock2.notifyAll();
            }
        }

        private void guess() throws InterruptedException {
            synchronized (lock) {

                String start = " ";
                if (RankLeader == name) {
                    start = "Leader Thread Name: ";
                } else {
                    start = "Thread Name: ";
                }
                System.out.println("\n" + start + name + " current rank " + rank);

                queue.add(rank);
                queueID.add(name);
                ThreadCount++;
                int num = 0;
                try {
                    num = Math.abs(r.nextInt(0 + (queue.size() - 1)));
                } catch (IllegalArgumentException e) {

                }
                String original = RankLeader;
                String nameGuess = (String) queueID.toArray()[num];
                int numberGuess = (int) queue.toArray()[num];
                System.out.println(name + " believes the leader thread is: " + nameGuess
                        + " that has rank " + numberGuess);
                if (nameGuess == RankLeader) {
                    System.out.println("Guess is valid. The leader is " + original);

                }


                try {
                    Test();
                } catch (Exception e) {
                    System.out.println(name + " unable to notify");
                }

                lock.wait();


                if ( name != RankLeader) {
                    nameGuess = RankLeader;
                    numberGuess = RankLeaderNum;
                    System.out.println("\n" + name + " has found new Leader " + nameGuess
                            + " that has rank " + numberGuess);




                } else {
                    System.out.println(name + " is rank Leader");

                }
            }

        }
    }

    public static class rankMaster implements Runnable {
        private String name;
        private BlockingQueue<Integer> queueNum;
        private BlockingQueue<String> queueID;
        private Object lock;

        public rankMaster(String name, BlockingQueue<Integer>
                queueNum, BlockingQueue<String> queueID, Object lock) {
            this.name = name;
            this.queueNum = queueNum;
            this.queueID = queueID;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                verify();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }

        private void Test() {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        private void verify() throws InterruptedException {
            synchronized (lock2) {
                int maxSet = size;

                boolean state = true;
                int[] values = new int[size];
                String[] ID = new String[size];
                while (state) {
                    if (!queueID.isEmpty() && !queueNum.isEmpty()) {
                        System.out.println("\n" + queueID.size() + " new size");
                        for (int i = 0; i < queueNum.size(); i++) {
                            values[i] = (int) queueNum.toArray()[i];
                        }
                        for (int i = 0; i < queueID.size(); i++) {
                            ID[i] = (String) queueID.toArray()[i];

                        }
                        int max = values[0];
                        String leader = ID[0];
                        for (int i = 1; i < values.length; i++) {
                            if (values[i] > max) {
                                max = values[i];
                                leader = ID[i];
                            }
                        }
                        if (RankLeader != leader) {
                            RankLeader = leader;
                            RankLeaderNum = max;
                            System.out.println(max + " " + leader + " thread count " + ThreadCount);
                            try {
                                column2.interrupt();
                            } catch (Exception s) {
                                System.out.println("column2 is already interrupted");
                            }

                            int z = queueNum.size();
                            z = z - 1;
                            for (int i = 0; i < z; i++) {
                                try {
                                    column[i].interrupt();
                                } catch (Exception g) {
                                    System.out.println("column " + i + " is already interrupted");

                                }

                            }
                            if ((queueNum.size() == (max) && queueID.size() == (max)) || ThreadCount >= max) {
                                System.out.println("hello");
                                try {
                                    Test();
                                } catch (Exception e) {
                                    System.out.println(" exit ");
                                }
                                state = false;
                            }


                        } else {
                            lock2.wait();
                        }
                        if ((queueNum.size() == (maxSet) && queueID.size() == (maxSet)) || ThreadCount >= maxSet) {
                            try {
                                Test();
                            }catch (Exception e){
                                System.out.println(" exit ");
                            }
                            state = false;
                        }
                    } else {
                        lock2.wait();
                    }
                }
                System.out.println("done");

            }
        }

    }



    public static void main(String[] args) {
        ThreadCount = 0;
        Random r = new Random();
        size = 6;
        pause = false;
        terminate = false;
        queueName = new ArrayBlockingQueue<>(size);
        queueRank = new ArrayBlockingQueue<>(size);
        column = new Thread[size];

        new Thread(new Question3.rankMaster("Rank Official ", queueRank, queueName, lock)).start();

        String id = "Thread " + 1;
        String Leader = "Yes";
        RankLeader = id;
        RankLeaderNum = r.nextInt(100);
        column2 = new Thread(new officicals(lock, queueName, queueRank, RankLeaderNum, id));
        column2.start();

        int x = size;
        x = x - 1;
        for (int i = 0; i < x; i++) {
            String name = "Thread " + (i + 1);
            column[i] = new Thread(new officicals(lock, queueName, queueRank, r.nextInt(100), name));
            column[i].start();
        }


    }


}
