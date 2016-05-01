package SAMAnalyzer;

/**
 * App for analyzing and displaying metadata about a SAM/BAM/CRAM file.
 *
 * Currently only for CRAM.
 */
public class SAMDescribe {

    public static void main(String argv[]) {
        SAMAnalyzer samAnalyzer = SAMAnalyzerFactory.getAnalyzer(argv[0]);
        samAnalyzer.analyze();
    }
}
