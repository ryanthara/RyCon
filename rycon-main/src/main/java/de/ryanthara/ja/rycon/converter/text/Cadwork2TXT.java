package de.ryanthara.ja.rycon.converter.text;

import java.util.ArrayList;

/**
 * This class provides functions to convert coordinate files from Cadwork CAD program into text formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */public class Cadwork2TXT {

    private ArrayList<String> readStringLines;

    /**
     * Class constructor for read line based coordinate files from cadwork CAD program.
     *
     * @param readStringLines {@code ArrayList<String>} with lines as {@code String}
     */
    public Cadwork2TXT(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a cadwork node.dat file into a text formatted file.
     * <p>
     * Due to issues maybe data precision is going to be lost.
     *
     * @param useCodeColumn  Use the code column from node.dat
     * @param useZeroHeights Use heights with zero (0.000) values
     *
     * @return converted {@code ArrayList<String>} with lines of GSI8 or GSI16 format
     */
    public ArrayList<String> convertCadwork2TXT(boolean useCodeColumn, boolean useZeroHeights) {
        return null;
    }

} // end of Cadwork2TXT
