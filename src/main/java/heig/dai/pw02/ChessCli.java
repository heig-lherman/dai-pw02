package heig.dai.pw02;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import heig.dai.pw02.command.ClientCommand;
import heig.dai.pw02.command.ServerCommand;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

/**
 * Main CLI for the Chess client and server.
 *
 * @author Loïc Herman
 * @author Massimo Steffani
 */
@Command(
        name = "Chess",
        description = "Remote play a game of chess",
        version = "Chess 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                HelpCommand.class,
                ClientCommand.class,
                ServerCommand.class,
        }
)
public class ChessCli implements Runnable {

    @Option(
            names = "-v",
            description = "Change log verbosity. Use -vvv for maximum verbosity.",
            scope = ScopeType.INHERIT
    )
    public void setVerbosity(boolean[] verbose) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(
                verbose.length > 2 ? Level.ALL
                        : verbose.length > 1 ? Level.DEBUG
                        : verbose.length > 0 ? Level.INFO
                        : Level.WARN
        );
    }

    /**
     * Print basic usage information when the user doesn't use
     * one of the built-in subcommands.
     */
    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    /**
     * Main entry point for the CLI.
     *
     * @param args arguments that will be parsed by picocli
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new ChessCli()).execute(args);
        System.exit(exitCode);
    }
}
