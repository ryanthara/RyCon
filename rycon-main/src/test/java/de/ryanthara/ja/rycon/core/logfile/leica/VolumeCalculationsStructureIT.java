package de.ryanthara.ja.rycon.core.logfile.leica;

import de.ryanthara.ja.rycon.core.clearup.LogfileClearUp;
import de.ryanthara.ja.rycon.nio.LineReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class VolumeCalculationsStructureIT {

    private VolumeCalculationsStructure volumeCalculationsStructure;

    @Test
    void setUpComplete() {
        assertNotNull(volumeCalculationsStructure);
    }

    @BeforeEach
    void setUp() {
        final Path pathStakeOutLogfile = Paths.get("src/test/resources/analyzer/VOLUME-CALCULATIONS_logfile.txt");

        volumeCalculationsStructure = initLogfile(pathStakeOutLogfile);
    }

    private VolumeCalculationsStructure initLogfile(Path logfilePath) {
        // read logfile.txt
        LineReader lineReader = new LineReader(logfilePath);
        lineReader.readFile(false);
        List<String> lines = lineReader.getLines();

        // clean up logfile.txt
        LogfileClearUp logfileClearUp = new LogfileClearUp(lines);
        List<String> cleanLogfile = logfileClearUp.process(true);

        // initialize SetupStructure
        VolumeCalculationsStructure volumeCalculationsStructure = new VolumeCalculationsStructure(cleanLogfile);
        boolean setUpComplete = volumeCalculationsStructure.analyze();

        if (setUpComplete) {
            return volumeCalculationsStructure;
        } else {
            return null;
        }
    }


}
