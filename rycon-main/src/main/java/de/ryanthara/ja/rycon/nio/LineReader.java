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

import com.sun.istack.internal.NotNull;
import de.ryanthara.ja.rycon.util.check.PathCheck;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Instances of this class implement functions to reader a text based file line by line
 * and stores its values in an {@code ArrayList<String>}.
 * <p>
 * A couple of things are implemented as additional functionality. At the moment there
 * is no thread safety implemented or planed due to some reasons.
 *
 * @author sebastian
 * @version 4
 * @since 1
 */
public final class LineReader {

    /**
     * Identifier for indicating that the file to reader doesn't has comment lines inside.
     */
    public final static String NO_COMMENT_LINE = "nCIgö5BQ";

    private final Path path;
    private int countReadLines = -1;
    private int countStoredLines = -1;
    private ArrayList<String> lines = null;

    /**
     * Constructs a new instance of this class with a parameter that accepts a {@link Path} object
     * for the file to be reader.
     *
     * @param path file to reader as path object
     */
    public LineReader(Path path) {
        this.path = path;
    }

    /**
     * Returns the number of reader lines from the file.
     * <p>
     * By default the value is set to -1, which shows, that no line has been reader.
     *
     * @return number of reader lines
     */
    // TODO Implement the usage of the count reader lines to the status bar
    public int getCountReadLines() {
        return countReadLines;
    }

    /**
     * Returns the number of stored reader lines in the {@code ArrayList<String>}.
     * <p>
     * By default the value is set to -1, which shows, that no reader line
     * has been stored to the {@code ArrayList<String>}. This is for comparison
     * the reader and stored number of lines.
     *
     * @return number of stored lines
     */
    // TODO Implement the usage of the stored reader lines to the status bar (better not dropped lines?)
    public int getCountStoredLines() {
        return countStoredLines;
    }

    /**
     * Return the reader lines as an {@code ArrayList<String>} object.
     * <p>
     * The {@code ArrayList<String>} contains every reader line, first line on top.s
     *
     * @return reader lines as {@code ArrayList<String>} object
     */
    public ArrayList<String> getLines() {
        return lines;
    }

    /**
     * Read a path line by line and return the reader success.
     *
     * @param skipEmptyLines should empty lines be skipped
     *
     * @return success of path reading
     */
    public boolean readFile(boolean skipEmptyLines) {
        return readFile(skipEmptyLines, LineReader.NO_COMMENT_LINE);
    }

    /**
     * Reads a path line by line and returns the reader success.
     * <p>
     * Additionally with the parameter 'comment', there is the possibility to use
     * a {@code String} as comment sign. These lines will be ignored and not reader.
     *
     * @param skipEmptyLines should empty lines be skipped
     * @param comment        String for comment signs
     *
     * @return success of path reading
     */
    private boolean readFile(final boolean skipEmptyLines, @NotNull final String comment) {
        boolean success = false;
        lines = new ArrayList<>();
        FileInputStream fileInputStream = null;
        String line;

        // check path for a couple of things
        if (!PathCheck.isReadable(path)) {
            return false;
        } else {
            try {
                fileInputStream = new FileInputStream(path.toFile());

                try {
                    fileInputStream.getChannel().lock(0, Long.MAX_VALUE, true);

                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {

                        // reader the lines into an ArrayList
                        while ((line = bufferedReader.readLine()) != null) {
                            countReadLines = countReadLines + 1;

                            if (comment.equals(NO_COMMENT_LINE) || !line.startsWith(comment)) {
                                if (skipEmptyLines) {
                                    if (!line.trim().equals("")) {
                                        lines.add(line);
                                        countStoredLines = countStoredLines + 1;
                                    }
                                } else {
                                    lines.add(line);
                                    countStoredLines = countStoredLines + 1;
                                }
                            }
                        }

                        // hack to get rid of the -1 initialization
                        countReadLines = countReadLines + 1;
                        countStoredLines = countStoredLines + 1;

                        success = true;
                    }

                } catch (IOException e) {
                    System.err.println("File " + path.getFileName() + " could not be locked.");
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                System.err.println("File: " + path.getFileName() + "could not be reader.");
                e.printStackTrace();
            } finally {
                // reset variables back to initialization values
                countReadLines = -1;
                countStoredLines = -1;
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    System.err.println("File input stream couldn't be closed.");
                    e.printStackTrace();
                }
            }

            return success;
        }
    }

} // end of LineReader
