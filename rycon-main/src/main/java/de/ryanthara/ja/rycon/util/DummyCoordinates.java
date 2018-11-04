/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.util
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

import org.eclipse.swt.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * This class delivers dummy coordinates for the
 * {@link de.ryanthara.ja.rycon.ui.widgets.LevellingWidget}.
 *
 * <p>
 * The coordinate space for rectangles and points is considered
 * to have increasing values downward and to the right from its
 * origin making this the normal, computer graphics oriented notion
 * of (x, y) coordinates rather than the strict mathematical one.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public final class DummyCoordinates {

    /**
     * DummyCoordinates is non-instantiable.
     */
    private DummyCoordinates() {
        throw new AssertionError();
    }

    /**
     * Returns the dummy coordinate as {@link List} of 2D {@link Point}.
     * <p>
     * The dummy coordinates raises one for one with a distance of one metre.
     *
     * @param length array length
     * @return dummy coordinates
     */
    public static List<Point> getList(int length) {
        List<Point> points = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            points.add(new Point(i, i));
        }

        return List.copyOf(points);
    }

}
