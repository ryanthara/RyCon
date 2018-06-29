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

    // same order like logfile
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
     * Returns the application start date and time string.
     *
     * @return application start date and time
     */
    public String getApplicationStart() {
        return applicationStart;
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

} // end of LeicaLogfileBaseStructure
