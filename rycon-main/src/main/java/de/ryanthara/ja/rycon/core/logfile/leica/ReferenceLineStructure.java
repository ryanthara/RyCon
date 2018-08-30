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

import de.ryanthara.ja.rycon.core.elements.RyMeasuredPoint;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.core.elements.RyStakedPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * The {@code ReferenceLineStructure} implements functions based on the REFERENCE LINE part of
 * the <tt>Leica Geosystems</tt> logfile.txt for <tt>RyCON</tt>.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class ReferenceLineStructure extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;
    private final ArrayList<RyMeasuredPoint> measuredPoints;
    private final ArrayList<RyStakedPoint> stakedPoints;
    private RyPoint tpsStation;
    private String referenceLineId;
    private RyPoint startPoint;
    private RyPoint endPoint;
    private RyPoint stakeoutDifference;
    private RyPoint designLineOffsets;
    private String pointId;
    private RyPoint designPoint;
    private RyPoint measuredPoint;
    private RyPoint measuredLineOffset;
    private RyPoint stakedPoint;
    private String referenceLineLength;
    private String referenceLineGradeAngular;
    private String referenceLineGradePercent;
    private String offsetLine;
    private String shiftLine;
    private String heightOffset;
    private String rotate;

    /**
     * Constructs a new {@code ReferenceLineStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public ReferenceLineStructure(ArrayList<String> lines) {
        this.lines = lines;
        this.lines.removeAll(Arrays.asList(null, ""));

        this.measuredPoints = new ArrayList<>();
        this.stakedPoints = new ArrayList<>();
    }

    /**
     * Analyzes the REFERENCE LINE structure of the <tt>Leica Geosystems</tt> logfile.txt and
     * fills the results into the return arrays.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        boolean success = false;

        super.analyzeHeader(lines);

        for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            String line = iterator.next();

            if (line.startsWith(Elements.TPS_STATION.identifier)) {
                tpsStation = super.getTpsStation(line);
            } else if (line.startsWith(Elements.REFERENCE_LINE_INFORMATION.identifier)) {
                iterator.next();

                if ((line = iterator.next()).startsWith(Elements.REFERENCE_LINE_ID.identifier)) {
                    referenceLineId = line.split(":")[1].trim();
                }
                if ((line = iterator.next()).startsWith(Elements.START_POINT_ID.identifier)) {
                    startPoint = super.getStartPoint(line);
                }
                if ((line = iterator.next()).startsWith(Elements.END_POINT_ID.identifier)) {
                    endPoint = super.getEndPoint(line);
                }
                if ((line = iterator.next()).startsWith(Elements.LENGTH.identifier)) {
                    referenceLineLength = line.split(":")[1].trim();
                }
                if ((line = iterator.next()).startsWith(Elements.GRADE.identifier)) {
                    final String gradeLine = line.split(":")[1].trim();

                    referenceLineGradeAngular = gradeLine.split(";")[0].trim();
                    referenceLineGradePercent = gradeLine.split(";")[1].trim();
                }
            } else if (line.startsWith(Elements.DEFINE_REFERENCE_LINE_OFFSETS.identifier)) {
                if ((line = iterator.next()).startsWith(Elements.OFFSET_LINE.identifier)) {
                    offsetLine = line.split(":")[1].trim();
                }
                if ((line = iterator.next()).startsWith(Elements.SHIFT_LINE.identifier)) {
                    shiftLine = line.split(":")[1].trim();
                }
                if ((line = iterator.next()).startsWith(Elements.HEIGHT_OFFSET.identifier)) {
                    heightOffset = line.split(":")[1].trim();
                }
                if ((line = iterator.next()).startsWith(Elements.ROTATE.identifier)) {
                    rotate = line.split(":")[1].trim();
                }
            } else if (line.startsWith(Elements.MEASURED_POINT.identifier)) {
                iterator.next();

                if ((line = iterator.next()).startsWith(Elements.POINT_ID.identifier)) {
                    pointId = line.split(":")[1].trim();
                }
                if ((line = iterator.next()).startsWith(Elements.MEASURED.identifier)) {
                    measuredPoint = super.getMeasuredPoint(line);
                }
                if ((line = iterator.next()).startsWith(Elements.LINE_OFFSET.identifier)) {
                    measuredLineOffset = super.getMeasuredPointDeviation(line);
                }

                measuredPoints.add(new RyMeasuredPoint(pointId, measuredPoint, measuredLineOffset));
            } else if (line.startsWith(Elements.STAKEOUT_POINT.identifier)) {
                iterator.next();

                if ((line = iterator.next()).startsWith(Elements.POINT_ID.identifier)) {
                    pointId = line.split(":")[1].trim();
                }
                if ((line = iterator.next()).startsWith(Elements.DESIGN_POINT.identifier)) {
                    designPoint = super.getDesignPoint(line);
                }
                if ((line = iterator.next()).startsWith(Elements.STAKED_POINT.identifier)) {
                    stakedPoint = super.getStakedPoint(line);
                }
                if ((line = iterator.next()).startsWith(Elements.STAKEOUT_DIFF.identifier)) {
                    stakeoutDifference = super.getStakeoutDifference(line);
                }
                if ((line = iterator.next()).startsWith(Elements.DESIGN_LINE_OFFS.identifier)) {
                    designLineOffsets = super.getDesignLineOffset(line);
                }

                stakedPoints.add(new RyStakedPoint(pointId, designPoint, stakedPoint, stakeoutDifference, designLineOffsets));
            }
        }

        return measuredPoints.size() > 0;
    }

    /**
     * Returns the design line offsets as {@link RyPoint}.
     *
     * @return design line offsets
     */
    public RyPoint getDesignLineOffsets() {
        return designLineOffsets;
    }

    /**
     * Returns the design point as {@link RyPoint}.
     *
     * @return design point
     */
    public RyPoint getDesignPoint() {
        return designPoint;
    }

    /**
     * Returns the line endpoint as {@link RyPoint}.
     *
     * @return line endpoint
     */
    public RyPoint getEndPoint() {
        return endPoint;
    }

    /**
     * Returns the height offset of the reference line.
     *
     * @return height offset
     */
    public String getHeightOffset() {
        return heightOffset;
    }

    /**
     * Returns the measured line offset (deviation) of the measured point for the reference line as {@link RyPoint}.
     *
     * @return deviation of measured point
     */
    public RyPoint getMeasuredLineOffset() {
        return measuredLineOffset;
    }

    /**
     * Returns the measured point as {@link RyPoint}.
     *
     * @return measured point
     */
    public RyPoint getMeasuredPoint() {
        return measuredPoint;
    }

    /**
     * Returns the measured points as {@link ArrayList} of {@link RyMeasuredPoint}.
     *
     * @return the measured points
     */
    public ArrayList<RyMeasuredPoint> getMeasuredPoints() {
        return measuredPoints;
    }

    /**
     * Returns the offset of the reference line.
     *
     * @return offset
     */
    public String getOffsetLine() {
        return offsetLine;
    }

    /**
     * Returns the point id.
     *
     * @return point id
     */
    public String getPointId() {
        return pointId;
    }

    /**
     * Return the angular grade of the reference line.
     *
     * @return angular grade of the reference line
     */
    public String getReferenceLineGradeAngular() {
        return referenceLineGradeAngular;
    }

    /**
     * Return the percentage grade of the reference line.
     *
     * @return percentage grade of the reference line
     */
    public String getReferenceLineGradePercent() {
        return referenceLineGradePercent;
    }

    /**
     * Returns the reference line id.
     *
     * @return reference line id
     */
    public String getReferenceLineId() {
        return referenceLineId;
    }

    /**
     * Returns the reference line length.
     *
     * @return reference line length
     */
    public String getReferenceLineLength() {
        return referenceLineLength;
    }

    /**
     * Returns the rotation of the reference line.
     *
     * @return rotation
     */
    public String getRotate() {
        return rotate;
    }

    /**
     * Returns the height shift of the reference line.
     *
     * @return height shift
     */
    public String getShiftLine() {
        return shiftLine;
    }

    /**
     * Returns the staked point as {@link RyPoint}.
     *
     * @return staked point
     */
    public RyPoint getStakedPoint() {
        return stakedPoint;
    }

    /**
     * Returns the staked points as {@link ArrayList} of {@link RyStakedPoint}.
     *
     * @return the staked points
     */
    public ArrayList<RyStakedPoint> getStakedPoints() {
        return stakedPoints;
    }

    /**
     * Return the stakeout difference.
     *
     * @return stakeout difference
     */
    public RyPoint getStakeoutDifference() {
        return stakeoutDifference;
    }

    /**
     * Returns the start point as {@link RyPoint}.
     *
     * @return start point
     */
    public RyPoint getStartPoint() {
        return startPoint;
    }

    /**
     * Returns the current TPS station as {@link RyPoint}.
     *
     * @return the tps station
     */
    public RyPoint getTpsStation() {
        return tpsStation;
    }

    // use original order for enum
    private enum Elements {
        TPS_STATION("TPS Station"),

        // Reference Line Information
        REFERENCE_LINE_INFORMATION("Reference Line Information"),

        REFERENCE_LINE_ID("Reference Line ID"),
        START_POINT_ID("Start Point ID"),
        END_POINT_ID("End Point ID"),
        LENGTH("Length"),
        GRADE("Grade"),

        DEFINE_REFERENCE_LINE_OFFSETS("Define Reference Line Offsets"),
        OFFSET_LINE("Offset Line"),
        SHIFT_LINE("Shift Line"),
        HEIGHT_OFFSET("Height Offset"),
        ROTATE("Rotate"),

        MEASURED_POINT("Measured Point"),
        MEASURED("Measured"),
        LINE_OFFSET("Line/Offset"),

        STAKEOUT_POINT("Stakeout Point"),
        DESIGN_LINE_OFFS("Design Line/Offs."),
        STAKEOUT_DIFFERENCE("Stakeout Difference"),

        POINT_ID("Point ID"),
        DESIGN_POINT("Design Point"),
        STAKED_POINT("Staked Point"),
        STAKEOUT_DIFF("Stakeout Diff");

        private final String identifier;

        Elements(String identifier) {
            this.identifier = identifier;
        }
    }

} // end of ReferenceLineStructure
