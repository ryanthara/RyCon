/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.elements
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
package de.ryanthara.ja.rycon.core.elements;

import de.ryanthara.ja.rycon.ui.widgets.AnalyzerWidget;

/**
 * The {@code RyMeasuredPoint} class represents a container for storing geodetic point measurements
 * for the reference line structure.
 * <p>
 * These measurement points are used in the analyzer for Leica Geosystems logfile.txt files in the
 * {@link AnalyzerWidget} of <tt>RyCON</tt>.
 * <p>
 * This class based on an equal idea to the {@link RyPoint} structure.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class RyMeasuredPoint {

    private final String pointID;
    private final RyPoint measured;
    private final RyPoint lineOffset;

    /**
     * Initializes a new measured point container with a set of parameters.
     *
     * @param pointID    the point id
     * @param measured   the measured point
     * @param lineOffset the line offset values
     */
    public RyMeasuredPoint(final String pointID, final RyPoint measured, final RyPoint lineOffset) {
        this.pointID = pointID;
        this.measured = measured;
        this.lineOffset = lineOffset;
    }

    /**
     * Returns the line offset as {@link RyPoint}.
     *
     * @return the line offset
     */
    public RyPoint getLineOffset() {
        return lineOffset;
    }

    /**
     * Returns the measured point as {@link RyPoint}.
     *
     * @return the measured point
     */
    public RyPoint getMeasured() {
        return measured;
    }

    /**
     * Returns the point id as string.
     *
     * @return the point id
     */
    public String getPointID() {
        return pointID;
    }

} // end of RyMeasuredPoint
