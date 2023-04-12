package timnekk.commands;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import timnekk.services.Server;

@Command(name = "start", mixinStandardHelpOptions = true, description = "Starts the server", requiredOptionMarker = '*', abbreviateSynopsis = true)
public class StartCommand implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StartCommand.class);

    @Option(names = { "-P", "--port" }, description = "Port number.", required = true)
    private int port;

    @Override
    public void run() {
        try (Server server = new Server(port)) {
            setShutdownHook(server);
            server.run();
        } catch (IOException e) {
            logger.error("Failed to start server: {}", e.getMessage());
        }
    }

    private void setShutdownHook(Server server) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.close();
            }
        });
    }
}