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
 * The {@code PreferenceKey} enumeration holds all of the preference keys for RyCON.
 * <p>
 * The default values for the keys are stored in the enum {@link DefaultKey}.
 *
 * @author sebastian
 * @version 2
 * @see PreferenceHandler
 * @since 23
 */
public enum PreferenceKey {
    ADD_TRAILING_ZEROES,
    BUILD_VERSION,
    CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE,
    CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE,
    CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE,
    CONVERTER_SETTING_ZEISS_DIALECT,
    DIR_ADMIN,
    DIR_ADMIN_TEMPLATE,
    DIR_BASE,
    DIR_BIG_DATA,
    DIR_BIG_DATA_TEMPLATE,
    DIR_CARD_READER,
    DIR_CARD_READER_DATA_FILES,
    DIR_CARD_READER_EXPORT_FILES,
    DIR_CARD_READER_JOB_FILES,
    DIR_PROJECT,
    DIR_PROJECT_JOB_FILES,
    DIR_PROJECT_LOG_FILES,
    DIR_PROJECT_MEASUREMENT_FILES,
    DIR_PROJECT_TEMPLATE,
    GENERATOR,
    GSI_SETTING_LINE_ENDING_WITH_BLANK,
    LAST_COPIED_LOGFILE,
    LAST_POS_PRIMARY_MONITOR,
    LAST_POS_SECONDARY_MONITOR,
    LAST_USED_DIRECTORY,
    LAST_USED_DISPLAY,
    LAST_USED_PROJECTS,
    OVERWRITE_EXISTING,
    PARAM_CODE_STRING,
    PARAM_CONTROL_POINT_STRING,
    PARAM_EDIT_STRING,
    PARAM_FREE_STATION_STRING,
    PARAM_KNOWN_STATION_STRING,
    PARAM_LEVEL_STRING,
    PARAM_LTOP_STRING,
    PARAM_USER_STRING;

    /**
     * Returns the number of {@code PreferenceKey} in the enumeration.
     *
     * @return number of preference keys
     */
    public static int getNumberOf() {
        return values().length;
    }

}
