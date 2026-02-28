import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Client {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter IP Address: ");
        String ip = scanner.nextLine().trim();
        int port;

        while (true) {
            System.out.print("Enter port number (1025-4998): ");
            String serverPort = scanner.nextLine().trim();

            try {
                port = Integer.parseInt(serverPort);

                if (port < 1025 || port > 4998) {
                    System.out.println("Invalid port");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid port");
            }
        }

        while (true) {
            int requestCount;
            String command;

            while (true) {
                System.out.println("Enter command: ");

                System.out.println("1) date");
                System.out.println("2) uptime");
                System.out.println("3) memory (free)");
                System.out.println("4) netstat");
                System.out.println("5) users (who)");
                System.out.println("6) processes (ps -ef)");
                System.out.print("Enter command name: ");

                command = scanner.nextLine().trim().toLowerCase();

                Set<String> valid = Set.of("date", "uptime", "memory", "free",
                        "netstat", "users", "who", "processes", "ps", "ps -ef");
                if (valid.contains(command)) {
                    break;
                }

                System.out.println("Invalid command");

            }

            while (true) {
                System.out.print("How many requests (1, 5, 10, 15, 20, 25)? ");
                String requests = scanner.nextLine().trim().toLowerCase();

                try {
                    requestCount = Integer.parseInt(requests);
                    if (List.of(1, 5, 10, 15, 20, 25).contains(requestCount)) {
                        break;
                    } else {
                        System.out.println("Invalid number of requests.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            System.out.println("Preparing to send " + requestCount + " requests for command: " + command);

            //noinspection resource
            ExecutorService executor = Executors.newFixedThreadPool(requestCount);
            List<Future<Long>> futures = new ArrayList<>();

            for (int i = 0; i < requestCount; i++) {
                futures.add(executor.submit(new ThreadHandler(ip, port, command)));
            }
            executor.shutdown();
            try {
                //noinspection ResultOfMethodCallIgnored
                executor.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            long totalTime = 0;
            System.out.println("\nIndividual turn-around times (ms):");
            for (int i = 0; i < futures.size(); i++) {
                try {
                    long time = futures.get(i).get();
                    System.out.printf("Request %d: %d ms%n", i+1, time);
                    totalTime += time;
                } catch (Exception e) {
                    System.out.printf("Request %d: error retrieving time%n", i+1);
                }
            }

            double average = (double) totalTime / futures.size();
            System.out.printf("Total turn-around time: %d ms%n", totalTime);
            System.out.printf("Average turn-around time: %.2f ms%n", average);



        }
    }
}
