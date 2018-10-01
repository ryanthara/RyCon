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

import de.ryanthara.ja.rycon.ui.widgets.ReportWidget;

/**
 * The {@code  RyTraverse} class represents a container for storing geodetic traverse results.
 * <p>
 * These results are used in the analyzer for Leica Geosystems logfile.txt files in the
 * {@link ReportWidget} of <tt>RyCON</tt>.
 * <p>
 * This class based on an equal idea to the {@link RyPoint} structure.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class RyTraverse {

    private final RyPoint from;
    private final String direction;
    private final String distance;
    private final RyPoint to;

    /**
     * Initializes a new traverse with a set of parameters.
     *
     * @param from      traverse from this point
     * @param direction the direction
     * @param distance  the distance
     * @param to        traverse to this point
     */
    public RyTraverse(RyPoint from, String direction, String distance, RyPoint to) {
        this.from = from;
        this.direction = direction;
        this.distance = distance;
        this.to = to;
    }

    /**
     * Returns the direction.
     *
     * @return the direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Returns the distance between from an to point.
     *
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Returns the from point of the traverse.
     *
     * @return the from point
     */
    public RyPoint getFrom() {
        return from;
    }

    /**
     * Returns the to point of the traverse.
     *
     * @return the to point
     */
    public RyPoint getTo() {
        return to;
    }

} // end of RyTraverse
