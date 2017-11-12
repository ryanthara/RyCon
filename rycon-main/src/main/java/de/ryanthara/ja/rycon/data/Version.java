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

import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;

/**
 * The {@code Version} class holds static information about the current RyCON version
 * and it's development cycle.
 * <p>
 * With a couple of static methods a simple access to the values is realized.
 * <p>
 * Every new version change of RyCON has to be filled in here! This is necessary
 * because of the online check for a new version of RyCON.
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public final class Version {

    private static final int buildYear = 2017;
    private static final int buildMonth = 11;
    private static final int buildDay = 11;
    private static final short buildNumber = 25;
    private static final short majorRelease = 2;
    private static final short minorRelease = 0;
    private static final short patchLevel = 0;

    /**
     * Returns the build date of RyCON as string.
     *
     * @return the build date
     */
    public static String getBuildDate() {
        return buildYear + "-" + buildMonth + "-" + buildDay;
    }

    /**
     * Returns the current build number of RyCON as integer value.
     *
     * @return the build number
     */
    public static short getBuildNumber() {
        return buildNumber;
    }

    /**
     * Returns the current build number and the build date as formatted string.
     *
     * @return formatted build string
     */
    public static String getBuildString() {
        LocalDate date = LocalDate.of(buildYear, buildMonth, buildDay);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);

        return "Build #" + buildNumber + ResourceBundleUtils.getLangString(LABELS, Labels.buildString) + date.format(formatter);
    }

    /**
     * Returns the current major release number of RyCON as short value.
     *
     * @return the major release number
     */
    public static short getMajorRelease() {
        return majorRelease;
    }

    /**
     * Returns the current minor release number of RyCON as short value.
     *
     * @return the minor release number
     */
    public static short getMinorRelease() {
        return minorRelease;
    }

    /**
     * Returns the current patch level number of RyCON as short value.
     *
     * @return the patch level
     */
    public static short getPatchLevel() {
        return patchLevel;
    }

    /**
     * Returns the current version of RyCON as string value (major.minor.patch_level).
     *
     * @return the {@code major.minor.patch_level} as concatenated version string
     */
    public static String getVersion() {
        return Short.toString(majorRelease) + "." + Short.toString(minorRelease) + "." + Short.toString(patchLevel);
    }

} // end of Version
