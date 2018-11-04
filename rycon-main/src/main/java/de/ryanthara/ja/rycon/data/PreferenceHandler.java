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

import de.ryanthara.ja.rycon.i18n.LangString;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.LANG_STRING;

/**
 * Instances of this class provides functions for handling system and user settings for RyCON.
 * <p>
 * The less needed configuration settings of RyCON are stored with the mechanism of the
 * JAVA PreferenceKey API in the system and user area of your computer.
 * <p>
 * The settings are stored:
 * <ul>
 * <li>Under Windows in the windows registry under {@code HKEY_CURRENT_USER\USID\Software\JavaSoft\Prefs\de\ryanthara\rycon2}
 * <li>Under OS X in your home folder under {@code ~/Library/PreferenceKey/de.ryanthara.rycon2.plist}
 * <li>Under *nix in a location under {@code /etc/.java/.systemPrefs}
 * </ul>
 * <p>
 * Due to some experiences with Windows REGISTRY made during the development cycle of RyCON 2,
 * the preference keys are stored in lower case with an underscore (e.g. 'param_name').
 *
 * @author sebastian
 * @version 9
 * @since 1
 */
public class PreferenceHandler implements PreferenceChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(PreferenceHandler.class.getName());

    private final String currentNode = "/de/ryanthara/rycon2";
    private boolean isDefaultSettingsGenerated = false;
    private Preferences userPreferences;

    /**
     * Constructs a new instance of this class and initializes the configuration handling.
     * <p>
     * First it will attempted to load the existing user preferences. If this fails, RyCON
     * try to load default preferences with meaningfully assumptions.
     */
    public PreferenceHandler() {
        init();
    }

    /**
     * Checks a path which is stored in the user preferences of RyCON.
     * <p>
     * If the path doesn't exists, RyCON tries to use the value of the base dir. If the base dir doesn't exist,
     * then the value of the "HOME" variable of the system will be returned.
     *
     * @param pathToBeChecked stored path which has to be checked
     * @return checked path from user preference
     */
    public static String checkUserPrefPathExist(String pathToBeChecked) {
        if (pathToBeChecked != null) {
            if (Files.exists(Paths.get(pathToBeChecked))) {
                return pathToBeChecked;
            } else if (Files.exists(Paths.get(PreferenceKey.DIR_BASE.name()))) {
                return PreferenceKey.DIR_BASE.name();
            }
        }

        return System.getenv().get("HOME");
    }

    public static void removeOldKeys1() {
        Preferences prefs = Preferences.userRoot().node("/de/ryanthara/rycon");
        try {
            prefs.removeNode();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        System.out.println("OLD KEYS REMOVED");
    }

    public static void removeOldKeys2() {
        Preferences prefs = Preferences.userRoot().node("/de/ryanthara/rycon2");
        try {
            prefs.removeNode();
        } catch (BackingStoreException e) {
            logger.error("Can not remove old user preferences keys for RyCON version 2.", e.getCause());
        }

        System.out.println("NEW KEYS REMOVED");
    }

    private void init() {
        if (checkIfNodeExists()) {
            loadUserPreferences();
            clearUpPreferences();
        } else {
            if (!createDefaultPreferences()) {
                logger.error("Can't create default preferences.");
            }
        }

        registerPreferenceChangeListener();
    }

    /**
     * Returns the keys of the user preference object.
     *
     * @return keys array
     * @throws BackingStoreException thrown exception
     */
    public String[] getKeys() throws BackingStoreException {
        return userPreferences.keys();
    }

    /**
     * Returns a specified user preference by a given position in the node array.
     *
     * @param position array position
     * @return system preference as string
     */
    public String getUserPreference(int position) {
        String key = null;

        try {
            if (position < getKeys().length) {
                key = getKeys()[position];
            }
        } catch (BackingStoreException e) {
            logger.error("Can not get user preference key on position '{}'.", position, e.getCause());
        }

        return userPreferences.get(key, "");
    }

    /**
     * Returns a system preference by given name.
     *
     * @param preference reference to the preference to be reader
     * @return system preference as string
     * @since 3
     */
    public String getUserPreference(PreferenceKey preference) {
        // TODO catch java.lang.IllegalArgumentException: No enum constant for missing preference keys

        try {
            //System.out.println("Try to get UserPreference: " + preference.getKey());
            return userPreferences.get(preference.name().toLowerCase(), "");
        } catch (Exception e) {
            logger.error("Can not get user preference key '{}'.", preference.toString(), e.getCause());
            return "";
        }
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
        logger.info("preferenceChange called");
    }

    /**
     * Removes the user preference by a given key.
     * <p>
     * This function is used only for development issues of RyCON.
     *
     * @param key key of the user preference to be removed
     */
    private void removeUserPreference(String key) {
        userPreferences.remove(key);
    }

    /**
     * Sets a defined user preference by reference to the preference object and value.
     *
     * @param preference the preference object to be set
     * @param value      value to be set
     * @since 3
     */
    public void setUserPreference(PreferenceKey preference, final String value) {
        userPreferences.put(preference.name().toLowerCase(), value);
    }

    @Override
    public String toString() {
        return userPreferences.toString();
    }

    private boolean checkIfNodeExists() {
        try {
            return Preferences.userRoot().nodeExists(currentNode);
        } catch (BackingStoreException e) {
            logger.error("Check for RyCON preferences failed. Node '{}' doesn't exist in user root.", currentNode);
        }

        return false;
    }

    /**
     * Controls the writing of default values into the user preferences.
     * <p>
     * It is <b>highly recommend</b> that the user will overwrite this settings to her or his
     * preferred values after the first start of RyCON. To do this, hit the key 'p' on
     * the keyboard or choose the 'settings' button on the main window.
     */
    private boolean createDefaultPreferences() {
        userPreferences = Preferences.userRoot().node(currentNode);

        createDefaultPreferencesForDisplay();
        createDefaultPreferencesForFiles();
        createDefaultPreferencesForGeneral();
        createDefaultPreferencesForUser();
        createDefaultPreferencesForWidgetClearUp();
        createDefaultPreferencesForWidgetConverter();
        createDefaultPreferencesForWidgetGenerator();
        createDefaultPreferencesForWidgetTransfer();

        // TODO remove Rapp dependency to a more general behaviour
        setUserPreference(PreferenceKey.DIR_PROJECT_LOG_FILES, "08.Bearb_Rapp/Messdaten/LOG");
        setUserPreference(PreferenceKey.DIR_PROJECT_MEASUREMENT_FILES, "08.Bearb_Rapp/Messdaten/GSI");
        setUserPreference(PreferenceKey.DIR_PROJECT_JOB_FILES, "08.Bearb_Rapp/Messdaten/DBX");

        logger.info("Default settings created.");

        return isDefaultSettingsGenerated = true;
    }

    /**
     * Writes the default values for the {@link de.ryanthara.ja.rycon.ui.widgets.ConverterWidget}
     * of RyCON into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <ul>
     * <li>'CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE' -  'true' </li>
     * <li>'CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE' -  'false' </li>
     * <li>'CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE' - '0.03' </li>
     * <li>'CONVERTER_SETTING_ZEISS_DIALECT' - 'M5' </li>
     * </ul>
     */
    private void createDefaultPreferencesForWidgetConverter() {
        setUserPreference(PreferenceKey.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE, DefaultKey.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE.getValue());
        setUserPreference(PreferenceKey.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE, DefaultKey.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE.getValue());
        setUserPreference(PreferenceKey.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE, DefaultKey.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE.getValue());
        setUserPreference(PreferenceKey.CONVERTER_SETTING_ZEISS_DIALECT, DefaultKey.CONVERTER_SETTING_ZEISS_DIALECT.getValue());
    }

    /**
     * Writes the default values for the {@link de.ryanthara.ja.rycon.ui.widgets.ClearUpWidget}
     * of RyCON into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <ul>
     * <li>'PARAM_CONTROL_POINT_STRING' - 'STKE'
     * <li>'PARAM_FREE_STATION_STRING' - 'FS'
     * <li>'PARAM_KNOWN_STATION_STRING' - 'ST'
     * <li>'PARAM_LTOP_STRING' - 'LTOP'
     * </ul>
     */
    private void createDefaultPreferencesForWidgetClearUp() {
        setUserPreference(PreferenceKey.PARAM_CONTROL_POINT_STRING, DefaultKey.PARAM_CONTROL_POINT_STRING.getValue());
        setUserPreference(PreferenceKey.PARAM_FREE_STATION_STRING, DefaultKey.PARAM_FREE_STATION_STRING.getValue());
        setUserPreference(PreferenceKey.PARAM_KNOWN_STATION_STRING, DefaultKey.PARAM_KNOWN_STATION_STRING.getValue());
        setUserPreference(PreferenceKey.PARAM_LTOP_STRING, DefaultKey.PARAM_LTOP_STRING.getValue());
    }

    /**
     * Writes the default values for the {@link de.ryanthara.ja.rycon.ui.widgets.TransferWidget}
     * of RyCON into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <ul>
     * <li>'LAST_USED_PROJECTS' - '[]'
     * <li>'DIR_CARD_READER' - '.'
     * <li>'DIR_CARD_READER_DATA_FILES' - 'Data'
     * <li>'DIR_CARD_READER_EXPORT_FILES' - 'GSI'
     * <li>'DIR_CARD_READER_JOB_FILES' - 'DBX'
     * </ul>
     */
    private void createDefaultPreferencesForWidgetTransfer() {
        setUserPreference(PreferenceKey.LAST_USED_PROJECTS, "[]");
        setUserPreference(PreferenceKey.DIR_CARD_READER, DefaultKey.DIR_CARD_READER.getValue());
        /* Leica Geosystems card structure */
        setUserPreference(PreferenceKey.DIR_CARD_READER_DATA_FILES, "Data");
        setUserPreference(PreferenceKey.DIR_CARD_READER_EXPORT_FILES, "Gsi");
        setUserPreference(PreferenceKey.DIR_CARD_READER_JOB_FILES, "DBX");
    }

    /**
     * Writes the default values for the {@link de.ryanthara.ja.rycon.ui.widgets.GeneratorWidget}
     * of RyCON into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <ul>
     * <li>'DIR_BASE' - '.'
     * <li>'DIR_ADMIN' - './admin'
     * <li>'DIR_ADMIN_TEMPLATE' - './admin/template-folder'
     * <li>'DIR_BIG_DATA' - './big_data'
     * <li>'DIR_BIG_DATA_TEMPLATE' - './big_data/template-folder'
     * <li>'DIR_PROJECT' - './project'
     * <li>'DIR_PROJECT_TEMPLATE' - './project/template-folder'
     * </ul>
     */
    private void createDefaultPreferencesForWidgetGenerator() {
        setUserPreference(PreferenceKey.DIR_BASE, DefaultKey.DIR_BASE.getValue());
        setUserPreference(PreferenceKey.DIR_ADMIN, DefaultKey.DIR_ADMIN.getValue());
        setUserPreference(PreferenceKey.DIR_ADMIN_TEMPLATE, DefaultKey.DIR_ADMIN_TEMPLATE.getValue());
        setUserPreference(PreferenceKey.DIR_BIG_DATA, DefaultKey.DIR_BIG_DATA.getValue());
        setUserPreference(PreferenceKey.DIR_BIG_DATA_TEMPLATE, DefaultKey.DIR_BIG_DATA_TEMPLATE.getValue());
        setUserPreference(PreferenceKey.DIR_PROJECT, DefaultKey.DIR_PROJECT.getValue());
        setUserPreference(PreferenceKey.DIR_PROJECT_TEMPLATE, DefaultKey.DIR_PROJECT_TEMPLATE.getValue());
    }

    /**
     * Writes the default values for the user of RyCON
     * into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <p>
     * <ul>
     * <li>'PARAM_USER_STRING' - 'RyCON user'
     * <li>'LAST_COPIED_LOGFILE' - '.'
     * <li>'LAST_USED_DIRECTORY' - '.'
     * </ul>
     */
    private void createDefaultPreferencesForUser() {
        setUserPreference(PreferenceKey.PARAM_USER_STRING, DefaultKey.PARAM_USER_STRING.getValue());
        setUserPreference(PreferenceKey.LAST_COPIED_LOGFILE, DefaultKey.LAST_COPIED_LOGFILE.getValue());
        setUserPreference(PreferenceKey.LAST_USED_DIRECTORY, System.getProperty("user.home"));
    }

    /**
     * Writes the default values for the display settings of RyCON
     * into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <ul>
     * <li>'LAST_USED_DISPLAY' - '-1'
     * <li>'LAST_POS_PRIMARY_MONITOR' - '-9999,-9999'
     * <li>'LAST_POS_SECONDARY_MONITOR' - '-9998,-9998'
     * </ul>
     */
    private void createDefaultPreferencesForDisplay() {
        setUserPreference(PreferenceKey.LAST_USED_DISPLAY, DefaultKey.LAST_USED_DISPLAY.getValue());
        setUserPreference(PreferenceKey.LAST_POS_PRIMARY_MONITOR, DefaultKey.LAST_POS_PRIMARY_MONITOR.getValue());
        setUserPreference(PreferenceKey.LAST_POS_SECONDARY_MONITOR, DefaultKey.LAST_POS_SECONDARY_MONITOR.getValue());
    }

    /**
     * Writes the default values for files and file writing of RyCON
     * into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <ul>
     * <li>'ADD_TRAILING_ZEROES' -  'true'
     * <li>'GSI_SETTING_LINE_ENDING_WITH_BLANK' -  'true'
     * </ul>
     */
    private void createDefaultPreferencesForFiles() {
        setUserPreference(PreferenceKey.ADD_TRAILING_ZEROES, DefaultKey.ADD_TRAILING_ZEROES.getValue());
        setUserPreference(PreferenceKey.GSI_SETTING_LINE_ENDING_WITH_BLANK, DefaultKey.GSI_SETTING_LINE_ENDING_WITH_BLANK.getValue());
    }

    /**
     * Writes the default values for general purposes of RyCON
     * into the user preferences of the uses operating system.
     * <p>
     * The following parameters (parameter name - value) are used:
     * <ul>
     * <li>'BUILD_VERSION' - 'version : YYYY-MM-DD'
     * <li>'GENERATOR' - 'RyCON'
     * <li>'OVERWRITE_EXISTING' - 'false'
     * <li>'PARAM_CODE_STRING' - '_CODE'
     * <li>'PARAM_LEVEL_STRING' - '_LEVEL'
     * <li>'PARAM_EDIT_STRING' - '_EDIT'
     * </ul>
     */
    private void createDefaultPreferencesForGeneral() {
        setUserPreference(PreferenceKey.BUILD_VERSION, Version.getBuildNumber() + " : " + Version.getBuildDate());
        setUserPreference(PreferenceKey.GENERATOR, ResourceBundleUtils.getLangStringFromXml(LANG_STRING, LangString.application_Name));
        setUserPreference(PreferenceKey.OVERWRITE_EXISTING, DefaultKey.OVERWRITE_EXISTING.getValue());
        setUserPreference(PreferenceKey.PARAM_CODE_STRING, DefaultKey.PARAM_CODE_STRING.getValue());
        setUserPreference(PreferenceKey.PARAM_LEVEL_STRING, DefaultKey.PARAM_LEVEL_STRING.getValue());
        setUserPreference(PreferenceKey.PARAM_EDIT_STRING, DefaultKey.PARAM_EDIT_STRING.getValue());
    }

    private void loadUserPreferences() {
        userPreferences = Preferences.userRoot().node(currentNode);
    }

    /*
     * Clear up unused user preference entries and fills in the new ones
     * after an update or a new RyCON version.
     */
    private void clearUpPreferences() {
        try {
            final int numberOfDefaultPreferenceKeys = DefaultKey.getNumberOf();
            final int numberOfStoredPreferenceKeys = getKeys().length;

            if (numberOfDefaultPreferenceKeys == numberOfStoredPreferenceKeys) {
                logger.info("Number of default preferences keys and stored preference keys are equal.");
            } else {
                if (numberOfDefaultPreferenceKeys > numberOfStoredPreferenceKeys) {
                    // default keys are larger -> insert missing keys
                    insertMissingKeys();
                } else {
                    // stored keys are larger -> remove unused keys
                    removeUnusedKeys();
                }
            }
        } catch (BackingStoreException e) {
            logger.error("Can not load user preferences.", e.getCause());
        }
    }

    private void removeUnusedKeys() {
        List<String> defaultPreferenceKeyList = new ArrayList<>(DefaultKey.getNumberOf());

        for (DefaultKey key : DefaultKey.values()) {
            defaultPreferenceKeyList.add(key.name().toLowerCase());
        }

        try {
            List<String> storedPreferenceKeyList = new ArrayList<>(Arrays.asList(getKeys()));
            storedPreferenceKeyList.removeAll(defaultPreferenceKeyList);

            for (String key : storedPreferenceKeyList) {
                removeUserPreference(key);
                logger.info("User preference '{}' removed successfully.", key.toUpperCase());
            }
        } catch (BackingStoreException e) {
            logger.error("Can not load user preferences.", e.getCause());
        }
    }

    private void insertMissingKeys() {
        List<String> defaultPreferenceKeyList = new ArrayList<>(DefaultKey.getNumberOf());

        for (DefaultKey key : DefaultKey.values()) {
            defaultPreferenceKeyList.add(key.name().toLowerCase());
        }

        try {
            List<String> storedPreferenceKeyList = new ArrayList<>(Arrays.asList(getKeys()));
            defaultPreferenceKeyList.removeAll(storedPreferenceKeyList);

            for (String key : defaultPreferenceKeyList) {
                setUserPreference(PreferenceKey.valueOf(key.toUpperCase()), DefaultKey.valueOf(key.toUpperCase()).getValue());
                logger.info("User preference '{}' added successfully.", key.toUpperCase());
            }
        } catch (BackingStoreException e) {
            logger.error("Can not load user preferences.", e.getCause());
        }
    }

    /*
     * Add the listener to the node and not to an instance of it!
     */
    private void registerPreferenceChangeListener() {
        Preferences.userRoot().node(currentNode).addPreferenceChangeListener(this);
        logger.info("Preference listener '{}' added.", this.toString());
    }

}
