package de.ryanthara.ja.rycon.tools;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * License: GPL. Copyright 2015- (C) by Sebastian Aust (http://www.ryanthara.de/)
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

/**
 * This class manages the strings of the Leica GSI format word indices for multi-language support in RyCON.
 * <p>
 * The strings are stored in Resource Bundle LeicaGSIWordIndices and loaded from this class.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class LeicaGSIWordIndices {

    private static final String BUNDLE_NAME = "de/ryanthara/ja/rycon/tools/LeicaGSIWordIndices";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * Returns the key-text-pair.
     *
     * @param key key to look up
     * @return matched text
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

} // end of LeicaGSIWordIndices