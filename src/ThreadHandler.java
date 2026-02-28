import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

@SuppressWarnings("ClassCanBeRecord")
class ThreadHandler implements Callable<Long> {
    private final String ip;
    private final int port;
    private final String command;

    public ThreadHandler(String ip, int port, String command) {
        this.ip = ip;
        this.port = port;
        this.command = command;
    }

    @Override
    public Long call() {
        long start = System.nanoTime();
        StringBuilder response = new StringBuilder();

        try (Socket socket = new Socket(ip, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(command);

            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("END")) break;
                response.append(line).append("\n");
            }

            System.out.println("\n--- Response from server (" + command + ") ---");
            System.out.println(response);

        } catch (IOException e) {
            System.err.println("Error in thread for command " + command + ": " + e.getMessage());
        }

        long end = System.nanoTime();
        long elapsedMillis = (end - start) / 1_000_000;
        System.out.println("Turn-around time: " + elapsedMillis + " ms");
        return elapsedMillis;
    }
}
