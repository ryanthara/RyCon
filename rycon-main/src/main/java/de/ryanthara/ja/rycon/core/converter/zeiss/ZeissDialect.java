/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.core.converter.zeiss;

import java.util.Optional;

/**
 * This enumeration holds the dialect names of the Zeiss REC files.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public enum ZeissDialect {

    R4, R5, REC500, M5;

    /**
     * Returns the {@link ZeissDialect} from index parameter as static access from switch cases.
     *
     * @param index index to return
     * @return ZeissDialect by index
     */
    public static Optional<ZeissDialect> fromIndex(int index) {
        assert index < values().length;

        for (ZeissDialect zeissDialect : values()) {
            if (zeissDialect.ordinal() == index) {
                return Optional.of(zeissDialect);
            }
        }

        return Optional.empty();
    }

}
