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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code DefaultKeys} enumeration holds all the default values for {@code RyCON}.
 * <p>
 * This enumeration is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum DefaultKeys {

    CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE("true"),
    CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE("false"),
    CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE("0.03"),
    CONVERTER_SETTING_ZEISS_DIALECT("M5"),

    DIR_ADMIN("./admin"),
    DIR_ADMIN_TEMPLATE("./admin/template-folder"),
    DIR_BASE("."),
    DIR_BIG_DATA("./big_data"),
    DIR_BIG_DATA_TEMPLATE("./big_data/template-folder"),
    DIR_PROJECT("./projects"),
    DIR_PROJECT_TEMPLATE("./projects/template-folder"),

    ADD_TRAILING_ZEROES("true"),
    GSI_SETTING_LINE_ENDING_WITH_BLANK("true"),

    // transfer widget
    // TODO remove Rapp dependency to a more general behaviour
    DIR_CARD_READER("."),
    DIR_CARD_READER_DATA_FILES("Data"),
    DIR_CARD_READER_EXPORT_FILES("Gsi"),
    DIR_CARD_READER_JOB_FILES("DBX"),
    DIR_PROJECT_LOG_FILES( "08.Bearb_Rapp/Messdaten/LOG"),
    DIR_PROJECT_MEASUREMENT_FILES( "08.Bearb_Rapp/Messdaten/GSI"),
    DIR_PROJECT_JOB_FILES( "08.Bearb_Rapp/Messdaten/DBX"),

    LAST_POS_PRIMARY_MONITOR("-9999, -9999"),
    LAST_POS_SECONDARY_MONITOR("-9998, -9998"),
    LAST_USED_DISPLAY("-1"),

    PARAM_CODE_STRING("CODE"),
    PARAM_CONTROL_POINT_STRING("STKE"),
    PARAM_EDIT_STRING("EDIT"),
    PARAM_FREE_STATION_STRING("FS"),
    PARAM_KNOWN_STATION_STRING("ST"),
    PARAM_LEVEL_STRING("LEVEL"),
    PARAM_LTOP_STRING("LTOP"),

    OVERWRITE_EXISTING("false"),

    RyCON_UPDATE_URL("https://code.ryanthara.de/content/3-RyCON/_current.version"),
    RyCON_WEBSITE("https://code.ryanthara.de/RyCON"),
    RyCON_WEBSITE_HELP("https://code.ryanthara.de/RyCON/help"),
    RyCON_WHATS_NEW_URL("https://code.ryanthara.de/content/3-RyCON/_whats.new"),

    JAVA_WEBSITE("https://java.com/");

    private final static Logger logger = Logger.getLogger(DefaultKeys.class.getName());
    private String value;

    DefaultKeys(String value) {
        this.value = value;
    }

    /**
     * Returns the default key value as URI.
     *
     * @return default key value as URI
     */
    public Optional<URI> getURI() {
        try {
            return Optional.of(new URI(value));
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "wrong value to create URI: " + value, e);
        }

        return Optional.empty();
    }

    /**
     * Returns the value of the default key.
     *
     * @return the default key value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the default key value as string.
     *
     * @return the default key value as string
     */
    @Override
    public String toString() {
        return value;
    }

} // end of DefaultKeys
