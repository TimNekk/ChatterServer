package timnekk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParameterException;

@Command(name = "chatter-server", subcommands = {
        StartCommand.class }, mixinStandardHelpOptions = true, description = "Chatter server", abbreviateSynopsis = true)
public class App implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void run() {
    }

    public static void main(String[] args) {
        final CommandLine cmd = new CommandLine(new App());

        try {
            cmd.parseArgs(args);
            cmd.execute(args);
        } catch (ParameterException e) {
            logger.error("Parsing command line arguments failed: {}", e.getMessage());
        }
    }
}
