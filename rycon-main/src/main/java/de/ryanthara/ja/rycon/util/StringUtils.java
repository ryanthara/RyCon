/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.util;

/**
 * The {@code StringUtils} is a small set of utility methods for string operations.
 * <p>
 * Due to the usage of this helper functions the implementation are done with static methods.
 *
 * @author sebastian
 * @version 1
 * @since 2
 */
public class StringUtils {

    /**
     * Helper for string preparation to differ between singular and plural text.
     * <p>
     * In the properties file the string "§§" is used as separator between the one
     * line singular and plural message. The switch to differ between singular and
     * plural strings is set with {@code Main.TEXT_SINGULAR} and {@code Main.TEXT_PLURAL}
     *
     * @param message  message to look up
     * @param singular set to get a singular or plural text back
     *
     * @return singular or plural string message
     */
    public static String singularPluralMessage(final String message, final boolean singular) {
        final String[] s = message.split("§§");

        if (singular) {
            return s[0];
        } else {
            return s[1];
        }
    }

} // end of StringUtils
