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
package de.ryanthara.ja.rycon.ui.widgets.convert;

import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;

import java.util.Optional;

/**
 * This enumeration is used for the source radio buttons of the {@link ConverterWidget}.
 * <p>
 * There are buttons for the
 * <ul>
 * <li>Leica Geosystems GSI8 format</li>
 * <li>Leica Geosystems GSI16 format</li>
 * <li>Texts based formatted files</li>
 * <li>Comma separated values formatted files</li>
 * <li>Caplan K format</li>
 * <li>Zeiss REC formats and it's dialects</li>
 * <li>Cadwork node.dat format</li>
 * <li>Basel Stadt CSV based format</li>
 * <li>Basel Landschaft TXT based format</li>
 * <li>Toporail MEP format</li>
 * <li>Toporail PTS format</li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum SourceButton {

    // this is the button order in the group
    GSI8("GSI8"),
    GSI16("GSI16"),
    TXT("TXT"),
    CSV("CSV"),
    CAPLAN_K("CAPLAN (.K)"),
    ZEISS_REC("Zeiss (.REC)"),
    CADWORK("cadwork (node.dat)"),
    BASEL_STADT("Geodaten Basel Stadt"),
    BASEL_LANDSCHAFT("Geodaten Basel Landschaft"),
    TOPORAIL_MEP("Toporail (.MEP)"),
    TOPORAIL_PTS("Toporail (.PTS)");

    private final String text;

    SourceButton(String text) {
        this.text = text;
    }

    /**
     * Returns the {@link SourceButton} by index parameter as static access for switch cases.
     *
     * @param index index to return
     * @return SourceButton by index
     */
    public static Optional<SourceButton> fromIndex(int index) {
        assert index < values().length;

        for (SourceButton sourceButton : values()) {
            if (sourceButton.ordinal() == index) {
                return Optional.of(sourceButton);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the text.
     *
     * @return the text
     */
    public String getText() {
        return this.text;
    }

}
