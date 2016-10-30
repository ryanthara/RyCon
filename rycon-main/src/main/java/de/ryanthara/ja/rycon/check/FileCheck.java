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
import java.util.ArrayList;

/**
 * This class implements different kind of checks for {@link File} objects.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class FileCheck {

    /**
     * Checks the content of a file array for valid files by a given file suffix.
     * <p>
     * Non readable file objects and directories will be excluded from the returned file array.
     *
     * @param files                array of files to be checked
     * @param acceptableFileSuffix string array with the acceptable file suffixes
     *
     * @return checked file array with valid and readable file objects
     */
    public static File[] checkForValidFiles(File[] files, String[] acceptableFileSuffix) {
        ArrayList<File> temp = new ArrayList<>();

        for (File file : files) {
            if (file.isFile() && file.canRead()) {
                for (String anAcceptableFileSuffix : acceptableFileSuffix) {
                    String reducedSuffix = anAcceptableFileSuffix.substring(2, anAcceptableFileSuffix.length());

                    if (file.getName().toLowerCase().endsWith(reducedSuffix)) {
                        temp.add(file);
                    }
                }
            }
        }

        return temp.toArray(new File[temp.size()]);
    }

    /**
     * Checks a given String URL to be an existing and valid directory.
     *
     * @param file directory URL as String
     *
     * @return true if is directory and exists
     */
    public static boolean checkIsDirectory(String file) {
        File f = new File(file);

        return f.exists() & f.isDirectory();
    }

    /**
     * Checks a given String URL to be an existing and valid file.
     *
     * @param file file URL as String
     *
     * @return true if is file and exists
     */
    public static boolean checkIsFile(String file) {
        File f = new File(file);

        return f.exists() & f.isFile();
    }

} // end of FileCheck
