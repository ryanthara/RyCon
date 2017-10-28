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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instances of this class provide functions to write an {@code ArrayList<String>} line by line into a file.
 * <p>
 * A couple of things are implemented as additional functionality. At the moment, there is no thread safety
 * implemented or planed.
 * <p>
 * With version 4 {@link LineWriter} is using only java.nio functions.
 *
 * @author sebastian
 * @version 4
 * @since 1
 */
public final class LineWriter {

    private final static Logger logger = Logger.getLogger(LineWriter.class.getName());

    private final Path fileName;
    private int writtenLines = -1;

    /**
     * Constructs a new instance of this class with the filename as {@code Path} parameter.
     *
     * @param fileName filename as {@code Path}
     */
    public LineWriter(final Path fileName) {
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
     * <p>
     * This second version of the writeFile method uses java.nio functions to write the file
     * buffered to the file system.
     *
     * @param lines lines to be written to the file
     *
     * @return write file success - true if the file was written otherwise false
     *
     * @throws IllegalArgumentException throws an {@link IllegalArgumentException} when the lines parameter is null
     */
    public boolean writeFile(final ArrayList<String> lines) {
        boolean success = false;

        if (lines == null) {
            throw new IllegalArgumentException();
        } else {
            final Charset charset = Charset.forName("UTF-8");

            writtenLines = 0;

            try (BufferedWriter writer = Files.newBufferedWriter(fileName, charset)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();

                    writtenLines = writtenLines + 1;
                }

                success = true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IOException occurred while file writing in line " + writtenLines + ". ");
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        return success;
    }

} // end of LineWriter
