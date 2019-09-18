package HTSDescribe;

import htsjdk.samtools.BAMIndexer;

import java.io.File;

/**
 * Analyzer for BAI files.
 */
public class BAIAnalyzer extends HTSAnalyzer {

    public BAIAnalyzer(String fileName) {
        super(fileName);
    }

    /**
     * Run the analyzer for the file.
     */
    protected void doAnalysis() {
        final File outputFile = new File(new File(fileName).getName() + ".txt");
        System.out.println(String.format("\nOutput written to %s\n", outputFile));
        BAMIndexer.createAndWriteIndex(new File(fileName), outputFile, true);
    }

}

