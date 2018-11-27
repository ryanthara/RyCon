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

import de.ryanthara.ja.rycon.data.XMLResourceBundleControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.MissingResourceException;

/**
 * The {@code ResourceBundleUtils} prepares all the texts for RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public final class ResourceBundleUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResourceBundleUtils.class.getName());

    private static final String INDICATOR_MISSING_RESOURCE = "?";
    private static final String INDICATOR_MISSING_KEY = "??";

    public static String getLangString(ResourceBundles bundleName, final ResourceKey key) {
        java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle(bundleName.getBundleName());

        if (resourceBundle != null) {
            try {
                return resourceBundle.getString(key.toString());
            } catch (MissingResourceException e) {
                logger.error("String '{}' is not in the resource bundle '{}'.", key.toString(), bundleName.getBundleName(), e.getCause());
                return INDICATOR_MISSING_KEY + key;
            }
        }

        return INDICATOR_MISSING_RESOURCE + key;
    }

    public static String getLangStringFromXml(ResourceBundles bundleName, final ResourceKey key) {
        java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle(bundleName.getBundleName(), new XMLResourceBundleControl());

        if (resourceBundle != null) {
            try {
                return resourceBundle.getString(key.toString());
            } catch (MissingResourceException e) {
                logger.error("Missing key '{}", key.toString(), e.getCause());
                return INDICATOR_MISSING_KEY + key;
            }
        }

        logger.warn("Missing resource '{}", key.toString());

        return INDICATOR_MISSING_RESOURCE + key;
    }

}
