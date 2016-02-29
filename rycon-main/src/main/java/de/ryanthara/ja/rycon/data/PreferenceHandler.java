/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.data
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

import de.ryanthara.ja.rycon.Main;

import java.io.File;
import java.util.prefs.Preferences;


/**
 * This class brought the functionality for handling system and user settings to RyCON.
 * <p>
 * The less needed configuration settings of RyCON are stored with the mechanism of the 
 * JAVA Preferences API in the system and user area of your computer.
 * <p>
 * The settings are stored:
 *  - Under Windows in a location like 'HKEY_CURRENT_USER\Software\JavaSoft\Prefs\de\ryanthara\ja'
 *  - Under OS X in a location ~/Library/Preferences/de.ryanthara.ja.plist
 *  - Under *nix in a location /etc/.java/.systemPrefs
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>6: implementation of a new directory structure, code improvements, small corrections</li>
 *     <li>5: defeat bug #1 and #3 </li>
 *     <li>4: code improvements and clean up </li>
 *     <li>3: change from properties file to Java Preferences API </li>
 *     <li>2: add a couple of parameters </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 5
 * @since 1
 */
public class PreferenceHandler {

    /**
     * Member for the preference key of the GSI setting for line ending.
     * Maybe only a problem with the Autocad import tool of RAPP AG.
     * @since 5
     */
    public final static String GSI_SETTING_LINE_ENDING_WITH_BLANK = "gsi_setting_line_ending_with_blank";
    /**
     * Member for the preference key of the build and version number value.
     * @since 3
     */
    public final static String BUILD_VERSION = "build_version";
    /**
     * Member for the preference key of the base directory value.
     * @since 3
     */
    public final static String DIR_BASE = "dir_base";
    /**
     * Member for the preference key of the big data directory value.
     * @since 6
     */
    public final static String DIR_BIG_DATA = "dir_big_data";
    /**
     * Member for the preference key of the big data template directory value.
     * @since 6
     */
    public final static String DIR_BIG_DATA_TEMPLATE = "dir_big_data_template";
    /**
     * Member for the preference key of the admin directory value.
     * @since 3
     */
    public final static String DIR_ADMIN = "dir_admin";
    /**
     * Member for the preference key of the admin template directory value.
     * @since 3
     */
    public final static String DIR_ADMIN_TEMPLATE = "dir_admin_template";
    /**
     * Member for the preference key of the project directory value.
     * @since 3
     */
    public final static String DIR_PROJECT = "dir_projects";
    /**
     * Member for the preference key of the project template directory value.
     * @since 3
     */
    public final static String DIR_PROJECT_TEMPLATE = "dir_projects_template";
    /**
     * Member for the preference key of the generator value.
     * @since 3
     */
    public final static String GENERATOR = "generator";
    /**
     * Member for the preference key of the information string value.
     * @since 3
     */
    public final static String INFORMATION_STRING = "information_string";
    /**
     * Member for the preference key for the control point identifier string.
     * @since 3
     */
    public final static String PARAM_CONTROL_POINT_STRING = "param_control_point_string";
    /**
     * Member for the preference key for the free station identifier string.
     * @since 3
     */
    public final static String PARAM_FREE_STATION_STRING = "param_free_station_string";
    /**
     * Member for the preference key for the free station identifier string.
     * @since 3
     */
    public final static String PARAM_KNOWN_STATION_STRING = "param_known_station_string";
    /**
     * Member for the preference key for the last used directory.
     * @since 3
     */
    public final static String USER_LAST_USED_DIR = "user_last_used_dir";
    private boolean isDefaultSettingsGenerated = false;
    private Preferences userPreferences;

    /**
     * Class constructor which initializes the configuration handling.
     */
    public PreferenceHandler() {
        userPreferences = Preferences.userRoot().node("/de/ryanthara/rycon");
        
        if (!userPreferences.get(PreferenceHandler.GENERATOR, "").equals(Main.getRyCONAppName())) {
            System.out.println("DEFAULT SETTINGS GENERATED");
            createDefaultSettings();
            isDefaultSettingsGenerated = true;
        }
    }

    /**
     * Checks a path which is stored in the user preferences of RyCON.
     * <p>
     * If the path doesn't exists, RyCON tries to use the value of the base dir. If base dir doesn't exist, then
     * the value of the "HOME" variable of the system will be returned.
     *
     * @param pathToBeChecked stored path which has to be checked
     */
    public static String checkUserPrefPathExist(String pathToBeChecked) {
        File f = new File(pathToBeChecked);
        if (f.exists()) {
            return pathToBeChecked;
        } else if (new File(PreferenceHandler.DIR_BASE).exists()) {
            return PreferenceHandler.DIR_BASE;
        } else {
            return System.getenv().get("HOME");
        }
    }

    /**
     * Fills in the default values for RyCON into user preferences.
     * <p>
     * Default settings are generated for the following parameters (parameter name - value).
     * <ul>
     *     <li>'GENERATOR' - 'RyCON' </li>
     *     <li>'BUILD_VERSION' - 'version - build date' </li>
     *     <li>'INFORMATION' - 'information string' </li>
     *     <li>'DIR_BASE' - '.' </li>
     *     <li>'DIR_ADMIN' - './admin' </li>
     *     <li>'DIR_ADMIN_TEMPLATE' - './admin/template-folder' </li>
     *     <li>'DIR_BIG_DATA' - './big_data' </li>
     *     <li>'DIR_BIG_DATA_TEMPLATE' - './big_data/template-folder' </li>
     *     <li>'DIR_PROJECT' - './project' </li>
     *     <li>'DIR_PROJECT_TEMPLATE' - './project/template-folder' </li>
     *     <li>'GSI_SETTING_LINE_ENDING_WITH_BLANK' -  true </li>
     *     <li>'PARAM_CONTROL_POINT_STRING' - 'STKE' </li>
     *     <li>'PARAM_FREE_STATION_STRING' - 'FS' </li>
     *     <li>'PARAM_STAKE_OUT_STRING' - 'ST' </li>
     *     <li>'USER_LAST_USED_DIR' - '.' </li>
     * </ul>
     * <p>
     * It is <b>highly recommend</b> that the user will overwrite this settings to his preferred values
     * after the first start of RyCON. To do this, hit the key 'p' on the keyboard.
     */
    public void createDefaultSettings() {
        // general settings
        userPreferences.put(GENERATOR, Main.getRyCONAppName());
        userPreferences.put(BUILD_VERSION, Version.getBuildNumber() + Version.getBuildDate());
        userPreferences.put(INFORMATION_STRING, Main.getRyCONWebsite());

        // parameters for module #1 - clean up
        userPreferences.put(PARAM_CONTROL_POINT_STRING, Main.getParamControlPointString());
        userPreferences.put(PARAM_FREE_STATION_STRING, Main.getParamFreeStationString());
        userPreferences.put(PARAM_KNOWN_STATION_STRING, Main.getParamKnownStationString());

        // GSI file format settings
        userPreferences.put(GSI_SETTING_LINE_ENDING_WITH_BLANK, Main.getGSISettingLineEnding());

        // paths for module #5 - project generation
        userPreferences.put(DIR_BASE, Main.getDirBase());
        userPreferences.put(DIR_ADMIN, Main.getDirAdmin());
        userPreferences.put(DIR_ADMIN_TEMPLATE, Main.getDirAdminTemplate());
        userPreferences.put(DIR_BIG_DATA, Main.getDirBigData());
        userPreferences.put(DIR_BIG_DATA_TEMPLATE, Main.getDirBigDataTemplate());
        userPreferences.put(DIR_PROJECT, Main.getDirProject());
        userPreferences.put(DIR_PROJECT_TEMPLATE, Main.getDirProjectTemplate());

        // user settings
        userPreferences.put(USER_LAST_USED_DIR, System.getProperty("user.home"));

        isDefaultSettingsGenerated = true;
    }

    /**
     * Returns a system preference by given name.
     * @param prefName name of the system preference to be read
     * @return system preference as String
     * @since 3
     */
    public String getUserPref(String prefName) {
        return userPreferences.get(prefName, "");
    }

    /**
     * Returns true if a file with default settings was generated.
     * @return success
     */
    public boolean isDefaultSettingsGenerated() {
        return isDefaultSettingsGenerated;
    }

    /**
     * Sets the value for defaultSettingsGenerated from outside this class.
     * @param defaultSettingsGenerated value to be set
     */
    public void setDefaultSettingsGenerated(boolean defaultSettingsGenerated) {
        isDefaultSettingsGenerated = defaultSettingsGenerated;
    }

    /**
     * Sets a defined system preference by name and value. 
     * @param prefName name of the system preference to be set
     * @param value value to be set
     * @since 3
     */
    public void setUserPref(String prefName, String value) {
        userPreferences.put(prefName, value);
    }

} // end of Preferences
