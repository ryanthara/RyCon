/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
 *     <li>4: code improvements and clean up </li>
 *     <li>3: change from properties file to Java Preferences API </li>
 *     <li>2: add a couple of parameters </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 4
 * @since 1
 */
public class PreferenceHandler {

    private boolean isDefaultSettingsGenerated = false;
    private Preferences userPreferences;

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
     * Member for the preference key of the jobs directory value.
     * @since 3
     */
    public final static String DIR_JOBS = "dir_jobs";

    /**
     * Member for the preference key of the jobs template directory value.
     * @since 3
     */
    public final static String DIR_JOBS_TEMPLATE = "dir_jobs_template";

    /**
     * Member for the preference key of the projects directory value.
     * @since 3
     */
    public final static String DIR_PROJECTS = "dir_projects";

    /**
     * Member for the preference key of the projects template directory value.
     * @since 3
     */
    public final static String DIR_PROJECTS_TEMPLATE = "dir_projects_template";

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

    /**
     * Class constructor which initializes the configuration handling.
     */
    public PreferenceHandler() {
        userPreferences = Preferences.userRoot().node("/de/ryanthara/rycon");
        
        if (userPreferences.get(PreferenceHandler.GENERATOR, "").equals(Main.getRyCONAppName())) {
            
        } else {
            System.out.println("DEFAULT SETTINGS");
            createDefaultSettings();
            isDefaultSettingsGenerated = true;
        }
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
     * Fills in the default values for RyCON into user preferences.
     * <p>
     * Default settings are generated for the following parameters (parameter name - value).
     * <ul>
     *     <li>'GENERATOR' - 'RyCON'
     *     <li>'BUILD_VERSION' - 'version - build date'
     *     <li>'INFORMATION' - 'information string'
     *     <li>'DIR_BASE' - '.'
     *     <li>'DIR_JOBS' - './jobs'
     *     <li>'DIR_JOBS_TEMPLATE' - './jobs/template-folder'
     *     <li>'DIR_PROJECTS' - './projects'
     *     <li>'DIR_PROJECTS_TEMPLATE' - './projects/template-folder'
     *     <li>'PARAM_CONTROL_POINT_STRING' - 'STKE'
     *     <li>'PARAM_FREE_STATION_STRING' - 'FS'
     *     <li>'PARAM_STAKE_OUT_STRING' - 'ST'
     *     <li>'USER_LAST_USED_DIR' - '.'
     * </ul>
     * <p>
     * It is <b>highly recommend</b> that the user will overwrite this settings to his preferred values
     * after the first start of RyCON. To do this, hit the key 'p' on the keyboard.
     */
    public void createDefaultSettings() {
        Version version = new Version();

        // general settings
        userPreferences.put(GENERATOR, Main.getRyCONAppName());
        userPreferences.put(BUILD_VERSION, version.getBuildNumber() + version.getBuildDate());
        userPreferences.put(INFORMATION_STRING, Main.getRyCONWebsite());

        // parameters for module #1 - clean up
        userPreferences.put(PARAM_CONTROL_POINT_STRING, Main.getParamControlPointString());
        userPreferences.put(PARAM_FREE_STATION_STRING, Main.getParamFreeStationString());
        userPreferences.put(PARAM_KNOWN_STATION_STRING, Main.getParamKnownStationString());

        // paths for module #5 - project generation
        userPreferences.put(DIR_BASE, Main.getDirBase());
        userPreferences.put(DIR_JOBS, Main.getDirJobs());
        userPreferences.put(DIR_JOBS_TEMPLATE, Main.getDirJobsTemplate());
        userPreferences.put(DIR_PROJECTS, Main.getDirProject());
        userPreferences.put(DIR_PROJECTS_TEMPLATE, Main.getDirProjectTemplate());
        
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
     * Sets a defined system preference by name and value. 
     * @param prefName name of the system preference to be set
     * @param value value to be set
     * @since 3
     */
    public void setUserPref(String prefName, String value) {
        userPreferences.put(prefName, value);
    }

} // end of Preferences
