/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

import de.ryanthara.ja.rycon.cli.CmdLineInterfaceException;
import de.ryanthara.ja.rycon.cli.CmdLineInterfaceParser;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.gui.UpdateDialog;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.gui.custom.StatusBar;
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.Messages;
import de.ryanthara.ja.rycon.tools.Updater;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Main implements values, constants and objects for the complete RyCON application as an abstract class.
 * <p>
 * This class was implemented after version 1 of RyCON to get easier access to different things.
 * The main idea to do this step was influenced by the code base of JOSM, which is the most popular
 * java written editor for OpenStreetMap data.
 *
 * @author sebastian
 * @version 9
 * @since 2
 */
public abstract class Main {

    /**
     * Contains the URL of the RyCON update check website. RyCON uses https connection of course.
     *
     * @since 3
     */
    public static final String RyCON_UPDATE_URL = "https://code.ryanthara.de/content/3-RyCON/_current.version";
    /**
     * Contains the URL of the RyCON website.
     */
    public static final String RyCON_WEBSITE = "https://code.ryanthara.de/RyCON";
    /**
     * Contains for the URL of the RyCON help website.
     */
    public static final String RyCON_WEBSITE_HELP = "https://code.ryanthara.de/RyCON/help";
    /**
     * Contains the URL of the RyCON what's new website.
     * <p>
     * It's content is shown in the result window of the update check dialog.
     *
     * @since 3
     */
    public static final String RyCON_WHATS_NEW_URL = "https://code.ryanthara.de/content/3-RyCON/_whats.new";
    /**
     * Member that is used to indicate that a text is in singular.
     */
    public static final boolean TEXT_SINGULAR = true;
    /**
     * Member that is used to indicate that a text is in plural.
     */
    public static final boolean TEXT_PLURAL = false;
    // a couple of private members are used for storing values
    private static final boolean GSI8 = false;
    private static final boolean GSI16 = true;
    private static final int RyCON_GRID_WIDTH = 325;
    private static final int RyCON_GRID_HEIGHT = 135;
    private static final int RyCON_WIDGET_WIDTH = 666;
    private static final int RyCON_WIDGET_HEIGHT = 412;
    private static final String APP_NAME = "RyCON";
    private static final String DIR_BASE = ".";
    private static final String DIR_ADMIN = "./admin";
    private static final String DIR_ADMIN_TEMPLATE = "./admin/template-folder";
    private static final String DIR_BIG_DATA = "./big_data";
    private static final String DIR_BIG_DATA_TEMPLATE = "./big_data/template-folder";
    private static final String DIR_PROJECT = "./projects";
    private static final String DIR_PROJECT_TEMPLATE = "./projects/template-folder";
    private static final String CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE = "true";
    private static final String CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE = "false";
    private static final String CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE = "0.03";
    private static final String CONVERTER_SETTING_ZEISS_DIALECT = "M5";
    private static final String GSI_SETTING_LINE_ENDING_WITH_BLANK = "true";
    private static final String LAST_USED_DISPLAY = "-1";
    private static final String PARAM_CODE_STRING = "CODE";
    private static final String PARAM_CONTROL_POINT_STRING = "STKE";
    private static final String PARAM_EDIT_STRING = "EDIT";
    private static final String PARAM_FREE_STATION_STRING = "FS";
    private static final String PARAM_KNOWN_STATION_STRING = "ST";
    private static final String PARAM_LTOP_STRING = "LTOP";
    private static final String LAST_POS_PRIMARY_MONITOR = "-9999, -9999";
    private static final String LAST_POS_SECONDARY_MONITOR = "-9998, -9998,";

    /**
     * Contains a value for application wide count of processed file operations.
     */
    public static int countFileOps = -1;
    /**
     * The reference to the global application preferences handler.
     */
    public static PreferenceHandler pref;
    /**
     * The reference to the global application shell.
     */
    public static Shell shell;
    /**
     * The reference to the global application status bar.
     */
    public static StatusBar statusBar;
    private static int cliSourceBtnNumber;
    private static int cliTargetBtnNumber;
    private static String cliInputFile;
    private static boolean isSettingsWidgetOpenStatus = false;
    private static boolean isSubShellOpenStatus = false;

    /**
     * Construct a new {@code Main} object with all it's functionality.
     */
    public Main() {
    }

    /**
     * Checks the command line interface and it's given arguments.
     * <p>
     * RyCON accept a couple of simple command line interface (cli) arguments. At the moment there are
     * the following parameters implemented and can be used as described.
     * <p>
     * --help               shows the help and the valid cli arguments
     * --locale=[LOCALE]    [LOCALE] in ISO 639 alpha-2 or alpha-3 language code (e.g. de for GERMAN, en for ENGLISH)
     * --file=[input file]  [input file] input file with path which is used in the source text field
     *
     * @param args command line interface arguments
     *
     * @since 5
     */
    protected static void checkCommandLineInterfaceArguments(String... args) {
        CmdLineInterfaceParser parser = new CmdLineInterfaceParser();

        try {
            parser.parseArguments(args);
        } catch (CmdLineInterfaceException e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        if (parser.getParsedLanguageCode() != null) {
            setLocaleTo(parser.getParsedLanguageCode());
        }

        if (parser.getInputFile() != null) {
            cliInputFile = parser.getInputFile();
        }

        if (parser.getSourceBtnNumber() > -1) {
            cliSourceBtnNumber = parser.getSourceBtnNumber();
        }

        if (parser.getTargetBtnNumber() > -1) {
            cliTargetBtnNumber = parser.getTargetBtnNumber();
        }
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
     * @return current JAVA version
     *
     * @since 2
     */
    protected static String checkJavaVersion() {
        String version = System.getProperty("java.version");

        if (version != null) {
            // safe check
            int pos = version.indexOf('.');
            pos = version.indexOf('.', pos + 1);
            double ver = Double.parseDouble(version.substring(0, pos));

            if (ver < 1.7) {
                Display display = new Display();
                Shell shell = new Shell(display);

                int rc = MessageBoxes.showMessageBox(shell, SWT.ICON_ERROR | SWT.YES | SWT.NO,
                        Errors.getString("javaVersionText"), Errors.getString("javaVersionMessage"));

                if (rc == SWT.YES) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://java.com/"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Could not open the connection in the default browser.");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        System.err.println("Could not open the URI in the default browser.");
                    }
                }

                System.err.println("Version of installed JRE " + version + " is to low.");
                System.err.println("Please install current JRE from http://java.com/");

                display.dispose();
                System.exit(0);
            }
            return version;
        } else {
            return "JAVA version couldn't be recognized: ";
        }
    }

    /**
     * Performs an online check for a new RyCON version.
     * <p>
     * If a newer version of RyCON is available an info dialog is shown to the user
     * and an update is offered. This update has to be installed manually.
     * <p>
     * At the moment it is not planned to force an automatic update via Java Webstart functions or special routines.
     *
     * @since 3
     */
    protected static void checkRyCONVersion() {
        Updater updater = new Updater();
        boolean updateSuccessful = false;

        try {
            updateSuccessful = updater.checkForUpdate();
        } catch (Exception e) {
            System.err.println("checkRyCONVersion failed");
            e.printStackTrace();
        }

        if (updateSuccessful) {
            if (updater.isUpdateAvailable()) {
                Display display = new Display();
                Shell shell = new Shell(display);

                UpdateDialog updateDialog = new UpdateDialog(shell);
                updateDialog.setText(Labels.getString("ryCONUpdateText"));
                updateDialog.setMessage(Messages.getString("ryCONUpdate"));
                updateDialog.setWhatsNewInfo(updater.getWhatsNew());
                int returnCode = updateDialog.open();

                if (returnCode == UpdateDialog.CLOSE_AND_OPEN_BROWSER) {
                    try {
                        Desktop.getDesktop().browse(new URI(getRyCONWebsite()));

                        display.dispose();
                        System.exit(0);
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                        System.err.println("Could not open default browser.");
                    }
                } else if (returnCode == UpdateDialog.CLOSE_AND_CONTINUE) {
                    System.out.println("An old version of RyCON is used.");
                    System.out.println("Please update from " + getRyCONWebsite());
                }

                display.dispose();
            }
        }
    }

    /**
     * Returns the read command line interface input files as string.
     *
     * @return cli input files
     */
    public static String getCLIInputFiles() {
        return cliInputFile;
    }

    /**
     * Returns the selected source button from the command line interface.
     *
     * @return cli source button number
     */
    public static int getCliSourceBtnNumber() {
        return cliSourceBtnNumber;
    }

    /**
     * Returns the selected destination button from the command line interface.
     *
     * @return cli destination button number
     */
    public static int getCliTargetBtnNumber() {
        return cliTargetBtnNumber;
    }

    /**
     * Returns the path of the admin directory as string value.
     *
     * @return admin directory path
     *
     * @since 3
     */
    public static String getDirAdmin() {
        return DIR_ADMIN;
    }

    /**
     * Returns the path of the admin template directory as string value.
     *
     * @return admin template directory path
     *
     * @since 3
     */
    public static String getDirAdminTemplate() {
        return DIR_ADMIN_TEMPLATE;
    }

    /**
     * Returns the path of the base directory as string value.
     *
     * @return base directory path
     *
     * @since 3
     */
    public static String getDirBase() {
        return DIR_BASE;
    }

    /**
     * Returns the path of the big data directory as string value
     *
     * @return big data directory path
     */
    public static String getDirBigData() {
        return DIR_BIG_DATA;
    }

    /**
     * Returns the path of the big data template directory as string value.
     *
     * @return big data template directory path
     */
    public static String getDirBigDataTemplate() {
        return DIR_BIG_DATA_TEMPLATE;
    }

    /**
     * Returns the path of the project directory as string value.
     *
     * @return project directory path
     *
     * @since 3
     */
    public static String getDirProject() {
        return DIR_PROJECT;
    }

    /**
     * Returns the path of the project template directory as string value.
     *
     * @return project template directory
     *
     * @since 3
     */
    public static String getDirProjectTemplate() {
        return DIR_PROJECT_TEMPLATE;
    }

    /**
     * Returns true as the indicator for GSI16.
     *
     * @return true as indicator for GSI16 format
     *
     * @since 3
     */
    public static boolean getGSI16() {
        return GSI16;
    }

    /**
     * Returns false as the indicator for GSI8.
     *
     * @return false as indicator for GSI8 format
     *
     * @since 3
     */
    public static boolean getGSI8() {
        return GSI8;
    }

    /**
     * Returns true or false as String to indicate a blank at the end of a gsi format line.
     *
     * @return true or false
     *
     * @since 4
     */
    public static String getGSISettingLineEnding() {
        return GSI_SETTING_LINE_ENDING_WITH_BLANK;
    }

    /**
     * Returns the value of the primary position string.
     *
     * @return primary position string
     */
    public static String getLastPosPrimaryMonitor() {
        return LAST_POS_PRIMARY_MONITOR;
    }

    /**
     * Returns the value of the secondary position string.
     *
     * @return secondary position string
     */
    public static String getLastPosSecondaryMonitor() {
        return LAST_POS_SECONDARY_MONITOR;
    }

    /**
     * Returns the number of the last used display RyCON was shown on.
     *
     * @return last used display
     */
    public static String getLastUsedDisplay() {
        return LAST_USED_DISPLAY;
    }

    /**
     * Returns the value of the code string ('CODE').
     *
     * @return code string
     *
     * @since 8
     */
    public static String getParamCodeString() {
        return PARAM_CODE_STRING;
    }

    /**
     * Returns the value of the control point string ('STKE').
     *
     * @return control point string
     *
     * @since 3
     */
    public static String getParamControlPointString() {
        return PARAM_CONTROL_POINT_STRING;
    }

    /**
     * Returns the value of the edit string ('EDIT').
     *
     * @return edit string
     *
     * @since 8
     */
    public static String getParamEditString() {
        return PARAM_EDIT_STRING;
    }

    /**
     * Returns the value of the eliminate zero coordinates flag.
     *
     * @return eliminate zero coordinates
     *
     * @since 8
     */
    public static String getParamEliminateZeroCoordinates() {
        return CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE;
    }

    /**
     * Returns the value of the free station string ('FS').
     *
     * @return free station string
     *
     * @since 3
     */
    public static String getParamFreeStationString() {
        return PARAM_FREE_STATION_STRING;
    }

    /**
     * Returns the value of the known station string ('ST').
     *
     * @return known station string
     *
     * @since 3
     */
    public static String getParamKnownStationString() {
        return PARAM_KNOWN_STATION_STRING;
    }

    /**
     * Returns the value of the LTOP string ('LTOP').
     *
     * @return ltop string
     */
    public static String getParamLTOPString() {
        return PARAM_LTOP_STRING;
    }

    /**
     * Returns the value of the LTOP use zenith distance flag.
     *
     * @return LTOP use zenith distance instead of height angle
     *
     * @since 8
     */
    public static String getParamLTOPUseZenithDistance() {
        return CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE;
    }

    /**
     * Sets the Zeiss REC dialect format value.
     *
     * @return Zeiss REC format dialect string
     */
    public static String getParamZeissRecDialect() {
        return CONVERTER_SETTING_ZEISS_DIALECT;
    }

    /**
     * Returns the value for the minimum distance in which two points may be equal. (e.g. two points within 3cm may be
     * the same points)
     *
     * @return minimum distance for points to be equal
     */
    public static String getPointIdenticalDistance() {
        return CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE;
    }

    /**
     * Returns the app name ('RyCON') as String.
     *
     * @return the app name ('RyCON')
     *
     * @since 3
     */
    public static String getRyCONAppName() {
        return APP_NAME;
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
    protected static int getRyCON_GRID_HEIGHT() {
        return RyCON_GRID_HEIGHT;
    }

    /**
     * Returns the width of a grid cell as {@code int} value.
     *
     * @return width of a grid cell as {@code int} value
     */
    protected static int getRyCON_GRID_WIDTH() {
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
     * Sets the status to indicate an open subshell.
     *
     * @param isSubShellOpen subshell open status
     */
    public static void setSubShellStatus(boolean isSubShellOpen) {
        isSubShellOpenStatus = isSubShellOpen;
    }

    /**
     * Initializes access to {@code Settings} with {@code Main.pref} in normal context.
     */
    protected static void initApplicationPreferences() {
        Main.pref = new PreferenceHandler();
    }

    /**
     * Returns the status to indicate an open settings widget.
     *
     * @return true if a settings widget is open
     */
    public static boolean isSettingsWidgetOpen() {
        return isSettingsWidgetOpenStatus;
    }

    /**
     * Opens an uri in the default browser of the system.
     *
     * @param uri uri to open in default browser
     */
    public static void openURI(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.err.println("Could not open default browser.");
        }
    }

    /**
     * Sets the status to indicate an open settings widget.
     *
     * @param isSettingsWidgetOpen settings widget open status
     */
    public static void setIsSettingsWidgetOpen(boolean isSettingsWidgetOpen) {
        isSettingsWidgetOpenStatus = isSettingsWidgetOpen;
    }

    /**
     * Sets the locale to a given language code in alpha-2 or alpha-3 language code.
     *
     * @param languageCode language code
     *
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html">Locale.html</a>
     */
    private static void setLocaleTo(String languageCode) {
        Locale.setDefault(new Locale(languageCode, languageCode.toUpperCase()));
    }
}  // end of Main
