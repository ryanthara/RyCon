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

import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.gui.StatusBar;
import de.ryanthara.ja.rycon.tools.Updater;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Abstract class for holding values, constants and objects for the complete RyCON application.
 * <p>
 * This class was implemented after version 1 of RyCON to get easier access to different things.
 * The main idea to do this comes from JOSM.
 *
 * @author sebastian
 * @version 3
 * @since 2
 */
public abstract class Main {

    /**
     * Member to hold the app name - here 'RyCON'.
     */
    private static final String APP_NAME = "RyCON";
    
    /**
     * Member for the comma delimiter sign.
     */
    private static final String DELIMITER_COMMA = ",";
    
    /**
     * Member for the semicolon delimiter sign.
     */
    private static final String DELIMITER_SEMICOLON = ";";
   
    /**
     * Member for the space delimiter string.
     */
    private static final String DELIMITER_SPACE = " ";
    
    /**
     * Member for the tab delimiter string.
     */
    private static final String DELIMITER_TAB = "\t";

    /**
     * Member for the jobs directory.
     * @since 3
     */
    private static final String DIR_BASE = ".";
    
    /**
     * Member for the jobs directory.
     * @since 3
     */
    private static final String DIR_JOBS = "./jobs";

    /**
     * Member for the jobs template directory.
     * @since 3
     */
    private static final String DIR_JOBS_TEMPLATE = "./jobs/template-folder";

    /**
     * Member for the project directory.
     * @since 3
     */
    private static final String DIR_PROJECT = "./projects";

    /**
     * Member for the project template directory.
     * @since 3
     */
    private static final String DIR_PROJECT_TEMPLATE = "./projects/template-folder";

    /**
     * Member for indicating that GSI8 format is used. The value is 'false'.
     */
    private static final boolean GSI8 = false;
    
    /**
     *  Member for indication that GSI16 format is used. The value is 'true'.
     */
    public static final boolean GSI16 = true;

    /**
     * Member for the default value of the control point string ('STKE').
     * @since 3
     */
     private static final String PARAM_CONTROL_POINT_STRING = "STKE";

     /**
     * Member for the default value of the free station string ('FS').
     * @since 3
     */
     private static final String PARAM_FREE_STATION_STRING = "FS";

     /**
     * Member for the default value of the known station string ('ST').
     * @since 3
     */
     private static final String PARAM_KNOWN_STATION_STRING = "ST";

    /**
     * The RyCON build number and date as {@code String}.
     * <p>
     * This string is used for the update checker of RyCON and has to be set with every new version.     
     */
    private static final String RyCON_BUILD = "3 : 2015-02-23";

    /**
     * The RyCON version as {@code String}.
     * <p>
     * This string is used for the update checker of RyCON and has to be set with every new version.     
     */
    private static final String RyCON_VERSION = "1.00";

    /**
     * Member for the URL of the RyCON update check website.
     * @since 3
     */
     public static final String RyCON_UPDATE_URL = "http://code.ryanthara.de/content/3-RyCON/_current.version";

     /**
     * Member for the URL of the RyCON website.
     */
     public static final String RyCON_WEBSITE = "http://code.ryanthara.de/RyCON";

    /**
     * Member for the URL of the RyCON help website.
     */
    public static final String RyCON_WEBSITE_HELP = "http://code.ryanthara.de/RyCON/help";

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
    public static PreferenceHandler pref;
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
     * Checks the current JAVA version.
     * <p>
     * During the startup of RyCON the version of the installed JRE is checked. 
     * RyCON can be started only if a minimum version of a JRE is installed on 
     * the system. This is due to swt dependencies and java dependencies.
     * <p>
     * At minimum a JRE version of 1.7 is necessary and must be installed on the
     * target system.
     * 
     * @since 2
     * @return current JAVA version
     */
    public static String checkJavaVersion() {

        String version = System.getProperty("java.version");

        if (version != null) {

            // safe check
            int pos = version.indexOf('.');
            pos = version.indexOf('.', pos+1);
            double ver = Double.parseDouble (version.substring (0, pos));
            
            if (ver < 1.7) {

                Display display = new Display();
                Shell shell = new Shell(display);

                MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.YES | SWT.NO);
                messageBox.setText(I18N.getErrorTitleJavaVersion());
                messageBox.setMessage(I18N.getErrorTextJavaVersion());
                int rc = messageBox.open();
                
                if (rc == SWT.YES) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://java.com/"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Could not open default browser.");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        System.err.println("Could not open default browser.");
                    }
                }
                
                System.out.println("Version of installed JRE " + version + " is to low.");
                System.out.println("Please install current JRE from http://java.com/");
                
                display.dispose();
                
                System.exit(0);
            }

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
     * Performs an online check for a new RyCON version.
     * <p>
     * If a newer version of RyCON is available an info dialog is shown to the user
     * and an update is offered.
     * <p>
     * At the moment there is not planed to force an automatic update via Java Webstart functions.
     * 
     * @since 3
     */
    public static void checkRyCONVersion() {

        Updater updater = new Updater(Main.RyCON_UPDATE_URL);

        if (updater.checkForUpdate()) {
            if (updater.isUpdateAvailable()) {
                Display display = new Display();
                Shell shell = new Shell(display);

                MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.YES | SWT.NO);
                messageBox.setText(I18N.getInfoTitleRyCONUpdate());
                messageBox.setMessage(I18N.getInfoTextRyCONUpdate());
                int rc = messageBox.open();

                if (rc == SWT.YES) {
                    try {
                        Desktop.getDesktop().browse(new URI(getRyCONWebsite()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Could not open default browser.");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        System.err.println("Could not open default browser.");
                    }
                }

                System.out.println("An old version of RyCON is used.");
                System.out.println("Please update from " + getRyCONWebsite());

                display.dispose();

                System.exit(0);
            }
        }

    }

    /**
     * Returns the sign for the comma delimiter as string.
     * @return comma sign
     * @since 3 
     */
    public static String getDelimiterComma() {
        return DELIMITER_COMMA;
    }

    /**
     * Returns the sign for the semicolon delimiter as string.
     * @return semicolon sign
     * @since 3
     */
    public static String getDelimiterSemicolon() {
        return DELIMITER_SEMICOLON;
    }

    /**
     * Returns the sign for the space delimiter as string.
     * @return space sign
     * @since 3
     */
    public static String getDelimiterSpace() {
        return DELIMITER_SPACE;
    }

    /**
     * Returns the sign for the tab delimiter as string.
     * @return tab sign
     * @since 3
     */
    public static String getDelimiterTab() {
        return DELIMITER_TAB;
    }

    /**
     * Returns the base directory as string value.
     * @return base directory
     * @since 3
     */
    public static String getDirBase() {
        return DIR_BASE;
    }

    /**
     * Returns the jobs directory as string value.
     * @return jobs directory
     * @since 3
     */
    public static String getDirJobs() {
        return DIR_JOBS;
    }
    /**
     * Returns the jobs template directory as string value.
     * @return jobs template directory
     * @since 3
     */
    public static String getDirJobsTemplate() {
        return DIR_JOBS_TEMPLATE;
    }

    /**
     * Returns the project directory as string value.
     * @return project directory
     * @since 3
     */
    public static String getDirProject() {
        return DIR_PROJECT;
    }
    
    /**
     * Returns the project template directory as string value.
     * @return project template directory
     * @since 3
     */
    public static String getDirProjectTemplate() {
        return DIR_PROJECT_TEMPLATE;
    }

    /**
     * Returns false as the indicator for GSI8.   
     * @return false as indicator
     * @since 3
     */
    public static boolean getGSI8() {
        return GSI8;
    }

    /**
     * Returns true as the indicator for GSI16.
     * @return true as indicator
     * @since 3
     */
    public static boolean getGSI16() {
        return GSI16;
    }

    /**
     * Returns the value of the control point string ('STKE').
     * @return control point string
     * @since 3
     */
    public static String getParamControlPointString() {
        return PARAM_CONTROL_POINT_STRING;
    }

    /**
     * Returns the value of the free station string ('FS').
     * @return free station string
     * @since 3
     */
    public static String getParamFreeStationString() {
        return PARAM_FREE_STATION_STRING;
    }

    /**
     * Returns the value of the known station string ('ST').
     * @return known station string
     * @since 3
     */
    public static String getParamKnownStationString() {
        return PARAM_KNOWN_STATION_STRING;
    }

    /**
     * Returns the app name ('RyCON') as String.
     * @return the app name ('RyCON')
     * @since 3
     */
    public static String getRyCONAppName() {
        return APP_NAME;
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
     * Returns the RyCON version as {@code String}.
     *
     * @return the version as {@code String}
     * @since 3
     */
    public static String getRyCONVersion() {
        return RyCON_VERSION;
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
     * Initializes access to {@code Settings} with {@code Main.pref} in normal context.
     */
    public static void initApplicationPreferences() {
        Main.pref = new PreferenceHandler();
    }

    /**
     * Opens an uri in the default browser of the system.
     *  
     * @param uri uri to open in default browser
     */
    public static void openURI(String uri) {

        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not open default browser.");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.err.println("Could not open default browser.");
        }
        
    }

    /**
     * Set the status to indicate an open subshell.
     *
     * @param isSubShellOpen subshell status
     */
    public static void setSubShellStatus(boolean isSubShellOpen) {
        isSubShellOpenStatus = isSubShellOpen;
    }

}  // end of Main
