package timnekk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable, AutoCloseable {
    private final Socket client;
    private final Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private boolean running = false;

    public ClientHandler(Socket client, Server server) {
        this.client = client;
        this.server = server;

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            // TODO: handle
        }
    }

    @Override
    public void close() throws Exception {
        running = false;

        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            // TODO: handle
        }
    }

    @Override
    public void run() {
        running = true;

        try {
            handleUsername();
            server.broadcast(username + " joined the chat!");
            
            String message;
            while (running && (message = in.readLine()) != null) {
                server.broadcast(username + ": " + message);
            }
        } catch (IOException e) {
            // TODO: handle
        }
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }

    private void handleUsername() throws IOException {
        while (running) {
            out.println("Enter username:");
            username = in.readLine();

            if (isUsernameValid(username)) {
                break;
            }
        }
    }

    private boolean isUsernameValid(String username) {
        if (username.length() < 3 || username.length() > 12) {
            out.println("Username must be between 3 and 12 characters");
            return false;
        }

        if (!username.matches("[a-zA-Z0-9]+")) {
            out.println("Username must only contain letters and numbers");
            return false;
        }

        return true;
    }
}