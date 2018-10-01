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
 * The {@code RyIntersection} class represents a container for storing geodetic intersections.
 * <p>
 * These intersections are used in the analyzer for Leica Geosystems logfile.txt files in the
 * {@link ReportWidget} of <tt>RyCON</tt>.
 * <p>
 * This class based on an equal idea to the {@link RyPoint} structure.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class RyIntersection {

    private String direction1;
    private String direction2;
    private RyPoint start = null;
    private RyPoint end = null;
    private String distance1 = null;
    private String distance2 = null;
    private RyPoint id1 = null;
    private RyPoint id2 = null;
    private RyPoint id3 = null;
    private RyPoint id4 = null;

    /**
     * Initializes a new intersection container with a set of parameters.
     *
     * @param start     start point
     * @param end       end point
     * @param distance1 distance from start point (p1)
     * @param distance2 distance from end point (p2)
     */
    public RyIntersection(RyPoint start, RyPoint end, String distance1, String distance2) {
        this.start = start;
        this.end = end;
        this.distance1 = distance1;
        this.distance2 = distance2;
    }

    /**
     * Initializes a new four point intersection container with a set of parameters.
     *
     * @param id1 first point
     * @param id2 second point
     * @param id3 third point
     * @param id4 fourth point
     */
    public RyIntersection(RyPoint id1, RyPoint id2, RyPoint id3, RyPoint id4) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.id4 = id4;
    }

    /**
     * Initializes a new bearing bearing intersection container with a set of parameters.
     *
     * @param id1        first point
     * @param direction1 first direction
     * @param id2        second point
     * @param direction2 second direction
     */
    public RyIntersection(RyPoint id1, String direction1, RyPoint id2, String direction2) {
        this.id1 = id1;
        this.direction1 = direction1;
        this.id2 = id2;
        this.direction2 = direction2;
    }

    /**
     * Returns the direction for the first point.
     *
     * @return first direction
     */
    public String getDirection1() {
        return direction1;
    }

    /**
     * Returns the direction for the second point.
     *
     * @return second direction
     */
    public String getDirection2() {
        return direction2;
    }


    /**
     * Returns the first distance from start point.
     *
     * @return the first distance
     */
    public String getDistance1() {
        return distance1;
    }

    /**
     * Returns the second distance from end point.
     *
     * @return the second distance
     */
    public String getDistance2() {
        return distance2;
    }

    /**
     * Returns the end point of the line.
     *
     * @return the endpoint
     */
    public RyPoint getEnd() {
        return end;
    }

    /**
     * Returns the first point.
     *
     * @return first point
     */
    public RyPoint getId1() {
        return id1;
    }

    /**
     * Returns the second point.
     *
     * @return second point
     */
    public RyPoint getId2() {
        return id2;
    }

    /**
     * Returns the third point.
     *
     * @return third point
     */
    public RyPoint getId3() {
        return id3;
    }

    /**
     * Returns the fourth point.
     *
     * @return fourth point
     */
    public RyPoint getId4() {
        return id4;
    }

    /**
     * Returns the start point of the line.
     *
     * @return the start point
     */
    public RyPoint getStart() {
        return start;
    }

} // end of RyIntersection
