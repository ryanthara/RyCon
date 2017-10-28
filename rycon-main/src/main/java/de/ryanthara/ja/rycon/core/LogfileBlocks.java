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
package de.ryanthara.ja.rycon.core;

/**
 * The {@code LogfileBlocks} enumeration holds all the identification strings for
 * the <tt>Leica Geosystems</tt> logfile.txt for {@code RyCON}.
 * <p>
 * This enumeration is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum LogfileBlocks {

    COGO("Leica System 1200 COGO,"),
    REFERENCE_LINE("Leica System 1200 Reference Line,"),
    SETUP("Leica System 1200 Setup,"),
    STAKEOUT("Leica System 1200 Stakeout,");

    private String identifier;

    LogfileBlocks(String identifier) {
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

} // end of LogfileBlocks
