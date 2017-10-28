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

import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.ResourceBundles;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * Instances of this class provides functions for handling system and user settings for RyCON.
 * <p>
 * The less needed configuration settings of RyCON are stored with the mechanism of the
 * JAVA PreferenceKeys API in the system and user area of your computer.
 * <p>
 * The settings are stored:
 * - Under Windows in a location like 'HKEY_CURRENT_USER\Software\JavaSoft\Prefs\de\ryanthara\ja'
 * - Under OS X in a location ~/Library/PreferenceKeys/de.ryanthara.ja.plist
 * - Under *nix in a location /etc/.java/.systemPrefs
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class PreferenceHandler implements PreferenceChangeListener {
    private final static Logger logger = Logger.getLogger(PreferenceHandler.class.getName());
    private boolean isDefaultSettingsGenerated = false;
    private Preferences userPreferences;

    /**
     * Constructs a new instance of this class and initializes the configuration handling.
     */
    public PreferenceHandler() {
        userPreferences = Preferences.userRoot().node("/de/ryanthara/rycon");

        initKeys();

        // add listener to the node and not to an instance of it!
        Preferences.userRoot().node("/de/ryanthara/rycon").addPreferenceChangeListener(this);
        logger.log(Level.FINE, "preference change listener added");
    }

    /**
     * Checks a path which is stored in the user preferences of RyCON.
     * <p>
     * If the path doesn't exists, RyCON tries to use the value of the base dir. If the base dir doesn't exist,
     * then the value of the "HOME" variable of the system will be returned.
     *
     * @param pathToBeChecked stored path which has to be checked
     *
     * @return checked path from user preference
     */
    public static String checkUserPrefPathExist(String pathToBeChecked) {
        if (pathToBeChecked != null) {
            if (Files.exists(Paths.get(pathToBeChecked))) {
                return pathToBeChecked;
            } else if (Files.exists(Paths.get(PreferenceKeys.DIR_BASE.name()))) {
                return PreferenceKeys.DIR_BASE.name();
            }
        }

        return System.getenv().get("HOME");
    }

    /**
     * Returns the keys of the user preference object.
     *
     * @return keys array
     *
     * @throws BackingStoreException thrown exception
     */
    public String[] getKeys() throws BackingStoreException {
        return userPreferences.keys();
    }

    /**
     * Returns a specified user preference by a given position in the node array.
     *
     * @param position array position
     *
     * @return system preference as string
     */
    public String getUserPreference(int position) {
        String key = null;

        try {
            if (position < getKeys().length) {
                key = getKeys()[position];
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        return userPreferences.get(key, "");
    }

    /**
     * Returns a system preference by given name.
     *
     * @param preference reference to the preference to be read
     *
     * @return system preference as string
     *
     * @since 3
     */
    public String getUserPreference(PreferenceKeys preference) {
        return userPreferences.get(preference.name(), "");
    }

    /**
     * Returns true if a file with default settings was generated.
     *
     * @return success
     */
    public boolean isDefaultSettingsGenerated() {
        return isDefaultSettingsGenerated;
    }

    /**
     * Sets the value for defaultSettingsGenerated from outside this class.
     *
     * @param defaultSettingsGenerated value to be set
     */
    public void setDefaultSettingsGenerated(boolean defaultSettingsGenerated) {
        isDefaultSettingsGenerated = defaultSettingsGenerated;
    }

    /**
     * This method gets called when a preference is added, removed or when
     * its value is changed.
     * <p>
     *
     * @param evt A PreferenceChangeEvent object describing the event source
     *            and the preference that has changed.
     */
    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        logger.log(Level.FINE, "called preferenceChange");
    }

    /**
     * Removes the user preference by a given key.
     * <p>
     * This function is used only for development issues of RyCON.
     *
     * @param key key of the user preference to be removed
     */
    public void removeUserPreference(String key) {
        userPreferences.remove(key);
    }

    /**
     * Removes a set of user preferences by an array of given keys.
     *
     * @param keys key of the user preference to be removed
     */
    private void removeUserPreference(String[] keys) {
        for (String key : keys) {
            userPreferences.remove(key);
        }
    }

    /**
     * Sets a defined system preference by reference to the preference object and value.
     *
     * @param preference reference to the preference object to be set
     * @param value      value to be set
     *
     * @since 3
     */
    public void setUserPreference(final PreferenceKeys preference, final String value) {
        userPreferences.put(preference.name(), value);
    }

    @Override
    public String toString() {
        return userPreferences.toString();
    }

    /**
     * Fills in the default values for RyCON into user preference.
     * <p>
     * Default settings are generated for the following parameters (parameter name - value):
     * <p>
     * <h3>General settings</h3>
     * <ul>
     * <li>'BUILD_VERSION' - 'version : YYYY-MM-DD' </li>
     * <li>'GENERATOR' - 'RyCON' </li>
     * <li>'INFORMATION_STRING' - 'information string' </li>
     * <li>'OVERWRITE_EXISTING' - 'false' </li>
     * <li>'PARAM_CODE_STRING' - 'CODE' </li>
     * <li>'PARAM_CONTROL_POINT_STRING' - 'STKE' </li>
     * </ul>
     * <h3>Display settings</h3>
     * <ul>
     * <li>'LAST_USED_DISPLAY' - '-1' </li>
     * <li>'LAST_POS_PRIMARY_MONITOR' - '-9999,-9999' </li>
     * <li>'LAST_POS_SECONDARY_MONITOR' - '-9998,-9998' </li>
     * </ul>
     * <h3>User settings</h3>
     * <ul>
     * <li>'USER_LAST_USED_DIR' - '.' </li>
     * </ul>
     * <h3>Paths for module 1 - project generation</h3>
     * <ul>
     * <li>'DIR_BASE' - '.' </li>
     * <li>'DIR_ADMIN' - './admin' </li>
     * <li>'DIR_ADMIN_TEMPLATE' - './admin/template-folder' </li>
     * <li>'DIR_BIG_DATA' - './big_data' </li>
     * <li>'DIR_BIG_DATA_TEMPLATE' - './big_data/template-folder' </li>
     * <li>'DIR_PROJECT' - './project' </li>
     * <li>'DIR_PROJECT_TEMPLATE' - './project/template-folder' </li>
     * </ul>
     * <h3>Parameters for module 2 - transfer widget</h3>
     * <ul>
     * <li>'LAST_USED_PROJECTS' - '[]' </li>
     * <li>'DIR_CARD_READER_DATA_FILES' - 'Data' </li>
     * <li>'DIR_CARD_READER_EXPORT_FILES' - 'GSI' </li>
     * <li>'DIR_CARD_READER_JOB_FILES' - 'DBX' </li>
     * </ul>
     * <h3>Parameters for module 3 - tidy up widget</h3>
     * <ul>
     * <li>'PARAM_EDIT_STRING' - 'EDIT' </li>
     * <li>'PARAM_FREE_STATION_STRING' - 'FS' </li>
     * <li>'PARAM_LTOP_STRING' - 'LTOP' </li>
     * <li>'PARAM_STAKE_OUT_STRING' - 'ST' </li>
     * </ul>
     * <h3>Parameters for module 6 - converter widget</h3>
     * <ul>
     * <li>'CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE' -  'true' </li>
     * <li>'CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE' -  'false' </li>
     * <li>'CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE' - '0.03' </li>
     * <li>'CONVERTER_SETTING_ZEISS_DIALECT' - 'M5' </li>
     * </ul>
     * <h3>File format settings</h3>
     * <ul>
     * <li>'GSI_SETTING_LINE_ENDING_WITH_BLANK' -  'true' </li>
     * </ul>
     * <p>
     * It is <b>highly recommend</b> that the user will overwrite this settings to his preferred values
     * after the first start of RyCON. To do this, hit the key 'p' on the keyboard.
     */
    private void createDefaultSettings() {
        // general settings
        setUserPreference(PreferenceKeys.BUILD_VERSION, Version.getBuildNumber() + " : " + Version.getBuildDate());
        setUserPreference(PreferenceKeys.GENERATOR, ResourceBundleUtils.getLangString(ResourceBundles.LABELS, Labels.applicationName));
        setUserPreference(PreferenceKeys.INFORMATION_STRING, DefaultKeys.RyCON_WEBSITE.getValue());
        setUserPreference(PreferenceKeys.OVERWRITE_EXISTING, DefaultKeys.OVERWRITE_EXISTING.getValue());
        setUserPreference(PreferenceKeys.PARAM_CODE_STRING, DefaultKeys.PARAM_CODE_STRING.getValue());
        setUserPreference(PreferenceKeys.PARAM_EDIT_STRING, DefaultKeys.PARAM_EDIT_STRING.getValue());

        // display settings
        setUserPreference(PreferenceKeys.LAST_USED_DISPLAY, DefaultKeys.LAST_USED_DISPLAY.getValue());
        setUserPreference(PreferenceKeys.LAST_POS_PRIMARY_MONITOR, DefaultKeys.LAST_POS_PRIMARY_MONITOR.getValue());
        setUserPreference(PreferenceKeys.LAST_POS_SECONDARY_MONITOR, DefaultKeys.LAST_POS_SECONDARY_MONITOR.getValue());

        // user settings
        setUserPreference(PreferenceKeys.USER_LAST_USED_DIR, System.getProperty("user.home"));

        // paths for module #1 - project generation
        setUserPreference(PreferenceKeys.DIR_BASE, DefaultKeys.DIR_BASE.getValue());
        setUserPreference(PreferenceKeys.DIR_ADMIN, DefaultKeys.DIR_ADMIN.getValue());
        setUserPreference(PreferenceKeys.DIR_ADMIN_TEMPLATE, DefaultKeys.DIR_ADMIN_TEMPLATE.getValue());
        setUserPreference(PreferenceKeys.DIR_BIG_DATA, DefaultKeys.DIR_BIG_DATA.getValue());
        setUserPreference(PreferenceKeys.DIR_BIG_DATA_TEMPLATE, DefaultKeys.DIR_BIG_DATA_TEMPLATE.getValue());
        setUserPreference(PreferenceKeys.DIR_PROJECT, DefaultKeys.DIR_PROJECT.getValue());
        setUserPreference(PreferenceKeys.DIR_PROJECT_TEMPLATE, DefaultKeys.DIR_PROJECT_TEMPLATE.getValue());

        // parameters for module #2 transfer widgets
        setUserPreference(PreferenceKeys.LAST_USED_PROJECTS, "[]");
        setUserPreference(PreferenceKeys.DIR_CARD_READER, DefaultKeys.DIR_CARD_READER.getValue());
        /* Leica Geosystems card structure */
        setUserPreference(PreferenceKeys.DIR_CARD_READER_DATA_FILES, "Data");
        setUserPreference(PreferenceKeys.DIR_CARD_READER_EXPORT_FILES, "Gsi");
        setUserPreference(PreferenceKeys.DIR_CARD_READER_JOB_FILES, "DBX");

        // TODO remove Rapp dependency to a more general behaviour
        setUserPreference(PreferenceKeys.DIR_PROJECT_LOG_FILES, "08.Bearb_Rapp/Messdaten/LOG");
        setUserPreference(PreferenceKeys.DIR_PROJECT_MEASUREMENT_FILES, "08.Bearb_Rapp/Messdaten/GSI");
        setUserPreference(PreferenceKeys.DIR_PROJECT_JOB_FILES, "08.Bearb_Rapp/Messdaten/DBX");

        // parameters for module #3 - tidy up
        setUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING, DefaultKeys.PARAM_CONTROL_POINT_STRING.getValue());
        setUserPreference(PreferenceKeys.PARAM_FREE_STATION_STRING, DefaultKeys.PARAM_FREE_STATION_STRING.getValue());
        setUserPreference(PreferenceKeys.PARAM_KNOWN_STATION_STRING, DefaultKeys.PARAM_KNOWN_STATION_STRING.getValue());
        setUserPreference(PreferenceKeys.PARAM_LTOP_STRING, DefaultKeys.PARAM_LTOP_STRING.getValue());

        // parameters for module #6 - converter
        setUserPreference(PreferenceKeys.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE, DefaultKeys.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE.getValue());
        setUserPreference(PreferenceKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE, DefaultKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE.getValue());
        setUserPreference(PreferenceKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE, DefaultKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE.getValue());
        setUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT, DefaultKeys.CONVERTER_SETTING_ZEISS_DIALECT.getValue());

        // GSI file format settings
        setUserPreference(PreferenceKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK, DefaultKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK.getValue());

        logger.log(Level.INFO, "default settings generated");

        isDefaultSettingsGenerated = true;
    }

    private void initKeys() {
        // Create default settings when the generator key doesn't contains the value 'RyCON'
        if (!getUserPreference(PreferenceKeys.GENERATOR).equals(ResourceBundleUtils.getLangString(ResourceBundles.LABELS, Labels.applicationName))) {
            createDefaultSettings();

            isDefaultSettingsGenerated = true;

            logger.log(Level.INFO, "default settings generated");
        } else {
            // Checks the stored node for valid keys and for length.
            // This is used to handle updates for RyCON, hence the user can use existing preferences.
            try {
                final int countDefaultPreferenceKeys = DefaultKeys.values().length;
                final int countStoredPreferenceKeys = getKeys().length;

                if (countDefaultPreferenceKeys != countStoredPreferenceKeys) {
                    // fetch old keys and values
                    final String[] oldKeys = userPreferences.keys();
                    final String[] oldValues = new String[oldKeys.length];

                    for (int i = 0; i < oldKeys.length; i++) {
                        oldValues[i] = getUserPreference(PreferenceKeys.valueOf(oldKeys[i]));
                    }

                    // delete the old preference keys (lower_chars) and their values
                    removeUserPreference(oldKeys);

                    createDefaultSettings();

                    // transfer values from old keys to the new ones
                    for (int i = 0; i < oldKeys.length; i++) {
                        setUserPreference(PreferenceKeys.valueOf(oldKeys[i].toUpperCase()), oldValues[i]);
                    }

                    logger.log(Level.FINE, "Not needed preference keys deleted.");
                    logger.log(Level.INFO, "settings successful transferred");
                }
            } catch (BackingStoreException e) {
                logger.log(Level.SEVERE, "Can't write settings back. " + e.getMessage());
            }
        }
    }

} // end of PreferenceKeys
