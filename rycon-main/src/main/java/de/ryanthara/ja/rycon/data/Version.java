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
 * Version implements all static information about the current RyCON version.
 * <p>
 * Every new version change of RyCON has to be filled in here! This is necessary
 * because of the online check for a new version of RyCON.
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public class Version {

    private static final short buildNumber = 25;
    private static final short majorRelease = 0;
    private static final short minorRelease = 9;
    private static final short patchLevel = 11;
    private static final String buildDate = "2016-05-03";

    /**
     * Return the build date of RyCON as string.
     *
     * @return the build date
     */
    public static String getBuildDate() {
        return buildDate;
    }

    /**
     * Return the current build number of RyCON as integer value.
     *
     * @return the build number
     */
    public static short getBuildNumber() {
        return buildNumber;
    }

    /**
     * Return the current major release number of RyCON as short value.
     *
     * @return the major release number
     */
    public static short getMajorRelease() {
        return majorRelease;
    }

    /**
     * Return the current minor release number of RyCON as short value.
     *
     * @return the minor release number
     */
    public static short getMinorRelease() {
        return minorRelease;
    }

    /**
     * Return the current patch level number of RyCON as short value.
     *
     * @return the patch level
     */
    public static short getPatchLevel() {
        return patchLevel;
    }

    /**
     * Return the current version of RyCON as string value (major.minor.patch).
     *
     * @return the major.minor.patch as version string
     */
    public static String getVersion() {
        return Short.toString(majorRelease) + "." + Short.toString(minorRelease) + "." + Short.toString(patchLevel);
    }

}
