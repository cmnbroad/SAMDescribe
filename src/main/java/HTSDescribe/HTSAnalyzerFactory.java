package HTSDescribe;

import htsjdk.samtools.util.FileExtensions;

/**
 * Class for creating an analyzer based on an alignment file type.
 */
public class HTSAnalyzerFactory {

    public static HTSAnalyzer getFileAnalyzer(String fileName) {
        System.out.println(fileName);
        if (fileName.endsWith(FileExtensions.CRAM)) {
            return new CRAMAnalyzer(fileName);
        } else if (fileName.endsWith(FileExtensions.CRAM_INDEX)) {
            return new CRAIAnalyzer(fileName);
        } else if (fileName.endsWith(FileExtensions.BAI_INDEX)) {
            return new BAIAnalyzer(fileName);
        } else {
            throw new RuntimeException("Unsupported file type: " + fileName);
        }
    }
}
