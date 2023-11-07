package heig.dai.pw02;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

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
        }
)
public class ChessCli implements Runnable {

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
