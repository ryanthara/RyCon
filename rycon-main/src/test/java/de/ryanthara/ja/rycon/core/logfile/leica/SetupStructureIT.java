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

class SetupStructureIT {

    private SetupStructure setupStructureKnownAzimuth;
    private SetupStructure setupStructureKnownBacksightPoint;
    private SetupStructure setupStructureOrientationAndHeightTransfer;
    private SetupStructure setupStructureResection;
    private SetupStructure setupStructureResectionHelmert;
    private SetupStructure setupStructureResectionLocal;

    @Test
    void getObservation() {
        assertEquals("1", setupStructureKnownAzimuth.getObservation().getPointId());
        assertEquals("0.0000", setupStructureKnownAzimuth.getObservation().getHz());
        assertEquals("45.0781", setupStructureKnownAzimuth.getObservation().getV());
        assertEquals("-----", setupStructureKnownAzimuth.getObservation().getSd());
        assertEquals("0.000", setupStructureKnownAzimuth.getObservation().getHr());
        assertEquals("0.0000", setupStructureKnownAzimuth.getObservation().getReflectorConstant());

        assertEquals("5001", setupStructureKnownBacksightPoint.getObservation().getPointId());
        assertEquals("360.3008", setupStructureKnownBacksightPoint.getObservation().getHz());
        assertEquals("101.1693", setupStructureKnownBacksightPoint.getObservation().getV());
        assertEquals("70.928", setupStructureKnownBacksightPoint.getObservation().getSd());
        assertEquals("0.000", setupStructureKnownBacksightPoint.getObservation().getHr());
        assertEquals("0.0000", setupStructureKnownBacksightPoint.getObservation().getReflectorConstant());
    }

    @Test
    void getObservationsList() {
        assertEquals(2, setupStructureOrientationAndHeightTransfer.getObservationsList().size());

        assertEquals("3.02", setupStructureOrientationAndHeightTransfer.getObservationsList().get(0).getPointId());
        assertEquals("183.4384", setupStructureOrientationAndHeightTransfer.getObservationsList().get(0).getHz());
        assertEquals("71.5158", setupStructureOrientationAndHeightTransfer.getObservationsList().get(0).getV());
        assertEquals("3.178", setupStructureOrientationAndHeightTransfer.getObservationsList().get(0).getSd());
        assertEquals("0.000", setupStructureOrientationAndHeightTransfer.getObservationsList().get(0).getHr());
        assertEquals("0.0344", setupStructureOrientationAndHeightTransfer.getObservationsList().get(0).getReflectorConstant());

        assertEquals("3.02", setupStructureOrientationAndHeightTransfer.getObservationsList().get(1).getPointId());
        assertEquals("54.8656", setupStructureOrientationAndHeightTransfer.getObservationsList().get(1).getHz());
        assertEquals("75.6636", setupStructureOrientationAndHeightTransfer.getObservationsList().get(1).getV());
        assertEquals("3.692", setupStructureOrientationAndHeightTransfer.getObservationsList().get(1).getSd());
        assertEquals("0.000", setupStructureOrientationAndHeightTransfer.getObservationsList().get(1).getHr());
        assertEquals("0.0344", setupStructureOrientationAndHeightTransfer.getObservationsList().get(1).getReflectorConstant());

        assertEquals(4, setupStructureResection.getObservationsList().size());

        assertEquals("9001", setupStructureResection.getObservationsList().get(0).getPointId());
        assertEquals("316.2384", setupStructureResection.getObservationsList().get(0).getHz());
        assertEquals("101.8207", setupStructureResection.getObservationsList().get(0).getV());
        assertEquals("10.840", setupStructureResection.getObservationsList().get(0).getSd());
        assertEquals("0.000", setupStructureResection.getObservationsList().get(0).getHr());
        assertEquals("0.0344", setupStructureResection.getObservationsList().get(0).getReflectorConstant());

        assertEquals("9002", setupStructureResection.getObservationsList().get(1).getPointId());
        assertEquals("338.2548", setupStructureResection.getObservationsList().get(1).getHz());
        assertEquals("101.4151", setupStructureResection.getObservationsList().get(1).getV());
        assertEquals("11.144", setupStructureResection.getObservationsList().get(1).getSd());
        assertEquals("0.000", setupStructureResection.getObservationsList().get(1).getHr());
        assertEquals("0.0344", setupStructureResection.getObservationsList().get(1).getReflectorConstant());

        assertEquals("9003", setupStructureResection.getObservationsList().get(2).getPointId());
        assertEquals("122.1084", setupStructureResection.getObservationsList().get(2).getHz());
        assertEquals("99.9900", setupStructureResection.getObservationsList().get(2).getV());
        assertEquals("49.900", setupStructureResection.getObservationsList().get(2).getSd());
        assertEquals("0.000", setupStructureResection.getObservationsList().get(2).getHr());
        assertEquals("0.0344", setupStructureResection.getObservationsList().get(2).getReflectorConstant());

        assertEquals("9004", setupStructureResection.getObservationsList().get(3).getPointId());
        assertEquals("124.4605", setupStructureResection.getObservationsList().get(3).getHz());
        assertEquals("99.0518", setupStructureResection.getObservationsList().get(3).getV());
        assertEquals("38.234", setupStructureResection.getObservationsList().get(3).getSd());
        assertEquals("0.000", setupStructureResection.getObservationsList().get(3).getHr());
        assertEquals("0.0344", setupStructureResection.getObservationsList().get(3).getReflectorConstant());

        assertEquals(8, setupStructureResectionHelmert.getObservationsList().size());

        assertEquals("2042", setupStructureResectionHelmert.getObservationsList().get(7).getPointId());
        assertEquals("62.5230", setupStructureResectionHelmert.getObservationsList().get(7).getHz());
        assertEquals("95.8357", setupStructureResectionHelmert.getObservationsList().get(7).getV());
        assertEquals("138.896", setupStructureResectionHelmert.getObservationsList().get(7).getSd());
        assertEquals("0.000", setupStructureResectionHelmert.getObservationsList().get(7).getHr());
        assertEquals("0.0344", setupStructureResectionHelmert.getObservationsList().get(7).getReflectorConstant());

        assertEquals(2, setupStructureResectionLocal.getObservationsList().size());

        assertEquals("5013", setupStructureResectionLocal.getObservationsList().get(1).getPointId());
        assertEquals("397.5911", setupStructureResectionLocal.getObservationsList().get(1).getHz());
        assertEquals("96.7183", setupStructureResectionLocal.getObservationsList().get(1).getV());
        assertEquals("7.407", setupStructureResectionLocal.getObservationsList().get(1).getSd());
        assertEquals("0.000", setupStructureResectionLocal.getObservationsList().get(1).getHr());
        assertEquals("0.0344", setupStructureResectionLocal.getObservationsList().get(1).getReflectorConstant());


    }

    @Test
    void getOrientationCorrection() {
        assertEquals("-216.7082", setupStructureKnownAzimuth.getOrientationCorrection());
        assertEquals("12.5231", setupStructureKnownBacksightPoint.getOrientationCorrection());
        assertEquals("0.0000", setupStructureOrientationAndHeightTransfer.getOrientationCorrection());
        assertEquals("2.8074", setupStructureResection.getOrientationCorrection());
        assertEquals("48.4169", setupStructureResectionHelmert.getOrientationCorrection());
        assertEquals("332.6351", setupStructureResectionLocal.getOrientationCorrection());
    }

    @Test
    void getResidualsList() {
        assertEquals("3.02", setupStructureOrientationAndHeightTransfer.getResidualsList().get(0).getPointId());
        assertEquals("-----", setupStructureOrientationAndHeightTransfer.getResidualsList().get(0).getdHz());
        assertEquals("", setupStructureOrientationAndHeightTransfer.getResidualsList().get(0).getdHeight());
        assertEquals("-----", setupStructureOrientationAndHeightTransfer.getResidualsList().get(0).getdHD());
        assertEquals("1D", setupStructureOrientationAndHeightTransfer.getResidualsList().get(0).getUse());

        assertEquals(4, setupStructureResection.getResidualsList().size());

        assertEquals("9001", setupStructureResection.getResidualsList().get(0).getPointId());
        assertEquals("0.0029", setupStructureResection.getResidualsList().get(0).getdHz());
        assertEquals("-0.000", setupStructureResection.getResidualsList().get(0).getdHeight());
        assertEquals("0.000", setupStructureResection.getResidualsList().get(0).getdHD());
        assertEquals("3D", setupStructureResection.getResidualsList().get(0).getUse());

        assertEquals("9002", setupStructureResection.getResidualsList().get(1).getPointId());
        assertEquals("-0.0018", setupStructureResection.getResidualsList().get(1).getdHz());
        assertEquals("-0.000", setupStructureResection.getResidualsList().get(1).getdHeight());
        assertEquals("0.000", setupStructureResection.getResidualsList().get(1).getdHD());
        assertEquals("3D", setupStructureResection.getResidualsList().get(1).getUse());

        assertEquals("9003", setupStructureResection.getResidualsList().get(2).getPointId());
        assertEquals("-0.0011", setupStructureResection.getResidualsList().get(2).getdHz());
        assertEquals("-0.000", setupStructureResection.getResidualsList().get(2).getdHeight());
        assertEquals("-0.000", setupStructureResection.getResidualsList().get(2).getdHD());
        assertEquals("3D", setupStructureResection.getResidualsList().get(2).getUse());

        assertEquals("9004", setupStructureResection.getResidualsList().get(3).getPointId());
        assertEquals("0.0018", setupStructureResection.getResidualsList().get(3).getdHz());
        assertEquals("0.001", setupStructureResection.getResidualsList().get(3).getdHeight());
        assertEquals("0.001", setupStructureResection.getResidualsList().get(3).getdHD());
        assertEquals("3D", setupStructureResection.getResidualsList().get(3).getUse());

        assertEquals(4, setupStructureResectionHelmert.getResidualsList().size());

        assertEquals("2072", setupStructureResectionHelmert.getResidualsList().get(0).getPointId());
        assertEquals("-0.0023", setupStructureResectionHelmert.getResidualsList().get(0).getdHz());
        assertEquals("-0.000", setupStructureResectionHelmert.getResidualsList().get(0).getdHeight());
        assertEquals("-0.005", setupStructureResectionHelmert.getResidualsList().get(0).getdHD());
        assertEquals("3D", setupStructureResectionHelmert.getResidualsList().get(0).getUse());

        assertEquals("2073", setupStructureResectionHelmert.getResidualsList().get(1).getPointId());
        assertEquals("-0.0004", setupStructureResectionHelmert.getResidualsList().get(1).getdHz());
        assertEquals("-0.001", setupStructureResectionHelmert.getResidualsList().get(1).getdHeight());
        assertEquals("0.000", setupStructureResectionHelmert.getResidualsList().get(1).getdHD());
        assertEquals("3D", setupStructureResectionHelmert.getResidualsList().get(1).getUse());

        assertEquals("2074", setupStructureResectionHelmert.getResidualsList().get(2).getPointId());
        assertEquals("-0.0007", setupStructureResectionHelmert.getResidualsList().get(2).getdHz());
        assertEquals("0.001", setupStructureResectionHelmert.getResidualsList().get(2).getdHeight());
        assertEquals("-0.003", setupStructureResectionHelmert.getResidualsList().get(2).getdHD());
        assertEquals("3D", setupStructureResectionHelmert.getResidualsList().get(2).getUse());

        assertEquals("2042", setupStructureResectionHelmert.getResidualsList().get(3).getPointId());
        assertEquals("0.0015", setupStructureResectionHelmert.getResidualsList().get(3).getdHz());
        assertEquals("0.001", setupStructureResectionHelmert.getResidualsList().get(3).getdHeight());
        assertEquals("-0.001", setupStructureResectionHelmert.getResidualsList().get(3).getdHD());
        assertEquals("3D", setupStructureResectionHelmert.getResidualsList().get(3).getUse());

        assertEquals(0, setupStructureResectionLocal.getResidualsList().size());
    }

    @Test
    void getScale() {
        assertNull(setupStructureKnownAzimuth.getScale());
        assertNull(setupStructureKnownBacksightPoint.getScale());
        assertNull(setupStructureOrientationAndHeightTransfer.getScale());
        assertEquals("", setupStructureResection.getScale());
        assertEquals("", setupStructureResectionHelmert.getScale());
        assertNull(setupStructureResectionLocal.getScale());
    }

    @Test
    void getStandardDeviationEasting() {
        assertNull(setupStructureKnownAzimuth.getStandardDeviationEasting());
        assertNull(setupStructureKnownBacksightPoint.getStandardDeviationEasting());
        assertNull(setupStructureOrientationAndHeightTransfer.getStandardDeviationEasting());
        assertEquals("0.002", setupStructureResection.getStandardDeviationEasting());
        assertEquals("0.002", setupStructureResectionHelmert.getStandardDeviationEasting());
        assertNull(setupStructureResectionLocal.getStandardDeviationEasting());
    }

    @Test
    void getStandardDeviationHeight() {
        assertNull(setupStructureKnownAzimuth.getStandardDeviationHeight());
        assertNull(setupStructureKnownBacksightPoint.getStandardDeviationHeight());
        assertEquals("", setupStructureOrientationAndHeightTransfer.getStandardDeviationHeight());
        assertEquals("0.000", setupStructureResection.getStandardDeviationHeight());
        assertEquals("0.001", setupStructureResectionHelmert.getStandardDeviationHeight());
        assertNull(setupStructureResectionLocal.getStandardDeviationHeight());
    }

    @Test
    void getStandardDeviationNorthing() {
        assertNull(setupStructureKnownAzimuth.getStandardDeviationNorthing());
        assertNull(setupStructureKnownBacksightPoint.getStandardDeviationNorthing());
        assertNull(setupStructureOrientationAndHeightTransfer.getStandardDeviationNorthing());
        assertEquals("0.001", setupStructureResection.getStandardDeviationNorthing());
        assertEquals("0.003", setupStructureResectionHelmert.getStandardDeviationNorthing());
        assertNull(setupStructureResectionLocal.getStandardDeviationNorthing());
    }

    @Test
    void getStandardDeviationOrientation() {
        assertNull(setupStructureKnownAzimuth.getStandardDeviationOrientation());
        assertNull(setupStructureKnownBacksightPoint.getStandardDeviationOrientation());
        assertEquals("-----", setupStructureOrientationAndHeightTransfer.getStandardDeviationOrientation());
        assertEquals("0.0008", setupStructureResection.getStandardDeviationOrientation());
        assertEquals("0.0013", setupStructureResectionHelmert.getStandardDeviationOrientation());
        assertNull(setupStructureResectionLocal.getStandardDeviationOrientation());
    }

    @Test
    void getStation() {
        RyPoint stationKnownAzimuth = new RyPoint("ST-WEST", "60.000", "20.000", "15.000");
        RyPoint stationKnownBacksightPoint = new RyPoint("5002", "621906.180", "265244.026", "293.451");
        RyPoint stationOrientationAndHeightTransfer = new RyPoint("FS03", "2610218.287", "1267037.378", "1.643", "0.000");
        RyPoint stationResection = new RyPoint("FS04", "2610184.057", "1267049.603", "1.612", "0.000");
        RyPoint stationResectionHelmert = new RyPoint("FS01", "6919.584", "2080.066", "290.882", "0.000");
        RyPoint stationResectionLocal = new RyPoint("BS1", "0.280", "15.294", "1.426", "0.000");

        assertEquals(setupStructureKnownAzimuth.getStation(), stationKnownAzimuth);
        assertEquals(setupStructureKnownBacksightPoint.getStation(), stationKnownBacksightPoint);
        assertEquals(setupStructureOrientationAndHeightTransfer.getStation(), stationOrientationAndHeightTransfer);
        assertEquals(setupStructureResection.getStation(), stationResection);
        assertEquals(setupStructureResectionHelmert.getStation(), stationResectionHelmert);
        assertEquals(setupStructureResectionLocal.getStation(), stationResectionLocal);
    }

    @Test
    void setUpComplete() {
        assertTrue(
                setupStructureKnownAzimuth != null
                        && setupStructureKnownBacksightPoint != null
                        && setupStructureOrientationAndHeightTransfer != null
                        && setupStructureResection != null
                        && setupStructureResectionHelmert != null
                        && setupStructureResectionLocal != null
        );
    }

    @BeforeEach
    void setUp() {
        // prepare paths
        final Path pathKnownAzimuthLogfile = Paths.get("src/test/resources/analyzer/SETUP_Known_Azimuth_logfile.txt");
        final Path pathKnownBacksightPointLogfile = Paths.get("src/test/resources/analyzer/SETUP_Known_Backsight_Point_logfile.txt");
        final Path pathOrientationAndHeightTransferLogfile = Paths.get("src/test/resources/analyzer/SETUP_Orientation_and_Height_Transfer_logfile.txt");
        final Path pathResection = Paths.get("src/test/resources/analyzer/SETUP_Resection_logfile.txt");
        final Path pathResectionHelmert = Paths.get("src/test/resources/analyzer/SETUP_Resection_Helmert_logfile.txt");
        final Path pathResectionLocal = Paths.get("src/test/resources/analyzer/SETUP_Resection_Local_logfile.txt");

        setupStructureKnownAzimuth = initLogfile(pathKnownAzimuthLogfile);
        setupStructureKnownBacksightPoint = initLogfile(pathKnownBacksightPointLogfile);
        setupStructureOrientationAndHeightTransfer = initLogfile(pathOrientationAndHeightTransferLogfile);
        setupStructureResection = initLogfile(pathResection);
        setupStructureResectionHelmert = initLogfile(pathResectionHelmert);
        setupStructureResectionLocal = initLogfile(pathResectionLocal);
    }

    private SetupStructure initLogfile(Path logfilePath) {
        // read logfile.txt
        LineReader lineReader = new LineReader(logfilePath);
        lineReader.readFile(false);
        List<String> lines = lineReader.getLines();

        // clean up logfile.txt
        LogfileClearUp logfileClearUp = new LogfileClearUp(lines);
        List<String> cleanLogfile = logfileClearUp.process(true);

        // initialize SetupStructure
        SetupStructure setupStructure = new SetupStructure(cleanLogfile);
        boolean setUpComplete = setupStructure.analyze();

        if (setUpComplete) {
            return setupStructure;
        } else {
            return null;
        }
    }

}
