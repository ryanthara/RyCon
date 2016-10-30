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

import java.io.*;

/**
 * This class implements static access to basic file io-operations for copying, etc.
 * <p>
 * Because of the fact that there are a lot of user who has java version 7
 * running, RyCON still not uses any functions of java version 8 in versions lower than 2.
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class FileUtils {

    /**
     * Copies a file or directory and it's subdirectories recursively from source to target location.
     * <p>
     * Alternatively the Apache Commons IO functions can be used for the same task. But at the moment
     * RyCON tries to use at less external libraries as necessary.
     *
     * @param sourceLocation source location files and folders to be copied
     * @param targetLocation target location files and folders to be copied
     *
     * @throws IOException copying failed
     */
    public static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private static void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            if (!target.mkdir()) {
                System.err.println("copying directories and files failed. Directory couldn't be created.");
            }
        }

        for (String file : source.list()) {
            File sourceFile = new File(source, file);
            File targetFile = new File(target, file);
            copy(sourceFile, targetFile);
        }
    }

    private static void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

} // end of FileUtils
