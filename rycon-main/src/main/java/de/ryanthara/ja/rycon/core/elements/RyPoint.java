/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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

import java.util.Objects;

/**
 * A RyPoint defines a one to three dimensional point object with optional attributes.
 *
 * @author sebastian
 * @version 2
 * @since 9
 */
public class RyPoint {

    private final String number;
    private final double x;
    private final double y;
    private final double z;
    private final String easting;
    private final String northing;
    private final String height;
    private final String printLine;
    private final String instrumentOrReflectorHeight;

    /*
     * In the context of evolving RyCON,
     * facing design issues and lost readability
     * we decided to use a builder pattern
     * to achieve a clearer code and avoid telescoping constructors,
     * accepting some additional inner classes.
     */
    private RyPoint(Builder builder) {
        this.number = builder.number;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.easting = builder.easting;
        this.northing = builder.northing;
        this.height = builder.height;
        this.instrumentOrReflectorHeight = builder.instrumentOrReflectorHeight;
        this.printLine = builder.printLine;
    }

    /**
     * Compares an {@link Object} to the current {@link RyPoint} if they are equal.
     * <p>
     * They are equal if they have the same values for point number, easting, northing
     * and height. Although instrument or reflector height if is set.
     *
     * @param obj object to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        // self check
        if (this == obj) {
            return true;
        }

        // null check
        if (obj == null) {
            return false;
        }

        // type check and cast
        if (getClass() != obj.getClass()) {
            return false;
        }

        RyPoint point = (RyPoint) obj;

        // field comparison
        if (point.getInstrumentOrReflectorHeight() == null) {
            return Objects.equals(number, point.number)
                    && Objects.equals(easting, point.easting)
                    && Objects.equals(northing, point.northing)
                    && Objects.equals(height, point.height);
        } else {
            return Objects.equals(number, point.number)
                    && Objects.equals(easting, point.easting)
                    && Objects.equals(northing, point.northing)
                    && Objects.equals(height, point.height)
                    && Objects.equals(instrumentOrReflectorHeight, point.instrumentOrReflectorHeight);
        }
    }

    /**
     * Returns the easting string.
     *
     * @return the easting
     */
    public String getEasting() {
        return easting;
    }

    /**
     * Returns the height string.
     *
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * Returns the horizontal distance between this point and the point p2.
     *
     * @param p2 second point
     * @return horizontal distance
     */
    public double getHorizontalDistance(RyPoint p2) {
        double dx = this.getDistanceX(p2);
        double dy = this.getDistanceY(p2);

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the instrument or reflector height string.
     *
     * @return the instrument or reflector height
     */
    private String getInstrumentOrReflectorHeight() {
        return instrumentOrReflectorHeight;
    }

    /**
     * Returns the northing string.
     *
     * @return the northing
     */
    public String getNorthing() {
        return northing;
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
    private double getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     *
     * @return y coordinate
     */
    private double getY() {
        return y;
    }

    /**
     * Returns the z coordinate.
     *
     * @return z coordinate
     */
    private double getZ() {
        return z;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + number.hashCode();
        hash = hash * 31
                + (height == null ? 0 : height.hashCode())
                + (printLine == null ? 0 : printLine.hashCode());

        return hash;
    }

    /**
     * Returns the x coordinate difference (p.x - p2.x)
     *
     * @param p2 second point
     * @return x coordinate difference (p.x - p2.x)
     */
    private double getDistanceX(RyPoint p2) {
        return this.x - p2.getX();
    }

    /**
     * Returns the y coordinate difference (p.y - p2.y)
     *
     * @param p2 second point
     * @return y coordinate difference (p.y - p2.y)
     */
    private double getDistanceY(RyPoint p2) {
        return this.y - p2.getY();
    }

    /**
     * Returns the z coordinate difference (p.z - p2.z)
     *
     * @param p2 second point
     * @return z coordinate difference (p.z - p2.z)
     */
    private double getDistanceZ(RyPoint p2) {
        return this.z - p2.getZ();
    }

    /**
     * The builder pattern for the RyPoint class.
     *
     * @author sebastian
     * @version 1
     * @since 26
     */
    public static class Builder {
        // required
        private final String number;

        // optional
        private double x, y, z;
        private String printLine;
        private String easting;
        private String northing;
        private String height;
        private String instrumentOrReflectorHeight;

        /**
         * Build a new {@link RyPoint} with a point number string.
         *
         * @param number point number
         */
        public Builder(String number) {
            this.number = number;
        }

        /**
         * Sets the easting coordinate of the {@link RyPoint} to given string value.
         *
         * @param easting value for the easting coordinate
         * @return the Builder
         */
        public Builder setEasting(String easting) {
            this.easting = easting;
            return this;
        }

        /**
         * Sets the northing coordinate of the {@link RyPoint} to given string value.
         *
         * @param northing value for the northing coordinate
         * @return the Builder
         */
        public Builder setNorthing(String northing) {
            this.northing = northing;
            return this;
        }

        /**
         * Sets the height coordinate of the {@link RyPoint} to given string value.
         *
         * @param height value for the height coordinate
         * @return the Builder
         */
        public Builder setHeight(String height) {
            this.height = height;
            return this;
        }

        /**
         * Sets the x coordinate of the {@link RyPoint} to given value.
         *
         * @param x value for the x coordinate
         * @return the Builder
         */
        public Builder setX(double x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the y coordinate of the {@link RyPoint} to given value.
         *
         * @param y value for the y coordinate
         * @return the Builder
         */
        public Builder setY(double y) {
            this.y = y;
            return this;
        }

        /**
         * Sets the z coordinate of the {@link RyPoint} to given value.
         *
         * @param z value for the z coordinate
         * @return the Builder
         */
        public Builder setZ(double z) {
            this.z = z;
            return this;
        }

        /**
         * Appends the ready to use print line to the {@link RyPoint}.
         *
         * <p>
         * The print line contains a complete ready to use print line of the {@link RyPoint} that
         * contains all the other attributes and values of the {@link RyPoint} in a single string line.
         *
         * @param printLine the print line
         * @return the Builder
         */
        public Builder appendPrintLine(String printLine) {
            this.printLine = printLine;
            return this;
        }

        /**
         * Sets the the instrument or reflector height of the {@link RyPoint} to given value.
         *
         * @param instrumentOrReflectorHeight the instrument or reflector height
         * @return the Builder
         */
        public Builder setInstrumentOrReflectorHeight(String instrumentOrReflectorHeight) {
            this.instrumentOrReflectorHeight = instrumentOrReflectorHeight;
            return this;
        }

        /**
         * Builds the {@link RyPoint} with a call to the private constructor.
         *
         * @return the Builder
         */
        public RyPoint build() {
            return new RyPoint(this);
        }

    }

}
