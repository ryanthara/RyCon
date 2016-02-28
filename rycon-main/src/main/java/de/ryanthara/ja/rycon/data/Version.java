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
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: change to static behaviour </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class Version {

    private static final int buildNumber = 20;
    private static final int majorVersion = 0;
    private static final int minorVersion = 98;
    private static final String buildDate = "2015-11-20";

    /**
     * Returns the build date of RyCON as string.
     *
     * @return the build date
     */
    public static final String getBuildDate() {
        return buildDate;
    }

    /**
     * Returns the current build number of RyCON as integer value.
     *
     * @return the build number
     */
    public static final int getBuildNumber() {
        return buildNumber;
    }

    /**
     * Returns the current major version number of RyCON as integer value.
     *
     * @return the major version number
     */
    public static final int getMajorVersionNumber() {
        return majorVersion;
    }

    /**
     * Returns the current minor version number of RyCON as integer value.
     *
     * @return the minor version number
     */
    public static final int getMinorVersionNumber() {
        return minorVersion;
    }

    /**
     * Returns the current version of RyCON as string value (major.minor).
     * @return the major.minor version
     */
    public static final String getVersion() {
        return Integer.toString(majorVersion) + "." + Integer.toString(minorVersion);
    }

}
