/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.elements
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
package de.ryanthara.ja.rycon.core.elements;

import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissTypeIdentifier;

/**
 * Instances of this class defines a helper for operations on Zeiss measurement and coordinate files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class ZeissBlock {

    private final String value, unit;
    private final ZeissTypeIdentifier typeIdentifier;

    /**
     * Constructs a new instance of this class given an identifier, a value and the unit.
     *
     * @param typeIdentifier typeIdentifier of the block
     * @param value          value of the block
     * @param unit           unit of the block
     */
    public ZeissBlock(ZeissTypeIdentifier typeIdentifier, String value, String unit) {
        this.typeIdentifier = typeIdentifier;
        this.value = value;
        this.unit = unit;
    }

    /**
     * Returns the typeIdentifier of the Zeiss block.
     *
     * @return the typeIdentifier
     */
    public ZeissTypeIdentifier getTypeIdentifier() {
        return typeIdentifier;
    }

    /**
     * Returns the unit of the Zeiss block.
     *
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns the value of the Zeiss block.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

}  // end of ZeissBlock
