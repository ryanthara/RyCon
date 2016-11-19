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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

/**
 * This class implements static access to basic file io-operations for copying, etc.
 * <p>
 * Because of the fact that there are a lot of users who has not the current java version
 * running, RyCON still not uses any functions of java version 8 in versions lower than 2.
 *
 * @author sebastian
 * @version 4
 * @since 1
 */
public class FileUtils {

    /**
     * Copies a file or directory and it's subdirectories recursively from source to destination location.
     * <p>
     * Alternatively the Apache Commons IO functions can be used for the same task. But at the moment
     * RyCON uses as less external libraries as necessary.
     * <p>
     * This code is inspired from the <a href='http://docs.oracle.com/javase/tutorial/essential/io/examples/Copy.java'>Java Tutorial</a>.
     *
     * @param source      source location files and folders to be copied
     * @param destination destination location files and folders to be copied
     *
     * @throws IOException copying failed
     */
    public static void copy(Path source, Path destination) throws IOException {
        if (Files.isDirectory(source)) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyDirectory(Path source, Path destination) throws IOException {
        // follow links when copying files
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        TreeCopier tc = new TreeCopier(source, destination, false);
        Files.walkFileTree(source, opts, Integer.MAX_VALUE, tc);
    }

    /*
    Copies a file, and only a file, without following symbolic links.
     */
    private static void copyFile(Path source, Path destination) throws IOException {
        try {
            Files.copy(source, destination, NOFOLLOW_LINKS);
        } catch (UnsupportedOperationException ex) {
            System.err.println("Unable to copy file - UnsupportedOperationException");
        } catch (FileAlreadyExistsException ex) {
            System.err.println("Unable to copy file - FileAlreadyExistsException");
        } catch (IOException ex) {
            System.err.format("Unable to copy: %s: %s%n", source, ex);
        }
    }

    /**
     * A {@code FileVisitor} that copies a file-tree ("cp -r")
     */
    private static class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path destination;
        private final boolean preserve;

        TreeCopier(Path source, Path destination, boolean preserve) {
            this.source = source;
            this.destination = destination;
            this.preserve = preserve;
        }

        /**
         * Invoked for a directory after entries in the directory, and all of their
         * descendants, have been visited. This method is also invoked when iteration
         * of the directory completes prematurely (by a {@link #visitFile visitFile}
         * method returning {@link FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS},
         * or an I/O error when iterating over the directory).
         *
         * @param dir a reference to the directory
         * @param exc {@code null} if the iteration of the directory completes without
         *            an error; otherwise the I/O exception that caused the iteration
         *            of the directory to complete prematurely
         *
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            // fix up modification time of directory when done
            if (exc == null && preserve) {
                Path newDir = destination.resolve(source.relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newDir, time);
                } catch (IOException x) {
                    System.err.format("Unable to copy all attributes to: %s: %s%n", newDir, x);
                }
            }

            return CONTINUE;
        }

        /**
         * Invoked for a directory before entries in the directory are visited.
         * <p>
         * <p> If this method returns {@link FileVisitResult#CONTINUE CONTINUE},
         * then entries in the directory are visited. If this method returns {@link
         * FileVisitResult#SKIP_SUBTREE SKIP_SUBTREE} or {@link
         * FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} then entries in the
         * directory (and any descendants) will not be visited.
         *
         * @param dir   a reference to the directory
         * @param attrs the directory's basic attributes
         *
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = (preserve) ?
                    new CopyOption[]{COPY_ATTRIBUTES} : new CopyOption[0];

            Path newDir = destination.resolve(source.relativize(dir));

            try {
                Files.copy(dir, newDir, options);
            } catch (FileAlreadyExistsException x) {
                // ignore
            } catch (IOException x) {
                System.err.format("Unable to create: %s: %s%n", newDir, x);
                return SKIP_SUBTREE;
            }

            return CONTINUE;
        }

        /**
         * Invoked for a file in a directory.
         *
         * @param file  a reference to the file
         * @param attrs the file's basic attributes
         *
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            copyFile(file, destination.resolve(source.relativize(file)));

            return CONTINUE;
        }

        /**
         * Invoked for a file that could not be visited. This method is invoked
         * if the file's attributes could not be read, the file is a directory
         * that could not be opened, and other reasons.
         *
         * @param file a reference to the file
         * @param exc  the I/O exception that prevented the file from being visited
         *
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            if (exc instanceof FileSystemLoopException) {
                System.err.println("cycle detected: " + file);
            } else {
                System.err.format("Unable to copy: %s: %s%n", file, exc);
            }

            return CONTINUE;
        }

    } // end of TreeCopier (inner class)

} // end of FileUtils
