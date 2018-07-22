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

/**
 * The {@code RyResidual} class represents a container for storing geodetic residuals.
 * <p>
 * These residuals are used in the analyzer for Leica Geosystems logfile.txt files in the
 * {@link de.ryanthara.ja.rycon.ui.widgets.ReportWidget} of {@code RyCON}.
 * <p>
 * This class based on an equal idea to the {@link RyPoint} structure.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class RyResidual {
    private final String pointId;
    private final String dHz;
    private final String dHeight;
    private final String dHD;
    private final String use;

    /**
     * Initializes a new residual with a set of parameters.
     *
     * @param pointId the point id
     * @param dHz     the horizontal angle difference
     * @param dHeight the height difference
     * @param dHD     the horizontal distance difference
     * @param use     the usage (1D, 2D or 3D)
     */
    public RyResidual(String pointId, String dHz, String dHeight, String dHD, String use) {
        this.pointId = pointId;
        this.dHz = dHz;
        this.dHeight = dHeight;
        this.dHD = dHD;
        this.use = use;
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
     * Returns the usage (1D, 2D or 3D) of the point.
     *
     * @return point usage
     */
    public String getUse() {
        return use;
    }

    /**
     * Returns the horizontal distance difference.
     *
     * @return horizontal distance difference
     */
    public String getdHD() {
        return dHD;
    }

    /**
     * Returns the height difference.
     *
     * @return height difference
     */
    public String getdHeight() {
        return dHeight;
    }

    /**
     * Returns the horizontal angle difference.
     *
     * @return horizontal angle difference
     */
    public String getdHz() {
        return dHz;
    }

} // end of RyResidual
