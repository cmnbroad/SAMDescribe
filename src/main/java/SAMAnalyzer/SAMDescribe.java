package SAMAnalyzer;

/**
 * App for analyzing and displaying metadata about a SAM/BAM/CRAM file.
 *
 * Currently only for CRAM.
 */
public class SAMDescribe {

    public static void main(String argv[]) {
        if (argv.length < 1) {
            throw new IllegalArgumentException("A CRAM file must be specified on the command line");
        }

        SAMAnalyzer samAnalyzer = SAMAnalyzerFactory.getAnalyzer(argv[0]);

        try {
            samAnalyzer.analyze();
        }
        catch (Exception e) {
            System.out.println("Exception processing file: " + argv[0]);
            throw e;
        }
    }
}
