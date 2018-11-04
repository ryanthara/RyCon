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
import java.util.List;
import java.util.Objects;

/**
 * A ReferencePlaneProcessor clears up a known Reference Plane block from a Leica Geosystems logfile.txt file.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public final class ReferencePlaneProcessor implements Processor {

    /**
     * ReferencePlaneProcessor is non-instantiable.
     */
    private ReferencePlaneProcessor() {
        throw new AssertionError();
    }

    /**
     * Clears up the unnecessary lines from the logfile.txt file that was written
     * by the Reference Plane program.
     *
     * @param block Reference Plane logfile block
     * @return cleared up Reference Plane block
     */
    public static List<String> run(List<String> block) {
        ArrayList<String> result;

        System.out.println(block.size());

        // TODO add reference plane clean
        result = (ArrayList<String>) block;

        Objects.requireNonNull(result, "Reference Plane logfile block is null!");
        return List.copyOf(result);
    }

}
