import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port;


        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
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
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            int clientCount = 0;
            boolean running = true;

            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    clientCount++;
                    String threadName = "Client-Handler-" + clientCount;
                    Thread thread = new Thread(new ClientHandler(client), threadName);
                    thread.start();
                } catch (IOException e) {
                    System.out.println("Error in accept(), stopping server: " + e.getMessage());
                    running = false;
                }
            }

        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
        }
    }

    public static String executeLinuxCommand(String command) {
        String cmds = (command == null ? "" : command.trim().toLowerCase());
        java.util.ArrayList<String> cmd = new java.util.ArrayList<>();
        cmd.add("/bin/sh");
        cmd.add("-c");

        switch (cmds) {
            case "date":
                cmd.add("date");
                break;
            case "uptime":
                cmd.add("uptime");
                break;
            case "free":
                cmd.add("free -h");
                break;
            case "netstat":
                cmd.add("netstat -tuln");
                break;
            case "who":
                cmd.add("who");
                break;
            case "ps":
            case "ps -ef":
                cmd.add("ps -ef");
                break;
            default:
                return "Invalid command: " + command;
        }

        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            output.append("Exit code: ").append(exitCode).append("\n");
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage()).append("\n");
        }

        return output.toString();
    }
}
