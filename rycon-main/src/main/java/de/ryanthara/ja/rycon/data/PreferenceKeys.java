/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

/**
 * The {@code PreferenceKeys} enumeration holds all of the preference keys for {@code RyCON}.
 * <p>
 * The default values for the keys are stored in the enum {@link DefaultKeys}.
 * <p>
 * Due to some issues with the representation of the windows registry keys, the second version
 * of the {@code PreferenceKeys} uses this improved implementation with lower case key values.
 *
 * @author sebastian
 * @version 2
 * @see PreferenceHandler
 * @since 23
 */
public enum PreferenceKeys {

    ADD_TRAILING_ZEROES("add_trailing_zeroes"),
    BUILD_VERSION("build_version"),
    CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE("converter_setting_eliminate_zero_coordinate"),
    CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE("converter_setting_ltop_use_zenith_distance"),
    CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE("converter_setting_point_identical_distance"),
    CONVERTER_SETTING_ZEISS_DIALECT("converter_setting_zeiss_dialect"),
    DIR_ADMIN("dir_admin"),
    DIR_ADMIN_TEMPLATE("dir_admin_template"),
    DIR_BASE("dir_base"),
    DIR_BIG_DATA("dir_big_data"),
    DIR_BIG_DATA_TEMPLATE("dir_big_data_template"),
    DIR_CARD_READER("dir_card_reader"),
    DIR_CARD_READER_DATA_FILES("dir_card_reader_data_files"),
    DIR_CARD_READER_EXPORT_FILES("dir_card_reader_export_files"),
    DIR_CARD_READER_JOB_FILES("dir_card_reader_job_files"),
    DIR_PROJECT("dir_project"),
    DIR_PROJECT_JOB_FILES("dir_project_job_files"),
    DIR_PROJECT_LAST_USED("dir_project_last_used"),
    DIR_PROJECT_LOG_FILES("dir_project_log_files"),
    DIR_PROJECT_MEASUREMENT_FILES("dir_project_measurement_files"),
    DIR_PROJECT_TEMPLATE("dir_project_template"),
    GENERATOR("generator"),
    GSI_SETTING_LINE_ENDING_WITH_BLANK("gsi_setting_line_ending_with_blank"),
    INFORMATION_STRING("information_string"),
    LAST_COPIED_LOGFILE("last_copied_logfile"),
    LAST_POS_PRIMARY_MONITOR("last_pos_primary_monitor"),
    LAST_POS_SECONDARY_MONITOR("last_pos_secondary_monitor"),
    LAST_USED_DISPLAY("last_used_display"),
    LAST_USED_PROJECTS("last_used_projects"),
    OVERWRITE_EXISTING("overwrite_existing"),
    PARAM_CODE_STRING("param_code_string"),
    PARAM_CONTROL_POINT_STRING("param_control_point_string"),
    PARAM_EDIT_STRING("param_edit_string"),
    PARAM_FREE_STATION_STRING("param_free_station_string"),
    PARAM_KNOWN_STATION_STRING("param_known_station_string"),
    PARAM_LTOP_STRING("param_ltop_string"),
    PARAM_LEVEL_STRING("param_level_string"),
    PARAM_USER_STRING("param_user_string"),
    USER_LAST_USED_DIR("user_last_used_dir");

    private final String key;

    PreferenceKeys(String key) {
        this.key = key;
    }

    /**
     * Checks whether the key is inside the {@link PreferenceKeys} enum or not.
     *
     * @param key key to be in the enum
     *
     * @return true if the enum contains the key
     */
    public static boolean contains(final String key) {
        for (PreferenceKeys prefKey : PreferenceKeys.values()) {
            if (prefKey.getKey().equals(key)) {
                return true;
            }
        }

        return false;
    }

    public String getKey() {
        return key;
    }

} // end of PreferenceKeys
