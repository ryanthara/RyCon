package de.ryanthara.ja.rycon.core.logfile.leica;

import de.ryanthara.ja.rycon.core.clearup.LogfileClearUp;
import de.ryanthara.ja.rycon.core.elements.RyIntersection;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.core.elements.RyTraverse;
import de.ryanthara.ja.rycon.nio.LineReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CogoStructureIT {

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
        final RyPoint arcCenter = new RyPoint.Builder("204")
                .setEasting("612270.803")
                .setNorthing("268455.801")
                .setHeight("262.756")
                .build();

        assertEquals(arcCenter, cogoStructureArcCenter.getArcCenter());
    }

    @Test
    void getComputed() {
        final RyPoint computed = new RyPoint.Builder("T1")
                .setEasting("629441.106")
                .setNorthing("268653.794")
                .setHeight("297.850")
                .build();

        assertEquals(cogoStructureArcOffsetPoint.getComputed(), computed);
    }

    @Test
    void getComputedBasePoint() {
        final RyPoint offsetPoint = new RyPoint.Builder("2.4")
                .setEasting("612270.795")
                .setNorthing("268455.796")
                .setHeight("262.757")
                .build();

        assertEquals(offsetPoint, cogoStructureLineBasePoint.getOffsetPoint());

        final RyPoint computed = new RyPoint.Builder("2.5")
                .setEasting("612270.796")
                .setNorthing("268455.794")
                .setHeight("255.970")
                .build();

        assertEquals(computed, cogoStructureLineBasePoint.getComputed());

        final String distAlongLine = "-0.003";

        assertEquals(distAlongLine, cogoStructureLineBasePoint.getDistAlongLine());

        final String offsetFromLine = "0.002";

        assertEquals(offsetFromLine, cogoStructureLineBasePoint.getOffsetFromLine());
    }

    @Test
    void getComputedIntersectionBearingBearing() {
        final RyPoint id1 = new RyPoint.Builder("E6")
                .setEasting("7.844")
                .setNorthing("-9.000")
                .setHeight("-----")
                .build();
        final RyPoint id2 = new RyPoint.Builder("D5")
                .setEasting("0.344")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();
        final RyPoint id3 = new RyPoint.Builder("D2")
                .setEasting("-20.926")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();
        final RyPoint id4 = new RyPoint.Builder("D5")
                .setEasting("0.344")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();
        final RyPoint id5 = new RyPoint.Builder("C1")
                .setEasting("-29.926")
                .setNorthing("-24.000")
                .setHeight("-----")
                .build();
        final RyPoint id6 = new RyPoint.Builder("D5")
                .setEasting("0.344")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();
        final RyPoint computed1 = new RyPoint.Builder("1")
                .setEasting("7.844")
                .setNorthing("-9.000")
                .setHeight("-----")
                .build();
        final RyPoint computed2 = new RyPoint.Builder("8")
                .setEasting("-10.291")
                .setNorthing("-5.865")
                .setHeight("-----")
                .build();
        final RyPoint computed3 = new RyPoint.Builder("9")
                .setEasting("-29.926")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();

        final String direction1 = "120.0000";
        final String direction2 = "50.0000";
        final String direction3 = "50.0000";
        final String direction4 = "350.0000";
        final String direction5 = "0.0000";
        final String direction6 = "300.0000";

        final List<RyPoint> computedPoints = cogoStructureIntersectionBearingBearing.getComputedPoints();
        final List<RyIntersection> intersections = cogoStructureIntersectionBearingBearing.getIntersections();

        assertEquals(3, intersections.size());
        assertEquals(id1, intersections.get(0).getId1());
        assertEquals(id2, intersections.get(0).getId2());
        assertEquals(id3, intersections.get(1).getId1());
        assertEquals(id4, intersections.get(1).getId2());
        assertEquals(id5, intersections.get(2).getId1());
        assertEquals(id6, intersections.get(2).getId2());

        assertEquals(3, computedPoints.size());
        assertEquals(computed1, computedPoints.get(0));
        assertEquals(computed2, computedPoints.get(1));
        assertEquals(computed3, computedPoints.get(2));


        assertEquals(direction1, intersections.get(0).getDirection1());
        assertEquals(direction2, intersections.get(0).getDirection2());
        assertEquals(direction3, intersections.get(1).getDirection1());
        assertEquals(direction4, intersections.get(1).getDirection2());
        assertEquals(direction5, intersections.get(2).getDirection1());
        assertEquals(direction6, intersections.get(2).getDirection2());
    }

    @Test
    void getComputedIntersectionBearingDistance() {
        final RyPoint id1 = new RyPoint.Builder("E4")
                .setEasting("-10.156")
                .setNorthing("-9.000")
                .setHeight("-----")
                .build();
        final RyPoint id2 = new RyPoint.Builder("D6")
                .setEasting("7.844")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();
        final RyPoint id3 = new RyPoint.Builder("E4")
                .setEasting("-10.156")
                .setNorthing("-9.000")
                .setHeight("-----")
                .build();
        final RyPoint id4 = new RyPoint.Builder("D6")
                .setEasting("7.844")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();
        final RyPoint id5 = new RyPoint.Builder("F1")
                .setEasting("-29.926")
                .setNorthing("0.000")
                .setHeight("-----")
                .build();
        final RyPoint id6 = new RyPoint.Builder("E4")
                .setEasting("-10.156")
                .setNorthing("-9.000")
                .setHeight("-----")
                .build();
        final RyPoint id7 = new RyPoint.Builder("F1")
                .setEasting("-29.926")
                .setNorthing("0.000")
                .setHeight("-----")
                .build();
        final RyPoint id8 = new RyPoint.Builder("E4")
                .setEasting("-10.156")
                .setNorthing("-9.000")
                .setHeight("-----")
                .build();
        final RyPoint computed10 = new RyPoint.Builder("10")
                .setEasting("-2.143")
                .setNorthing("-17.013")
                .setHeight("-----")
                .build();
        final RyPoint computed10_1 = new RyPoint.Builder("10.1")
                .setEasting("7.331")
                .setNorthing("-26.487")
                .setHeight("-----")
                .build();
        final RyPoint computed11 = new RyPoint.Builder("11")
                .setEasting("-0.178")
                .setNorthing("-9.666")
                .setHeight("-----")
                .build();
        final RyPoint computed11_1 = new RyPoint.Builder("11.1")
                .setEasting("-18.620")
                .setNorthing("-3.674")
                .setHeight("-----")
                .build();

        final String direction1 = "150.0000";
        final String distance1 = "10.000";
        final String direction2 = "150.0000";
        final String distance2 = "10.000";
        final String direction3 = "120.0000";
        final String distance3 = "10.000";
        final String direction4 = "120.0000";
        final String distance4 = "10.000";

        final List<RyPoint> computedPoints = cogoStructureIntersectionBearingDistance.getComputedPoints();
        final List<RyIntersection> intersections = cogoStructureIntersectionBearingDistance.getIntersections();

        assertEquals(4, intersections.size());
        assertEquals(id1, intersections.get(0).getId1());
        assertEquals(id2, intersections.get(0).getId2());
        assertEquals(id3, intersections.get(1).getId1());
        assertEquals(id4, intersections.get(1).getId2());
        assertEquals(id5, intersections.get(2).getId1());
        assertEquals(id6, intersections.get(2).getId2());
        assertEquals(id7, intersections.get(3).getId1());
        assertEquals(id8, intersections.get(3).getId2());

        assertEquals(4, computedPoints.size());
        assertEquals(computed10, computedPoints.get(0));
        assertEquals(computed10_1, computedPoints.get(1));
        assertEquals(computed11, computedPoints.get(2));
        assertEquals(computed11_1, computedPoints.get(3));

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
        final RyPoint startPoint = new RyPoint.Builder("2")
                .setEasting("909.008")
                .setNorthing("2721.609")
                .setHeight("287.692")
                .build();
        final RyPoint endPoint = new RyPoint.Builder("3")
                .setEasting("908.002")
                .setNorthing("2722.769")
                .setHeight("287.692")
                .build();
        final RyPoint computed = new RyPoint.Builder("100")
                .setEasting("908.505")
                .setNorthing("2722.189")
                .setHeight("287.692")
                .build();

        final String length = "1.535";
        final String azimuth = "354.515";

        final String distAlongLine = "0.767";
        final String offsetFromLine = "0.000";

        assertEquals(startPoint, cogoStructureLineOffsetPoint.getStartPoint());
        assertEquals(endPoint, cogoStructureLineOffsetPoint.getEndPoint());
        assertEquals(computed, cogoStructureLineOffsetPoint.getComputed());

        assertEquals(length, cogoStructureLineOffsetPoint.getLength());
        assertEquals(azimuth, cogoStructureLineOffsetPoint.getAzimuth());
        assertEquals(distAlongLine, cogoStructureLineOffsetPoint.getDistAlongLine());
        assertEquals(offsetFromLine, cogoStructureLineOffsetPoint.getOffsetFromLine());
    }

    @Test
    void getComputedPointsForAnalyzeShiftRotateScale() {
        final RyPoint shift = new RyPoint.Builder("Shift")
                .setEasting("-5400000.000")
                .setNorthing("0.000")
                .setHeight("0.000")
                .build();

        final String rotate = "0.0000g";
        final String scale = "1.00000000";
        final String ofPointsNew = "16";
        final String ofPointsSkipped = "0";

        assertEquals(shift, cogoStructureShiftRotateScale.getShift());
        assertEquals(rotate, cogoStructureShiftRotateScale.getRotation());
        assertEquals(scale, cogoStructureShiftRotateScale.getScale());
        assertEquals(ofPointsNew, cogoStructureShiftRotateScale.getNumberOfPointsNew());
        assertEquals(ofPointsSkipped, cogoStructureShiftRotateScale.getNumberOfPointsSkipped());

        RyPoint computed_1 = new RyPoint.Builder("1")
                .setEasting("621049.650")
                .setNorthing("263217.922")
                .setHeight("-----")
                .build();
        RyPoint computed_2 = new RyPoint.Builder("2")
                .setEasting("621052.123")
                .setNorthing("263216.224")
                .setHeight("-----")
                .build();
        RyPoint computed_3 = new RyPoint.Builder("3")
                .setEasting("621090.214")
                .setNorthing("263198.951")
                .setHeight("-----")
                .build();
        RyPoint computed_4 = new RyPoint.Builder("4")
                .setEasting("621087.340")
                .setNorthing("263199.811")
                .setHeight("-----")
                .build();
        RyPoint computed_5 = new RyPoint.Builder("5")
                .setEasting("621084.480")
                .setNorthing("263200.715")
                .setHeight("-----")
                .build();
        RyPoint computed_6 = new RyPoint.Builder("6")
                .setEasting("621081.635")
                .setNorthing("263201.668")
                .setHeight("-----")
                .build();
        RyPoint computed_7 = new RyPoint.Builder("7")
                .setEasting("621078.809")
                .setNorthing("263202.675")
                .setHeight("-----")
                .build();
        RyPoint computed_8 = new RyPoint.Builder("8")
                .setEasting("621076.005")
                .setNorthing("263203.740")
                .setHeight("-----")
                .build();
        RyPoint computed_9 = new RyPoint.Builder("9")
                .setEasting("621073.225")
                .setNorthing("263204.869")
                .setHeight("-----")
                .build();
        RyPoint computed_10 = new RyPoint.Builder("10")
                .setEasting("621070.473")
                .setNorthing("263206.064")
                .setHeight("-----")
                .build();
        RyPoint computed_11 = new RyPoint.Builder("11")
                .setEasting("621067.751")
                .setNorthing("263207.324")
                .setHeight("-----")
                .build();
        RyPoint computed_12 = new RyPoint.Builder("12")
                .setEasting("621065.060")
                .setNorthing("263208.650")
                .setHeight("-----")
                .build();
        RyPoint computed_13 = new RyPoint.Builder("13")
                .setEasting("621062.401")
                .setNorthing("263210.040")
                .setHeight("-----")
                .build();
        RyPoint computed_14 = new RyPoint.Builder("14")
                .setEasting("621059.777")
                .setNorthing("263211.493")
                .setHeight("-----")
                .build();
        RyPoint computed_15 = new RyPoint.Builder("15")
                .setEasting("621057.188")
                .setNorthing("263213.008")
                .setHeight("-----")
                .build();
        RyPoint computed_16 = new RyPoint.Builder("16")
                .setEasting("621054.636")
                .setNorthing("263214.586")
                .setHeight("-----")
                .build();

        List<RyPoint> transformedPoints = cogoStructureShiftRotateScale.getComputedPoints();

        assertEquals(16, transformedPoints.size());

        assertEquals(computed_1, transformedPoints.get(0));
        assertEquals(computed_10, transformedPoints.get(1));
        assertEquals(computed_11, transformedPoints.get(2));
        assertEquals(computed_12, transformedPoints.get(3));
        assertEquals(computed_13, transformedPoints.get(4));
        assertEquals(computed_14, transformedPoints.get(5));
        assertEquals(computed_15, transformedPoints.get(6));
        assertEquals(computed_16, transformedPoints.get(7));
        assertEquals(computed_2, transformedPoints.get(8));
        assertEquals(computed_3, transformedPoints.get(9));
        assertEquals(computed_4, transformedPoints.get(10));
        assertEquals(computed_5, transformedPoints.get(11));
        assertEquals(computed_6, transformedPoints.get(12));
        assertEquals(computed_7, transformedPoints.get(13));
        assertEquals(computed_8, transformedPoints.get(14));
        assertEquals(computed_9, transformedPoints.get(15));
    }

    @Test
    void getComputedPointsForArcSegmentation() {
        RyPoint p1 = new RyPoint.Builder("R MH1")
                .setEasting("629490.733")
                .setNorthing("268733.806")
                .setHeight("297.850")
                .build();
        RyPoint p2 = new RyPoint.Builder("R MH2")
                .setEasting("629490.732")
                .setNorthing("268733.807")
                .setHeight("297.851")
                .build();
        RyPoint p3 = new RyPoint.Builder("R MH3")
                .setEasting("629490.731")
                .setNorthing("268733.808")
                .setHeight("297.852")
                .build();
        RyPoint p4 = new RyPoint.Builder("R MH4")
                .setEasting("629490.730")
                .setNorthing("268733.809")
                .setHeight("297.853")
                .build();

        List<RyPoint> computedPoints = cogoStructureArcSegmentationInfo.getComputedPoints();

        assertEquals(4, computedPoints.size());

        assertEquals(p1, cogoStructureArcSegmentationInfo.getComputedPoints().get(0));
        assertEquals(p2, cogoStructureArcSegmentationInfo.getComputedPoints().get(1));
        assertEquals(p3, cogoStructureArcSegmentationInfo.getComputedPoints().get(2));
        assertEquals(p4, cogoStructureArcSegmentationInfo.getComputedPoints().get(3));
    }

    @Test
    void getComputedPointsForIntersectionFourPoints() {
        RyPoint id01 = new RyPoint.Builder("5E")
                .setEasting("6250.933")
                .setNorthing("2176.430")
                .setHeight("-----")
                .build();
        RyPoint id02 = new RyPoint.Builder("3E")
                .setEasting("6246.990")
                .setNorthing("2176.430")
                .setHeight("-----")
                .build();
        RyPoint id03 = new RyPoint.Builder("6E")
                .setEasting("6249.933")
                .setNorthing("2177.430")
                .setHeight("-----")
                .build();
        RyPoint id04 = new RyPoint.Builder("8E")
                .setEasting("6249.933")
                .setNorthing("2173.487")
                .setHeight("-----")
                .build();
        RyPoint id11 = new RyPoint.Builder("9E")
                .setEasting("6250.933")
                .setNorthing("2174.487")
                .setHeight("-----")
                .build();
        RyPoint id12 = new RyPoint.Builder("11E")
                .setEasting("6246.990")
                .setNorthing("2174.487")
                .setHeight("-----")
                .build();
        RyPoint id13 = new RyPoint.Builder("6E")
                .setEasting("6249.933")
                .setNorthing("2177.430")
                .setHeight("-----")
                .build();
        RyPoint id14 = new RyPoint.Builder("8E")
                .setEasting("6249.933")
                .setNorthing("2173.487")
                .setHeight("-----")
                .build();
        RyPoint computed1 = new RyPoint.Builder("13E")
                .setEasting("6249.933")
                .setNorthing("2176.430")
                .setHeight("-----")
                .build();
        RyPoint computed2 = new RyPoint.Builder("14E")
                .setEasting("6249.933")
                .setNorthing("2174.487")
                .setHeight("-----")
                .build();

        List<RyPoint> computedPoints = cogoStructureIntersectionFourPoints.getComputedPoints();
        List<RyIntersection> intersections = cogoStructureIntersectionFourPoints.getIntersections();

        assertEquals(2, computedPoints.size());
        assertEquals(2, intersections.size());

        assertEquals(id01, intersections.get(0).getId1());
        assertEquals(id02, intersections.get(0).getId2());
        assertEquals(id03, intersections.get(0).getId3());
        assertEquals(id04, intersections.get(0).getId4());

        assertEquals(id11, intersections.get(1).getId1());
        assertEquals(id12, intersections.get(1).getId2());
        assertEquals(id13, intersections.get(1).getId3());
        assertEquals(id14, intersections.get(1).getId4());

        assertEquals(computed1, cogoStructureIntersectionFourPoints.getComputedPoints().get(0));
        assertEquals(computed2, cogoStructureIntersectionFourPoints.getComputedPoints().get(1));
    }

    @Test
    void getComputedPointsForInverse() {
        final RyPoint from = new RyPoint.Builder("1")
                .setEasting("6243.086")
                .setNorthing("2200.414")
                .setHeight("244.940")
                .build();
        final RyPoint to = new RyPoint.Builder("2")
                .setEasting("6213.109")
                .setNorthing("2202.264")
                .setHeight("244.940")
                .build();

        final String direction = "303.9223";
        final String distance = "30.034";
        final String heightDifference = "0.001";

        assertEquals(direction, cogoStructureInverse.getDirection());
        assertEquals(distance, cogoStructureInverse.getDistance());
        assertEquals(heightDifference, cogoStructureInverse.getHeightDifference());

        assertEquals(from, cogoStructureInverse.getStartPoint());
        assertEquals(to, cogoStructureInverse.getEndPoint());
    }

    @Test
    void getComputedPointsForLineDistanceDistanceIntersection() {
        final String distance1 = "325.000";
        final String distance2 = "325.000";

        assertEquals(distance1, cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getDistance1());
        assertEquals(distance2, cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getDistance2());

        RyPoint start1 = new RyPoint.Builder("318")
                .setEasting("2613619.086")
                .setNorthing("1260823.689")
                .setHeight("325.528")
                .build();
        RyPoint end1 = new RyPoint.Builder("310")
                .setEasting("2613619.086")
                .setNorthing("1260863.565")
                .setHeight("323.530")
                .build();
        RyPoint computed1 = new RyPoint.Builder("R1")
                .setEasting("2613943.584")
                .setNorthing("1260845.514")
                .setHeight("325.528")
                .build();
        RyPoint start2 = new RyPoint.Builder("318")
                .setEasting("2613619.086")
                .setNorthing("1260823.689")
                .setHeight("325.528")
                .build();
        RyPoint end2 = new RyPoint.Builder("310")
                .setEasting("2613619.086")
                .setNorthing("1260863.565")
                .setHeight("323.530")
                .build();
        RyPoint computed2 = new RyPoint.Builder("R2")
                .setEasting("2613294.820")
                .setNorthing("1260841.740")
                .setHeight("325.528")
                .build();

        List<RyPoint> computedPoints = cogoStructureIntersectionDistanceDistance.getComputedPoints();
        List<RyIntersection> intersections = cogoStructureIntersectionDistanceDistance.getIntersections();

        assertEquals(2, computedPoints.size());
        assertEquals(2, intersections.size());

        assertEquals(start1, cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getStart());
        assertEquals(end1, cogoStructureIntersectionDistanceDistance.getIntersections().get(0).getEnd());
        assertEquals(computed1, cogoStructureIntersectionDistanceDistance.getComputedPoints().get(0));

        assertEquals(start2, cogoStructureIntersectionDistanceDistance.getIntersections().get(1).getStart());
        assertEquals(end2, cogoStructureIntersectionDistanceDistance.getIntersections().get(1).getEnd());
        assertEquals(computed2, cogoStructureIntersectionDistanceDistance.getComputedPoints().get(1));
    }

    @Test
    void getComputedPointsForLineSegmentation() {
        RyPoint start = new RyPoint.Builder("2")
                .setEasting("617187.520")
                .setNorthing("264445.281")
                .setHeight("284.317")
                .build();
        RyPoint end = new RyPoint.Builder("1")
                .setEasting("617192.135")
                .setNorthing("264452.911")
                .setHeight("280.218")
                .build();

        assertEquals(start, cogoStructureLineSegmentationInfo.getStartPoint());
        assertEquals(end, cogoStructureLineSegmentationInfo.getEndPoint());

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

        RyPoint r1 = new RyPoint.Builder("100")
                .setEasting("617188.290")
                .setNorthing("264446.552")
                .setHeight("283.633")
                .build();
        RyPoint r2 = new RyPoint.Builder("101")
                .setEasting("617189.059")
                .setNorthing("264447.824")
                .setHeight("282.950")
                .build();
        RyPoint r3 = new RyPoint.Builder("102")
                .setEasting("617189.828")
                .setNorthing("264449.096")
                .setHeight("282.267")
                .build();
        RyPoint r4 = new RyPoint.Builder("103")
                .setEasting("617190.597")
                .setNorthing("264450.367")
                .setHeight("281.584")
                .build();
        RyPoint r5 = new RyPoint.Builder("104")
                .setEasting("617191.366")
                .setNorthing("264451.639")
                .setHeight("280.901")
                .build();

        List<RyPoint> computedPoints = cogoStructureLineSegmentationInfo.getComputedPoints();

        assertEquals(5, computedPoints.size());

        assertEquals(r1, cogoStructureLineSegmentationInfo.getComputedPoints().get(0));
        assertEquals(r2, cogoStructureLineSegmentationInfo.getComputedPoints().get(1));
        assertEquals(r3, cogoStructureLineSegmentationInfo.getComputedPoints().get(2));
        assertEquals(r4, cogoStructureLineSegmentationInfo.getComputedPoints().get(3));
        assertEquals(r5, cogoStructureLineSegmentationInfo.getComputedPoints().get(4));
    }

    @Test
    void getComputedTraverse() {
        final RyPoint fromPoint1 = new RyPoint.Builder("F4")
                .setEasting("-10.156")
                .setNorthing("0.000")
                .setHeight("-----")
                .build();
        final RyPoint toPoint1 = new RyPoint.Builder("7")
                .setEasting("-10.156")
                .setNorthing("0.000")
                .setHeight("-----")
                .build();

        final String direction1 = "365.0000";
        final String distance1 = "0.000";


        final RyPoint fromPoint2 = new RyPoint.Builder("D6")
                .setEasting("7.844")
                .setNorthing("-16.500")
                .setHeight("-----")
                .build();
        final RyPoint toPoint2 = new RyPoint.Builder("5")
                .setEasting("4.157")
                .setNorthing("-13.235")
                .setHeight("-----")
                .build();

        final String direction2 = "272.7662";
        final String distance2 = "2.000";

        final RyPoint fromPoint7 = new RyPoint.Builder("H4")
                .setEasting("-10.156")
                .setNorthing("0.000")
                .setHeight("-----")
                .build();
        final RyPoint toPoint7 = new RyPoint.Builder("7")
                .setEasting("-10.156")
                .setNorthing("0.000")
                .setHeight("-----")
                .build();

        final String direction7 = "365.0000";
        final String distance7 = "0.000";

        List<RyTraverse> traverses = cogoStructureTraverse.getTraverses();

        assertEquals(7, traverses.size());

        assertEquals(fromPoint1, traverses.get(0).getFrom());
        assertEquals(direction1, traverses.get(0).getDirection());
        assertEquals(distance1, traverses.get(0).getDistance());
        assertEquals(toPoint1, traverses.get(0).getTo());

        assertEquals(fromPoint2, traverses.get(4).getFrom());
        assertEquals(direction2, traverses.get(4).getDirection());
        assertEquals(distance2, traverses.get(4).getDistance());
        assertEquals(toPoint2, traverses.get(4).getTo());

        assertEquals(fromPoint7, traverses.get(6).getFrom());
        assertEquals(direction7, traverses.get(6).getDirection());
        assertEquals(distance7, traverses.get(6).getDistance());
        assertEquals(toPoint7, traverses.get(6).getTo());
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
        List<String> lines = lineReader.getLines();

        /*

        for (String s : lines) {
            System.out.println("+++ + " + s);
        }

        */

        // use full cleaned logfile.txt
        LogfileClearUp logfileClearUp = new LogfileClearUp(lines);
        List<String> cleanLogfile = logfileClearUp.processFullClearUp();

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

}
