package timnekk;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable, AutoCloseable {
    private ServerSocket serverSocket;
    private final List<ClientHandler> handlers = new ArrayList<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private boolean running = false;

    public Server(int port) {
        if (port < 1024 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1024 and 65535");
        }

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            // TODO: handle
        }
    }
    
    @Override
    public void close() throws Exception {
        running = false;

        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO: handle
            }
        }

        for (ClientHandler handler : handlers) {
            try {
                handler.close();
            } catch (IOException e) {
                // TODO: handle
            }
        }

        pool.shutdown();
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                Socket client = serverSocket.accept();
                ClientHandler handler = new ClientHandler(client, this);
                handlers.add(handler);
                pool.execute(handler);
            }
            catch (IOException e) {
                // TODO: handle
            }
        }
    }
    
    public void broadcast(String message) {
        for (ClientHandler handler : handlers) {
            handler.sendMessage(message);
        }
    }
}
