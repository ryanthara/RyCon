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
package de.ryanthara.ja.rycon.util;

import de.ryanthara.ja.rycon.core.elements.GsiBlock;
import de.ryanthara.ja.rycon.core.elements.RyBlock;

import java.util.Comparator;
import java.util.List;

/**
 * Provides functions to sort different type of {@link java.util.ArrayList} for RyCON's widgets.
 *
 * @author sebastian
 * @version 1
 * @see <a href="https://docs.oracle.com/javase/tutorial/collections/interfaces/order.html">Javadoc</a>
 * @since 12
 */
// TODO: 29.07.17 implement a clean compare function with equals(), hash() and compare() for RyCON
public final class SortUtils {

    /**
     * SortUtils is non-instantiable.
     */
    private SortUtils() {
        throw new AssertionError();
    }

    /**
     * Sorts an {@code List<RyBlock>} of {@code RyBlock} elements by integer value.
     *
     * @param ryBlocks {@code List<RyBlock>} to be sorted by integer value
     */
    public static void sortByCode(List<RyBlock> ryBlocks) {
        ryBlocks.sort(Comparator.comparingInt(RyBlock::getNumber));
    }

    /**
     * Sorts an {@code List<GsiBlock>} of {@code GsiBlock} elements by word index (WI).
     *
     * <p>
     * The line doesn't contains two or more blocks with the same word index.
     *
     * @param gsiBlocks {@code List<GsiBlock>} to be sorted by word index (WI)
     */
    public static void sortByWordIndex(List<GsiBlock> gsiBlocks) {
        gsiBlocks.sort((o1, o2) -> {
            if (o1.getWordIndex() > o2.getWordIndex()) {
                return 1;
            } else if (o1.equals(o2)) {
                return 0;
            } else {
                return -1;
            }
        });
    }

}
