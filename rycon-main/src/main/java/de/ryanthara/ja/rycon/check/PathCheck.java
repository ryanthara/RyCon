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
package de.ryanthara.ja.rycon.check;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * This class implements different kind of checks for {@link Path} objects.
 * <p>
 * RyCON now uses path objects for better file handling and a better error detection.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class PathCheck {

    /**
     * Checks the content of a paths array for valid paths by a given file suffix.
     * <p>
     * Non readable file objects and directories will be excluded from the returned file array.
     *
     * @param paths                array of paths to be checked
     * @param acceptableFileSuffix string array with the acceptable file suffixes
     *
     * @return checked path array with valid and readable path objects
     */
    @SuppressWarnings("MethodCanBeVariableArityMethod")
    public static Path[] getValidFiles(Path[] paths, String[] acceptableFileSuffix) {
        ArrayList<Path> temp = new ArrayList<>();

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
     * Checks a given String URL to be an existing and valid directory.
     *
     * @param file directory URL as String
     *
     * @return true if is directory and exists
     *
     * @throws IllegalArgumentException is thrown if file is null or an empty string
     */
    public static boolean isDirectory(String file) throws IllegalArgumentException {
        if (file == null) {
            throw new IllegalArgumentException();
        } else if (file.equalsIgnoreCase("")) {
            throw new IllegalArgumentException();
        }

        Path path = Paths.get(file);

        return Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * Checks a given <tt>Files URL</tt> to be an existing and valid file.
     *
     * @param file file URL as String
     *
     * @return true if is file and exists
     *
     * @throws IllegalArgumentException is thrown if file is null or an empty string
     */
    public static boolean isFile(File file) throws IllegalArgumentException {
        return isFile(file.getPath());
    }

    /**
     * Checks a given <tt>String URL</tt> to be an existing and valid file.
     *
     * @param file file URL as String
     *
     * @return true if is file and exists
     *
     * @throws IllegalArgumentException is thrown if file is null or empty string
     */
    public static boolean isFile(String file) throws IllegalArgumentException {
        if (file == null) {
            throw new IllegalArgumentException();
        } else if (file.equalsIgnoreCase("")) {
            throw new IllegalArgumentException();
        }

        Path path = Paths.get(file);

        return Files.exists(path) && Files.isRegularFile(path);
    }

} // end of PathCheck
