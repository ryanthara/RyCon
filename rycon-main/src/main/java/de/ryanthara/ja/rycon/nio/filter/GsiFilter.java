/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.nio.filter
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
package de.ryanthara.ja.rycon.nio.filter;

import de.ryanthara.ja.rycon.nio.FileNameExtension;

import java.nio.file.DirectoryStream;
import java.nio.file.Path;

/**
 * A filter that accepts files in the Leica Geosystems GSI format with the file ending '.gsi'.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class GsiFilter implements DirectoryStream.Filter<Path> {

    /**
     * Decides if the given directory path should be accepted or filtered.
     *
     * @param path the directory path to be tested
     * @return {@code true} if the directory path should be accepted
     */
    @Override
    public boolean accept(Path path) {
        if (path != null) {
            Path fileName = path.getFileName();

            if (fileName != null) {
                return fileName.toString().toUpperCase().endsWith(FileNameExtension.LEICA_GSI.getExtension());
            }
        }

        return false;
    }

}
