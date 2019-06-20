package HTSDescribe;

import org.broadinstitute.barclay.argparser.Argument;

/**
 * Command line tool for analyzing and displaying metadata about a SAM/BAM/CRAM/VCF/BCF file or index file.
 *
 * Currently only for CRAM.
 */
public class HTSDescribe extends CommandLineProgram {

    @Argument(shortName="targetPath", fullName="targetPath",
            doc="Path to file to be analyzed",
            optional=false)
    private String targetPath;

    @Argument(shortName="companionIndex", fullName="companionIndex",
            doc="Path of companion index file to be analyzed",
            optional=true)
    private String companionIndex;

    @Argument(shortName="analysisDepth", fullName="analysisDepth",
            doc="depth of analysis to be performed")
    private int analysisDepth;

    public final int doWork() {
        final HTSAnalyzer HTSAnalyzer = HTSAnalyzerFactory.getFileAnalyzer(targetPath);
        HTSAnalyzer.analyze();
        return 0; // TODO: fix this
    }

    public static void main(String argv[]) {
        final HTSDescribe htsDescribe = new HTSDescribe();
        htsDescribe.parseArgs(argv);
        htsDescribe.doWork();
    }
}
