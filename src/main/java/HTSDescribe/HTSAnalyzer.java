package HTSDescribe;

import htsjdk.samtools.util.Log;

/**
 * Base class for alignment file analyzers.
 */
public abstract class HTSAnalyzer {

    protected String fileName;

    public HTSAnalyzer(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Run the analyzer for the file specified by fileName.
     */
    public void analyze() {
        // set and then reset the global log level
        Log.setGlobalLogLevel(Log.LogLevel.ERROR);
        doAnalysis();
        emitln("");
        Log.setGlobalLogLevel(Log.LogLevel.DEBUG);
    }

    /**
     * Emit a string to the output destination.
     * @param s
     */
    protected void emit(String s) {
        System.out.print(s);
    }

    /**
     * Emit a string followed by a newline to the output destination.
     * @param s
     */
    protected void emitln(String s) {
        System.out.println(s);
    }

    protected abstract void doAnalysis();

}

