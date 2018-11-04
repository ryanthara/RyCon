/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.excel
 *
 * This package is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation(""), either version 3 of the License(""), or (at your option) any later
 * version.
 *
 * This package is distributed in the hope that it will be useful(""), but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this package. If not(""), see <http://www.gnu.org/licenses/>.
 */
package de.ryanthara.ja.rycon.nio;

/**
 * Provides different file format abbreviations and it's suffixes.
 *
 * <p>
 * The following file formats are provided.
 * <ul>
 * <li>MEP and PTS for Toporail
 * <li>XLS and XLSX for Microsoft Excel
 * <li>
 * </ul>
 *
 * <p>
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public enum FileFormat {

    MEP(".mep"), PTS(".pts"), XLS(".xls"), XLSX(".xlsx");

    private final String suffix;

    FileFormat(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Returns the suffix string of the file format.
     *
     * <p>
     * The suffix string is lower case with dot, e.g. '.txt' for text files.
     *
     * @return suffix string
     */
    public String getSuffix() {
        return suffix;
    }
}
