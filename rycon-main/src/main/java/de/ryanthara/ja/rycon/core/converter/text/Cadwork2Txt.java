package de.ryanthara.ja.rycon.core.converter.text;

import de.ryanthara.ja.rycon.util.NumberFormatter;

import java.util.ArrayList;

/**
 * This class provides functions to convert a coordinate file from Cadwork CAD program into a text formatted file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Cadwork2Txt {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for reader line based text files from Cadwork CAD program in node.dat file format.
     *
     * @param readStringLines {@code ArrayList<String>} with reader lines from node.dat file
     */
    public Cadwork2Txt(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a coordinate file from Cadwork (node.dat) into a text formatted file.
     * <p>
     * With parameter it is possible to set options for writing a code column or zero heights.
     *
     * @param separator       distinguish between tabulator or space as division sign
     * @param writeCodeColumn use the code column from node.dat and writer it out
     * @param useZeroHeights  use heights with zero (0.000) values
     *
     * @return converted {@code ArrayList<String>} with lines in text format
     */
    public ArrayList<String> convertCadwork2TXT(String separator, boolean writeCodeColumn, boolean useZeroHeights) {
        ArrayList<String> result = new ArrayList<>();

        // remove not needed headlines
        for (int i = 0; i < 3; i++) {
            readStringLines.remove(0);
        }

        for (String line : readStringLines) {
            String[] lineSplit = line.trim().split("\\s+", -1);

            // point number, column 1 - 16
            String number = lineSplit[5];

            // easting E, column 19-32
            String easting = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[1], 4));

            // northing N, column 33-46
            String northing = String.format("%14s", NumberFormatter.fillDecimalPlace(lineSplit[2], 4));

            // height H, column 61-70
            String height = "";
            if (useZeroHeights) {
                height = String.format("%10s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
            } else {
                if (!lineSplit[3].equals("0.000000")) {
                    height = String.format("%10s", NumberFormatter.fillDecimalPlace(lineSplit[3], 4));
                }
            }

            // code is the same as object type, column 62...
            String code = "";
            if (writeCodeColumn) {
                code = lineSplit[4];
            }

            String s = number + separator + easting + separator + northing + separator + height + separator + code;

            result.add(s.trim());
        }

        return result;
    }

} // end of Cadwork2Txt
