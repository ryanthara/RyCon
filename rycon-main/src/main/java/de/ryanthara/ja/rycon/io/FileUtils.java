/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
 * This class implements basic file io-operations for copying, etc to RyCON.
 * <p>
 * Because of the fact that there are a lot of user who has java version 6 or 7
 * running, RyCON uses no functions of java version 8 in versions lower than 2.
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
public class FileUtils {

    /**
     * Class Constructor without parameters.
     */
    public FileUtils() {
    }

    /**
     * Copies a file or directory and it's subdirectories recursively from source to target location.
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

    /**
     * Internal method which copies folders and containing files.
     * <p>
     * Internally there is a recursion used to copy the folders and the containing content.
     *
     * @param source source directory to be copied
     * @param target target directory to be copied
     * @throws IOException copying directory failed
     */
    private void copyDirectory(File source, File target) throws IOException {

        //if directory not exists, create it
        if (!target.exists()) {
            if (!target.mkdir()) {
                System.err.println("copying dirs and files failed. Directory couldn't be created.");
            }
        }

        //list all the directory contents
        String files[] = source.list();

        for (String file : files) {
            //construct the source and target file structure
            File sourceFile = new File(source, file);
            File targetFile = new File(target, file);
            //recursive copy
            copy(sourceFile, targetFile);
        }

    }

    /**
     * Internal method which copies a file bit by bit.
     * <p>
     * In most cases the folders contains only less empty folders and a couple of small files.
     * Maybe in a later version of RyCON there will be other methods used.
     *
     * @param source source file to be copied
     * @param target target file to be copied
     * @throws IOException copying file failed
     */
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
            // if file, then copy it
            // use bytes stream to support all file types
            in = new FileInputStream(source);
            out = new FileOutputStream(target);

            byte[] buffer = new byte[1024];

            int length;

            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            // force closing the in and out streams
            in.close();
            out.close();
        }

    }

} // end of FileUtils
