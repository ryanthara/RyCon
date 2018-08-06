package de.ryanthara.ja.rycon.core.logfile.leica;

import de.ryanthara.ja.rycon.core.LogfileClearUp;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.nio.LineReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StakeOutStructureTest {

    private StakeOutStructure stakeOutStructure;

    @Test
    void getDesignPoint() {
        RyPoint designPoint1 = new RyPoint("Design Point", "1267032.621", "1267035.272", "1.620");
        RyPoint designPoint2 = new RyPoint("Design Point", "2610219.501", "1267035.272", "2.182");

        assertFalse(stakeOutStructure.getDesignPoint().equals(designPoint1));
        assertTrue(stakeOutStructure.getDesignPoint().equals(designPoint2));
    }

    @Test
    void getPointId() {
        assertNotEquals("9003STKE", stakeOutStructure.getPointId());
        assertEquals("9004STKE", stakeOutStructure.getPointId());
    }

    @Test
    void getStakedPoints() {
        RyPoint stakedPoint1 = new RyPoint("Staked Point", "2610230.979", "1267032.620", "1.620", "0.000");
        RyPoint stakedPoint2 = new RyPoint("Staked Point", "2610219.499", "1267035.272", "2.181", "0.000");

        assertEquals(2, stakeOutStructure.getStakedPoints().size());

        assertTrue(stakeOutStructure.getStakedPoints().get(0).getStakedPoint().equals(stakedPoint1));
        assertTrue(stakeOutStructure.getStakedPoints().get(1).getStakedPoint().equals(stakedPoint2));
    }

    @Test
    void getStakeoutDifference() {
        RyPoint stakeoutDifference1 = new RyPoint("Stakeout Diff", "-0.000", "0.001", "0.000");
        RyPoint stakeoutDifference2 = new RyPoint("Stakeout Diff", "0.001", "-0.000", "0.001");

        assertEquals(2, stakeOutStructure.getStakedPoints().size());

        assertFalse(stakeoutDifference1.equals(stakeOutStructure.getStakeoutDifference()));
        assertTrue(stakeoutDifference2.equals(stakeOutStructure.getStakeoutDifference()));

        assertTrue(stakeOutStructure.getStakedPoints().get(0).getStakeoutDifference().equals(stakeoutDifference1));
        assertTrue(stakeOutStructure.getStakedPoints().get(1).getStakeoutDifference().equals(stakeoutDifference2));
    }

    @Test
    void getTpsStation() {
        RyPoint tpsStation = new RyPoint("FS04", "2610184.057", "1267049.603", "1.612", "0.000");

        assertTrue(stakeOutStructure.getTpsStation().equals(tpsStation));
    }

    @Test
    void setUpComplete() {
        assertNotNull(stakeOutStructure);
    }

    @BeforeEach
    void setup() {
        final Path pathStakeOutLogfile = Paths.get("src/test/resources/analyzer/STAKEOUT_logfile.txt");

        stakeOutStructure = initLogfile(pathStakeOutLogfile);
    }

    private StakeOutStructure initLogfile(Path logfilePath) {
        // read logfile.txt
        LineReader lineReader = new LineReader(logfilePath);
        lineReader.readFile(false);
        ArrayList<String> lines = lineReader.getLines();

        // clean up logfile.txt
        LogfileClearUp logfileClearUp = new LogfileClearUp(lines);
        ArrayList<String> cleanLogfile = logfileClearUp.processClean(true);

        // initialize SetupStructure
        StakeOutStructure stakeOutStructure = new StakeOutStructure(cleanLogfile);
        boolean setUpComplete = stakeOutStructure.analyze();

        if (setUpComplete) {
            return stakeOutStructure;
        } else {
            return null;
        }
    }

} // end of StakeOutStructureTest