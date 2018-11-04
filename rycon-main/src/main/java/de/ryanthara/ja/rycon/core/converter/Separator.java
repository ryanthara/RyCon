/*
 * License: GPL. Copyright 2018- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter
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
package de.ryanthara.ja.rycon.core.converter;

/**
 * Contains all the used separator signs for the classes in
 * the {@link de.ryanthara.ja.rycon.core.converter} package.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public enum Separator {

    COMMA(","), DOT("."), SEMICOLON(";"), TABULATOR("\t"), WHITESPACE(" ");

    private final String sign;

    Separator(String sign) {
        this.sign = sign;
    }

    /**
     * Returns the separator sign as string.
     *
     * @return separator sign string
     */
    public String getSign() {
        return this.sign;
    }

}
