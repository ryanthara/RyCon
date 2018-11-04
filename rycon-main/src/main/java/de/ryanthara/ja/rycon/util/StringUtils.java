/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.util
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

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Helper class containing useful string manipulation functions.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public final class StringUtils {

    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class.getName());

    /**
     * StringUtils is non-instantiable.
     */
    private StringUtils() {
        throw new AssertionError();
    }

    /**
     * Fills the given string with whitespaces from the beginning.
     *
     * @param input  the input string
     * @param length the length
     * @return with whitespace filled string of defined length
     * @throws NullPointerException     will be thrown if input is null
     * @throws IllegalArgumentException will be thrown if length is 0
     */
    public static String fillWithSpacesFromBeginning(String input, int length) {
        Objects.requireNonNull(input, "input must not be null!");

        if (input.length() == 0) {
            throw new IllegalArgumentException("input length must not be 0!");
        }

        String format = "%" + length + "." + length + "s";

        return String.format(format, input);
    }

    /**
     * Fills the given string with zeros from the beginning.
     *
     * @param input  the input string
     * @param length the length
     * @return with whitespace filled string of defined length
     * @throws NullPointerException     will be thrown if input is null
     * @throws IllegalArgumentException will be thrown if length is 0
     */
    public static String fillWithZerosFromBeginning(String input, int length) {
        Objects.requireNonNull(input, "input must not be null!");

        if (input.length() == 0) {
            throw new IllegalArgumentException("input length must not be 0!");
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length - input.length(); i++) {
            builder.append("0");
        }

        return builder.append(input).toString();
    }

    /**
     * Returns the plural message part of the given string.
     *
     * <p>
     * In the properties file the string "§§" is used as separator between the one
     * line singular and plural message.
     *
     * @param message message that contains singular and plural message string
     * @return plural message string
     */
    public static String getPluralMessage(String message) {
        return prepareMessage(message, CountingWord.PLURAL_MESSAGE.countingWord);
    }

    /**
     * Returns the singular message part of the given string.
     *
     * <p>
     * In the properties file the string "§§" is used as separator between the one
     * line singular and plural message.
     *
     * @param message message that contains singular and plural message string
     * @return singular message string
     */
    public static String getSingularMessage(String message) {
        return prepareMessage(message, CountingWord.SINGULAR_MESSAGE.countingWord);
    }

    private static String prepareMessage(String message, int countingWord) {
        Objects.requireNonNull(message, "message must not be null!");

        if (message.contains("§§")) {
            final String[] split = message.split("§§");

            if (split.length > 0) {
                return split[countingWord];
            } else {
                throw new IllegalArgumentException("message must not be an empty string!");
            }
        } else {
            logger.warn("message '{}' does not contain the split indicator '§§'", message);
            throw new IllegalArgumentException("message does not contain the split indicator '§§'");
        }
    }

    /**
     * Parses a string stored boolean {@link PreferenceKey} value to boolean.
     *
     * @param key preference key to be parsed
     * @return parsed boolean value - false if parsing fails
     */
    public static boolean parseBooleanValue(PreferenceKey key) {
        try {
            return Boolean.parseBoolean(Main.pref.getUserPreference(key));
        } catch (Exception e) {
            logger.error("Can not convert user preference '{}' to boolean.", Main.pref.getUserPreference(key), e.getCause());
        }

        return false;
    }

    /**
     * Parses a string stored double value to double.
     *
     * @param value value to be parsed
     * @return parsed double value - Double.NEGATIVE_INFINITY if parsing fails
     */
    public static double parseDoubleValue(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.error("Can not parse string value '{}' to double.", value, e.getCause());
        }

        return Double.NEGATIVE_INFINITY;
    }

    /**
     * Parses a string stored double {@link PreferenceKey} value to double.
     *
     * @param key preference key to be parsed
     * @return parsed double value - Double.NEGATIVE_INFINITY if parsing fails
     */
    public static double parseDoubleValue(PreferenceKey key) {
        try {
            return Double.parseDouble(Main.pref.getUserPreference(key));
        } catch (NumberFormatException e) {
            logger.error("Can not parse string value '{}' to double.", Main.pref.getUserPreference(key), e.getCause());
        }

        return Double.NEGATIVE_INFINITY;
    }

    /**
     * Parses a string stored integer {@link PreferenceKey} value to integer.
     *
     * @param key preference key to be parsed
     * @return parsed int value - Integer.MIN_VALUE if parsing fails
     */
    public static int parseIntegerValue(PreferenceKey key) {
        try {
            return Integer.parseInt(Main.pref.getUserPreference(key));
        } catch (NumberFormatException e) {
            logger.error("Can not parse string value '{}' to integer.", Main.pref.getUserPreference(key), e.getCause());
        }

        return Integer.MIN_VALUE;
    }

    /**
     * Parses a string stored integer value to integer.
     *
     * @param value value to be parsed
     * @return parsed integer value - Integer.MIN_VALUE if parsing fails
     */
    public static int parseIntegerValue(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("Can not parse string value '{}' to integer.", value, e.getCause());
        }

        return Integer.MIN_VALUE;
    }

    /**
     * Parses a string stored short value to short.
     *
     * @param value value to be parsed
     * @return parsed short value - Integer.MIN_VALUE if parsing fails
     */
    static short parseShort(String value) {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            logger.error("Can not parse string value '{}' to short.", value, e.getCause());
        }

        return Short.MIN_VALUE;
    }

    private enum CountingWord {
        SINGULAR_MESSAGE(0), PLURAL_MESSAGE(1);

        final int countingWord;

        CountingWord(int countingWord) {
            this.countingWord = countingWord;
        }
    }

}
