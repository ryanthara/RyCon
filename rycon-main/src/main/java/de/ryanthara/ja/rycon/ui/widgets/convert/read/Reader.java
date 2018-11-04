/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package ${PACKAGE_NAME}
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
package de.ryanthara.ja.rycon.ui.widgets.convert.read;

import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface for reading operations in the {@link ConverterWidget}.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public abstract class Reader {

    /**
     * Returns the read string lines as {@link List}.
     *
     * <p>
     * This method is used vice versa with the method {@link #getLines()}. The one which is not used,
     * returns an empty list for indication.
     *
     * @return read string lines
     */
    public List<String[]> getCsv() {
        return List.of();
    }

    /**
     * Returns the read string lines as {@link List}.
     *
     * <p>
     * This method is used vice versa with the method {@link #getCsv()}. The one which is not used,
     * returns an empty list for indication.
     *
     * @return read string lines
     */
    public List<String> getLines() {
        return List.of();
    }

    /**
     * Reads the ... file from ... given as parameter and returns the read file success.
     *
     * @param file2Read {@link Path} reference to file
     * @return read file success
     */
    public abstract boolean readFile(Path file2Read);

}
