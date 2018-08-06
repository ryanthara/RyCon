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
 * The {@code RyStakedPoint} class represents a container for storing geodetic stake out points.
 * <p>
 * These staked points are used in the analyzer for Leica Geosystems logfile.txt files in the
 * {@link de.ryanthara.ja.rycon.ui.widgets.ReportWidget} of {@code RyCON}.
 * <p>
 * This class based on an equal idea to the {@link RyPoint} structure.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class RyStakedPoint {

    private final String pointID;
    private final RyPoint designLineOffset;
    private final RyPoint designPoint;
    private final RyPoint stakedPoint;
    private final RyPoint stakeoutDifference;

    /**
     * Initializes a new staked out point container with a set of parameters.
     *
     * @param pointID            the point id
     * @param designPoint        the design point
     * @param stakedPoint        the staked point
     * @param stakeoutDifference the difference between design and staked point
     */
    public RyStakedPoint(final String pointID, final RyPoint designPoint, final RyPoint stakedPoint, final RyPoint stakeoutDifference) {
        this.pointID = pointID;
        this.designPoint = designPoint;
        this.stakedPoint = stakedPoint;
        this.stakeoutDifference = stakeoutDifference;
        this.designLineOffset = null;
    }

    /**
     * Initializes a new staked out point container with a set of parameters.
     *
     * @param pointID            the point id
     * @param designPoint        the design point
     * @param stakedPoint        the staked point
     * @param stakeoutDifference the difference between design and staked point
     * @param designLineOffset   the design line offset
     */
    public RyStakedPoint(final String pointID, final RyPoint designPoint, final RyPoint stakedPoint, final RyPoint stakeoutDifference, final RyPoint designLineOffset) {
        this.pointID = pointID;
        this.designPoint = designPoint;
        this.stakedPoint = stakedPoint;
        this.stakeoutDifference = stakeoutDifference;
        this.designLineOffset = designLineOffset;
    }

    /**
     * Returns the design line offset of an staked point from the reference line structure.
     *
     * @return the design line offset
     */
    public RyPoint getDesignLineOffset() {
        return designLineOffset;
    }

    /**
     * Returns the design point as {@link RyPoint}.
     *
     * @return the design point
     */
    public RyPoint getDesignPoint() {
        return designPoint;
    }

    /**
     * Returns the point id as string.
     *
     * @return the point id
     */
    public String getPointID() {
        return pointID;
    }

    /**
     * Returns the staked point as {@link RyPoint}.
     *
     * @return the staked point
     */
    public RyPoint getStakedPoint() {
        return stakedPoint;
    }

    /**
     * Returns the stake out difference as {@link RyPoint}.
     *
     * @return the stake out difference
     */
    public RyPoint getStakeoutDifference() {
        return stakeoutDifference;
    }

} // end of RyStakedPoint
