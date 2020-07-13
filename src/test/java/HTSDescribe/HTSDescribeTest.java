package HTSDescribe;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;

public class HTSDescribeTest {

    public String getTestDataDir() { return "src/test/resources/"; }

    @DataProvider(name = "cramFiles")
    public Object[][] getSerializationTestData() {
        return new Object[][]{
                //{new File(getTestDataDir(), "valid.cram")},
                {new File("/Users/cnorman/projects/references/NA12878.cram")}
                //{new File("/Users/cnorman/projects/gatk/src/test/resources/large/CEUTrio.HiSeq.WGS.b37.NA12878.20.21.cram")}
                //{new File("/var/folders/cr/16ghvyfj5lvfwxx01rt1k4tdl04sy3/T/testRoundTrip6855768792672827021.cram")}

        };
    }

    @Test(dataProvider="cramFiles")
    public void testAnalyze(File inputFile) {
        HTSDescribe.main(
                new String[] { "--target-path", inputFile.getAbsolutePath() });
    }
}