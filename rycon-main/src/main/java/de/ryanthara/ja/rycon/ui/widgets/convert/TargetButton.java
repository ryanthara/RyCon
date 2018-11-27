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
 * This enumeration is used for the target format radio buttons of the {@link ConverterWidget}.
 * <p>
 * There are buttons for the
 * <ul>
 * <li>Leica Geosystems GSI8 format</li>
 * <li>Leica Geosystems GSI16 format</li>
 * <li>Texts based formatted files</li>
 * <li>Comma separated values formatted files</li>
 * <li>Zeiss REC formats and it's dialects</li>
 * <li>LTOP KOO and MES formats</li>
 * <li>Toporail MEP and PTS formats</li>
 * <li>Microsoft Excel XLS and XLSX formats</li>
 * <li>Open Document Foundation format</li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum TargetButton {

    // this is the button order in the group
    GSI8("GSI8"),
    GSI16("GSI16"),
    TXT("TXT"),
    CSV("CSV"),
    CAPLAN_K("CAPLAN (.K)"),
    ZEISS_REC("Zeiss (.REC)"),
    LTOP_KOO("LTOP (.KOO)"),
    LTOP_MES("LTOP (.MES)"),
    TOPORAIL_MEP("Toporail (.MEP)"),
    TOPORAIL_PTS("Toporail (.PTS)"),
    EXCEL_XLSX("Excel 2007 (.xlsx)"),
    EXCEL_XLS("Excel '97 (.xls)"),
    ODF_ODS("Open Document Format (.ods)");

    private final String text;

    TargetButton(String text) {
        this.text = text;
    }

    /**
     * Returns the {@link TargetButton} by index parameter as static access for switch cases.
     *
     * @param index index to return
     * @return TargetButton by index
     */
    public static Optional<TargetButton> fromIndex(int index) {
        assert index < values().length;

        for (TargetButton targetButton : values()) {
            if (targetButton.ordinal() == index) {
                return Optional.of(targetButton);
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
