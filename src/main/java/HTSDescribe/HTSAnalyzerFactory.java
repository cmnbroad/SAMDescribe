package HTSDescribe;

import htsjdk.samtools.cram.build.CramIO;

/**
 * Class for creating an analyzer based on an alignment file type.
 */
public class HTSAnalyzerFactory {

    public static HTSAnalyzer getFileAnalyzer(String fileName) {
       if (fileName.endsWith(CramIO.CRAM_FILE_EXTENSION)) {
           return new CRAMAnalyzer(fileName);
       }
        else {
           throw new RuntimeException("Unsupported file type: " + fileName);
       }
    }
}
