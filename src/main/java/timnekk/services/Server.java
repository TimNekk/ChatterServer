package timnekk.services;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final ServerSocket serverSocket;
    private final List<ClientHandler> handlers = new ArrayList<>();
    private boolean running = false;

    public Server(int port) throws IOException {
        if (port < 1024 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1024 and 65535");
        }

        serverSocket = new ServerSocket(port);
        logger.debug("Server socket created on port {}", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        running = true;

        logger.info("Server started on port {}", serverSocket.getLocalPort());
        while (running) {
            Optional<Socket> clientSocket = waitForConnection();

            if (!clientSocket.isPresent()) {
                return;
            }

            Optional<ClientHandler> handler = createHandler(clientSocket.get());

            handler.ifPresent(h -> {
                handlers.add(h);
                h.start();
            });
        }
    }

    private Optional<Socket> waitForConnection() {
        try {
            logger.debug("Waiting for new client connection");
            Socket client = serverSocket.accept();
            logger.info("Client connected from {}", client.getRemoteSocketAddress());
            return Optional.of(client);
        } catch (IOException e) {
            if (running) {
                logger.error("Failed to accept client connection: {}", e.getMessage());
            }
        }
        return Optional.empty();
    }

    private Optional<ClientHandler> createHandler(Socket client) {
        try {
            return Optional.of(new ClientHandler(client, this));
        } catch (IOException e) {
            logger.error("Failed to create client handler: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public void broadcast(String message) {
        for (ClientHandler handler : handlers) {
            if (handler.hasJoined()) {
                handler.sendMessage(message);
            }
        }
    }

    public void removeHandler(ClientHandler handler) {
        handlers.remove(handler);
    }

    @Override
    public void close() {
        if (!running) {
            return;
        }
        running = false;

        for (ClientHandler handler : handlers) {
            handler.interrupt();
            handler.close();
        }
        logger.debug("All Client handlers closed");

        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
                logger.debug("Server socket closed");
            } catch (IOException e) {
                logger.error("Failed to close server socket: {}", e.getMessage());
            }
        }

        logger.debug("Server closed");
    }
}
