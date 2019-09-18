package HTSDescribe;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.cram.build.CramIO;
import htsjdk.samtools.cram.encoding.CRAMEncoding;
import htsjdk.samtools.cram.encoding.EncodingFactory;
import htsjdk.samtools.cram.ref.ReferenceContext;
import htsjdk.samtools.cram.structure.*;
import htsjdk.samtools.cram.structure.block.Block;
import htsjdk.samtools.seekablestream.SeekableFileStream;
import htsjdk.samtools.util.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;

/**
 * Analyzer for CRAM files. Displays metadata for each CRAM container,
 * slice, and block.
 *
 * Note: the analyzer does not require a reference for the since it only
 * enumerates metadata and doesn't attempt to dereference the reads.
 */
public class CRAMAnalyzer extends HTSAnalyzer {

    int recordCount = 0;

    public CRAMAnalyzer(String fileName) {
        super(fileName);
    }

    /**
     * Run the analyzer for the file.
     */
    protected void doAnalysis() {
        int containerCount = 0;
        try (final SeekableFileStream seekableStream = new SeekableFileStream(new File(this.fileName))) {
            final CramHeader cramHeader = analyzeCRAMHeader(seekableStream);
            boolean isEOF = false;
            while (!isEOF) {
                long containerOffset = seekableStream.position();
                final Container container = new Container(
                        cramHeader.getVersion(),
                        seekableStream,
                        containerOffset);
                 isEOF = analyzeContainer(container, ++containerCount) ;
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
        final CramHeader cramHeader = CramIO.readCramHeader(is);
        emitln("\nCRAM File: " + fileName);
        emitln("CRAM Version: " + cramHeader.getVersion().toString());
        emitln("CRAM ID Contents: " + String.format("%b", cramHeader.getId()));

        final SAMFileHeader samHeader = cramHeader.getSamFileHeader();
        emitln("\n" + samHeader.toString());
        final SAMSequenceDictionary dict = samHeader.getSequenceDictionary();
        emitln(dict.toString());
        dict.getSequences().forEach(e -> emitln(e.toString()));
        return cramHeader;
    }

    /**
     * Display metadata for a CRAM file container.
     * return true if container is EOF container
     */
    public boolean analyzeContainer(Container container, int containerCount) {
        emitln("\n***Container #" + containerCount + ": " + container.toString());
        if (container.isEOF()) {
            return true;
        }
        analyzeCompressionHeader(container.getCompressionHeader());
        int sliceCount = 0;
        for (final Slice slice : container.getSlices()) {
            analyzeCRAMSlice(slice, ++sliceCount);
        }
        return false;
    }

    public void analyzeCompressionHeader(final CompressionHeader compressionHeader) {
        //preservation map, data series encoding map, and tag encoding map
        analyzePreservationMap(compressionHeader);
        analyzeDataSeriesEncodingMap(compressionHeader);
        analyzeTagEncodingMap(compressionHeader);
    }

    public void analyzePreservationMap(final CompressionHeader compressionHeader) {
        //    public Map<Integer, EncodingParams> tMap;
        //    public SubstitutionMatrix substitutionMatrix;
        //    public byte[][][] dictionary;
        emitln(String.format(
                "Requires reference (no access); Include read names (%b); APDelta (%b)",
                    compressionHeader.readNamesIncluded,
                    compressionHeader.isCoordinateSorted()));
    }

    public void  analyzeDataSeriesEncodingMap(final CompressionHeader compressionHeader) {
        emitln("\nData Series Encodings:");
        final CompressionHeaderEncodingMap encodingMap = compressionHeader.getEncodingMap();
        for (final DataSeries dataSeries: DataSeries.values()) {
            final EncodingDescriptor encodingDescriptor = encodingMap.getEncodingDescriptorForDataSeries(dataSeries);
            if (encodingDescriptor == null) {
                emitln(String.format("%-50s not present",
                        String.format("DataSeries (%s/%s)",
                                dataSeries.getCanonicalName(),
                                dataSeries.name())));
            } else {
                emitln(String.format("%-50s %s",
                        String.format("DataSeries (%s/%s)",
                                dataSeries.getCanonicalName(),
                                dataSeries.name()),
                        encodingDescriptorAsString(
                                dataSeries.getType(),
                                encodingDescriptor)));
            }
        }
    }

    public void analyzeTagEncodingMap(final CompressionHeader compressionHeader) {
        emitln("\nTag Encodings:");
        for (final Map.Entry<Integer, EncodingDescriptor> entry : compressionHeader.tMap.entrySet()) {
            final Integer contentID = entry.getKey(); // is this content ID ?
            final EncodingDescriptor ep = entry.getValue();
            emitln(String.format("%-50s %s",
                    String.format("Content ID/Tag (%s/%s)",
                            Integer.toString(contentID),
                            decomposeTagNameAndType(contentID)),
                     encodingDescriptorAsString(DataSeriesType.BYTE_ARRAY, ep)));
       }
    }

    /**
     * Display metadata for a CRAM container slice.
     *
     */
    public void analyzeCRAMSlice(final Slice slice, final int sliceCount) {
        //TODO: add CRC display
        emitln(String.format(
                "\n******Slice #: %d Reference Context: %s MD5: %032x",
                    sliceCount,
                    slice.getReferenceContext() == ReferenceContext.MULTIPLE_REFERENCE_CONTEXT ?
                        " Multi" :
                        " Single",
                    new BigInteger(1, slice.getRefMD5())));
        emitln(String.format("%-50s %s",
                "Header block ",
                slice.getSliceHeaderBlock()));
        emitln(String.format("%-50s %s",
                "Core block ",
                slice.getSliceBlocks().getCoreBlock()));
        slice.getSliceBlocks().getExternalContentIDs().forEach((id) -> emitln(
                String.format("%-50s %s",
                        String.format("External Block (%s):", dataSeriesNameFromContentID(id)),
                        slice.getSliceBlocks().getExternalBlock(id).toString())));
        recordCount += slice.getNumberOfRecords();
    }

    // Find a DataSeries that uses the provided content ID, otherwise return Block.NO_CONTENT_ID
    private String dataSeriesNameFromContentID(final Integer contentID) {
        if (contentID == Block.NO_CONTENT_ID) {
            return String.format("%d (NO_CONTENT_ID)", Block.NO_CONTENT_ID);
        }
        for (final DataSeries ds : DataSeries.values()) {
            if (ds.getExternalBlockContentId() == contentID) {
                return ds.name();
            }
        }
        // if we don't find a data series that matches this block's content ID, then its
        //a tag block, so decompose the contentID as a tag name
        return String.format("%d/%s",
                contentID,
                decomposeTagNameAndType(contentID));
    }

    private String encodingDescriptorAsString(final DataSeriesType dsType, final EncodingDescriptor dscriptor) {
        final String encodingIDString = EncodingID.values()[dscriptor.getEncodingID().getId()].toString();
        final CRAMEncoding<?> encoding = EncodingFactory.createCRAMEncoding(
                dsType, dscriptor.getEncodingID(), dscriptor.getEncodingParameters());
        return String.format("%s (%s)", encodingIDString, encoding.toString());
    }

    private String decomposeTagNameAndType(final int contentID) {
        return ReadTag.intToNameType4Bytes(contentID);
//        return String.format("%s(%s)",
//                tagString.substring(0, 2),
//                tagString.substring(2, 3));
    }

}

