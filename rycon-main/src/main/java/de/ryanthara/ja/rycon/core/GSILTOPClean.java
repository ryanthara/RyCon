/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.core;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.converter.gsi.BaseToolsGSI;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.i18n.I18N;
import org.eclipse.swt.SWT;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to clean up a LTOP measurement file in the Leica GSI format.
 * <p>
 * Normally the file is a GSI8 formatted file that contains the following structure:
 * <ul>
 * <li>station line</li>
 * <li>reference point</li>
 * <li>...</li>
 * <li>reference point</li>
 * <li>control point ('STKE')</li>
 * <li>measurement points</li>
 * <li>...</li>
 * <li>measurement points</li>
 * <li>control point ('STKE')</li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class GSILTOPClean {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class given a read line based Leica GSI formatted file.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public GSILTOPClean(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Cleans up a LTOP GSI8 polar measurement file and eliminates reference points and control points.
     * <p>
     * The measurement file must have the following structure:
     * <ul>
     * <li>station line</li>
     * <li>reference point</li>
     * <li>...</li>
     * <li>reference point</li>
     * <li>control point ('STKE')</li>
     * <li>measurement points</li>
     * <li>...</li>
     * <li>measurement points</li>
     * <li>control point ('STKE')</li>
     * </ul>
     * <p>
     * Free Station lines are identified by the defined free station parameter.
     *
     * @return clean up LTOP MES file
     */
    public ArrayList<String> processLTOPClean() {
        ArrayList<String> result = new ArrayList<>();

        /*
        Strategy
            - identify a station line (WI 84, 85, 86 and 88 (instrument height)
            - identify the first control point after the reference points by the char sequence 'STKE'
            - identify the last control point before the next station line or at the file ending by the char sequence 'STKE'
         */

        int range = 0, status = 0;
        String currentStation = "", previousLine = "";

        for (String line : readStringLines) {
            int size = BaseToolsGSI.getBlockSize(line);

            if (size == 24) {
                line = line.substring(1, line.length());
            }

            int tokens = (line.length() + size - 1) / size;

            switch (tokens) {
                case 5:         // station line
                    // detect two free station lines and delete the first one of them from the result array list
                    if (previousLine.contains(Main.getParamFreeStationString())) {
                        result.remove(result.size() - 1);
                    }

                    currentStation = line.substring(line.indexOf(Main.getParamFreeStationString()), line.indexOf(" "));
                    result.add(line);

                    range = 0;
                    status = 0;
                    break;
                case 6:         // polar measurement line
                    // previous line contains a free station and a maximum of 4 reference points is used
                    if ((status == 0) & (range < 4)) {
                        if (line.contains(Main.getParamControlPointString())) {     // control point
                            range = range + 1;
                            status = 5;
                        } else {                                                    // reference point
                            range = range + 1;
                        }
                    } else if ((status == 0) & (range == 4)) {                       // no control point in range
                        MessageBoxes.showMessageBox(Main.shell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(),
                                String.format(I18N.getLTOPCleanNoControlPointWarning(), currentStation));

                        range = range + 1;
                    } else {
                        if (!line.contains(Main.getParamControlPointString())) {
                            result.add(line);
                        }
                    }
                    break;
            }

            previousLine = line;
        }

        return result;
    }

} // end of GSILTOPClean
