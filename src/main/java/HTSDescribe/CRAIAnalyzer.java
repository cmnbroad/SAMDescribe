package HTSDescribe;

import htsjdk.samtools.CRAMCRAIIndexer;
import htsjdk.samtools.cram.CRAIIndex;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Analyzer for CRAM (.crai) index files.
 */
public class CRAIAnalyzer extends HTSAnalyzer {

    public CRAIAnalyzer(String fileName) {
        super(fileName);
    }

    /**
     * Run the analyzer for the file.
     */
    protected void doAnalysis() {
        try (final FileInputStream fis = new FileInputStream(fileName)) {
            final CRAIIndex craiIndex = CRAMCRAIIndexer.readIndex(fis);

            System.out.println("\nSeqId AlignmentStart AlignmentSpan ContainerOffset SliceOffset SliceSize\n");
            craiIndex.getCRAIEntries().stream().forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

