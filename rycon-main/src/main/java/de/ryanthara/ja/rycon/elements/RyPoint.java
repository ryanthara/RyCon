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
package de.ryanthara.ja.rycon.elements;

/**
 * Instances of this class defines an object of type point as 'RyPoint' with coordinates and additional attributes.
 * <p>
 * RyPoint is used for duplicate elimination, first for writing LTOP KOO files.
 *
 * @author sebastian
 * @version 2
 * @since 9
 */
public class RyPoint {

    private final double x, y, z;
    private final String number, printLine;

    /**
     * Constructs a new instance of this class given a bunch of parameters.
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
     * Returns the horizontal distance between this point and the point p2.
     *
     * @param p2 second point
     *
     * @return horizontal distance
     */
    public double getHorizontalDistance(RyPoint p2) {
        double dx = this.getDistanceX(p2);
        double dy = this.getDistanceY(p2);

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the number string.
     *
     * @return point number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Returns the print line string.
     *
     * @return print line string
     */
    public String getPrintLine() {
        return printLine;
    }

    /**
     * Returns the slope distance between this point and the point p2.
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
     * Returns the x coordinate.
     *
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     *
     * @return y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the z coordinate.
     *
     * @return z coordinate
     */
    public double getZ() {
        return z;
    }

    /**
     * Returns the x coordinate difference (p.x - p2.x)
     *
     * @param p2 second point
     *
     * @return x coordinate difference (p.x - p2.x)
     */
    private double getDistanceX(RyPoint p2) {
        return this.x - p2.getX();
    }

    /**
     * Returns the y coordinate difference (p.y - p2.y)
     *
     * @param p2 second point
     *
     * @return y coordinate difference (p.y - p2.y)
     */
    private double getDistanceY(RyPoint p2) {
        return this.y - p2.getY();
    }

    /**
     * Returns the z coordinate difference (p.z - p2.z)
     *
     * @param p2 second point
     *
     * @return z coordinate difference (p.z - p2.z)
     */
    private double getDistanceZ(RyPoint p2) {
        return this.z - p2.getZ();
    }

} // end of RyPoint
