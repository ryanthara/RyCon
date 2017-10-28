/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.util.check;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code PathCheck} implements different kind of checks for {@link Path} objects.
 * <p>
 * RyCON now uses {@link Path} objects for better file handling and a better error detection
 * instead of {@link File} objects.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public final class PathCheck {

    /**
     * Checks if a given {@code Path URL} contains subfolder with the maximum depth give by parameter.
     *
     * @param directoryToCheck path to be checked
     * @param maxDepth         maximum depth to search for subfolder
     *
     * @return true if directory contains subfolder
     *
     * @throws IOException
     */
    public static boolean directoryContainsSubfolder(Path directoryToCheck, int maxDepth) throws IOException {
        List<Path> subfolder = Files.walk(directoryToCheck, maxDepth)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        subfolder.remove(0);

        if (subfolder.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Checks a given {@code Path URL} to be an existing and valid directory.
     *
     * @return true if is directory and exists
     *
     * @throws IllegalArgumentException is thrown if directory is null or an empty String
     */
    public static boolean directoryExists(final Path directoryToCheck) {
        if (directoryToCheck == null) {
            throw new IllegalArgumentException();
        }

        return Files.exists(directoryToCheck) && Files.isDirectory(directoryToCheck);
    }

    /**
     * Checks a given {@code String URL} to be an existing and valid directory.
     *
     * @param directoryToCheck directory URL as String
     *
     * @return true if is directory and exists
     *
     * @throws IllegalArgumentException is thrown if directory is null or an empty String
     */
    public static boolean directoryExists(final String directoryToCheck) {
        if (directoryToCheck == null || directoryToCheck.equalsIgnoreCase("")) {
            throw new IllegalArgumentException();
        }

        Path path = Paths.get(directoryToCheck);

        return directoryExists(path);
    }

    /**
     * Checks a given {@code String URL} to be an existing and valid file.
     *
     * @param fileToCheck file URL as String
     *
     * @return true if is file and exists
     *
     * @throws IllegalArgumentException is thrown if file is null or empty string
     */
    public static boolean fileExists(final String fileToCheck) {
        if (fileToCheck == null || fileToCheck.equalsIgnoreCase("")) {
            throw new IllegalArgumentException();
        }

        Path path = Paths.get(fileToCheck);

        return fileExists(path);
    }

    /**
     * Checks a given {@code Path URL} to be an existing and valid file.
     *
     * @param path file URL as {@code Path}
     *
     * @return true if is file and exists
     *
     * @throws IllegalArgumentException is thrown if file is null or empty string
     */
    public static boolean fileExists(final Path path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Checks the content of an array of {@code Path} for valid paths by a given file suffix.
     * <p>
     * Non readable {@code Path} objects and directories will be excluded from the returned file array.
     *
     * @param paths                array of paths to be checked
     * @param acceptableFileSuffix string array with the acceptable file suffixes
     *
     * @return checked path array with valid and readable path objects
     *
     * @throws IllegalArgumentException is thrown if path is null
     */
    @SuppressWarnings("MethodCanBeVariableArityMethod")
    public static Path[] getValidFiles(Path[] paths, String[] acceptableFileSuffix) {
        ArrayList<Path> temp = new ArrayList<>();

        if (paths == null) {
            throw new IllegalArgumentException();
        }

        for (Path path : paths) {
            if (Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path)) {
                for (String anAcceptableFileSuffix : acceptableFileSuffix) {
                    String reducedSuffix = anAcceptableFileSuffix.substring(2, anAcceptableFileSuffix.length());

                    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\." + reducedSuffix + ")");

                    if (matcher.matches(path)) {
                        temp.add(path);
                    }
                }
            }
        }

        return temp.toArray(new Path[temp.size()]);
    }

    /**
     * Checks a given {@code Path} if it exists in the file system and is readable.
     *
     * @param path path to be checked
     *
     * @return true if path exists in the file system and is readable
     */
    public static boolean isReadable(Path path) {
        return fileExists(path) || Files.isReadable(path);
    }

    /**
     * Checks a given {@code Path} if it exists in the file system.
     *
     * @param path path to be checked
     *
     * @return true if path exists in the file system
     *
     * @throws IllegalArgumentException is thrown if path is null
     */
    public static boolean isValid(Path path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        return Files.exists(path);
    }

} // end of PathCheck
