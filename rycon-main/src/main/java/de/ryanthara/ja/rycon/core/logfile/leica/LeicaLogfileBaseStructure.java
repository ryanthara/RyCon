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

import java.util.ArrayList;

/**
 * The {@code LeicaLogfileBaseStructure} implements basic functions for all logfile
 * structure elements in the <tt>Leica Geosystems</tt> logfile.txt for {@code RyCON}.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public abstract class LeicaLogfileBaseStructure {

    // same order like logfile.txt
    private String instrumentType = "unknown";
    private String serialNo = "not available";
    private String storeToJob = "not chosen";
    private String applicationStart = "not started at no time";

    /**
     * Analyze the structure and return the success.
     *
     * @return success
     */
    public boolean analyze() {
        return false;
    }

    /**
     * Compares two structure elements for being equal.
     *
     * @param obj structure to compare to
     *
     * @return true if two structures are equal
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Returns the application start date and time string.
     *
     * @return application start date and time
     */
    public String getApplicationStart() {
        return applicationStart;
    }

    /**
     * Returns the application start date string.
     *
     * @return application start date
     */
    public String getApplicationStartDate() {
        return applicationStart.split(",")[0].trim();
    }

    /**
     * Returns the application start time string.
     *
     * @return application start time
     */
    public String getApplicationStartTime() {
        return applicationStart.split(",")[1].trim();
    }

    /**
     * Returns the found instrument type of the used instrument.
     *
     * @return instrument type
     */
    public String getInstrumentType() {
        return instrumentType;
    }

    /**
     * Returns the found serial number of the used instrument.
     *
     * @return serial number
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * Returns the found store to job.
     *
     * @return store to job
     */
    public String getStoreToJob() {
        return storeToJob;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns the name of the structure.
     *
     * @return structure name
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Analyzes the header which should have the same order for all structures, but does not.
     * <p>
     * Therefore this method has to be overwritten sometimes.
     *
     * @param lines lines to be analyzed
     */
    void analyzeHeader(ArrayList<String> lines) {
        for (String line : lines) {
            if (line.startsWith(Identifier.INSTRUMENT_TYPE.getIdentifier())) {
                instrumentType = line.split(":")[1].trim();
            } else if (line.startsWith(Identifier.INSTRUMENT_SERIAL.getIdentifier())) {
                serialNo = line.split(":")[1].trim();
            } else if (line.regionMatches(true, 0, Identifier.STORE_TO_JOB.getIdentifier(), 0, 12)) {
                /*
                 * Necessary due to upper and lower case variation of 'Store to Job' vs. 'Store To Job'
                 * from Leica Geosystems templates.
                 */
                storeToJob = line.split(":")[1].trim();
            } else if (line.contains(Identifier.APPLICATION_START.getIdentifier())) {
                /*
                 * Be careful with the '\t' separators to identify the date and time strings.
                 * See Identifier.java additionally.
                 */
                applicationStart = line.split("\t:")[1].trim();
            }
        }
    }

    /**
     * Returns the computed point as {@link RyPoint}.
     *
     * @param line line that contains the computed point
     *
     * @return computed point
     */
    RyPoint getComputed(final String line) {
        return getPoint(line);
    }

    /**
     * Returns the design line offset as {@link RyPoint}.
     *
     * @param line line that contains the design line offset
     *
     * @return design line offset
     */
    RyPoint getDesignLineOffset(String line) {
        return getPointDesignAndStakeoutDifferenceAndLineOffset(line);
    }

    /**
     * Returns the design point as {@link RyPoint}.
     *
     * @param line line that contains the design point
     *
     * @return design point
     */
    RyPoint getDesignPoint(String line) {
        return getPointDesignAndStakeoutDifferenceAndLineOffset(line);
    }

    /**
     * Returns the end point as {@link RyPoint}.
     *
     * @param line line that contains the end point
     *
     * @return end point
     */
    RyPoint getEndPoint(final String line) {
        return getPoint(line);
    }

    /**
     * Returns the from point of the traverse as {@link RyPoint}.
     *
     * @param line line that contains the from point
     *
     * @return from point
     */
    RyPoint getFromPoint(String line) {
        return getComputed(line);
    }

    /**
     * Returns the measured point from the reference line as {@link RyPoint}.
     *
     * @param line line that contains the measured point
     *
     * @return measured point
     */
    RyPoint getMeasuredPoint(String line) {
        return getPointReflectorHeight(line);
    }

    /**
     * Returns the deviation of a measured point from the reference line as {@link RyPoint}.
     *
     * @param line line that contains the deviation results
     *
     * @return deviation results
     */
    RyPoint getMeasuredPointDeviation(String line) {
        return getPointDesignAndStakeoutDifferenceAndLineOffset(line);
    }

    /**
     * Returns the second point of an arc as {@link RyPoint}.
     *
     * @param line line that contains the second point
     *
     * @return second point
     */
    RyPoint getSecondPoint(String line) {
        return getPoint(line);
    }

    /**
     * Returns the staked point as {@link RyPoint}.
     *
     * @param line line that contains the staked point
     *
     * @return staked point
     */
    RyPoint getStakedPoint(String line) {
        return getPointReflectorHeight(line);
    }

    /**
     * Returns the stakeout difference as {@link RyPoint}.
     *
     * @param line line that contains the stakeout difference
     *
     * @return stake out difference
     */
    RyPoint getStakeoutDifference(String line) {
        return getPointDesignAndStakeoutDifferenceAndLineOffset(line);
    }

    /**
     * Returns the start point as {@link RyPoint}.
     *
     * @param line line that contains the start point
     *
     * @return start point
     */
    RyPoint getStartPoint(final String line) {
        return getPoint(line);
    }

    /**
     * Returns the to point of the traverse as {@link RyPoint}.
     *
     * @param line line that contains the to point
     *
     * @return the to point of the traverse
     */
    RyPoint getToPoint(String line) {
        return getComputed(line);
    }

    /**
     * Returns the TPS Station from a given line as {@link RyPoint}.
     *
     * @param line line that contains the station information
     *
     * @return tps station point
     */
    RyPoint getTpsStation(String line) {
        final String stationLine = line.split(":")[1].trim();
        final String number = stationLine.substring(0, stationLine.indexOf("\t")).trim();

        final String[] elements = stationLine.split("\\s+");
        final String easting = elements[2].trim();
        final String northing = elements[4].trim();
        final String height = elements[6].trim();
        final String instrumentHeight = elements[8].trim();

        return new RyPoint(number, easting, northing, height, instrumentHeight);
    }

    /*
     * Due to some issues from the logfile.txt a simple split by ':' or space is not possible
     *
     * old version: Computed		:              100	E:      613989.977 	N:      265313.169 	H:          264.836
     * new version: Computed		:              104	E=       617191.366	N=       264451.639	H=          280.901
     */
    private RyPoint getPoint(final String line) {
        // point elements between ':' and string length
        final String pointLine = line.substring(line.indexOf(":") + 1).trim();
        final String separator = pointLine.contains("E:") ? ":" : "=";

        final String number = pointLine.substring(0, pointLine.indexOf("E" + separator)).trim();
        final String coordinates = pointLine.substring(pointLine.indexOf("E" + separator));

        final String[] elements = coordinates.split("\\s+");
        final String easting = elements[1].trim();
        final String northing = elements[3].trim();

        if (elements.length > 5) {
            return new RyPoint(number, easting, northing, elements[5].trim());
        }

        return new RyPoint(number, easting, northing, "");
    }

    /*
     * Used for:
     *
     * Design Point			:  E=       621679.285	  N=       259099.912	  H=          370.138
     * Line/Offset			:	dL=           0.000	  dO=          -0.000	  dHO=         -3.547
     * Stakeout Diff		: dE=           -0.000	 dN=            0.000	 dH=            0.001
     * Design Line/Offs.	: dL=            0.000	 dO=            1.750	dHO=            0.000
     */
    private RyPoint getPointDesignAndStakeoutDifferenceAndLineOffset(String line) {
        final String number = line.split(":")[0].trim();
        final String designPointLine = line.split(":")[1].trim();

        final String[] elements = designPointLine.split("\\s+");
        final String easting = elements[1].trim();
        final String northing = elements[3].trim();
        final String height = elements[5].trim();

        return new RyPoint(number, easting, northing, height);
    }

    /*
     * Used for:
     *
     * Measured			:	E=       611723.917	  N=       269619.207	  H=          255.594	  hr/ha=    0.000
     * Staked Point		:  E=       621679.285	  N=       259099.912	  H=          370.137	  hr/ha=    0.000
     */
    private RyPoint getPointReflectorHeight(String line) {
        final String number = line.split(":")[0].trim();
        final String stakedPointLine = line.split(":")[1].trim();

        final String[] elements = stakedPointLine.split("\\s+");
        final String easting = elements[1].trim();
        final String northing = elements[3].trim();
        final String height = elements[5].trim();
        final String reflectorHeight = elements[7].trim();

        return new RyPoint(number, easting, northing, height, reflectorHeight);
    }

} // end of LeicaLogfileBaseStructure
