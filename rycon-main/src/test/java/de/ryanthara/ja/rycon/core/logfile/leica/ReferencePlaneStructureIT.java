package de.ryanthara.ja.rycon.core.logfile.leica;

import de.ryanthara.ja.rycon.core.clearup.LogfileClearUp;
import de.ryanthara.ja.rycon.nio.LineReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReferencePlaneStructureIT {

    private ReferencePlaneStructure referencePlaneStructure;

    @Test
    void setUpComplete() {
        assertNotNull(referencePlaneStructure);
    }

    @BeforeEach
    void setUp() {
        final Path pathStakeOutLogfile = Paths.get("src/test/resources/analyzer/REFERENCE-PLANE_logfile.txt");

        referencePlaneStructure = initLogfile(pathStakeOutLogfile);
    }

    private ReferencePlaneStructure initLogfile(Path logfilePath) {
        // read logfile.txt
        LineReader lineReader = new LineReader(logfilePath);
        lineReader.readFile(false);
        List<String> lines = lineReader.getLines();

        // clean up logfile.txt
        LogfileClearUp logfileClearUp = new LogfileClearUp(lines);
        List<String> cleanLogfile = logfileClearUp.process(true);

        // initialize SetupStructure
        ReferencePlaneStructure referencePlaneStructure = new ReferencePlaneStructure(cleanLogfile);
        boolean setUpComplete = referencePlaneStructure.analyze();

        if (setUpComplete) {
            return referencePlaneStructure;
        } else {
            return null;
        }
    }

}
