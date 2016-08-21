/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.tools.elements;

/**
 * RyPoint defines an object of type point as 'RyPoint' with coordinates and some
 * additional attributes.
 * <p>
 * RyPoint is used for duplicate elimination, first for writing LTOP KOO files.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 * <li>1: basic implementation as own class</li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 9
 */
public class RyPoint {

    private final double x;
    private final double y;
    private final double z;
    private final String printLine;
    private final String number;

    /**
     * Constructor which defines the RyPoint with needed parameters.
     *
     * @param number    the point number
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param z         the z coordinate
     * @param printLine the complete ready to use print line
     */
    public RyPoint(String number, double x, double y, double z, String printLine) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.z = z;
        this.printLine = printLine;
    }

    /**
     * Return the easting difference (p.x - p2.x)
     *
     * @param p2 second point
     *
     * @return easting difference (p.x - p2.x)
     */
    public double getDistanceX(RyPoint p2) {
        return this.x - p2.getX();
    }

    /**
     * Return the northing difference (p.y - p2.y)
     *
     * @param p2 second point
     *
     * @return easting difference (p.y - p2.y)
     */
    public double getDistanceY(RyPoint p2) {
        return this.y - p2.getY();
    }

    /**
     * Return the height difference (p.z - p2.z)
     *
     * @param p2 second point
     *
     * @return height difference (p.z - p2.z)
     */
    public double getDistanceZ(RyPoint p2) {
        return this.z - p2.getZ();
    }

    /**
     * Return the number string.
     *
     * @return point number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Return the print line string.
     *
     * @return print line string
     */
    public String getPrintLine() {
        return printLine;
    }

    /**
     * Return the slope distance between this point and the point p2.
     *
     * @param p2 second point
     *
     * @return slope distance
     */
    public double getSlopeDistance(RyPoint p2) {
        double dx = this.getDistanceX(p2);
        double dy = this.getDistanceY(p2);
        double dz = this.getDistanceZ(p2);

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Return the x coordinate.
     *
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Return the y coordinate.
     *
     * @return y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Return the z coordinate.
     *
     * @return z coordinate
     */
    public double getZ() {
        return z;
    }

} // end of RyPoint
