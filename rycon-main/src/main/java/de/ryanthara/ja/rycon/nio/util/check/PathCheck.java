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
package de.ryanthara.ja.rycon.nio.util.check;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides different kind of checks for {@link Path} objects.
 *
 * <p>
 * Due the development cycle of RyCON a change from java.io.File objects
 * to java.nio.Path objects was made.
 *
 * <p>
 * The functions let you check for
 * <ul>
 * <li>empty directories,
 * <li>existing directories,
 * <li>existing files,
 * <li>readable files,
 * <li>valid files,
 * <li>and if a directory contains sub folders.
 * </ul>
 *
 * <p>
 * The passed arguments are checked for null and if necessary for empty strings.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public final class PathCheck {

    /**
     * PathCheck is non-instantiable.
     */
    private PathCheck() {
        throw new AssertionError();
    }

    /**
     * Checks if a given {@code Path URL} contains subfolder with the maximum depth given by parameter.
     *
     * @param directoryToCheck path to be checked
     * @param maxDepth         maximum depth to search for subfolder
     * @return true if directory contains subfolder
     * @throws IOException occurred IOException
     */
    public static boolean directoryContainsSubfolder(Path directoryToCheck, int maxDepth) throws IOException {
        List<Path> subfolder = Files.walk(directoryToCheck, maxDepth)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        subfolder.remove(0);

        return subfolder.size() > 0;
    }

    /**
     * Checks a given {@code Path URL} to be an existing and valid directory.
     *
     * @param directoryToCheck directory to be checked
     * @return true if is directory and exists
     * @throws NullPointerException will be thrown if directoryToCheck is null
     */
    public static boolean directoryExists(Path directoryToCheck) {
        Objects.requireNonNull(directoryToCheck, "The path must never be null!");

        return Files.exists(directoryToCheck) && Files.isDirectory(directoryToCheck);
    }

    /**
     * Checks a given {@code String URL} to be an existing and valid directory.
     *
     * @param directoryToCheck directory as string
     * @return true if is directory and exists
     * @throws NullPointerException will be thrown if directoryToCheck is null
     */
    public static boolean directoryExists(String directoryToCheck) {
        Objects.requireNonNull(directoryToCheck, "The path string must never be null!");

        if (directoryToCheck.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("The empty string is not allowed here!");
        }

        Path path = Paths.get(directoryToCheck);

        return directoryExists(path);
    }

    /**
     * Checks a given {@code String URL} to be an existing and valid file.
     *
     * @param fileToCheck file URL as String
     * @return true if is file and exists
     * @throws NullPointerException     Will be thrown if fileToCheck is null
     * @throws IllegalArgumentException is thrown if file is null or empty string
     */
    public static boolean fileExists(String fileToCheck) {
        Objects.requireNonNull(fileToCheck, "The file string must never be null!");

        if (fileToCheck.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("The empty string is not allowed here!");
        }

        Path path = Paths.get(fileToCheck);

        return fileExists(path);
    }

    /**
     * Checks a given {@code Path URL} to be an existing and valid file.
     * <p>
     * From the perspective of RyCON empty strings are not valid files.
     *
     * @param path file URL as {@code Path}
     * @return true if is file and exists
     * @throws NullPointerException     Will be thrown if path is null
     * @throws IllegalArgumentException is thrown if file is null or empty string
     */
    public static boolean fileExists(Path path) {
        Objects.requireNonNull(path, "The path must never be null!");

        final Path fileName = path.getFileName();

        if (fileName != null) {
            if (fileName.toString().equals("")) {
                throw new IllegalArgumentException();
            }
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
     * @return checked path array with valid and readable path objects
     * @throws NullPointerException will be thrown if path array is null
     */
    @SuppressWarnings("MethodCanBeVariableArityMethod")
    public static Path[] getValidFiles(Path[] paths, String[] acceptableFileSuffix) {
        ArrayList<Path> temp = new ArrayList<>();

        Objects.requireNonNull(paths, "The paths array must never be null!");

        for (Path path : paths) {
            if (Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path)) {
                for (String anAcceptableFileSuffix : acceptableFileSuffix) {
                    String reducedSuffix = anAcceptableFileSuffix.substring(2);

                    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\." + reducedSuffix + ")");

                    if (matcher.matches(path)) {
                        temp.add(path);
                    }
                }
            }
        }

        return temp.toArray(new Path[0]);
    }

    /**
     * Checks a given {@code Path} if it exists in the file system and is readable.
     *
     * @param path path to be checked
     * @return true if path exists in the file system and is readable
     */
    public static boolean isReadable(Path path) {
        return fileExists(path) || Files.isReadable(path);
    }

    /**
     * Checks a given {@code Path} if it exists in the file system.
     *
     * @param path path to be checked
     * @return true if path exists in the file system
     * @throws NullPointerException will be thrown if path is null
     */
    public static boolean isValid(Path path) {
        Objects.requireNonNull(path, "The path must never be null!");

        return Files.exists(path);
    }

}
