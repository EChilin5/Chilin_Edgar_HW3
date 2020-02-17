import java.time.Duration;
import java.time.Instant;
import java.util.Random;


public class Question2 {
    static float[][] A;
    static float[][] B;
    static float[][] result;
    static float[][] result2;
    static float[][] result3;
    static long overallTime;
    static long overallTimeCell;
    public static Thread[] threadCell;
    public static Thread[] threadColumn;


    public static class MatrixColumn implements Runnable {
        private float[][] A;
        private float[][] B;
        private float[][] C;
        private int m, n, p;
        private String name;
        private int location;

        public MatrixColumn(String name, float[][] a, float[][] b,
                            float[][] c, int m, int n, int p) {
            this.name = name;
            A = a;
            B = b;
            C = c;
            this.m = m;
            this.n = n;
            this.p = p;
            location = 0;
        }

        @Override
        public void run() {
            try {

                mult2(name, A, B, C, m, n, p);
            } catch (InterruptedException e) {

            }
        }

        public static void print(float[][] arr) {
            Random r = new Random();
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    System.out.print(arr[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }

        public synchronized void mult2(String name, float[][] A,
                                       float[][] B, float[][] C, int rowA,
                                       int columnB, int p) throws InterruptedException {
            //synchronized (lock) {
            Instant start = Instant.now();
            // C[rowA][columnB] =0;
            for (int j = 0; j < B[0].length; j++) {
                C[rowA][j] = 0;
                for (int k = 0; k < A[0].length; k++) {
                    C[rowA][j] += A[rowA][k] * B[k][j];
                }
            }

            Instant end = Instant.now();
            long timeElapsed = Duration.between(start, end).getNano();
            System.out.println(" name: " + name + " time:" + timeElapsed + " nano seconds");
            overallTime += timeElapsed;
        }

    }

    public static class MatrixCell implements Runnable {
        private float[][] A;
        private float[][] B;
        private float[][] C;
        private int m, n, p;
        private String name;
        private int location;

        public MatrixCell(String name, float[][] a,
                          float[][] b, float[][] c, int m, int n, int p) {
            this.name = name;
            A = a;
            B = b;
            C = c;
            this.m = m;
            this.n = n;
            this.p = p;
            location = 0;
        }

        @Override
        public void run() {
            try {

                mult(name, A, B, C, m, n, p);
            } catch (InterruptedException e) {

            }
        }


        public synchronized void mult(String name, float[][] A,
                                      float[][] B, float[][] C, int rowA,
                                      int columnB, int p) throws InterruptedException {
            //synchronized (lock) {
            Instant start = Instant.now();

            C[rowA][columnB] = 0;
            for (int i = 0; i < p; i++) {
                C[rowA][columnB] += A[rowA][i] * B[i][columnB];
            }

            Instant end = Instant.now();
            long timeElapsed = Duration.between(start, end).getNano();
            System.out.println(" name: " + name + " time:" + timeElapsed + " nano seconds");
            overallTimeCell += timeElapsed;
        }

    }

    public static void setUp(float[][] arr) {
        Random r = new Random();

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = r.nextInt(10);
            }
        }
    }

    public static void print(float[][] arr) {
        Random r = new Random();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();

        }
        System.out.println();
    }


    public static void main(String[] args) throws InterruptedException {
        Random r = new Random();
        int row1 =5;// 1+r.nextInt(3);
        int colum =4;// 1 +r.nextInt(3);
        int columB = 3;//1 + r.nextInt(3);
        A = new float[row1][colum];
        B = new float[colum][columB];
        result = new float[colum][columB];
        result2 = new float[colum][columB];
        result3 = new float[colum][columB];
        System.out.println("Matrix A number of rows " + A.length + " number of columns " + A[0].length);
        System.out.println("Matrix B number of rows " + B.length + " number of columns " + B[0].length);
        System.out.println("Matrix C number of rows " + result.length + " number of columns " + result[0].length);
        setUp(A);
        setUp(B);
        int sum = 0;
        print(A);
        print(B);

        /////////// Main
        Instant start = Instant.now();
    /*    for (int i = 0; i < A[0].length; i++) {

            for (int j = 0; j < B[0].length; j++) {
                for (int k = 0; k < A[0].length; k++) {
                    result2[i][j] += A[i][k] * B[k][j];
                }
            }
        }*/
        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).getNano();
        System.out.println(" name: Main " + " time: " + timeElapsed);

        print(result2);

        // Columns
        threadColumn = new Thread[colum];
        int z = 0;
        for (int i = 0; i < A[0].length; i++) {
            z++;
            String name = "Thread based on Column " + z;
            threadColumn[i] = new Thread(new MatrixColumn(name, A, B, result, i, 0, A[0].length));
            threadColumn[i].start();
            threadColumn[i].join();
        }
        System.out.println("Threads created based on column " + overallTime+ " nano seconds");
        print(result);


        // threads created based on cells
        int val = colum * columB;
        threadCell = new Thread[val];
        int tcount = 0;
        int cellCount = 0;
        for (int i = 0; i < A[0].length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                tcount++;
                String name = "Thread base on Cell" + tcount;
                threadCell[cellCount] = new Thread(new MatrixCell(name, A, B, result3, i, j, A[0].length));
                threadCell[cellCount].start();
                threadCell[cellCount].join();
                cellCount++;
            }
        }

        System.out.println("Overall thread for each cell " + overallTimeCell + " nano seconds") ;
        print(result3);


        System.out.println("\nSummary");
        System.out.println(" Main thread " + " time: " + timeElapsed);
        System.out.println("Threads created based on column " + overallTime+ " nano seconds");
        System.out.println("Overall thread for each cell " + overallTimeCell+ " nano seconds");

        if (timeElapsed < overallTime && timeElapsed < overallTimeCell) {
            System.out.println("Main thread is faster");
        } else {
            System.out.println("Threads are faster");
        }


    }


}
