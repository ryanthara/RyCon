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

class ReferenceLineStructureIT {

    private ReferenceLineStructure referenceLineStructure;

    @Test
    void getMeasuredPoints() {
        assertEquals(2, referenceLineStructure.getMeasuredPoints().size());
    }

    @Test
    void getStakedPoints() {
        assertEquals(1, referenceLineStructure.getStakedPoints().size());
    }

    @Test
    void getDesignLineOffsets() {
        final RyPoint designLineOffsets = new RyPoint.Builder("Design Line/Offs.")
                .setEasting("0.000")
                .setNorthing("1.750")
                .setHeight("0.000")
                .build();

        assertEquals(referenceLineStructure.getDesignLineOffsets(), designLineOffsets);
    }

    @Test
    void getDesignPoint() {
        final RyPoint designPoint = new RyPoint.Builder("Design Point")
                .setEasting("35.150")
                .setNorthing("9.000")
                .setHeight("257.820")
                .build();

        assertEquals(referenceLineStructure.getDesignPoint(), designPoint);
    }

    @Test
    void getEndPoint() {
        final RyPoint endPoint = new RyPoint.Builder("72-47")
                .setEasting("33.400")
                .setNorthing("63.400")
                .setHeight("257.820")
                .build();

        assertEquals(referenceLineStructure.getEndPoint(), endPoint);
    }

    @Test
    void getHeightOffset() {
        final String heightOffset = "0.300";

        assertEquals(referenceLineStructure.getHeightOffset(), heightOffset);
    }

    @Test
    void getMeasuredLineOffset() {
        final RyPoint lineOffset1 = new RyPoint.Builder("Line/Offset")
                .setEasting("-0.001")
                .setNorthing("-9.318")
                .setHeight("0.096")
                .build();

        assertNotEquals(referenceLineStructure.getMeasuredLineOffset(), lineOffset1);

        final RyPoint lineOffset2 = new RyPoint.Builder("Line/Offset")
                .setEasting("-0.002")
                .setNorthing("-9.314")
                .setHeight("0.196")
                .build();

        assertEquals(referenceLineStructure.getMeasuredLineOffset(), lineOffset2);
    }

    @Test
    void getMeasuredPoint() {
        final RyPoint measuredPoint1 = new RyPoint.Builder("Measured")
                .setEasting("37.640")
                .setNorthing("-5.878")
                .setHeight("257.916")
                .setInstrumentOrReflectorHeight("0.000")
                .build();

        assertNotEquals(referenceLineStructure.getMeasuredPoint(), measuredPoint1);

        final RyPoint measuredPoint2 = new RyPoint.Builder("Measured")
                .setEasting("37.641")
                .setNorthing("-5.879")
                .setHeight("257.915")
                .setInstrumentOrReflectorHeight("0.000")
                .build();

        assertEquals(referenceLineStructure.getMeasuredPoint(), measuredPoint2);
    }

    @Test
    void getOffsetLine() {
        final String offsetLine = "0.100";

        assertEquals(referenceLineStructure.getOffsetLine(), offsetLine);
    }

    @Test
    void getPointId() {
        final String pointId = "17.1";

        assertEquals(referenceLineStructure.getPointId(), pointId);
    }


    @Test
    void getReferenceLineGradeAngular() {
        final String gradeAngular = "0.0000";

        assertEquals(referenceLineStructure.getReferenceLineGradeAngular(), gradeAngular);
    }

    @Test
    void getReferenceLineGradePercent() {
        final String gradePercent = "0.000";

        assertEquals(referenceLineStructure.getReferenceLineGradePercent(), gradePercent);
    }

    @Test
    void getReferenceLineId() {
        final String referenceLineId = "------";

        assertEquals(referenceLineId, referenceLineStructure.getReferenceLineId());
    }

    @Test
    void getReferenceLineLength() {
        final String referenceLineLength = "54.400";

        assertEquals(referenceLineLength, referenceLineStructure.getReferenceLineLength());
    }

    @Test
    void getRotate() {
        final String rotate = "10.0000";

        assertEquals(referenceLineStructure.getRotate(), rotate);
    }

    @Test
    void getShiftLine() {
        final String shiftLine = "0.200";

        assertEquals(referenceLineStructure.getShiftLine(), shiftLine);
    }

    @Test
    void getStakedPoint() {
        final RyPoint stakedPoint = new RyPoint.Builder("Staked Point")
                .setEasting("35.166")
                .setNorthing("9.012")
                .setHeight("257.250")
                .setInstrumentOrReflectorHeight("0.100")
                .build();

        assertEquals(referenceLineStructure.getStakedPoint(), stakedPoint);
    }

    @Test
    void getStakeoutDifference() {
        final RyPoint stakeoutDifference = new RyPoint.Builder("Stakeout Difference")
                .setEasting("-0.016")
                .setNorthing("-0.012")
                .setHeight("0.570")
                .build();

        assertEquals(referenceLineStructure.getStakeoutDifference(), stakeoutDifference);
    }

    @Test
    void getStartPoint() {
        final RyPoint startPoint = new RyPoint.Builder("72-17")
                .setEasting("33.400")
                .setNorthing("9.000")
                .setHeight("257.820")
                .build();

        assertEquals(referenceLineStructure.getStartPoint(), startPoint);
    }

    @Test
    void getTpsStation() {
        final RyPoint tpsStation = new RyPoint.Builder("FS01")
                .setEasting("34.204")
                .setNorthing("-5.348")
                .setHeight("259.397")
                .setInstrumentOrReflectorHeight("0.000")
                .build();

        assertEquals(referenceLineStructure.getTpsStation(), tpsStation);
    }

    @BeforeEach
    void setUp() {
        // prepare path
        final Path pathReferenceLine = Paths.get("src/test/resources/analyzer/REFERENCE-LINE_logfile.txt");

        referenceLineStructure = initLogfile(pathReferenceLine);
    }

    @Test
    void setUpComplete() {
        assertNotNull(referenceLineStructure);
    }

    private ReferenceLineStructure initLogfile(Path logfilePath) {
        // read logfile.txt
        LineReader lineReader = new LineReader(logfilePath);
        lineReader.readFile(false);
        List<String> lines = lineReader.getLines();

        // clean up logfile.txt
        LogfileClearUp logfileClearUp = new LogfileClearUp(lines);
        List<String> cleanLogfile = logfileClearUp.process(true);

        // initialize SetupStructure
        ReferenceLineStructure referenceLineStructure = new ReferenceLineStructure(cleanLogfile);
        boolean isCogoComplete = referenceLineStructure.analyze();

        if (isCogoComplete) {
            return referenceLineStructure;
        } else {
            return null;
        }
    }

}
