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
 * FileUtils implements basic file io-operations for copying, etc. to RyCON.
 * <p>
 * Because of the fact that there are a lot of user who has java version 7
 * running, RyCON uses no functions of java version 8 in versions lower than 2.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class FileUtils {

    /**
     * Check if a directory exists and is a valid directory for a given String URL.
     *
     * @param file directory URL as String to be checked
     * @return true if is directory and exists
     */
    public static boolean checkIsDirectory(String file) {
        File f = new File(file);

        return f.exists() & f.isDirectory();
    }

    /**
     * Check if a file exists and is a valid file for a given String URL.
     *
     * @param file file URL as String to be checked
     * @return true if is file and exists
     */
    public static boolean checkIsFile(String file) {
        File f = new File(file);

        return f.exists() & f.isFile();
    }

    /**
     * Copy a file or directory and it's subdirectories recursively from source to target location.
     * <p>
     * Alternatively the Apache Commons IO functions can be used for the same task. But at the moment
     * RyCON tries to use at less external libraries as necessary.
     *
     * @param sourceLocation source location files and folders to be copied
     * @param targetLocation target location files and folders to be copied
     * @throws IOException copying failed
     */
    public void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            if (!target.mkdir()) {
                System.err.println("copying dirs and files failed. Directory couldn't be created.");
            }
        }

        for (String file : source.list()) {
            File sourceFile = new File(source, file);
            File targetFile = new File(target, file);
            copy(sourceFile, targetFile);
        }
    }

    private void copyFile(File source, File target) throws IOException {
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {}
        };

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            in.close();
            out.close();
        }
    }

} // end of FileUtils
