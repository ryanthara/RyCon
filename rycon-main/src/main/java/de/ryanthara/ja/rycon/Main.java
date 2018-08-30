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
import de.ryanthara.ja.rycon.data.DefaultKeys;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.Messages;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.ui.UpdateDialog;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.custom.StatusBar;
import de.ryanthara.ja.rycon.util.Updater;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;

/**
 * {@code Main} implements values, constants and objects for the complete <tt>RyCON</tt> application as an abstract class.
 * <p>
 * This class was implemented after version 1 of <tt>RyCON</tt> to get easier access to different things.
 * The main idea to do this step was influenced by the code base of JOSM, which is the most popular
 * java written editor for OpenStreetMap data.
 *
 * @author sebastian
 * @version 10
 * @since 2
 */
public abstract class Main {

    /**
     * Member that is used to indicate that a text is in singular.
     */
    public static final boolean TEXT_SINGULAR = true;
    /**
     * Member that is used to indicate that a text is in plural.
     */
    public static final boolean TEXT_PLURAL = false;
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());
    private static final boolean GSI8 = false;
    private static final boolean GSI16 = true;
    /**
     * Contains a value for application wide count of processed file operations.
     */
    public static int countFileOps = -1;
    /**
     * The reference to the global application pref handler.
     */
    public static PreferenceHandler pref;
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
    protected Main() {
    }

    /**
     * Checks the command line interface and it's given arguments.
     * <p>
     * <tt>RyCON</tt> accept a couple of simple command line interface (cli) arguments. At the moment there are
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
            logger.error("Can not parse command line interface arguments '{}'.", Arrays.toString(args), e.getCause());

            // TODO implement better cli parsing error solution
            System.exit(1);
        }

        // TODO implement set logging level via cli
        if (parser.getLoggingLevel() != null) {
            // setLoggingLevel(parser.getLoggingLevel());
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

        logger.trace("CLI argument checked");
    }

    /**
     * Checks the current JAVA version.
     * <p>
     * During the startup of <tt>RyCON</tt> the version of the installed JRE is checked.
     * <tt>RyCON</tt> can be started only if a minimum version of a JRE is installed on
     * the system. This is due to swt dependencies and java dependencies.
     * <p>
     * At minimum a JRE version of 1.7 is necessary and must be installed on the
     * target system.
     *
     * @since 2
     */
    protected static void checkJavaVersion() {
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
                        ResourceBundleUtils.getLangString(ERRORS, Errors.javaVersionText),
                        ResourceBundleUtils.getLangString(ERRORS, Errors.javaVersionMessage));

                if (rc == SWT.YES) {
                    Optional<URI> uri = DefaultKeys.JAVA_WEBSITE.getURI();

                    try {
                        if (uri.isPresent()) {
                            Desktop.getDesktop().browse(uri.get());
                        }
                    } catch (IOException e) {
                        logger.warn("Can not open the java website '{}' with the default browser.", uri.get().getPath(), e.getCause());
                    }
                }

                logger.info("Installed JRE version {} is to low.", version);
                logger.info("Please install a current JRE from https://java.com/");

                display.dispose();
                System.exit(0);
            }
        }
    }

    /**
     * Performs an online check for a new <tt>RyCON</tt> version.
     * <p>
     * If a newer version of <tt>RyCON</tt> is available an info dialog is shown to the user
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
            logger.warn("Can not check the current RyCON version '{}'", Version.getVersion(), e.getCause());
        }

        if (updateSuccessful) {
            if (updater.isUpdateAvailable()) {
                Display display = new Display();
                Shell shell = new Shell(display);

                UpdateDialog updateDialog = new UpdateDialog(shell);
                updateDialog.setText(ResourceBundleUtils.getLangString(LABELS, Labels.ryCONUpdateText));
                updateDialog.setMessage(ResourceBundleUtils.getLangString(MESSAGES, Messages.ryCONUpdate));
                updateDialog.setWhatsNewInfo(updater.getWhatsNew());
                int returnCode = updateDialog.open();

                if (returnCode == UpdateDialog.CLOSE_AND_OPEN_BROWSER) {
                    Optional<URI> uri = DefaultKeys.RyCON_WEBSITE.getURI();

                    try {
                        if (uri.isPresent()) {
                            Desktop.getDesktop().browse(uri.get());
                        }

                        display.dispose();
                        System.exit(0);
                    } catch (IOException e) {
                        logger.warn("Can not open the RyCON website '{}' with the default browser.", DefaultKeys.RyCON_WEBSITE.getValue(), e.getCause());
                    }
                } else if (returnCode == UpdateDialog.CLOSE_AND_CONTINUE) {
                    logger.info("An old version of RyCON ({}) is used.", Version.getVersion());
                    logger.info("Please update RyCON from '{}'.", DefaultKeys.RyCON_WEBSITE.getValue());
                }

                display.dispose();
            }
        }
    }

    /**
     * Returns the reader command line interface input files as string.
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
     * Returns the selected target button from the command line interface.
     *
     * @return cli target button number
     */
    public static int getCliTargetBtnNumber() {
        return cliTargetBtnNumber;
    }

    /**
     * Returns true as the indicator for GSI16.
     *
     * @return true as indicator for GSI16 format
     *
     * @since 3
     */
    public static boolean getGsi16() {
        return GSI16;
    }

    /**
     * Returns false as the indicator for GSI8.
     *
     * @return false as indicator for GSI8 format
     *
     * @since 3
     */
    public static boolean getGsi8() {
        return GSI8;
    }

    /**
     * Returns the status to indicate an open subshell.
     *
     * @return true if a subshell is open
     */
    protected static boolean getSubShellStatus() {
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
     * @return true if a settings widgets is open
     */
    public static boolean isSettingsWidgetOpen() {
        return isSettingsWidgetOpenStatus;
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

    /**
     * Sets the status to indicate an open settings widget.
     *
     * @param isOpen true if the {@link de.ryanthara.ja.rycon.ui.widgets.SettingsWidget} is open.
     */
    public static void setSettingsWidgetIsOpen(boolean isOpen) {
        isSettingsWidgetOpenStatus = isOpen;
    }

    public static void updateStatus(String s) {
        System.out.println(s);
    }

}  // end of Main
