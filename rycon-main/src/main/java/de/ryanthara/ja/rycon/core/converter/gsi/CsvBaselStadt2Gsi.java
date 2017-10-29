package de.ryanthara.ja.rycon.core.converter.gsi;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class provides functions to convert csv formatted coordinate file from the geodata server
 * Basel Stadt (Switzerland) into a Leica GSI8 or GSI16 file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2Gsi {

    private List<String[]> readCSVLines = null;

    /**
     * Constructs a new instance of this class with a parameter for the reader line based CSV files from the
     * geodata server Basel Stadt (Switzerland).
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public CsvBaselStadt2Gsi(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (Switzerland) into a Leica GSI file.
     * <p>
     * With a parameter it is possible to distinguish between GSI8 and GSI16
     *
     * @param isGSI16                  distinguish between GSI8 or GSI16
     * @param sourceContainsCodeColumn if source file contains a code column
     *
     * @return converted {@code ArrayList<String>} with lines of text format
     */
    public ArrayList<String> convertCSVBaselStadt2GSI(boolean isGSI16, boolean sourceContainsCodeColumn) {
        ArrayList<String> result = new ArrayList<>();

        // remove comment line
        readCSVLines.remove(0);

        for (String[] stringField : readCSVLines) {
            String line;

            // point number is in column 1
            line = stringField[0].replaceAll("\\s+", "").trim();
            line = line.concat(" ");

            // easting (Y) is in column 3
            line = line.concat(stringField[2]);
            line = line.concat(" ");

            // northing (X) is in column 4
            line = line.concat(stringField[3]);
            line = line.concat(" ");

            // height (Z) is in column 5, but not always valued
            if (!stringField[4].equals("")) {
                line = line.concat(stringField[4]);
            } else {
                line = line.concat("-9999");
            }

            result.add(line.trim());
        }

        Txt2Gsi txt2Gsi = new Txt2Gsi(result);

        return txt2Gsi.convertTXT2GSI(isGSI16, sourceContainsCodeColumn);
    }

} // end of CsvBaselStadt2Gsi
