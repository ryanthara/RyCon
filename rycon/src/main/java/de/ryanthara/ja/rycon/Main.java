/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon
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

package de.ryanthara.ja.rycon;

import de.ryanthara.ja.rycon.data.Preferences;
import de.ryanthara.ja.rycon.gui.StatusBar;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract class for holding values, constants and objects for the complete RyCON application.
 * <p>
 * This class was implemented after version 1 of RyCON to get easier access to different things.
 * The main idea to do this comes from JOSM.
 *
 * @author sebastian
 * @version 1
 * @since 2
 */
public abstract class Main {

    /**
     * Member to hold the app name - here RyCON.
     */
    public static final String APP_NAME = "RyCON";

    /**
     * Member for the comma delimiter sign.
     */
    public static final String DELIMITER_COMMA = ",";
    /**
     * Member for the semicolon delimiter sign.
     */
    public static final String DELIMITER_SEMICOLON = ";";
    /**
     * Member for the space delimiter string.
     */
    public static final String DELIMITER_SPACE = " ";
    /**
     * Member for the tab delimiter string.
     */
    public static final String DELIMITER_TAB = "\t";
    /**
     * Member for indicating that GSI8 format is used. The value is 'false'.
     */
    public static final boolean GSI8 = false;
    /**
     *  Member for indication that GSI16 format is used. The value is 'true'.
     */
    public static final boolean GSI16 = true;
    /**
     * The RyCON build number and date as {@code String}.
     */
    private static final String RyCON_BUILD = "2 - 2014-12-31";
    /**
     * The height of a grid cell. Window size and others are calculated from these values.
     * RyCON grid uses a golden rectangle cut with an aspect ratio of 1.618:1
     */
    private static final int RyCON_GRID_HEIGHT = 200;
    /**
     * The width of a grid cell. Window size and others are calculated from these values.
     * RyCON grid uses golden rectangle cut with an aspect ratio of 1.618:1
     */
    private static final int RyCON_GRID_WIDTH = 324;
    /**
     * The RyCON website url as {@code String}.
     */
    private static final String RyCON_WEBSITE = "http://code.ryanthara.de/RyCON";
    /**
     * The height of a widget used in RyCON.
     */
    private static final int RyCON_WIDGET_HEIGHT = 412;
    /**
     * The width of a widget used in RyCON.
     */
    private static final int RyCON_WIDGET_WIDTH = 666;
    /**
     * Reference to the global application.
     */
    public static Main main;

    /**
     * Member for application wide counting of processed file operations.
     */
    public static int countFileOps = -1;

    /**
     * Member for indicating that a demo version without a valid license is used.
     */
    public static boolean LICENSE = false;

    /**
     * Member for indicating that a text is singular.
     */
    public static boolean TEXT_SINGULAR = true;

    /**
     * Member for indicating that a text is in plural.
     */
    public static boolean TEXT_PLURAL = false;

    /**
     * Reference to the global application preferences.
     */
    public static Preferences pref;
    /**
     * Reference to the global application shell.
     */
    public static Shell shell;
    /**
     * Reference tot the global application status bar.
     */
    public static StatusBar statusBar;
    /**
     * Indicator for an open sub shell of RyCON.
     */
    private static boolean isSubShellOpenStatus = false;

    /**
     * Constructs a new {@code Main} object with all it's functionality.
     */
    public Main() {
        main = this;
        // TODO Auto-generated constructor stub
    }

    /**
     * Simple check for the current JAVA version.
     *
     * @return current JAVA version
     */
    public static String checkJavaVersion() {

        String version = System.getProperty("java.version");

        if (version != null) {
            return version;
        } else {
            return "JAVA version couldn't be recognized: ";
        }

    }

    /**
     * Checks a valid license file and set the {@code LICENSE} member to 'true'.
     * @return success
     */
    public static boolean checkLicense() {

        // TODO default implementation

        boolean success = false;

        LICENSE = true;

        return success;

    }

    /**
     * Returns the RyCON build number and date as {@code String}.
     *
     * @return the build number and date as {@code String}
     */
    public static String getRyCONBuild() {
        return RyCON_BUILD;
    }

    /**
     * Returns the url of the RyCON website as {@code String}.
     *
     * @return the RyCON website as {@code String}
     */
    public static String getRyCONWebsite() {
        return RyCON_WEBSITE;
    }

    /**
     * Returns the global valid height of a widget.
     *
     * @return global valid height of a widget
     */
    public static int getRyCONWidgetHeight() {
        return RyCON_WIDGET_HEIGHT;
    }

    /**
     * Returns the global valid width of a widget.
     *
     * @return global valid width of a widget
     */
    public static int getRyCONWidgetWidth() {
        return RyCON_WIDGET_WIDTH;
    }

    /**
     * Returns the height of a grid cell as {@code int} value.
     *
     * @return height of a grid cell as {@code int} value
     */
    public static int getRyCON_GRID_HEIGHT() {
        return RyCON_GRID_HEIGHT;
    }

    /**
     * Returns the width of a grid cell as {@code int} value.
     *
     * @return width of a grid cell as {@code int} value
     */
    public static int getRyCON_GRID_WIDTH() {
        return RyCON_GRID_WIDTH;
    }

    /**
     * Returns the status to indicate an open subshell.
     *
     * @return true if a subshell is open
     */
    public static boolean getSubShellStatus() {
        return isSubShellOpenStatus;
    }

    /**
     * Set the status to indicate an open subshell.
     *
     * @param isSubShellOpen subshell status
     */
    public static void setSubShellStatus(boolean isSubShellOpen) {
        isSubShellOpenStatus = isSubShellOpen;
    }

    /**
     * Initializes access to {@code Preferences} with {@code Main.pref} in normal context.
     */
    public static void initApplicationPreferences() {
        Main.pref = new Preferences();
    }

}  // end of Main
