/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.converter.odf
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
package de.ryanthara.ja.rycon.converter.odf;

import org.odftoolkit.simple.SpreadsheetDocument;

import java.util.ArrayList;

/**
 * Created by sebastian on 14.09.16.
 */
public class Zeiss2ODF {

    private ArrayList<String> readStringLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Class constructor for read line based text files in different formats.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public Zeiss2ODF(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    public boolean convertZeiss2ODS(String name, boolean selection) {
        ArrayList<String> result = new ArrayList<>();

        return false;
    }

    /**
     * Returns the SpreadsheetDocument for writing it to a file.
     *
     * @return SpreadsheetDocument
     */
    public SpreadsheetDocument getSpreadsheetDocument() {
        return this.spreadsheetDocument;
    }

} // end of Zeiss2ODF
