/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The {@code ResourceBundleUtils} prepares all the texts for {@code RyCON}.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public final class ResourceBundleUtils {

    private static final String INDICATOR_MISSING_RESOURCE = "?";
    private static final String INDICATOR_MISSING_KEY = "??";

    public static String getLangString(final ResourceBundles bundleName, final ResourceKeys key) {

        ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName.getBundleName());

        if (resourceBundle != null) {
            try {
                return resourceBundle.getString(key.toString());
            } catch (final MissingResourceException e) {
                return INDICATOR_MISSING_KEY + key;
            }
        }

        return INDICATOR_MISSING_RESOURCE + key;

    }

} // end of ResourceBundleUtils
