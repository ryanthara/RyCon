/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core
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

/**
 * The {@code Identifier} enumeration holds all of the identification strings for
 * the Leica Geosystems logfile.txt for RyCON.
 * <p>
 * The identifiers are used to find the structure block (e.g. SETUP or STAKE OUT).
 * <p>
 * This enumeration is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum Identifier {

    // Identify the different structures
    GENERAL("Leica System 1200"),
    COGO("Leica System 1200 COGO,"),
    REFERENCE_LINE("Leica System 1200 Reference Line,"),
    REFERENCE_PLANE("Leica System 1200 Reference Plane,"),
    SETUP("Leica System 1200 Setup,"),
    STAKEOUT("Leica System 1200 Stakeout,"),
    VOLUME_CALCULATIONS("Leica System 1200, Volume Calculations"),

    // Identify the same general elements used in all structures (original order)
    INSTRUMENT_TYPE("Instrument Type"),
    INSTRUMENT_SERIAL("Instrument Serial No."),

    STORE_TO_JOB("Store To Job"),
    APPLICATION_START("Start\t");

    private final String identifier;

    Identifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the identifier of the logfile block.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the identifier as string.
     *
     * @return the identifier as string
     */
    @Override
    public String toString() {
        return identifier;
    }

}
