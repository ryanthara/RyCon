/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.io
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
package de.ryanthara.ja.rycon.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Instances of this class provides functions to write an {@code ArrayList<String>} line by line into a file.
 * <p>
 * A couple of things are implemented as additional functionality. At the moment, there is no thread safety
 * implemented or planed.
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public class LineWriter {

    private final String fileName;
    private int writtenLines = -1;

    /**
     * Constructs a new instance of this class with the filename as {@code String} parameter.
     *
     * @param fileName filename as {@code String}
     */
    public LineWriter(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the number of written lines.
     *
     * @return number of written lines
     */
    // TODO Implement the usage/displaying of the written lines to the status bar
    public int getCountWrittenLines() {
        return writtenLines;
    }

    /**
     * Writes a given {@code ArrayList<String>} line by line to the file system.
     *
     * @param lines given list to write into the file
     *
     * @return success true if the file is written successfully
     *
     * @throws IllegalArgumentException Throws an {@link IllegalArgumentException} when the lines parameter is null
     */
    public boolean writeFile(ArrayList<String> lines) {
        boolean success = false;
        PrintWriter pw = null;

        if (lines == null) {
            throw new IllegalArgumentException();
        }

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);

            for (String line : lines) {
                pw.println(line);
                writtenLines = writtenLines + 1;
            }
        } catch (IOException e) {
            System.err.format("File %s could not be written to the file system.", fileName);
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
                writtenLines = writtenLines + 1;
                success = true;
            }
        }

        return success;
    }

} // end of LineWriter
