/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.widgets.convert.read
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

/**
 * Instances of this class holds parameter for the reading classes in
 * the package {@link de.ryanthara.ja.rycon.ui.widgets.convert.read}
 * and returns them with getter methods.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class ReadParameter {

    private final boolean sourceContainsCode;
    private final boolean useSemicolonSeparator;
    private final boolean useZeroHeightsFromCadwork;

    /**
     * Constructs a new instance of this class with a couple of parameters.
     *
     * @param sourceContainsCode        source file contains code column on second position
     * @param useSemicolonSeparator     use semicolon instead of comma as separator sign
     * @param useZeroHeightsFromCadwork use the 0.000 from cadwork as height value for coordinates without a given height
     */
    public ReadParameter(boolean sourceContainsCode, boolean useSemicolonSeparator, boolean useZeroHeightsFromCadwork) {
        this.sourceContainsCode = sourceContainsCode;
        this.useSemicolonSeparator = useSemicolonSeparator;
        this.useZeroHeightsFromCadwork = useZeroHeightsFromCadwork;
    }

    /**
     * Returns true if the source file contains a code column on second position.
     *
     * @return true if source file contains code column
     */
    public boolean isSourceContainsCode() {
        return sourceContainsCode;
    }

    /**
     * Returns true if a semicolon is used as separator sign instead of the comma sign.
     *
     * @return true if separator sign is semicolon
     */
    public boolean isUseSemicolonSeparator() {
        return useSemicolonSeparator;
    }

    /**
     * Returns true if coordinates with height of 0.000 from cadwork should be used.
     *
     * @return true if zero height from cadwork should be used
     */
    public boolean isUseZeroHeightsFromCadwork() {
        return useZeroHeightsFromCadwork;
    }

} // end of ReadParameter
