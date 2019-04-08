package SAMAnalyzer;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.cram.build.CramIO;
import htsjdk.samtools.cram.ref.ReferenceContext;
import htsjdk.samtools.cram.structure.*;
import htsjdk.samtools.util.RuntimeIOException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Analyzer for CRAM files. Displays metadata for each CRAM container,
 * slice, and block.
 *
 * Note: the analyzer does not require a reference for the since it only
 * enumerates metadata and doesn't attempt to dereference the reads.
 */
public class CRAMAnalyzer extends SAMAnalyzer {

    int recordCount = 0;

    public CRAMAnalyzer(String fileName) {
        super(fileName);
    }

    /**
     * Run the analyzer for the file.
     */
    protected void doAnalysis() {
        int containerCount = 0;
        try (InputStream is = new BufferedInputStream(new FileInputStream(this.fileName))) {
            CramHeader cramHeader = analyzeCRAMHeader(is);
            Container container = null;
            while ((container = ContainerIO.readContainer(cramHeader.getVersion(), is)) != null &&
                    !container.isEOF()) {
                analyzeCRAMContainer(container, ++containerCount);
            }
        }
        catch (IOException e) {
            throw new RuntimeIOException(e);
        }

        emitln("\nTotal Record Count: " + recordCount);
    }

    /**
     * Display metadata for a CRAM file header.
     *
     */
    public CramHeader analyzeCRAMHeader(InputStream is) {
        CramHeader cramHeader = CramIO.readCramHeader(is);
        emitln("\nAnaylzing CRAM file: " + fileName);
        emitln("CRAM Version: " + cramHeader.getVersion().toString());

        SAMFileHeader samHeader = cramHeader.getSamFileHeader();
        emitln("\n" + samHeader.toString());
        SAMSequenceDictionary dict = samHeader.getSequenceDictionary();
        emitln(dict.toString());
        dict.getSequences().forEach(e -> emitln(e.toString()));
        return cramHeader;
    }

    /**
     * Display metadata for a CRAM file container.
     *
     */
    public void analyzeCRAMContainer(Container container, int containerCount) {
        emitln("\nContainer #" + containerCount + ": " + container.toString());
        int sliceCount = 0;
        for (Slice slice : container.slices) {
            analyzeCRAMSlice(slice, ++sliceCount);
        }
    }

    /**
     * Display metadata for a CRAM container slice.
     *
     */
    public void analyzeCRAMSlice(Slice slice, int sliceCount) {
        emit("Slice #" +
                sliceCount +
                (slice.getReferenceContext() == ReferenceContext.MULTIPLE_REFERENCE_CONTEXT? " Multi" : " Single") +
                " reference ");
        emit("MD5: ");
        emitln(String.format("%032x", new BigInteger(1, slice.refMD5)));
        emitln(slice.toString());
        emitln("Header block: " +
                slice.headerBlock);
        emitln("Core block: " +
                slice.coreBlock);
        slice.external.forEach((i, b) -> emitln("External Block " + i + ": "+ b.toString()));
        recordCount += slice.nofRecords;
    }

}

