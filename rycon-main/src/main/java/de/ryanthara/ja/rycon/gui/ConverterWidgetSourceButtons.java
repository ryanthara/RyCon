/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui
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
package de.ryanthara.ja.rycon.gui;

/**
 * This enumeration is used for the source radio buttons of the {@link ConverterWidget}.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
enum ConverterWidgetSourceButtons {

    GSI8("GSI8"),
    GSI16("GSI16"),
    TXT("TXT"),
    CSV("CSV"),
    CAPLAN_K("CAPLAN (.K)"),
    ZEISS_REC("Zeiss (.REC)"),
    CADWORK("cadwork (node.dat)"),
    BASEL_STADT("Basel Stadt (.CSV)"),
    BASEL_LANDSCHAFT("Basel Landschaft (.TXT)");

    private final String text;

    ConverterWidgetSourceButtons(String text) {
        this.text = text;
    }

    /**
     * Returns the text.
     *
     * @return the text
     */
    String getText() {
        return this.text;
    }

} // end of ConverterWidgetSourceButtons
