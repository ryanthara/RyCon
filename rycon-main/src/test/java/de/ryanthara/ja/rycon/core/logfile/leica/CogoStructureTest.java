package de.ryanthara.ja.rycon.core.logfile.leica;

import de.ryanthara.ja.rycon.core.LogfileClean;
import de.ryanthara.ja.rycon.core.elements.RyIntersection;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.core.elements.RyTraverse;
import de.ryanthara.ja.rycon.nio.LineReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CogoStructureTest {

    private CogoStructure cogoStructureArcCenter;
    private CogoStructure cogoStructureArcOffsetPoint;
    private CogoStructure cogoStructureArcSegmentationInfo;
    private CogoStructure cogoStructureIntersectionBearingBearing;
    private CogoStructure cogoStructureIntersectionBearingDistance;
    private CogoStructure cogoStructureIntersectionFourPoints;
    private CogoStructure cogoStructureIntersectionDistanceDistance;
    private CogoStructure cogoStructureInverse;
    private CogoStructure cogoStructureLineBasePoint;
    private CogoStructure cogoStructureLineOffsetPoint;
    private CogoStructure cogoStructureLineSegmentationInfo;
    private CogoStructure cogoStructureShiftRotateScale;
    private CogoStructure cogoStructureTraverse;

    @Test
    void getArcCenter() {
        RyPoint arcCenter = new RyPoint("204", "612270.803", "268455.801", "262.756");

        assertTrue(arcCenter.equals(cogoStructureArcCenter.getArcCenter()));
    }

    @Test
    void getComputed() {
        RyPoint computed = new RyPoint("T1", "629441.106", "268653.794", "297.850");

        assertTrue(cogoStructureArcOffsetPoint.getComputed().equals(computed));
    }

    @Test
    void getComputedBasePoint() {
        final RyPoint offsetPoint = new RyPoint("2.4", "612270.795", "268455.796", "262.757");
        final RyPoint computed = new RyPoint("2.5", "612270.796", "268455.794", "255.970");

        final String distAlongLine = "-0.003";
        final String offsetFromLine = "0.002";

        assertTrue(offsetPoint.equals(cogoStructureLineBasePoint.getOffsetPoint()));
        assertTrue(computed.equals(cogoStructureLineBasePoint.getComputed()));

        assertEquals(distAlongLine, cogoStructureLineBasePoint.getDistAlongLine());
        assertEquals(offsetFromLine, cogoStructureLineBasePoint.getOffsetFromLine());
    }

    @Test
    void getComputedIntersectionBearingBearing() {
        final RyPoint id1 = new RyPoint("E6", "7.844", "-9.000", "-----");
        final RyPoint id2 = new RyPoint("D5", "0.344", "-16.500", "-----");
        final RyPoint id3 = new RyPoint("D2", "-20.926", "-16.500", "-----");
        final RyPoint id4 = new RyPoint("D5", "0.344", "-16.500", "-----");
        final RyPoint id5 = new RyPoint("C1", "-29.926", "-24.000", "-----");
        final RyPoint id6 = new RyPoint("D5", "0.344", "-16.500", "-----");

        final RyPoint computed1 = new RyPoint("1", "7.844", "-9.000", "-----");
        final RyPoint computed2 = new RyPoint("8", "-10.291", "-5.865", "-----");
        final RyPoint computed3 = new RyPoint("9", "-29.926", "-16.500", "-----");

        final String direction1 = "120.0000";
        final String direction2 = "50.0000";
        final String direction3 = "50.0000";
        final String direction4 = "350.0000";
        final String direction5 = "0.0000";
        final String direction6 = "300.0000";

        final ArrayList<RyPoint> computedPoints = cogoStructureIntersectionBearingBearing.getComputedPoints();
        final ArrayList<RyIntersection> intersections = cogoStructureIntersectionBearingBearing.getIntersections();

        assertEquals(3, intersections.size());
        assertTrue(id1.equals(intersections.get(0).getId1()));
        assertTrue(id2.equals(intersections.get(0).getId2()));
        assertTrue(id3.equals(intersections.get(1).getId1()));
        assertTrue(id4.equals(intersections.get(1).getId2()));
        assertTrue(id5.equals(intersections.get(2).getId1()));
        assertTrue(id6.equals(intersections.get(2).getId2()));

        assertEquals(3, computedPoints.size());
        assertTrue(computed1.equals(computedPoints.get(0)));
        assertTrue(computed2.equals(computedPoints.get(1)));
        assertTrue(computed3.equals(computedPoints.get(2)));


        assertEquals(direction1, intersections.get(0).getDirection1());
        assertEquals(direction2, intersections.get(0).getDirection2());
        assertEquals(direction3, intersections.get(1).getDirection1());
        assertEquals(direction4, intersections.get(1).getDirection2());
        assertEquals(direction5, intersections.get(2).getDirection1());
        assertEquals(direction6, intersections.get(2).getDirection2());
    }

    @Test
    void getComputedIntersectionBearingDistance() {
        final RyPoint id1 = new RyPoint("E4", "-10.156", "-9.000", "-----");
        final RyPoint id2 = new RyPoint("D6", "7.844", "-16.500", "-----");
        final RyPoint id3 = new RyPoint("E4", "-10.156", "-9.000", "-----");
        final RyPoint id4 = new RyPoint("D6", "7.844", "-16.500", "-----");
        final RyPoint id5 = new RyPoint("F1", "-29.926", "0.000", "-----");
        final RyPoint id6 = new RyPoint("E4", "-10.156", "-9.000", "-----");
        final RyPoint id7 = new RyPoint("F1", "-29.926", "0.000", "-----");
        final RyPoint id8 = new RyPoint("E4", "-10.156", "-9.000", "-----");

        final RyPoint computed10   = new RyPoint("10", "-2.143", "-17.013", "-----");
        final RyPoint computed10_1 = new RyPoint("10.1", "7.331", "-26.487", "-----");
        final RyPoint computed11   = new RyPoint("11", "-0.178", "-9.666", "-----");
        final RyPoint computed11_1 = new RyPoint("11.1", "-18.620", "-3.674", "-----");

        final String direction1 = "150.0000";
        final String distance1 = "10.000";
        final String direction2 = "150.0000";
        final String distance2 = "10.000";
        final String direction3 = "120.0000";
        final String distance3 = "10.000";
        final String direction4 = "120.0000";
        final String distance4 = "10.000";

        final ArrayList<RyPoint> computedPoints = cogoStructureIntersectionBearingDistance.getComputedPoints();
        final ArrayList<RyIntersection> intersections = cogoStructureIntersectionBearingDistance.getIntersections();

        assertEquals(4, intersections.size());
        assertTrue(id1.equals(intersections.get(0).getId1()));
        assertTrue(id2.equals(intersections.get(0).getId2()));
        assertTrue(id3.equals(intersections.get(1).getId1()));
        assertTrue(id4.equals(intersections.get(1).getId2()));
        assertTrue(id5.equals(intersections.get(2).getId1()));
        assertTrue(id6.equals(intersections.get(2).getId2()));
        assertTrue(id7.equals(intersections.get(3).getId1()));
        assertTrue(id8.equals(intersections.get(3).getId2()));

        assertEquals(4, computedPoints.size());
        assertTrue(computed10.equals(computedPoints.get(0)));
        assertTrue(computed10_1.equals(computedPoints.get(1)));
        assertTrue(computed11.equals(computedPoints.get(2)));
        assertTrue(computed11_1.equals(computedPoints.get(3)));

        assertEquals(direction1, intersections.get(0).getDirection1());
        assertEquals(direction2, intersections.get(1).getDirection1());
        assertEquals(direction3, intersections.get(2).getDirection1());
        assertEquals(direction4, intersections.get(3).getDirection1());
        assertEquals(distance1, intersections.get(0).getDirection2());
        assertEquals(distance2, intersections.get(1).getDirection2());
        assertEquals(distance3, intersections.get(2).getDirection2());
        assertEquals(distance4, intersections.get(3).getDirection2());
    }

    @Test
    void getComputedLineOffsetPoint() {
        final RyPoint startPoint = new RyPoint("2", "909.008", "2721.609", "287.692");
        final RyPoint endPoint = new RyPoint("3", "908.002", "2722.769", "287.692");
        final RyPoint computed = new RyPoint("100", "908.505", "2722.189", "287.692");

        final String length = "1.535";
        final String azimuth = "354.515";

        final String distAlongLine = "0.767";
        final String offsetFromLine = "0.000";

        assertTrue(startPoint.equals(cogoStructureLineOffsetPoint.getStartPoint()));
        assertTrue(endPoint.equals(cogoStructureLineOffsetPoint.getEndPoint()));
        assertTrue(computed.equals(cogoStructureLineOffsetPoint.getComputed()));

        assertEquals(length, cogoStructureLineOffsetPoint.getLength());
        assertEquals(azimuth, cogoStructureLineOffsetPoint.getAzimuth());
        assertEquals(distAlongLine, cogoStructureLineOffsetPoint.getDistAlongLine());
        assertEquals(offsetFromLine, cogoStructureLineOffsetPoint.getOffsetFromLine());
    }

    @Test
    void getComputedPointsForAnalyzeShiftRotateScale() {
        final RyPoint shift = new RyPoint("Shift", "-5400000.000", "0.000", "0.000");
        final String rotate = "0.0000g";
        final String scale = "1.00000000";
        final String ofPointsNew = "16";
        final String ofPointsSkipped = "0";

        assertTrue(shift.equals(cogoStructureShiftRotateScale.getShift()));
        assertEquals(rotate, cogoStructureShiftRotateScale.getRotation());
        assertEquals(scale, cogoStructureShiftRotateScale.getScale());
        assertEquals(ofPointsNew, cogoStructureShiftRotateScale.getNumberOfPointsNew());
        assertEquals(ofPointsSkipped, cogoStructureShiftRotateScale.getNumberOfPointsSkipped());

        RyPoint computed_1 = new RyPoint("1", "621049.650", "263217.922", "-----");
        RyPoint computed_2 = new RyPoint("2", "621052.123", "263216.224", "-----");
        RyPoint computed_3 = new RyPoint("3", "621090.214", "263198.951", "-----");
        RyPoint computed_4 = new RyPoint("4", "621087.340", "263199.811", "-----");
        RyPoint computed_5 = new RyPoint("5", "621084.480", "263200.715", "-----");
        RyPoint computed_6 = new RyPoint("6", "621081.635", "263201.668", "-----");
        RyPoint computed_7 = new RyPoint("7", "621078.809", "263202.675", "-----");
        RyPoint computed_8 = new RyPoint("8", "621076.005", "263203.740", "-----");
        RyPoint computed_9 = new RyPoint("9", "621073.225", "263204.869", "-----");
        RyPoint computed_10 = new RyPoint("10", "621070.473", "263206.064", "-----");
        RyPoint computed_11 = new RyPoint("11", "621067.751", "263207.324", "-----");
        RyPoint computed_12 = new RyPoint("12", "621065.060", "263208.650", "-----");
        RyPoint computed_13 = new RyPoint("13", "621062.401", "263210.040", "-----");
        RyPoint computed_14 = new RyPoint("14", "621059.777", "263211.493", "-----");
        RyPoint computed_15 = new RyPoint("15", "621057.188", "263213.008", "-----");
        RyPoint computed_16 = new RyPoint("16", "621054.636", "263214.586", "-----");

        ArrayList<RyPoint> transformedPoints = cogoStructureShiftRotateScale.getComputedPoints();

        assertEquals(16, transformedPoints.size());

        assertTrue(computed_1.equals(transformedPoints.get(0)));
        assertTrue(computed_10.equals(transformedPoints.get(1)));
        assertTrue(computed_11.equals(transformedPoints.get(2)));
        assertTrue(computed_12.equals(transformedPoints.get(3)));
        assertTrue(computed_13.equals(transformedPoints.get(4)));
        assertTrue(computed_14.equals(transformedPoints.get(5)));
        assertTrue(computed_15.equals(transformedPoints.get(6)));
        assertTrue(computed_16.equals(transformedPoints.get(7)));
        assertTrue(computed_2.equals(transformedPoints.get(8)));
        assertTrue(computed_3.equals(transformedPoints.get(9)));
        assertTrue(computed_4.equals(transformedPoints.get(10)));
        assertTrue(computed_5.equals(transformedPoints.get(11)));
        assertTrue(computed_6.equals(transformedPoints.get(12)));
        assertTrue(computed_7.equals(transformedPoints.get(13)));
        assertTrue(computed_8.equals(transformedPoints.get(14)));
        assertTrue(computed_9.equals(transformedPoints.get(15)));
    }

    @Test
    void getComputedPointsForArcSegmentation() {
        RyPoint p1 = new RyPoint("R MH1", "629490.733", "268733.806", "297.850");
        RyPoint p2 = new RyPoint("R MH2", "629490.732", "268733.807", "297.851");
        RyPoint p3 = new RyPoint("R MH3", "629490.731", "268733.808", "297.852");
        RyPoint p4 = new RyPoint("R MH4", "629490.730", "268733.809", "297.853");

        ArrayList<RyPoint> computedPoints = cogoStructureArcSegmentationInfo.getComputedPoints();

        assertEquals(4, computedPoints.size());

        assertTrue(p1.equals(cogoStructureArcSegmentationInfo.getComputedPoints().get(0)));
        assertTrue(p2.equals(cogoStructureArcSegmentationInfo.getComputedPoints().get(1)));
        assertTrue(p3.equals(cogoStructureArcSegmentationInfo.getComputedPoints().get(2)));
        assertTrue(p4.equals(cogoStructureArcSegmentationInfo.getComputedPoints().get(3)));
    }

    @Test
    void getComputedPointsForIntersectionFourPoints() {
        RyPoint id01 = new RyPoint("5E", "6250.933", "2176.430", "-----");
        RyPoint id02 = new RyPoint("3E", "6246.990", "2176.430", "-----");
        RyPoint id03 = new RyPoint("6E", "6249.933", "2177.430", "-----");
        RyPoint id04 = new RyPoint("8E", "6249.933", "2173.487", "-----");

        RyPoint id11 = new RyPoint("9E", "6250.933", "2174.487", "-----");
        RyPoint id12 = new RyPoint("11E", "6246.990", "2174.487", "-----");
        RyPoint id13 = new RyPoint("6E", "6249.933", "2177.430", "-----");
        RyPoint id14 = new RyPoint("8E", "6249.933", "2173.487", "-----");

        RyPoint computed1 = new RyPoint("13E", "6249.933", "2176.430", "-----");
        RyPoint computed2 = new RyPoint("14E", "6249.933", "2174.487", "-----");

        ArrayList<RyPoint> computedPoints = cogoStructureIntersectionFourPoints.getComputedPoints();
        ArrayList<RyIntersection> intersections = cogoStructureIntersectionFourPoints.getIntersections();

        assertEquals(2, computedPoints.size());
        assertEquals(2, intersections.size());

        assertTrue(id01.equals(intersections.get(0).getId1()));
        assertTrue(id02.equals(intersections.get(0).getId2()));
        assertTrue(id03.equals(intersections.get(0).getId3()));
        assertTrue(id04.equals(intersections.get(0).getId4()));

        assertTrue(id11.equals(intersections.get(1).getId1()));
        assertTrue(id12.equals(intersections.get(1).getId2()));
        assertTrue(id13.equals(intersections.get(1).getId3()));
        assertTrue(id14.equals(intersections.get(1).getId4()));

        assertTrue(computed1.equals(cogoStructureIntersectionFourPoints.getComputedPoints().get(0)));
        assertTrue(computed2.equals(cogoStructureIntersectionFourPoints.getComputedPoints().get(1)));
    }

    @Test
    void getComputedPointsForInverse() {
        final RyPoint from = new RyPoint("1", "6243.086", "2200.414", "244.940");
        final RyPoint to = new RyPoint("2", "6213.109", "2202.264", "244.940");

        final String direction = "303.9223";
        final String distance = "30.034";
        final String heightDifference = "0.001";

        assertEquals(direction, cogoStructureInverse.getDirection());
        assertEquals(distance, cogoStructureInverse.getDistance());
        assertEquals(heightDifference, cogoStructureInverse.getHeightDifference());

        assertTrue(from.equals(cogoStructureInverse.getStartPoint()));
        assertTrue(to.equals(cogoStructureInverse.getEndPoint()));
    }

    @Test
    void getComputedPointsForLineDistanceDistanceIntersection() {
        final String distance1 = "325.000";
        final String distance2 = "325.000";

        assertEquals(distance1, cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getDistance1());
        assertEquals(distance2, cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getDistance2());

        RyPoint start1 = new RyPoint("318", "2613619.086", "1260823.689", "325.528");
        RyPoint end1 = new RyPoint("310", "2613619.086", "1260863.565", "323.530");
        RyPoint computed1 = new RyPoint("R1", "2613943.584", "1260845.514", "325.528");

        RyPoint start2 = new RyPoint("318", "2613619.086", "1260823.689", "325.528");
        RyPoint end2 = new RyPoint("310", "2613619.086", "1260863.565", "323.530");
        RyPoint computed2 = new RyPoint("R2", "2613294.820", "1260841.740", "325.528");

        ArrayList<RyPoint> computedPoints = cogoStructureIntersectionDistanceDistance.getComputedPoints();
        ArrayList<RyIntersection> intersections = cogoStructureIntersectionDistanceDistance.getIntersections();

        assertEquals(2, computedPoints.size());
        assertEquals(2, intersections.size());

        assertTrue(start1.equals(cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getStart()));
        assertTrue(end1.equals(cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getEnd()));
        assertTrue(computed1.equals(cogoStructureIntersectionDistanceDistance.getComputedPoints().get(0)));

        assertTrue(start2.equals(cogoStructureIntersectionDistanceDistance.getIntersections().get(1).getStart()));
        assertTrue(end2.equals(cogoStructureIntersectionDistanceDistance.getIntersections().get(1).getEnd()));
        assertTrue(computed2.equals(cogoStructureIntersectionDistanceDistance.getComputedPoints().get(1)));
    }

    @Test
    void getComputedPointsForLineSegmentation() {
        RyPoint start = new RyPoint("2", "617187.520", "264445.281", "284.317");
        RyPoint end = new RyPoint("1", "617192.135", "264452.911", "280.218");

        assertTrue(start.equals(cogoStructureLineSegmentationInfo.getStartPoint()));
        assertTrue(end.equals(cogoStructureLineSegmentationInfo.getEndPoint()));

        final String length = "8.917";
        final String azimuth = "34.627";

        assertEquals(length, cogoStructureLineSegmentationInfo.getLength());
        assertEquals(azimuth, cogoStructureLineSegmentationInfo.getAzimuth());

        final String numberOfSegments = "6";
        final String numberOfPoints = "5";
        final String segmentsLength = "1.486";
        final String method = "Anz. Segmente";

        assertEquals(numberOfSegments, cogoStructureLineSegmentationInfo.getNumberOfSegments());
        assertEquals(numberOfPoints, cogoStructureLineSegmentationInfo.getNumberOfPoints());
        assertEquals(segmentsLength, cogoStructureLineSegmentationInfo.getSegmentsLength());
        assertEquals(method, cogoStructureLineSegmentationInfo.getSegmentationMethod());


        RyPoint r1 = new RyPoint("100", "617188.290", "264446.552", "283.633");
        RyPoint r2 = new RyPoint("101", "617189.059", "264447.824", "282.950");
        RyPoint r3 = new RyPoint("102", "617189.828", "264449.096", "282.267");
        RyPoint r4 = new RyPoint("103", "617190.597", "264450.367", "281.584");
        RyPoint r5 = new RyPoint("104", "617191.366", "264451.639", "280.901");

        ArrayList<RyPoint> computedPoints = cogoStructureLineSegmentationInfo.getComputedPoints();

        assertEquals(5, computedPoints.size());

        assertTrue(r1.equals(cogoStructureLineSegmentationInfo.getComputedPoints().get(0)));
        assertTrue(r2.equals(cogoStructureLineSegmentationInfo.getComputedPoints().get(1)));
        assertTrue(r3.equals(cogoStructureLineSegmentationInfo.getComputedPoints().get(2)));
        assertTrue(r4.equals(cogoStructureLineSegmentationInfo.getComputedPoints().get(3)));
        assertTrue(r5.equals(cogoStructureLineSegmentationInfo.getComputedPoints().get(4)));
    }

    @Test
    void getComputedTraverse() {
        final RyPoint fromPoint1 = new RyPoint("F4", "-10.156", "0.000", "-----");
        final String direction1 = "365.0000";
        final String distance1 = "0.000";
        final RyPoint toPoint1 = new RyPoint("7", "-10.156", "0.000", "-----");

        final RyPoint fromPoint2 = new RyPoint("D6", "7.844", "-16.500", "-----");
        final String direction2 = "272.7662";
        final String distance2 = "2.000";
        final RyPoint toPoint2 = new RyPoint("5", "4.157", "-13.235", "-----");

        final RyPoint fromPoint7 = new RyPoint("H4", "-10.156", "0.000", "-----");
        final String direction7 = "365.0000";
        final String distance7 = "0.000";
        final RyPoint toPoint7 = new RyPoint("7", "-10.156", "0.000", "-----");

        ArrayList<RyTraverse> traverses = cogoStructureTraverse.getTraverses();

        assertEquals(7, traverses.size());

        assertTrue(fromPoint1.equals(traverses.get(0).getFrom()));
        assertEquals(direction1, traverses.get(0).getDirection());
        assertEquals(distance1, traverses.get(0).getDistance());
        assertTrue(toPoint1.equals(traverses.get(0).getTo()));

        assertTrue(fromPoint2.equals(traverses.get(4).getFrom()));
        assertEquals(direction2, traverses.get(4).getDirection());
        assertEquals(distance2, traverses.get(4).getDistance());
        assertTrue(toPoint2.equals(traverses.get(4).getTo()));

        assertTrue(fromPoint7.equals(traverses.get(6).getFrom()));
        assertEquals(direction7, traverses.get(6).getDirection());
        assertEquals(distance7, traverses.get(6).getDistance());
        assertTrue(toPoint7.equals(traverses.get(6).getTo()));
    }

    @Test
    void getDistAlongArc() {
        final String distAlongArc = "3.900";

        assertEquals(cogoStructureArcOffsetPoint.getDistAlongArc(), distAlongArc);
    }

    @Test
    void getNumberOfPoints() {
        assertEquals("1", cogoStructureArcSegmentationInfo.getNumberOfPoints());
    }

    @Test
    void getNumberOfSegments() {
        assertEquals("4", cogoStructureArcSegmentationInfo.getNumberOfSegments());
    }

    @Test
    void getOffsetFromArc() {
        final String offsetFromArc = "";

        assertEquals(cogoStructureArcOffsetPoint.getOffsetFromArc(), offsetFromArc);
    }

    @Test
    void getSegmentationMethod() {
        assertEquals("Anz. Segmente", cogoStructureArcSegmentationInfo.getSegmentationMethod());
        assertEquals("Anz. Segmente", cogoStructureLineSegmentationInfo.getSegmentationMethod());
    }

    @Test
    void getSegmentsLength() {
        assertEquals("3.884", cogoStructureArcSegmentationInfo.getSegmentsLength());
        assertEquals("1.486", cogoStructureLineSegmentationInfo.getSegmentsLength());
    }

    @BeforeEach
    void setUp() {
        // prepare paths
        final Path pathArcCenter = Paths.get("src/test/resources/analyzer/COGO_Arc_Center_logfile.txt");
        final Path pathArcOffsetPoint = Paths.get("src/test/resources/analyzer/COGO_Arc_Offset-Point_logfile.txt");
        final Path pathArcSegmentation = Paths.get("src/test/resources/analyzer/COGO_Arc_Segmentation_logfile.txt");
        final Path pathIntersectionBearingBearing = Paths.get("src/test/resources/analyzer/COGO_Intersection_Bearing-Bearing_logfile.txt");
        final Path pathIntersectionBearingDistance = Paths.get("src/test/resources/analyzer/COGO_Intersection_Bearing-Distance_logfile.txt");
        final Path pathIntersectionDistanceDistance = Paths.get("src/test/resources/analyzer/COGO_Intersection_Distance-Distance_logfile.txt");
        final Path pathIntersectionFourPoints = Paths.get("src/test/resources/analyzer/COGO_Intersection_Four-Points_logfile.txt");
        final Path pathInverse = Paths.get("src/test/resources/analyzer/COGO_Inverse_logfile.txt");
        final Path pathLineBasePoint = Paths.get("src/test/resources/analyzer/COGO_Line_Base-Point_logfile.txt");
        final Path pathLineOffsetPoint = Paths.get("src/test/resources/analyzer/COGO_Line_Offset-Point_logfile.txt");
        final Path pathLineSegmentation = Paths.get("src/test/resources/analyzer/COGO_Line_Segmentation_logfile.txt");
        final Path pathShiftRotateScale = Paths.get("src/test/resources/analyzer/COGO_Shift-Rotate-Scale_logfile.txt");
        final Path pathTraverse = Paths.get("src/test/resources/analyzer/COGO_Traverse_logfile.txt");

        cogoStructureArcCenter = initLogfile(pathArcCenter);
        cogoStructureArcOffsetPoint = initLogfile(pathArcOffsetPoint);
        cogoStructureArcSegmentationInfo = initLogfile(pathArcSegmentation);
        cogoStructureIntersectionBearingBearing = initLogfile(pathIntersectionBearingBearing);
        cogoStructureIntersectionBearingDistance = initLogfile(pathIntersectionBearingDistance);
        cogoStructureIntersectionDistanceDistance = initLogfile(pathIntersectionDistanceDistance);
        cogoStructureIntersectionFourPoints = initLogfile(pathIntersectionFourPoints);
        cogoStructureInverse = initLogfile(pathInverse);
        cogoStructureLineBasePoint = initLogfile(pathLineBasePoint);
        cogoStructureLineOffsetPoint = initLogfile(pathLineOffsetPoint);
        cogoStructureLineSegmentationInfo = initLogfile(pathLineSegmentation);
        cogoStructureShiftRotateScale = initLogfile(pathShiftRotateScale);
        cogoStructureTraverse = initLogfile(pathTraverse);
    }

    @Test
    void setUpComplete() {
        assertTrue(
                cogoStructureArcCenter != null
                        && cogoStructureArcOffsetPoint != null
                        && cogoStructureArcSegmentationInfo != null
                        && cogoStructureIntersectionBearingBearing != null
                        && cogoStructureIntersectionBearingDistance != null
                        && cogoStructureIntersectionDistanceDistance != null
                        && cogoStructureIntersectionFourPoints != null
                        && cogoStructureInverse != null
                        && cogoStructureLineBasePoint != null
                        && cogoStructureLineOffsetPoint != null
                        && cogoStructureLineSegmentationInfo != null
                        && cogoStructureShiftRotateScale != null
                        && cogoStructureTraverse != null
        );
    }

    private CogoStructure initLogfile(Path logfilePath) {
        // read logfile.txt
        LineReader lineReader = new LineReader(logfilePath);
        lineReader.readFile(false);
        ArrayList<String> lines = lineReader.getLines();

        /*

        for (String s : lines) {
            System.out.println("+++ + " + s);
        }

        */

        // use full cleaned logfile.txt
        LogfileClean logfileClean = new LogfileClean(lines);
        ArrayList<String> cleanLogfile = logfileClean.processCleanFull();

        /*
        for (String s : cleanLogfile) {
            System.out.println("### # " + s);
        }
        */

        // initialize SetupStructure
        CogoStructure cogoStructure = new CogoStructure(cleanLogfile);
        boolean isCogoComplete = cogoStructure.analyze();

        if (isCogoComplete) {
            return cogoStructure;
        } else {
            return null;
        }
    }

} // end of CogoStructureTest