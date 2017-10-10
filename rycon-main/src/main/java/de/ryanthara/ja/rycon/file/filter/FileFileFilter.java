/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.file.filter
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
package de.ryanthara.ja.rycon.file.filter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * A filter that accepts only files. Directories are ignored.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class FileFileFilter implements DirectoryStream.Filter<Path> {

    /**
     * Decides if the given directory entry should be accepted or filtered.
     *
     * @param entry the directory entry to be tested
     *
     * @return {@code true} if the directory entry should be accepted
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean accept(Path entry) {
        return Files.isRegularFile(entry, LinkOption.NOFOLLOW_LINKS);
    }

} // end of FileFileFilter
