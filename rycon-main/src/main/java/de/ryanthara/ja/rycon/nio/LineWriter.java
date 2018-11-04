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
package de.ryanthara.ja.rycon.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Instances of this class provide functions to writer an {@code List<String>} line by line into a file.
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
final class LineWriter {

    private static final Logger logger = LoggerFactory.getLogger(LineWriter.class.getName());

    private final Path fileName;
    private int writtenLines = -1;

    /**
     * Constructs a new instance of this class with the filename as {@code Path} parameter.
     *
     * @param fileName filename as {@code Path}
     */
    LineWriter(Path fileName) {
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
     * Writes a given {@code List<String>} line by line to the file system.
     * <p>
     * This second version of the writeFile method uses java.nio functions to writer the file
     * buffered to the file system.
     *
     * @param lines lines to be written to the file
     * @return writer file success - true if the file was written otherwise false
     * @throws NullPointerException will be thrown if lines is null
     */
    public boolean writeFile(List<String> lines) {
        Objects.requireNonNull(lines, "the path must never be null!");

        final Charset charset = Charset.forName("UTF-8");

        writtenLines = 0;

        try (BufferedWriter writer = Files.newBufferedWriter(fileName, charset)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();

                writtenLines = writtenLines + 1;
            }

            return true;
        } catch (IOException e) {
            logger.error("IOException occurred while writing file '{}' in line '{}'.", fileName, writtenLines, e.getCause());
        }

        return false;
    }

}
