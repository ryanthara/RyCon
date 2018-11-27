package de.ryanthara.ja.rycon.core.logfile.leica;

import de.ryanthara.ja.rycon.core.clearup.LogfileClearUp;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.nio.LineReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StakeOutStructureIT {

    private StakeOutStructure stakeOutStructure;

    @Test
    void getDesignPoint() {
        RyPoint designPoint1 = new RyPoint.Builder("Design Point")
                .setEasting("1267032.621")
                .setNorthing("1267035.272")
                .setHeight("1.620")
                .build();

        assertNotEquals(stakeOutStructure.getDesignPoint(), designPoint1);

        RyPoint designPoint2 = new RyPoint.Builder("Design Point")
                .setEasting("2610219.501")
                .setNorthing("1267035.272")
                .setHeight("2.182")
                .build();

        assertEquals(stakeOutStructure.getDesignPoint(), designPoint2);
    }

    @Test
    void getPointId() {
        assertNotEquals("9003STKE", stakeOutStructure.getPointId());
        assertEquals("9004STKE", stakeOutStructure.getPointId());
    }

    @Test
    void getStakedPoints() {
        assertEquals(2, stakeOutStructure.getStakedPoints().size());

        RyPoint stakedPoint1 = new RyPoint.Builder("Staked Point")
                .setEasting("2610230.979")
                .setNorthing("1267032.620")
                .setHeight("1.620")
                .setInstrumentOrReflectorHeight("0.000")
                .build();

        assertEquals(stakeOutStructure.getStakedPoints().get(0).getStakedPoint(), stakedPoint1);

        RyPoint stakedPoint2 = new RyPoint.Builder("Staked Point")
                .setEasting("2610219.499")
                .setNorthing("1267035.272")
                .setHeight("2.181")
                .setInstrumentOrReflectorHeight("0.000")
                .build();

        assertEquals(stakeOutStructure.getStakedPoints().get(1).getStakedPoint(), stakedPoint2);
    }

    @Test
    void getStakeoutDifference() {
        assertEquals(2, stakeOutStructure.getStakedPoints().size());

        RyPoint stakeoutDifference1 = new RyPoint.Builder("Stakeout Diff")
                .setEasting("-0.000")
                .setNorthing("0.001")
                .setHeight("0.000")
                .build();

        assertNotEquals(stakeoutDifference1, stakeOutStructure.getStakeoutDifference());
        assertEquals(stakeOutStructure.getStakedPoints().get(0).getStakeoutDifference(), stakeoutDifference1);

        RyPoint stakeoutDifference2 = new RyPoint.Builder("Stakeout Diff")
                .setEasting("0.001")
                .setNorthing("-0.000")
                .setHeight("0.001")
                .build();

        assertEquals(stakeoutDifference2, stakeOutStructure.getStakeoutDifference());
        assertEquals(stakeOutStructure.getStakedPoints().get(1).getStakeoutDifference(), stakeoutDifference2);
    }

    @Test
    void getTpsStation() {
        RyPoint tpsStation = new RyPoint.Builder("FS04")
                .setEasting("2610184.057")
                .setNorthing("1267049.603")
                .setHeight("1.612")
                .setInstrumentOrReflectorHeight("0.000")
                .build();

        assertEquals(stakeOutStructure.getTpsStation(), tpsStation);
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
        List<String> lines = lineReader.getLines();

        // clean up logfile.txt
        LogfileClearUp logfileClearUp = new LogfileClearUp(lines);
        List<String> cleanLogfile = logfileClearUp.process(true);

        // initialize SetupStructure
        StakeOutStructure stakeOutStructure = new StakeOutStructure(cleanLogfile);
        boolean setUpComplete = stakeOutStructure.analyze();

        if (setUpComplete) {
            return stakeOutStructure;
        } else {
            return null;
        }
    }

}
