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
package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.tools.elements.GSIBlock;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

import java.util.ArrayList;
import java.util.List;


/**
 * This class implements basic operations on spreadsheet output operations for Open Document Format files.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given files.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 * <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 9
 */
public class FileToolsODF {

    private ArrayList<String> readStringLines;
    private List<String[]> readCSVLines;
    private SpreadsheetDocument spreadsheetDocument;

    /**
     * Class Constructor with parameter.
     * <p>
     * As parameter the {@code ArrayList<String>} object with the lines in text format is used.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in text format
     */
    public FileToolsODF(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Class constructor with parameter for the read lines as {@code List<String[]>} object.
     * <p>
     * This constructor is used for reading csv file lines.
     *
     * @param readCSVLines {@code List<String[]>} with lines as {@code String[]}
     */
    public FileToolsODF(List<String[]> readCSVLines) {
        this.readCSVLines = readCSVLines;
    }

    /**
     * Converts a CSV file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success conversion success
     */
    public boolean convertCSV2ODS(String sheetName) {
        int colIndex;
        int rowIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            for (String[] csvLine : readCSVLines) {
                colIndex = 0;
                for (String element : csvLine) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(element);
                    colIndex++;
                }
                rowIndex++;
            }
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        return rowIndex > 1;
    }

    /**
     * Converts a CSV file from the geodata server Basel Stadt (Switzerland) into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion success
     */
    public boolean convertCSVBaselStadt2ODS(String sheetName, boolean writeCommentRow) {
        int colIndex = 0;
        int rowIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            // write comment row
            if (writeCommentRow) {
                String[] commentLine = readCSVLines.get(0);

                for (String description : commentLine) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(description);
                    colIndex++;
                }
                rowIndex++;
            }

            // remove furthermore the still not needed comment line
            readCSVLines.remove(0);

            for (String[] csvLine : readCSVLines) {
                colIndex = 0;

                for (int i = 0; i < csvLine.length; i++) {
                    cell = table.getCellByPosition(colIndex, rowIndex);

                    switch (i) {
                        case 0:
                        case 1:
                            cell.setStringValue(csvLine[i]);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            if (csvLine[i].equalsIgnoreCase("")) {
                                cell.setStringValue(csvLine[i]);
                            } else {
                                cell.setDoubleValue(Double.parseDouble(csvLine[i]));
                                cell.setFormatString("#,##0.000");
                            }
                            break;
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                            cell.setStringValue(csvLine[i]);
                            break;
                    }
                    colIndex++;
                }
                rowIndex++;
            }

        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        return rowIndex > 1;
    }

    /**
     * Converts a Cadwork node.dat file into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion
     */
    public boolean convertCadwork2ODS(String sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;
        String[] lineSplit;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            // remove not needed headlines
            for (int i = 0; i < 3; i++) {
                readStringLines.remove(0);
            }

            // write comment row
            if (writeCommentRow) {
                lineSplit = readStringLines.get(0).trim().split("\\s+");

                for (String description : lineSplit) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(description);
                    colIndex++;
                }
                rowIndex++;
            }

            // remove furthermore the still not needed comment line
            readStringLines.remove(0);

            for (String line : readStringLines) {
                colIndex = 0;

                lineSplit = line.trim().split("\\s+");

                cell = table.getCellByPosition(colIndex, rowIndex);      // No
                cell.setStringValue(lineSplit[0]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // X
                cell.setStringValue(lineSplit[1]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                cell.setStringValue(lineSplit[2]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                cell.setStringValue(lineSplit[3]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Code
                cell.setStringValue(lineSplit[4]);
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);      // Name
                cell.setStringValue(lineSplit[5]);
                rowIndex++;
            }
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        return rowIndex > 1;
    }

    /**
     * Converts a K file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion success
     */
    public boolean convertCaplan2ODS(String sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            // write comment row
            if (writeCommentRow) {
                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("pointNumber"));
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("easting"));
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("northing"));
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("height"));
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("object"));
                colIndex++;

                cell = table.getCellByPosition(colIndex, rowIndex);
                cell.setStringValue(I18N.getCaplanColumnTyp("attribute"));

                rowIndex++;
            }

            for (String line : readStringLines) {
                colIndex = 0;

                if (!line.startsWith("!")) {    // comment lines starting with '!' are ignored
                    String s;

                    if (line.length() >= 16) {
                        cell = table.getCellByPosition(colIndex, rowIndex);
                        cell.setStringValue(line.substring(0, 16).trim());          // point number (no '*', ',' and ';'), column 1 - 16
                        colIndex++;
                    }

                    if (line.length() >= 32) {
                        if (!(s = line.substring(20, 32).trim()).equals("")) {      // easting E, column 19-32
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(s));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex++;
                    }

                    if (line.length() >= 46) {
                        if (!(s = line.substring(34, 46).trim()).equals("")) {      // northing N, column 33-46
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(s));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex++;
                    }

                    if (line.length() >= 59) {
                        if (!(s = line.substring(48, 59).trim()).equals("")) {      // height H, column 47-59
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setDoubleValue(Double.parseDouble(s));
                            cell.setFormatString("#,##0.0000");
                        } else {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue("");
                        }

                        colIndex++;
                    }

                    if (line.length() >= 62) {
                        String[] lineSplit = line.substring(61, line.length()).trim().split("\\|+");

                        cell = table.getCellByPosition(colIndex, rowIndex);
                        cell.setStringValue(lineSplit[0].trim());                   // code is the same as object type, column 62...
                        colIndex++;

                        for (int i = 1; i < lineSplit.length; i++) {
                            cell = table.getCellByPosition(colIndex, rowIndex);
                            cell.setStringValue(lineSplit[i].trim());
                            colIndex++;
                        }
                    }
                rowIndex++;
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        return rowIndex > 1;
    }

    /**
     * Converts a GSI file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success conversion success
     */
    public boolean convertGSI2ODS(String sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            // preparation of the read gsi file
            FileToolsLeicaGSI gsiTools = new FileToolsLeicaGSI(readStringLines);
            ArrayList<ArrayList<GSIBlock>> blocksInLines = gsiTools.getEncodedGSIBlocks();

            // write comment row
            if (writeCommentRow) {
                for (int wordIndex : gsiTools.getFoundWordIndices()) {
                    cell = table.getCellByPosition(colIndex, 0);
                    colIndex++;

                    cell.setStringValue(I18N.getWordIndexDescription(wordIndex));
                }
                rowIndex++;
            }

            // fill gsi content into rows and cells
            for (ArrayList<GSIBlock> blocksAsLines : blocksInLines) {
                colIndex = 0;
                for (GSIBlock block : blocksAsLines) {
                    cell = table.getCellByPosition(colIndex, rowIndex);

                    switch (block.getWordIndex()) {
                        // GENERAL
                        case 11:    // Point number (includes block number)
                        case 12:    // Instrument serial no
                        case 13:    // Instrument type
                        case 18:    // Time format 1: pos. 8-9 year, 10-11 sec, 12-14 msec
                        case 19:    // Time format 2 : pos, 8-9 month 10-11 day, 12-13 hour, 14-15 min
                            cell.setStringValue(block.toPrintFormatCSV());
                            break;

                        // ANGLES
                        case 21:    // Horizontal Circle (Hz)
                        case 22:    // Vertical Angle (V)
                        case 25:    // Horizontal circle difference (Hz0-Hz)
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
                            break;

                        // DISTANCE
                        case 31:    // Slope Distance
                        case 32:    // Horizontal Distance
                        case 33:    // Height Difference
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
                            break;

                        // CODE BLOCK
                        case 41:    // Code number ( include block number)
                        case 42:    // Information 1
                        case 43:    // Information 2
                        case 44:    // Information 3
                        case 45:    // Information 4
                        case 46:    // Information 5
                        case 47:    // Information 6
                        case 48:    // Information 7
                        case 49:    // Information 8
                            cell.setStringValue(block.toPrintFormatCSV());
                            break;

                        // DISTANCE (additional information)
                        case 51:    // Constants(ppm, mm)
                        case 52:    // Number of measurements, standard deviation
                        case 53:    // Deviation
                        case 58:    // Signal strength
                        case 59:    // Reflector constant (1/10 mm)ppm
                            cell.setStringValue(block.toPrintFormatCSV());
                            break;

                        // POINT CODING
                        case 71:    // Point Code
                        case 72:    // Attribute 1
                        case 73:    // Attribute 2
                        case 74:    // Attribute 3
                        case 75:    // Attribute 4
                        case 76:    // Attribute 5
                        case 77:    // Attribute 6
                        case 78:    // Attribute 7
                        case 79:    // Attribute 8
                            cell.setStringValue(block.toPrintFormatCSV());
                            break;

                        // COORDINATES
                        case 81:    // Easting (Target)
                        case 82:    // Northing (Target)
                        case 83:    // Elevation (Target)
                        case 84:    // Station Easting (E0)
                        case 85:    // Station Northing (N0)
                        case 86:    // Station Elevation (H0)
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
                            cell.setFormatString("#,##0.0000");
                            break;
                        case 87:    // Reflector height (above ground)
                        case 88:    // Instrument height (above ground)
                            cell.setDoubleValue(Double.parseDouble(block.toPrintFormatCSV()));
                            cell.setFormatString("#,##0.000");
                            break;
                    }
                    colIndex++;
                }
                rowIndex++;
            }
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        // check number of written lines
        return rowIndex > 1;
    }

    /**
     * Converts a TXT file element by element into an Open Document Format spreadsheet file.
     *
     * @param sheetName name of the sheet (file name from input file)
     *
     * @return success conversion success
     */
    public boolean convertTXT2ODS(String sheetName) {
        int rowIndex = 0;
        int colIndex;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            for (String line : readStringLines) {
                String[] lineSplit = line.trim().split("\\s+");
                colIndex = 0;
                for (String element : lineSplit) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(element);
                    colIndex++;
                }
                rowIndex++;
            }
        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        return rowIndex > 1;
    }

    /**
     * Converts a txt file from the geodata server Basel Landschaft (Switzerland) element by element into an
     * Open Document Format spreadsheet file.
     *
     * @param sheetName       name of the sheet (file name from input file)
     * @param writeCommentRow write comment row
     *
     * @return success conversion success
     */
    public boolean convertTXTBaselLandschaft2ODS(String sheetName, boolean writeCommentRow) {
        int rowIndex = 0;
        int colIndex = 0;

        try {
            // prepare spreadsheet document
            spreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
            spreadsheetDocument.getTableByName("Sheet1").remove();

            Table table = Table.newTable(spreadsheetDocument);
            table.setTableName(sheetName);

            Cell cell;

            // write comment row
            if (writeCommentRow) {
                String[] lineSplit = readStringLines.get(0).trim().split("\\s+");

                for (String description : lineSplit) {
                    cell = table.getCellByPosition(colIndex, rowIndex);
                    cell.setStringValue(description);
                    colIndex++;
                }
                rowIndex++;
            }

            // remove furthermore the still not needed comment line
            readStringLines.remove(0);

            for (String line : readStringLines) {
                String[] lineSplit = line.trim().split("\\s+");

                colIndex = 0;

                switch (lineSplit.length) {
                    case 5:     // HFP file
                        cell = table.getCellByPosition(colIndex, rowIndex);      // Art
                        cell.setStringValue(lineSplit[0]);
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Number
                        cell.setStringValue(lineSplit[1]);
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // X
                        cell.setDoubleValue(Double.parseDouble(lineSplit[2]));
                        cell.setFormatString("#,##0.000");
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                        cell.setDoubleValue(Double.parseDouble(lineSplit[3]));
                        cell.setFormatString("#,##0.000");
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                        if (lineSplit[4].equalsIgnoreCase("NULL")) {
                            cell.setStringValue("NULL");
                        } else {
                            cell.setDoubleValue(Double.parseDouble(lineSplit[4]));
                            cell.setFormatString("#,##0.000");
                        }
                        break;

                    case 6:     // LFP file
                        cell = table.getCellByPosition(colIndex, rowIndex);      // Art
                        cell.setStringValue(lineSplit[0]);
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Number
                        cell.setStringValue(lineSplit[1]);
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // VArt
                        cell.setStringValue(lineSplit[2]);
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // X
                        cell.setDoubleValue(Double.parseDouble(lineSplit[3]));
                        cell.setFormatString("#,##0.000");
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Y
                        cell.setDoubleValue(Double.parseDouble(lineSplit[4]));
                        cell.setFormatString("#,##0.000");
                        colIndex++;

                        cell = table.getCellByPosition(colIndex, rowIndex);      // Z
                        if (lineSplit[5].equalsIgnoreCase("NULL")) {
                            cell.setStringValue("NULL");
                        } else {
                            cell.setDoubleValue(Double.parseDouble(lineSplit[5]));
                            cell.setFormatString("#,##0.000");
                        }
                        break;
                }
                rowIndex++;
            }

        } catch (Exception e) {
            System.err.println("ERROR: unable to create output file.");
        }

        return rowIndex > 1;
    }

    /**
     * Writes the Open Document Format Spreadsheet file to the filesystem.
     *
     * @param fileName output filename
     *
     * @return file writing success
     */
    public boolean writeODS(String fileName) {
        try {
            spreadsheetDocument.save(fileName);
            return true;
        } catch (Exception e) {
            System.err.println("unable to save Open Document  Spreadsheet file to disk.");
            System.err.println(e.getMessage());
        }

        return false;
    }

} // end of FileToolsODF
