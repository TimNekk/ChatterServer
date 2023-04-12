package timnekk.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends Thread implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final Server server;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;
    private boolean joined = false;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        logger.debug("Client handler created for client {}", clientSocket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try {
            processLogin();

            String message;
            while (!isInterrupted() && (message = in.readLine()) != null) {
                broadcast(message);
            }
        } catch (IOException e) {
            if (!isInterrupted()) {
                logger.debug("Failed to handle client {} {}: {}", username, clientSocket.getRemoteSocketAddress(),
                        e.getMessage());
                server.removeHandler(this);
                close();
            }
        }
    }

    private void broadcast(String message) {
        message = formatMessage(message);
        if (!isMessageValid(message)) {
            return;
        }

        server.broadcast(message);
        logger.info("{} {} sent message: {}", username, clientSocket.getRemoteSocketAddress(), message);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public boolean hasJoined() {
        return joined;
    }

    private void processLogin() throws IOException {
        while (!isInterrupted()) {
            out.println("Enter username:");
            
            logger.debug("Waiting for username from client {}", clientSocket.getRemoteSocketAddress());
            username = in.readLine();

            if (isUsernameValid(username)) {
                logger.debug("Client {} username set to {}", clientSocket.getRemoteSocketAddress(), username);
                break;
            }

            logger.debug("Client {} tried to set invalid username: {}", clientSocket.getRemoteSocketAddress(), username);
        }

        joined = true;

        server.broadcast(username + " joined the chat!");
        logger.info("{} {} joined the chat", username, clientSocket.getRemoteSocketAddress());
    }

    private boolean isUsernameValid(String username) {
        if (username == null) {
            return false;
        }

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

    private boolean isMessageValid(String message) {
        return !message.isBlank() && !message.isEmpty();
    }

    private String formatMessage(String message) {
        return String.format("%s: %s", username, message.trim());
    }

    @Override
    public void close() {
        if (joined) {
            server.broadcast(username + " left the chat!");
            logger.info("{} {} left the chat", username, clientSocket.getRemoteSocketAddress());
        }

        logger.debug("Closing client handler for client {}", clientSocket.getRemoteSocketAddress());
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error("Failed to close client handler: {}", e.getMessage());
        }

        logger.debug("Client handler closed for client {}", clientSocket.getRemoteSocketAddress());
    }
}