import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressWarnings("ClassCanBeRecord")
public class ClientHandler implements Runnable {
    private final Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Client connected: " + client.getInetAddress());

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true)
        ) {

            String input;
            while ((input = in.readLine()) != null) {
                System.out.println("[" + threadName + "] Received from client: " + input);

                if (input.equalsIgnoreCase("exit") ||
                        input.equalsIgnoreCase("end") ||
                        input.equalsIgnoreCase("close") ||
                        input.equalsIgnoreCase("quit")) {
                    out.println("Bye");
                    break;
                }

                String commandOutput = Server.executeLinuxCommand(input);
                out.println(commandOutput);
                out.println("END");
            }

        } catch (IOException e) {
            System.err.println("[" + threadName + "] I/O error: " + e.getMessage());
        } finally {
            try {
                client.close();
                System.out.println("[" + threadName + "] Client disconnected: " + client.getInetAddress());
            } catch (IOException e) {
                System.err.println("[" + threadName + "] Error closing socket: " + e.getMessage());
            }
        }
    }
}