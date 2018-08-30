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
 * The {@code RyObservation} class represents a container for storing geodetic observations.
 * <p>
 * These observations are used in the analyzer for Leica Geosystems logfile.txt files in the
 * {@link AnalyzerWidget} of <tt>RyCON</tt>.
 * <p>
 * This class based on an equal idea to the {@link RyPoint} structure.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class RyObservation {

    private final String pointId;
    private final String hz;
    private final String v;
    private final String sd;
    private final String hr;
    private final String reflectorConstant;

    /**
     * Initializes a new observation with a set of parameters.
     *
     * @param pointId           the point id
     * @param hz                the horizontal angle
     * @param v                 the vertical angle
     * @param sd                the slope distance
     * @param hr                the reflector height
     * @param reflectorConstant the reflector constant
     */
    public RyObservation(String pointId, String hz, String v, String sd, String hr, String reflectorConstant) {
        this.pointId = pointId;
        this.hz = hz;
        this.v = v;
        this.sd = sd;
        this.hr = hr;
        this.reflectorConstant = reflectorConstant;
    }

    /**
     * Returns the reflector height.
     *
     * @return reflector height
     */
    public String getHr() {
        return hr;
    }

    /**
     * Returns the horizontal angle.
     *
     * @return horizontal angle
     */
    public String getHz() {
        return hz;
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
     * Returns the reflector constant.
     *
     * @return reflector constant
     */
    public String getReflectorConstant() {
        return reflectorConstant;
    }

    /**
     * Returns the slope distance.
     *
     * @return slope distance
     */
    public String getSd() {
        return sd;
    }

    /**
     * Returns the vertical angle.
     *
     * @return vertical angle
     */
    public String getV() {
        return v;
    }

} // end of RyObservation
