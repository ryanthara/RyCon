/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.nio
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
package de.ryanthara.ja.rycon.nio;

/**
 * The {@code FileNameExtension} enumeration holds all filename extension strings for {@code RyCON}.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public enum FileNameExtension {

    ASC(".ASC"),
    CSV(".CSV"),
    DAT(".DAT"),
    K(".K"),
    KOO(".KOO"),
    LEICA_GSI(".GSI"),
    MEP(".MEP"),
    MES(".MES"),
    REC(".REC"),
    ODS(".ODS"),
    TPS(".TPS"),
    TXT(".TXT"),
    XLS(".XLS"),
    XLSX(".XLSX");

    private final String extension;

    FileNameExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Returns the filename extension (e.g. '.DAT').
     *
     * @return filename extension
     */
    public String getExtension() {
        return extension;
    }

} // end of FileNameExtension
