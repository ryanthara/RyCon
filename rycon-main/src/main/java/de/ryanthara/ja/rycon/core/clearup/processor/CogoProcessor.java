/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.clearup.processor
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
package de.ryanthara.ja.rycon.core.clearup.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A CogoProcessor clears up a known COGO block from a Leica Geosystems logfile.txt file.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public final class CogoProcessor implements Processor {

    /**
     * CogoProcessor is non-instantiable.
     */
    private CogoProcessor() {
        throw new AssertionError();
    }

    /**
     * Clears up the unnecessary lines from the logfile.txt file that was written
     * by the COGO program.
     *
     * @param block COGO logfile block
     * @return cleared up COGO block
     */
    public static List<String> run(List<String> block) {
        List<String> result = null;

        if (block != null) {
            result = new ArrayList<>();

            final String[] identifiers = {"Computed", "Base Point", "Inverse", "Offset Point", "Traverse", "Stakeout Diff"};
            boolean noCalculatedPoints = true;

            for (int i = 0; i < block.size(); i++) {
                // skip first two and last 4 lines
                if (i > 1 && i < block.size() - 4) {
                    final String s = block.get(i);

                    // detect identifier -> block is not empty
                    if (Arrays.stream(identifiers).parallel().anyMatch(s::contains)) {
                        noCalculatedPoints = false;
                    }

                    result.add(s);
                }
            }

            if (noCalculatedPoints) {
                return List.of();
            }
        }

        Objects.requireNonNull(result, "COGO logfile block is null!");
        return List.copyOf(result);
    }

}
