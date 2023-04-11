package timnekk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "start", mixinStandardHelpOptions = true, description = "Starts the server", requiredOptionMarker = '*', abbreviateSynopsis = true)
public class StartCommand implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StartCommand.class);

    @Option(names = { "-p", "--port" }, description = "Port number", required = true)
    private int port;

    @Override
    public void run() {
        logger.info("Starting server on port {}", port);

        try (Server server = new Server(port)) {
            server.run();
        } catch (Exception e) {
            logger.error("Server failed: {}", e.getMessage());
        }
    }
}