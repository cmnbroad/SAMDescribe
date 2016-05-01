package SAMAnalyzer;

import htsjdk.samtools.cram.build.CramIO;

/**
 * Class for creating an analyzer based on an alignment file type.
 */
public class SAMAnalyzerFactory {

    public static SAMAnalyzer getAnalyzer(String fileName) {
       if (fileName.endsWith(CramIO.CRAM_FILE_EXTENSION)) {
           return new CRAMAnalyzer(fileName);
       }
        else {
           throw new RuntimeException("Unsupport file type: " + fileName);
       }
    }
}
