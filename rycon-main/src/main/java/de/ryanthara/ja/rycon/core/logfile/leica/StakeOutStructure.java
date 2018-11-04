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

import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.core.elements.RyStakedPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code StakeOutStructure} implements functions based on the COGO part of
 * the Leica Geosystems logfile.txt for RyCON.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class StakeOutStructure extends LeicaLogfileBaseStructure {

    private final List<String> lines;
    private final List<RyStakedPoint> stakedPoints;
    private String pointId;
    private RyPoint tpsStation;
    private RyPoint designPoint;
    private RyPoint stakedPoint;
    private RyPoint stakeoutDifference;

    /**
     * Constructs a new {@code StakeOutStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public StakeOutStructure(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        this.lines.removeAll(Arrays.asList(null, ""));

        stakedPoints = new ArrayList<>();
    }

    /**
     * Analyzes the STAKE OUT structure of the Leica Geosystems logfile.txt and
     * fills the results into the return {@link ArrayList} of {@link RyStakedPoint} objects.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        super.analyzeHeader(lines);

        for (String line : lines) {
            if (line.startsWith(Elements.TPS_STATION.identifier)) {
                tpsStation = super.getTpsStation(line);
            } else if (line.startsWith(Elements.POINT_ID.identifier)) {
                pointId = line.split(":")[1].trim();
            } else if (line.startsWith(Elements.DESIGN_POINT.identifier)) {
                designPoint = super.getDesignPoint(line);
            } else if (line.startsWith(Elements.STAKED_POINT.identifier)) {
                stakedPoint = super.getStakedPoint(line);
            } else if (line.startsWith(Elements.STAKEOUT_DIFF.identifier)) {
                stakeoutDifference = super.getStakeoutDifference(line);
                stakedPoints.add(new RyStakedPoint(pointId, designPoint, stakedPoint, stakeoutDifference));
            }
        }

        return stakedPoints.size() > 0;
    }

    /**
     * Returns the design point.
     *
     * @return design point
     */
    RyPoint getDesignPoint() {
        return designPoint;
    }

    /**
     * Returns the current point id.
     *
     * @return current point id
     */
    String getPointId() {
        return pointId;
    }

    /**
     * Returns the staked points as {@link List} of {@link RyStakedPoint}.
     *
     * @return the staked points
     */
    List<RyStakedPoint> getStakedPoints() {
        return List.copyOf(stakedPoints);
    }

    /**
     * Returns the stakeout difference as {@link RyPoint}.
     *
     * @return the staked point
     */
    RyPoint getStakeoutDifference() {
        return stakeoutDifference;
    }

    /**
     * Returns the current TPS station as {@link RyPoint}.
     *
     * @return the tps station id
     */
    RyPoint getTpsStation() {
        return tpsStation;
    }

    // use original order for enum
    private enum Elements {
        TPS_STATION("TPS Station"),
        POINT_ID("Point ID"),
        DESIGN_POINT("Design Point"),
        STAKED_POINT("Staked Point"),
        STAKEOUT_DIFF("Stakeout Diff");

        private final String identifier;

        Elements(String identifier) {
            this.identifier = identifier;
        }
    }

}
