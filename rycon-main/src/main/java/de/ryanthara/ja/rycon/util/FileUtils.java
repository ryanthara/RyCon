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
package de.ryanthara.ja.rycon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * {@code FileUtils} implements static access to basic file nio-operations for copying, etc.
 * <p>
 * Because of the fact that there are a lot of users who has not the current java version
 * running, <tt>RyCON</tt> still not uses any functions of java version 8 in versions lower than 2.
 *
 * @author sebastian
 * @version 4
 * @since 1
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class.getName());

    /**
     * Copies a file or directory and it's subdirectories recursively from source to target location.
     * <p>
     * Alternatively the Apache Commons IO functions can be used for the same task. But at the moment
     * <tt>RyCON</tt> uses as less external libraries as necessary.
     * <p>
     * This code is inspired from the <a href='http://docs.oracle.com/javase/tutorial/essential/io/examples/Copy.java'>Java Tutorial</a>.
     *
     * @param source source location files and folders to be copied
     * @param target target location files and folders to be copied
     *
     * @throws IOException copying failed
     */
    public static void copy(Path source, Path target) throws IOException {
        if (Files.isDirectory(source)) {
            copyDirectory(source, target, false);
        } else {
            copyFile(source, target, false);
        }
    }

    /**
     * Copies a file or directory and it's subdirectories recursively from source to target location.
     * <p>
     * With this overloaded method it is possible to overwrite files or directories.
     * <p>
     * Alternatively the Apache Commons IO functions can be used for the same task. But at the moment
     * <tt>RyCON</tt> uses as less external libraries as necessary.
     * <p>
     * This code is inspired from the <a href='http://docs.oracle.com/javase/tutorial/essential/io/examples/Copy.java'>Java Tutorial</a>.
     *
     * @param source            source location files and folders to be copied
     * @param target            target location files and folders to be copied
     * @param overwriteExisting overwrite existing files
     *
     * @throws IOException copying failed
     */
    public static void copy(Path source, Path target, boolean overwriteExisting) throws IOException {
        if (Files.isDirectory(source)) {
            copyDirectory(source, target, overwriteExisting);
        } else {
            copyFile(source, target, overwriteExisting);
        }
    }

    /*
     * Copies a directory, and only a directory, with following symbolic links.
     */
    private static void copyDirectory(Path source, Path target, boolean overwriteExisting) throws IOException {
        // follow links when copying files
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        TreeCopier tc = new TreeCopier(source, target, overwriteExisting);
        Files.walkFileTree(source, opts, Integer.MAX_VALUE, tc);
    }

    /*
     * Copies a file, and only a file, without following symbolic links.
     */
    private static void copyFile(Path source, Path target, boolean overwriteExisting) throws IOException {
        if (overwriteExisting) {
            Files.copy(source, target, NOFOLLOW_LINKS, REPLACE_EXISTING);
        } else {
            Files.copy(source, target, NOFOLLOW_LINKS);
        }
    }

    /**
     * Returns the recent folder name in a given directory.
     *
     * @param dir path reference to the folder
     *
     * @return folder name
     */
    public static String getRecentFolder(Path dir) {
        // get the recent folder by using a simple comparator by lastModified filed
        Optional<Path> lastFilePath;

        try {
            lastFilePath = Files.list(dir)              // get the stream with full directory listing
                    .filter(f -> Files.isDirectory(f))  // include subdirectories into the listing, ignore files
                    .max(Comparator.comparingLong(f2 -> f2.toFile().lastModified()));

            // check for empty folder
            if (lastFilePath.isPresent()) {
                return lastFilePath.get().getName(lastFilePath.get().getNameCount() - 1).toString();
            }
        } catch (IOException e) {
            logger.error("Can not get the recent file in directory '{}'.", dir.toString(), e.getCause());
        }

        return "";
    }

    /**
     * Lists the content of the given directory as {@code List<Path>}.
     *
     * @param directory directory to list
     * @param filter    filter to be used
     *
     * @return directory content as list
     */
    public static ArrayList<Path> listFiles(String directory, Filter<Path> filter) {
        final Path dir = Paths.get(directory);

        ArrayList<Path> fileNames = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, filter)) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException e) {
            logger.error("Can not read directory '{}'.", directory, e.getCause());
        }

        return fileNames;
    }

    /**
     * Moves a file from source to target location.
     * <p>
     * With this overloaded method it is possible to overwrite files.
     * <p>
     * Alternatively the Apache Commons IO functions can be used for the same task. But at the moment
     * <tt>RyCON</tt> uses as less external libraries as necessary.
     * <p>
     * This code is inspired from the <a href='http://docs.oracle.com/javase/tutorial/essential/io/examples/Copy.java'>Java Tutorial</a>.
     *
     * @param source            source location files and folders to be copied
     * @param target            target location files and folders to be copied
     * @param overwriteExisting overwrite existing files
     *
     * @throws IOException copying failed
     */
    public static void move(Path source, Path target, boolean overwriteExisting) throws IOException {
        if (!Files.isDirectory(source)) {
            moveFile(source, target, overwriteExisting);
        }
    }

    /*
     * Moves a file, and only a file, without following symbolic links.
     */
    private static void moveFile(Path source, Path target, boolean overwriteExisting) throws IOException {
        if (overwriteExisting) {
            Files.move(source, target, NOFOLLOW_LINKS, REPLACE_EXISTING);
        } else {
            Files.move(source, target, NOFOLLOW_LINKS);
        }
    }

    /*
     * A {@code FileVisitor} that copies a file-tree ("cp -r")
     */
    private static class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;
        private final boolean preserve;
        private final boolean overwriteExisting;

        TreeCopier(Path source, Path target, boolean overwriteExisting) {
            this.source = source;
            this.target = target;
            this.preserve = false;
            this.overwriteExisting = overwriteExisting;
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
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            // fix up modification time of directory when done
            if (exc == null && preserve) {
                Path newDir = target.resolve(source.relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newDir, time);
                } catch (IOException e) {
                    logger.error("Unable to copy all attributes to '{}'.", newDir, e.getCause());
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
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = (preserve) ?
                    new CopyOption[]{COPY_ATTRIBUTES} : new CopyOption[0];

            Path newDir = target.resolve(source.relativize(dir));

            try {
                Files.copy(dir, newDir, options);
            } catch (FileAlreadyExistsException e) {
                // ignore
            } catch (IOException e) {
                logger.error("Unable to create directory '{}'.", newDir, e.getCause());
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
            copyFile(file, target.resolve(source.relativize(file)), overwriteExisting);

            return CONTINUE;
        }

        /**
         * Invoked for a file that could not be visited. This method is invoked
         * if the file's attributes could not be reader, the file is a directory
         * that could not be opened, and other reasons.
         *
         * @param file a reference to the file
         * @param e    the I/O exception that prevented the file from being visited
         *
         * @return the visit result
         */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) {
            if (e instanceof FileSystemLoopException) {
                logger.warn("Cycle detected '{}'.", file, e.getCause());
            } else {
                logger.warn("Unable to copy '{}'.", file, e.getCause());
            }

            return CONTINUE;
        }
    } // end of TreeCopier (inner class)

} // end of FileUtils
