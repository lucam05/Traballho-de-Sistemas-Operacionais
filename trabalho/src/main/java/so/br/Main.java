package so.br;

import java.util.Random;
import java.util.Arrays;

public class Main {
    static final int NUMBER_OF_CUSTOMERS = 5;
    static final int NUMBER_OF_RESOURCES = 3;
    static int[] available = new int[NUMBER_OF_RESOURCES];
    static int[][] maximum = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static int[][] allocation = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static int[][] need = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static Object lock = new Object();
    static Random rand = new Random();

    public static void main(String[] args) {
        if (args.length != NUMBER_OF_RESOURCES) {
            System.out.println("Usage: java so.br.Main <resource1> <resource2> <resource3>");
            return;
        }

        initialize(args);

        // Create and start customer threads
        Thread[] customers = new Thread[NUMBER_OF_CUSTOMERS];
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
            customers[i] = new Customer(i);
            customers[i].start();
        }

        // Let them run indefinitely
    }

    static void initialize(String[] args) {
        for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
            available[i] = Integer.parseInt(args[i]);
        }

        // Initialize maximum - hardcoded values
        maximum[0] = new int[]{7, 5, 3};
        maximum[1] = new int[]{3, 2, 2};
        maximum[2] = new int[]{9, 0, 2};
        maximum[3] = new int[]{2, 2, 2};
        maximum[4] = new int[]{4, 3, 3};

        // allocation is 0, need = maximum
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                need[i][j] = maximum[i][j];
            }
        }
    }

    static int request_resources(int customer_num, int[] request) {
        synchronized(lock) {
            // Check if request <= need
            for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                if (request[i] > need[customer_num][i]) return -1;
            }
            // Check if request <= available
            for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                if (request[i] > available[i]) return -1;
            }
            // Tentative allocation
            for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                available[i] -= request[i];
                allocation[customer_num][i] += request[i];
                need[customer_num][i] -= request[i];
            }
            // Check if safe
            if (isSafe()) {
                return 0; // Success
            } else {
                // Rollback
                for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                    available[i] += request[i];
                    allocation[customer_num][i] -= request[i];
                    need[customer_num][i] += request[i];
                }
                return -1;
            }
        }
    }

    static int release_resources(int customer_num, int[] release) {
        synchronized(lock) {
            // Check if release <= allocation
            for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                if (release[i] > allocation[customer_num][i]) return -1;
            }
            // Release
            for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                available[i] += release[i];
                allocation[customer_num][i] -= release[i];
                need[customer_num][i] += release[i];
            }
            return 0;
        }
    }

    static boolean isSafe() {
        int[] work = Arrays.copyOf(available, NUMBER_OF_RESOURCES);
        boolean[] finish = new boolean[NUMBER_OF_CUSTOMERS];
        int count = 0;
        while (count < NUMBER_OF_CUSTOMERS) {
            boolean found = false;
            for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                        if (need[i][j] > work[j]) {
                            canFinish = false;
                            break;
                        }
                    }
                    if (canFinish) {
                        for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                            work[j] += allocation[i][j];
                        }
                        finish[i] = true;
                        found = true;
                        count++;
                    }
                }
            }
            if (!found) {
                return false; // No process can finish
            }
        }
        return true;
    }

    static class Customer extends Thread {
        int customer_num;

        Customer(int num) {
            this.customer_num = num;
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(rand.nextInt(1000) + 500); // Sleep 0.5-1.5s
                } catch (InterruptedException e) {}
                // Decide to request or release
                if (rand.nextBoolean()) {
                    // Request
                    int[] request = new int[NUMBER_OF_RESOURCES];
                    synchronized(lock) {
                        for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                            request[i] = rand.nextInt(need[customer_num][i] + 1);
                        }
                    }
                    int result = request_resources(customer_num, request);
                    if (result == 0) {
                        System.out.println("Customer " + customer_num + " requested " + Arrays.toString(request) + " - SUCCESS");
                    } else {
                        System.out.println("Customer " + customer_num + " requested " + Arrays.toString(request) + " - DENIED");
                    }
                } else {
                    // Release
                    int[] release = new int[NUMBER_OF_RESOURCES];
                    synchronized(lock) {
                        for (int i = 0; i < NUMBER_OF_RESOURCES; i++) {
                            release[i] = rand.nextInt(allocation[customer_num][i] + 1);
                        }
                    }
                    int result = release_resources(customer_num, release);
                    if (result == 0) {
                        System.out.println("Customer " + customer_num + " released " + Arrays.toString(release) + " - SUCCESS");
                    } else {
                        System.out.println("Customer " + customer_num + " released " + Arrays.toString(release) + " - ERROR");
                    }
                }
            }
        }
    }
}