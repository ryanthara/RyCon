/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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

package de.ryanthara.ja.rycon.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * CaplanKDescription manages the strings of the Caplan K description for multi-language support in RyCON.
 * <p>
 * The strings are stored in Resource Bundle CaplanKDescription and loaded from this class.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 9
 */
class CaplanKDescription {

    private static final String BUNDLE_NAME = "de/ryanthara/ja/rycon/converter/CaplanKDescription";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * Return the key-text-pair for the description.
     *
     * @param description key to look up
     * @return matched text
     */
    static String getDescription(String description) {
        try {
            return RESOURCE_BUNDLE.getString(description);
        } catch (MissingResourceException e) {
            return '!' + description + '!';
        }
    }

} // end of GISWordIndices
