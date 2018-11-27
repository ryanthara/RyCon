/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.logfile
 *
 * This package is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This package is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this package. If not, see <http://www.gnu.org/licenses/>.
 */
package de.ryanthara.ja.rycon.core.logfile.leica;

import de.ryanthara.ja.rycon.core.elements.RyIntersection;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.core.elements.RyTraverse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The {@code CogoStructure} implements functions based on the COGO (COordinate GeOmetry)
 * part of the Leica Geosystems logfile.txt for RyCON.
 * <p>
 * The COGO calculation methods are:
 * <ul>
 * <li>Inverse
 * <ul>
 * <li>Inverse between point - point</li>
 * <li>Inverse between point - line</li>
 * <li>Inverse between point - arc</li>
 * </ul>
 * </li>
 * <li>Traverse</li>
 * <li>Intersections
 * <ul>
 * <li>Bearing - Bearing</li>
 * <li>Bearing - Distance</li>
 * <li>Distance - Distance</li>
 * <li>By Points</li>
 * <li>TPS Observation - TPS Observation</li>
 * <li></li>
 * </ul>
 * </li>
 * <li>Line calculations
 * <ul>
 * <li>Base point</li>
 * <li>Offset point</li>
 * <li>Segmentation</li>
 * </ul>
 * </li>
 * <li>Arc calculations
 * <ul>
 * <li>Arc center</li>
 * <li>Base point</li>
 * <li>Offset point</li>
 * </ul>
 * </li>
 * <li>Shift, Rotate &amp; Scale (Manual)
 * <ul>
 * <li>Shift</li>
 * <li>Rotation</li>
 * <li>Scale</li>
 * </ul>
 * </li>
 * <li>Shift, Rotate &amp; Scale (Match Pts)</li>
 * <li>Area Division</li>
 * </ul>
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class CogoStructure extends LeicaLogfileBaseStructure {

    private final List<String> lines;
    private final List<RyIntersection> intersections;
    private final List<RyPoint> computedPoints;
    private final List<RyTraverse> traverses;
    private RyPoint arcCenter;
    private RyPoint startPoint;
    private RyPoint secondPoint;
    private RyPoint endPoint;
    private String radius;
    private RyPoint computed;
    private String distAlongArc;
    private String offsetFromArc;
    private String numberOfSegments;
    private String segmentsLength;
    private String numberOfPoints;
    private String segmentationMethod;
    private String direction;
    private String distance;
    private String heightDifference;
    private RyPoint offsetPoint;
    private String distAlongLine;
    private String offsetFromLine;
    private String length;
    private String azimuth;
    private RyPoint shift;
    private String rotation;
    private String scale;
    private String numberOfPointsNew;
    private String numberOfPointsSkipped;

    /**
     * Constructs a new {@code CogoStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public CogoStructure(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        this.lines.removeAll(Arrays.asList(null, ""));

        computedPoints = new ArrayList<>();
        intersections = new ArrayList<>();
        traverses = new ArrayList<>();
    }

    /**
     * Analyzes the COGO structure of the Leica Geosystems logfile.txt and
     * fills the results into the return arrays.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        boolean success;
        boolean isArc = false;
        boolean isLine = false;

        super.analyzeHeader(lines);

        for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            String line = iterator.next();

            if (line.startsWith(Type.ARC_CALCULATIONS_ARC_INFO.identifier)) {
                isArc = true;
                isLine = false;

                analyzeArcInformation(iterator);
                line = iterator.next();
            } else if (line.startsWith(Type.LINE_CALCULATIONS_LINE_INFO.identifier)) {
                isLine = true;
                isArc = false;

                analyzeLineCalculationsLineInformation(iterator);
                line = iterator.next();
            }

            if (isArc) {
                if (line.startsWith(Element.ARC_CENTER.identifier)) {
                    analyzeArcCenter(iterator);
                } else if (line.startsWith(Element.OFFSET_POINT.identifier)) {
                    analyzeArcOffset(iterator);
                } else if (line.startsWith(Element.SEGMENTATION_INFO.identifier)) {
                    analyzeSegmentationInfoArcAndLine(iterator);
                    line = iterator.next();
                }

                if (line.contains(Element.SEGMENTATION_RESULTS.identifier)) {
                    analyzeSegmentationResultsArcAndLine(iterator);
                }
            } else if (isLine) {
                isLine = false;

                if (line.startsWith(Element.BASE_POINT.identifier)) {
                    analyzeLineBasePoint(iterator);
                } else if (line.startsWith(Element.OFFSET_POINT.identifier)) {
                    analyzeLineOffsetPoint(iterator);
                } else if (line.startsWith(Element.SEGMENTATION_INFO.identifier)) {
                    analyzeSegmentationInfoArcAndLine(iterator);
                    line = iterator.next();
                }

                if (line.contains(Element.SEGMENTATION_RESULTS.identifier)) {
                    analyzeSegmentationResultsArcAndLine(iterator);
                }
            } else {
                /*
                 * Do not push the iterator to the next line here. This because
                 * the structure does not have a header like the line information block.
                 */
                if (line.startsWith(Element.BEARING_BEARING_INTERSECTION.identifier)) {
                    analyzeBearingBearingIntersection(iterator);
                } else if (line.startsWith(Element.BEARING_DISTANCE_INTERSECTION.identifier)) {
                    analyzeBearingDistanceIntersection(iterator);
                } else if (line.startsWith(Element.DISTANCE_DISTANCE_INTERSECTION.identifier)) {
                    analyzeDistanceDistanceIntersection(iterator);
                } else if (line.startsWith(Element.FOUR_POINT_INTERSECTION.identifier)) {
                    analyzeFourPointIntersection(iterator);
                } else if (line.startsWith(Element.INVERSE.identifier)) {
                    analyzeInverse(iterator);
                } else if (line.startsWith(Element.SHIFT_ROTATE_SCALE.identifier)) {
                    analyzeShiftRotateScale(iterator);
                } else if (line.startsWith(Element.TRAVERSE.identifier)) {
                    analyzeTraverse(iterator);
                }
            }

            // TODO move this to the right position in if else after the missing log files are present
            /*
            if (line.startsWith(Element.INVERSE_POINT_LINE.identifier)) {
                System.out.println("BEFORE: " + line);
                analyzeInversePoint2Line(iterator);
                //line = iterator.next();
                System.out.println("AFTER : " + line);
            }
            if (line.startsWith(Element.INVERSE_POINT_ARC.identifier)) {
                System.out.println("BEFORE: " + line);
                analyzeInversePoint2Arc(iterator);
                //line = iterator.next();
                System.out.println("AFTER : " + line);
            }

            if (line.startsWith(Element.TRAVERSE_SINGLE.identifier)) {
                System.out.println("BEFORE: " + line);
                analyzeTraverseSinglePoint(iterator);
                //line = iterator.next();
                System.out.println("AFTER : " + line);
            }
            if (line.startsWith(Element.TRAVERSE_MULTIPLE.identifier)) {
                System.out.println("BEFORE: " + line);
                analyzeTraverseMultiplePoints(iterator);
                //line = iterator.next();
                System.out.println("AFTER : " + line);
            }

            if (line.startsWith(Element.TPS_OBSERVATION_TPS_OBSERVATION.identifier)) {
                System.out.println("BEFORE: " + line);
                analyzeTpsObservationTpsObservation(iterator);
                // line = iterator.next();
                System.out.println("AFTER : " + line);
            }
            */
        }


        // TODO correct return true to valid value
        success = true;

        return success;
    }

    /**
     * Returns the calculated arc center.
     *
     * @return arc center
     */
    RyPoint getArcCenter() {
        return arcCenter;
    }

    /**
     * Returns the calculated azimuth.
     *
     * @return the azimuth
     */
    public String getAzimuth() {
        return azimuth;
    }

    /**
     * Returns the computed point as {@link RyPoint}.
     *
     * @return the computed point
     */
    public RyPoint getComputed() {
        return computed;
    }

    /**
     * Returns the list of computed points as {@link List} of {@link RyPoint}s.
     *
     * @return the computed points
     */
    List<RyPoint> getComputedPoints() {
        return List.copyOf(computedPoints);
    }

    /**
     * Returns the direction.
     *
     * @return the direction
     */
    String getDirection() {
        return direction;
    }

    /**
     * Returns the distance along the arc.
     *
     * @return distance along the arc
     */
    String getDistAlongArc() {
        return distAlongArc;
    }

    /**
     * Returns the distance along the line.
     *
     * @return the distance along the line
     */
    String getDistAlongLine() {
        return distAlongLine;
    }

    /**
     * Returns the distance.
     *
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Returns the endpoint as {@link RyPoint}.
     *
     * @return the endpoint
     */
    RyPoint getEndPoint() {
        return endPoint;
    }

    /**
     * Returns the height difference.
     *
     * @return the height difference
     */
    String getHeightDifference() {
        return heightDifference;
    }

    /**
     * Returns the calculated intersections.
     *
     * @return the  intersections
     */
    List<RyIntersection> getIntersections() {
        return List.copyOf(intersections);
    }

    /**
     * Returns the calculated length.
     *
     * @return the length
     */
    public String getLength() {
        return length;
    }

    /**
     * Returns the number of points from a segmentation.
     *
     * @return the number of points
     */
    String getNumberOfPoints() {
        return numberOfPoints;
    }

    /**
     * Returns the number of new points for shift/rotate/scale transformation.
     *
     * @return the number of new points
     */
    String getNumberOfPointsNew() {
        return numberOfPointsNew;
    }

    /**
     * Returns the number of skipped points for shift/rotate/scale transformation.
     *
     * @return the number of skipped points
     */
    String getNumberOfPointsSkipped() {
        return numberOfPointsSkipped;
    }

    /**
     * Returns the number of segments of an arc or line.
     *
     * @return the number of segments
     */
    String getNumberOfSegments() {
        return numberOfSegments;
    }

    /**
     * Returns the offset from arc.
     *
     * @return the offset from arc
     */
    String getOffsetFromArc() {
        return offsetFromArc;
    }

    /**
     * Returns the offset from the line for the base point.
     *
     * @return offset from the line
     */
    String getOffsetFromLine() {
        return offsetFromLine;
    }

    /**
     * Returns the offset point of the base point calculation.
     *
     * @return the offset point
     */
    RyPoint getOffsetPoint() {
        return offsetPoint;
    }

    /**
     * Returns the arc or circle radius.
     *
     * @return arc or circle radius
     */
    public String getRadius() {
        return radius;
    }

    /**
     * Returns the calculated rotation of the shift/rotate/scale transformation.
     *
     * @return the rotation
     */
    String getRotation() {
        return rotation;
    }

    /**
     * Returns the calculated scale of the shift/rotate/scale transformation.
     *
     * @return the rotation
     */
    String getScale() {
        return scale;
    }

    /**
     * Returns the second point of an arc or line.
     *
     * @return the second point
     */
    public RyPoint getSecondPoint() {
        return secondPoint;
    }

    /**
     * Returns the segmentation method.
     *
     * @return the segmentation method
     */
    String getSegmentationMethod() {
        return segmentationMethod;
    }

    /**
     * Returns the segments length of an arc or line.
     *
     * @return the segments length
     */
    String getSegmentsLength() {
        return segmentsLength;
    }

    /**
     * Returns the shift result of a shift/rotate/scale transformation.
     *
     * @return the shift
     */
    public RyPoint getShift() {
        return shift;
    }

    /**
     * Returns the start point of an arc or line.
     *
     * @return the start point
     */
    RyPoint getStartPoint() {
        return startPoint;
    }

    /**
     * Returns the computed traverses as {@link List}.
     *
     * @return computed traverses
     */
    List<RyTraverse> getTraverses() {
        return List.copyOf(traverses);
    }

    private void analyzeArcCenter(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            arcCenter = super.getComputed(iterator.next());
        }
    }

    private void analyzeArcInformation(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            startPoint = super.getStartPoint(iterator.next());
            secondPoint = super.getSecondPoint(iterator.next());
            endPoint = super.getEndPoint(iterator.next());
            radius = iterator.next().split(":")[1].trim();
        }
    }

    private void analyzeArcOffset(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            final String line = iterator.next();
            computed = super.getComputed(line);

            distAlongArc = iterator.next().split(":")[1].trim();

            final String[] elements = iterator.next().split(":");
            offsetFromArc = elements.length == 2 ? elements[1].trim() : "";
        }
    }

    private void analyzeBearingBearingIntersection(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            final RyPoint id1 = super.getStartPoint(iterator.next());
            final String direction1 = iterator.next().split(":")[1].trim();
            final RyPoint id2 = super.getEndPoint(iterator.next());
            final String direction2 = iterator.next().split(":")[1].trim();
            computedPoints.add(super.getComputed(iterator.next()));
            intersections.add(new RyIntersection(id1, direction1, id2, direction2));

        }
    }

    private void analyzeBearingDistanceIntersection(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            final RyPoint id1 = super.getStartPoint(iterator.next());
            final String direction = iterator.next().split(":")[1].trim();
            final RyPoint id2 = super.getEndPoint(iterator.next());
            final String distance = iterator.next().split(":")[1].trim();
            computedPoints.add(super.getComputed(iterator.next()));
            intersections.add(new RyIntersection(id1, direction, id2, distance));
        }
    }

    private void analyzeDistanceDistanceIntersection(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            final RyPoint p1 = super.getStartPoint(iterator.next());
            final String distance1 = iterator.next().split(":")[1].trim();
            final RyPoint p2 = super.getStartPoint(iterator.next());
            final String distance2 = iterator.next().split(":")[1].trim();

            computedPoints.add(super.getComputed(iterator.next()));
            intersections.add(new RyIntersection(p1, p2, distance1, distance2));
        }
    }

    private void analyzeFourPointIntersection(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            final RyPoint id1 = super.getStartPoint(iterator.next());
            final RyPoint id2 = super.getStartPoint(iterator.next());
            final RyPoint id3 = super.getStartPoint(iterator.next());
            final RyPoint id4 = super.getStartPoint(iterator.next());

            computedPoints.add(super.getComputed(iterator.next()));
            intersections.add(new RyIntersection(id1, id2, id3, id4));
        }
    }

    private void analyzeInverse(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            startPoint = super.getStartPoint(iterator.next());
            endPoint = super.getEndPoint(iterator.next());
            direction = iterator.next().split(":")[1].trim();
            distance = iterator.next().split(":")[1].trim();
            heightDifference = iterator.next().split(":")[1].trim();
        }
    }

    // TODO get example logfile and check if this is necessary
    protected void analyzeInversePoint2Arc(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {

        }

    }

    // TODO get example logfile and check if this is necessary
    protected void analyzeInversePoint2Line(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {

        }

    }

    private void analyzeLineBasePoint(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            offsetPoint = super.getStartPoint(iterator.next());
            computed = super.getComputed(iterator.next());

            distAlongLine = iterator.next().split(":")[1].trim();
            offsetFromLine = iterator.next().split(":")[1].trim();
        }
    }

    private void analyzeLineCalculationsLineInformation(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            startPoint = super.getStartPoint(iterator.next());
            endPoint = super.getEndPoint(iterator.next());
            length = iterator.next().split(":")[1].trim();
            azimuth = iterator.next().split(":")[1].trim();
        }
    }

    private void analyzeLineOffsetPoint(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            computed = super.getComputed(iterator.next());
            distAlongLine = iterator.next().split(":")[1].trim();

            final String[] elements = iterator.next().split(":");

            offsetFromLine = elements.length == 2 ? elements[1].trim() : "";
        }
    }

    private void analyzeSegmentationInfoArcAndLine(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            // Segmentation Info
            final String info1 = iterator.next();
            final String info2 = iterator.next();

            numberOfSegments = info1.split(":")[1].split("\\t+")[0].trim();
            segmentsLength = info1.split("=")[1].trim();

            numberOfPoints = info2.split(":")[1].split("\\t+")[0].trim();
            segmentationMethod = info2.split("=")[1].trim();
        }
    }

    private void analyzeShiftRotateScale(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            final String[] shiftElements = iterator.next().split("\\s+");
            shift = new RyPoint.Builder(shiftElements[0])
                    .setEasting(shiftElements[3])
                    .setNorthing(shiftElements[6])
                    .setHeight(shiftElements[9])
                    .build();

            final String[] rotateScale = iterator.next().split("\\s+");
            rotation = rotateScale[4];
            scale = rotateScale[5];

            numberOfPointsNew = iterator.next().split(":")[1].trim();
            numberOfPointsSkipped = iterator.next().split(":")[1].trim();

            while (iterator.hasNext()) {
                final String line = iterator.next();

                if (line.startsWith(Element.COMPUTED.identifier)) {
                    computedPoints.add(super.getComputed(line));
                }
            }
        }
    }

    // TODO get example logfile and check if this is necessary
    protected void analyzeTpsObservationTpsObservation(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        System.out.println(iterator.next());

        if (iterator.hasNext()) {

        }
    }

    private void analyzeTraverse(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {
            final RyPoint from = super.getFromPoint(iterator.next());
            final String direction = iterator.next().split(":")[1].trim();
            final String distance = iterator.next().split(":")[1].trim();
            final RyPoint to = super.getToPoint(iterator.next());

            traverses.add(new RyTraverse(from, direction, distance, to));
        }
    }

    // TODO get example logfile and check if this is necessary
    protected void analyzeTraverseMultiplePoints(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {

        }
    }

    // TODO get example logfile and check if this is necessary
    protected void analyzeTraverseSinglePoint(Iterator<String> iterator) {
        // skip separator line with multiple '-'
        iterator.next();

        if (iterator.hasNext()) {

        }

    }

    private void analyzeSegmentationResultsArcAndLine(Iterator<String> iterator) {
        while (iterator.hasNext()) {
            // skip separator line with multiple '-'
            iterator.next();

            if (iterator.hasNext()) {
                computedPoints.add(super.getComputed(iterator.next()));

                if (iterator.hasNext()) {
                    // skip following 'Segmentation Results' line if present
                    iterator.next();
                }
            }
        }
    }

    private enum Type {
        ARC_CALCULATIONS_ARC_INFO("Arc Calculations - Arc Info"),
        LINE_CALCULATIONS_LINE_INFO("Line Calculations - Line Info");

        private final String identifier;

        Type(String identifier) {
            this.identifier = identifier;
        }
    }

    // use original order for enum
    protected enum Element {
        // Line Calculations - Line Info
        START_POINT_ID("Start Point ID"),
        END_POINT_ID("End Point ID"),
        LENGTH("Length"),
        AZIMUTH("Azimuth"),
        BASE_POINT("Base Point"),

        OFFSET_POINT_ID("Offset Point ID"),
        COMPUTED("Computed"),
        DIST_ALONG_LINE("Dist along line"),
        OFFSET_FROM_LINE("Offset from line"),

        INVERSE("Inverse"),
        // TODO Check for correctness
        INVERSE_POINT_ARC("Inverse Pt - Arc"),
        INVERSE_POINT_LINE("Inverse Pt - Line"),

        // TODO Check for correctness
        TRAVERSE("Traverse"),
        TRAVERSE_SINGLE("Traverse - Single Pt"),
        TRAVERSE_MULTIPLE("Traverse - Multiple Pts"),

        // TODO Check for correctness
        BEARING_BEARING_INTERSECTION("Bearing - Bearing Intersection"),
        BEARING_DISTANCE_INTERSECTION("Bearing - Distance Intersection"),

        DISTANCE_DISTANCE_INTERSECTION("Distance - Distance Intersection"),

        // TODO Check for correctness
        TPS_OBSERVATION_TPS_OBSERVATION("TPS Observation - TPS Observation"),

        ARC_CALCULATIONS_ARC_INFO("Arc Calculations - Arc Info"),
        ARC_CENTER("Arc Center"),
        OFFSET_POINT("Offset Point"),
        SEGMENTATION_INFO("Segmentation Info"),
        SEGMENTATION_RESULTS("Segmentation Results"),

        LINE_CALCULATIONS_LINE_INFO("Line Calculations - Line Info"),

        FOUR_POINT_INTERSECTION("Four Point Intersection"),

        SHIFT_ROTATE_SCALE("Shift/Rotate/Scale");

        final String identifier;

        Element(String identifier) {
            this.identifier = identifier;
        }
    }

}
