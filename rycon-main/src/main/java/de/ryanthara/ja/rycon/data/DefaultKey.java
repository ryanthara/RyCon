/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.i18n
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

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.LANG_STRING;

/**
 * The {@code DefaultKey} enumeration holds all the default values for RyCON.
 * <p>
 * Within the {@link de.ryanthara.ja.rycon.ui.widgets.SettingsWidget} of RyCON
 * a user can store individual settings for a number of functions. These values are set
 * with default values after the first start of RyCON and when the user reset
 * one or more values in the preferences tab.
 * <p>
 * This enumeration is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum DefaultKey {
    ADD_TRAILING_ZEROES("true"),
    BUILD_VERSION(Version.getBuildNumber() + " : " + Version.getBuildDate()),
    CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE("true"),
    CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE("false"),
    CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE("0.03"),
    CONVERTER_SETTING_ZEISS_DIALECT("M5"),
    DIR_ADMIN("./admin"),
    DIR_ADMIN_TEMPLATE("./admin/template-folder"),
    DIR_BASE("."),
    DIR_BIG_DATA("./big_data"),
    DIR_BIG_DATA_TEMPLATE("./big_data/template-folder"),
    DIR_CARD_READER("."),
    DIR_CARD_READER_DATA_FILES("Data"),
    DIR_CARD_READER_EXPORT_FILES("Gsi"),
    DIR_CARD_READER_JOB_FILES("DBX"),
    DIR_PROJECT("./projects"),
    // TODO remove Rapp dependency to a more general behaviour
    DIR_PROJECT_JOB_FILES("08.Bearb_Rapp/Messdaten/DBX"),
    DIR_PROJECT_LOG_FILES("08.Bearb_Rapp/Messdaten/LOG"),
    DIR_PROJECT_MEASUREMENT_FILES("08.Bearb_Rapp/Messdaten/GSI"),
    DIR_PROJECT_TEMPLATE("./projects/template-folder"),
    GENERATOR(ResourceBundleUtils.getLangStringFromXml(LANG_STRING, LangString.application_Name)),
    GSI_SETTING_LINE_ENDING_WITH_BLANK("true"),
    LAST_COPIED_LOGFILE(""),
    LAST_POS_PRIMARY_MONITOR("-9999, -9999"),
    LAST_POS_SECONDARY_MONITOR("-9998, -9998"),
    LAST_USED_DISPLAY("-1"),
    LAST_USED_DIRECTORY("."),
    LAST_USED_PROJECTS("."),
    OVERWRITE_EXISTING("false"),
    PARAM_CODE_STRING("_CODE"),
    PARAM_CONTROL_POINT_STRING("STKE"),
    PARAM_EDIT_STRING("_EDIT"),
    PARAM_FREE_STATION_STRING("FS"),
    PARAM_KNOWN_STATION_STRING("ST"),
    PARAM_LEVEL_STRING("_LEVEL"),
    PARAM_LTOP_STRING("_LTOP"),
    PARAM_USER_STRING("RyCON user");

    private final String value;

    DefaultKey(String value) {
        this.value = value;
    }

    /**
     * Returns the number of {@code DefaultKey} in the enumeration.
     *
     * @return number of default keys
     */
    public static int getNumberOf() {
        return values().length;
    }

    /**
     * Returns the value of the default key.
     *
     * @return the default key value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DefaultKey{" +
                "value='" + value + '\'' +
                '}';
    }

}
