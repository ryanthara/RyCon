/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.nio
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
package de.ryanthara.ja.rycon.nio.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides common functions for {@link java.nio.file.Path} objects.
 * <p>
 * The idea based on the popular <a href="https://github.com/apache/commons-io/blob/master/src/main/java/org/apache/commons/io/FilenameUtils.java">org.apache.commons.io.FilenameUtils.java</a> class
 * which provides common functions for filenames and filepath.
 * <p>
 * This basic version works with {@link java.nio.file.Paths} instead of {@link String} based filename representations.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class PathUtils {

    /**
     * The extension separator character.
     */
    private static final char EXTENSION_SEPARATOR = '.';
    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';
    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * PathUtils is non-instantiable.
     */
    private PathUtils() {
        throw new AssertionError();
    }

    /**
     * Gets the base name, minus the full path and extension, from a full filename.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * The text after the last forward or backslash and before the last dot is returned.
     * <pre>
     * a/b/c.txt --&gt; c
     * a.txt     --&gt; a
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     * <p>
     * It doesn't matter on which machine the code runs, the output will be the same.
     *
     * @param filename the filename to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists
     */
    public static Path getBaseName(Path filename) {
        return removeExtension(getName(filename));
    }

    /**
     * Gets the name minus the path from a full filename.
     *
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * The text after the last forward or backslash is returned.
     *
     * <pre>
     * a/b/c.txt --&gt; c.txt
     * a.txt     --&gt; a.txt
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     *
     * <p>
     * It doesn't matter on which machine the code runs, the output will be the same.
     *
     * @param filename the filename to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists
     */
    private static Path getName(Path filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfLastSeparator(filename);

        return Paths.get(filename.toString().substring(index + 1));
    }


    /**
     * Returns the index of the last extension separator character, which is a dot.
     * <p>
     * This method also checks that there is no directory separator after the last dot.
     * To do this it uses {@link #indexOfLastSeparator(Path)} which will
     * handle a file in either Unix or Windows format.
     * <p>
     * It doesn't matter on which machine the code runs, the output will be the same.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    private static int indexOfExtension(Path filename) {
        if (filename == null) {
            return -1;
        }

        int extensionPos = filename.toString().lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);

        return (lastSeparator > extensionPos ? -1 : extensionPos);
    }

    /**
     * Returns the index of the last directory separator character.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * The position of the last forward or backslash is returned.
     * <p>
     * It doesn't matter on which machine the code runs, the output will be the same.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    private static int indexOfLastSeparator(Path filename) {
        if (filename == null) {
            return -1;
        }

        int lastUnixPos = filename.toString().lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.toString().lastIndexOf(WINDOWS_SEPARATOR);

        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * Prepares the output filename.
     * <p>
     * This method returns the assembled filename which contains the original filename
     * of the source path and the current edit string.
     *
     * @param path              original filename
     * @param editString        edit string to be assembled
     * @param fileNameExtension filename extension to be assembled
     * @return new output filename
     */
    public static Path prepareOutputFileName(Path path, final String editString, final String fileNameExtension) {
        if (editString.equals("")) {
            return Paths.get(removeExtension(path) + fileNameExtension);
        } else {
            return Paths.get(removeExtension(path) + editString + fileNameExtension);
        }
    }

    /**
     * Removes the extension from a filename.
     * <p>
     * This method returns the textual part of the filename before the last dot.
     * There must be no directory separator after the dot.
     * <pre>
     * foo.txt    --&gt; foo
     * a\b\c.jpg  --&gt; a\b\c
     * a\b\c      --&gt; a\b\c
     * a.b\c      --&gt; a.b\c
     * </pre>
     * <p>
     * It doesn't matter on which machine the code runs, the output will be the same.
     *
     * @param filename the filename to query, null returns null
     * @return the filename minus the extension
     */
    private static Path removeExtension(Path filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return Paths.get(filename.toString().substring(0, index));
        }
    }

}
