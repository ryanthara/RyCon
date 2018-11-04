package de.ryanthara.ja.rycon.core.converter.text;

import de.ryanthara.ja.rycon.util.NumberFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert Cadwork CAD
 * program coordinate files into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2Txt {

    private final List<String> lines;

    /**
     * Creates a converter with a list for the read line based text file from Cadwork CAD program.
     *
     * @param lines list with read node.dat lines
     */
    public Cadwork2Txt(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a text formatted file.
     * <p>
     * With parameter it is possible to set options for writing a code column or zero heights.
     *
     * @param separator       distinguish between tabulator or space as division sign
     * @param writeCodeColumn use the code column from node.dat and writer it out
     * @param useZeroHeights  use heights with zero (0.000) values
     * @return converted {@code List<String>} with lines in text format
     */
    public List<String> convert(String separator, boolean writeCodeColumn, boolean useZeroHeights) {
        List<String> result = new ArrayList<>();

        removeHeadlines();

        for (String line : lines) {
            String[] values = line.trim().split("\\s+", -1);

            // point number, column 1 - 16
            String number = values[5];

            // easting E, column 19-32
            String easting = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[1], 4));

            // northing N, column 33-46
            String northing = String.format("%14s", NumberFormatter.fillDecimalPlaces(values[2], 4));

            // height H, column 61-70
            String height = "";
            if (useZeroHeights) {
                height = String.format("%10s", NumberFormatter.fillDecimalPlaces(values[3], 4));
            } else {
                if (!values[3].equals("0.000000")) {
                    height = String.format("%10s", NumberFormatter.fillDecimalPlaces(values[3], 4));
                }
            }

            // code is the same as object type, column 62...
            String code = "";
            if (writeCodeColumn) {
                code = values[4];
            }

            String s = number + separator + easting + separator + northing + separator + height + separator + code;

            result.add(s.trim());
        }

        return List.copyOf(result);
    }

    private void removeHeadlines() {
        lines.subList(0, 3).clear();
    }

}
