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
package de.ryanthara.ja.rycon.core.converter.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate files from the geodata
 * server Basel Landschaft (Switzerland) into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TxtBaselLandschaft2Txt {


    private static final Logger logger = LoggerFactory.getLogger(TxtBaselLandschaft2Txt.class.getName());

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text files
     * from the geodata server Basel Landschaft (Switzerland).
     *
     * @param lines list with coordinate lines
     */
    public TxtBaselLandschaft2Txt(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a text file from the geodata server Basel Landschaft (Switzerland)
     * into a text formatted file (no code x y z).
     * <p>
     * This method can differ between LFP and HFP files, which has a given different structure.
     * With a parameter it is possible to distinguish between tabulator and space divided files.
     *
     * @param separator       distinguish between tabulator or space as division sign
     * @param writeCodeColumn use 'Versicherungsart' (LFP) as code column on second position
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(String separator, boolean writeCodeColumn) {
        List<String> result = new ArrayList<>();

        removeCommentLine();

        for (String line : lines) {
            String s;

            String[] values = line.trim().split("\\t", -1);

            // point number is in column 2
            s = values[1];
            s = s.concat(separator);

            switch (values.length) {
                case 5:     // HFP file
                    // easting (Y) is in column 3
                    s = s.concat(values[2]);
                    s = s.concat(separator);

                    // northing (X) is in column 4
                    s = s.concat(values[3]);
                    s = s.concat(separator);

                    // height (Z) is in column 5, and always valued (HFP file)
                    s = s.concat(values[4]);
                    s = s.concat(separator);

                    result.add(s.trim());
                    break;

                case 6:     // LFP file
                    // use 'Versicherungsart' as code. It is in column 3
                    if (writeCodeColumn) {
                        s = s.concat(values[2]);
                        s = s.concat(separator);
                    }

                    // easting (Y) is in column 4
                    s = s.concat(values[3]);
                    s = s.concat(separator);

                    // northing (X) is in column 5
                    s = s.concat(values[4]);
                    s = s.concat(separator);

                    // height (Z) is in column 6, and not always valued (LFP file)
                    if (values[5].equals("NULL")) {
                        s = s.concat("-9999");
                    } else {
                        s = s.concat(values[5]);
                    }

                    result.add(s.trim());
                    break;

                default:
                    logger.trace("Line contains less or more tokens ({}) than needed or allowed.", values.length);
                    break;
            }
        }
        return List.copyOf(result);
    }

    private void removeCommentLine() {
        lines.remove(0);
    }

}
