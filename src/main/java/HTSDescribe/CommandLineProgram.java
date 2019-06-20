package HTSDescribe;

import org.broadinstitute.barclay.argparser.CommandLineArgumentParser;
import org.broadinstitute.barclay.argparser.CommandLineParser;

/**
 */
public abstract class CommandLineProgram {

    final CommandLineParser commandLineParser = getCommandLineParser();

    /**
     * Parse arguments and initialize any arguments
     */
    protected final boolean parseArgs(final String[] argv) {
        return getCommandLineParser().parseArguments(System.out, argv);
    }

    /**
     * @return this programs CommandLineParser.  If one is not initialized yet this will initialize it.
     */
    public final CommandLineParser getCommandLineParser() {
        if( commandLineParser == null) {
            return new CommandLineArgumentParser(this);
        }
        return commandLineParser;
    }
}
