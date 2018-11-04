package de.ryanthara.ja.rycon.core.converter.gsi;

import de.ryanthara.ja.rycon.core.converter.Separator;

import java.util.ArrayList;
import java.util.List;

/**
 * A converter with functions to convert coordinate coordinate files from the geodata
 * server Basel Stadt (Switzerland) into a Leica Geosystems GSI8 or GSI16 file.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvBaselStadt2Gsi {

    private final List<String[]> lines;

    /**
     * Creates a converter with a list for the read line based comma separated
     * values (CSV) file from the geodata server Basel Stadt (Switzerland).
     *
     * @param lines list with lines as string array
     */
    public CsvBaselStadt2Gsi(List<String[]> lines) {
        this.lines = new ArrayList<>(lines);
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (Switzerland) into a Leica Geosystems GSI file.
     * <p>
     * With a parameter it is possible to distinguish between GSI8 and GSI16
     *
     * @param isGSI16                  distinguish between GSI8 or GSI16
     * @param sourceContainsCodeColumn if source file contains a code column
     * @return converted {@code List<String>} with lines of text format
     */
    public List<String> convert(boolean isGSI16, boolean sourceContainsCodeColumn) {
        List<String> result = new ArrayList<>();

        removeHeadLine();

        for (String[] values : lines) {
            // point number is in column 1
            String line = values[0].replaceAll("\\s+", "").trim();
            line = line.concat(Separator.WHITESPACE.getSign());

            // easting (Y) is in column 3
            line = line.concat(values[2]);
            line = line.concat(Separator.WHITESPACE.getSign());

            // northing (X) is in column 4
            line = line.concat(values[3]);
            line = line.concat(Separator.WHITESPACE.getSign());

            // height (Z) is in column 5, but not always valued
            if (!values[4].equals("")) {
                line = line.concat(values[4]);
            } else {
                line = line.concat("-9999");
            }

            result.add(line.trim());
        }

        Txt2Gsi txt2Gsi = new Txt2Gsi(result);

        return txt2Gsi.convert(isGSI16, sourceContainsCodeColumn);
    }

    private void removeHeadLine() {
        lines.remove(0);
    }

}
