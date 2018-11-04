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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * The {@code ApplicationKey} enumeration holds all the unchangeable application key values for RyCON.
 * <p>
 * With version 2.0 of RyCON the preference keys are split into changeable user keys and unchangeable
 * application keys. The first ones are delivered with a suitable set of {@code DefaultKey}. The second ones
 * has fixed values they got from here.
 * <p>
 * This enumeration is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum ApplicationKey {

    JAVA_WEBSITE("https://java.com/"),

    // TODO remove Rapp dependency to a more general behaviour
    DIR_PROJECT_LOG_FILES("08.Bearb_Rapp/Messdaten/LOG"),
    DIR_PROJECT_MEASUREMENT_FILES("08.Bearb_Rapp/Messdaten/GSI"),
    DIR_PROJECT_JOB_FILES("08.Bearb_Rapp/Messdaten/DBX"),

    RyCON_UPDATE_URL("https://code.ryanthara.de/content/1-projects/1-rycon/_current.version"),
    RyCON_WEBSITE("https://code.ryanthara.de/RyCON"),
    RyCON_WEBSITE_HELP("https://code.ryanthara.de/RyCON/help"),
    RyCON_WHATS_NEW_URL("https://code.ryanthara.de/content/3-RyCON/_whats.new");

    private static final Logger logger = LoggerFactory.getLogger(ApplicationKey.class.getName());
    private final String value;

    ApplicationKey(String value) {
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
            logger.error("Wrong value '{}' to create URI.", value, e.getCause());
        }

        return Optional.empty();
    }

    /**
     * Returns the value of the application key.
     *
     * @return the default key value
     */
    public String getValue() {
        return value;
    }

}
