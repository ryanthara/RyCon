/*
 * License: GPL. Copyright 2015- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui
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

package de.ryanthara.ja.rycon.data;

/**
 * This class implements all static information about the current RyCON version.
 * <p>
 * Every new version change of RyCON has to be filled in here! This is necessary
 * because of the online check for a new version of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
public class Version {

    private final int buildNumber = 20;
    private final int majorVersion = 0;
    private final int minorVersion = 98;
    private final String buildDate = "2015-11-20";

    /**
     * Returns the build date of RyCON as string.
     *
     * @return the build date
     */
    public String getBuildDate() {
        return buildDate;
    }

    /**
     * Returns the current build number of RyCON as integer value.
     *
     * @return the build number
     */
    public int getBuildNumber() {
        return buildNumber;
    }

    /**
     * Returns the current major version number of RyCON as integer value.
     *
     * @return the major version number
     */
    public int getMajorVersionNumber() {
        return majorVersion;
    }

    /**
     * Returns the current minor version number of RyCON as integer value.
     *
     * @return the minor version number
     */
    public int getMinorVersionNumber() {
        return minorVersion;
    }

}
