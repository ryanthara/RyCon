/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.elements.GSIBlock;
import de.ryanthara.ja.rycon.elements.RyBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class provides static functions to sort different {@link java.util.ArrayList} for RyCON's widgets.
 *
 * @author sebastian
 * @version 1
 * @see <a href="https://docs.oracle.com/javase/tutorial/collections/interfaces/order.html">Javadoc</a>
 * @since 12
 */
public class SortHelper {

    /**
     * Sorts an {@link ArrayList<RyBlock>} of {@link RyBlock} elements by integer value.
     *
     * @param ryBlocks {@link ArrayList<RyBlock>} to be sorted by integer value
     */
    public static void sortByCode(ArrayList<RyBlock> ryBlocks) {
        Collections.sort(ryBlocks, new Comparator<RyBlock>() {
            @Override
            public int compare(RyBlock o1, RyBlock o2) {
                if (o1.getNumber() > o2.getNumber()) {
                    return 1;
                } else if (o1.getNumber() == o2.getNumber()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
    }

    /**
     * Sorts an {@link ArrayList<GSIBlock>} of {@link GSIBlock} elements by word index (WI).
     * <p>
     * The line doesn't contains two or more blocks with the same word index.
     *
     * @param gsiBlocks {@link ArrayList<GSIBlock>} to be sorted by word index (WI)
     */
    public static void sortByWordIndex(ArrayList<GSIBlock> gsiBlocks) {
        Collections.sort(gsiBlocks, new Comparator<GSIBlock>() {
            @Override
            public int compare(GSIBlock o1, GSIBlock o2) {
                if (o1.getWordIndex() > o2.getWordIndex()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

} // end of SortHelper
