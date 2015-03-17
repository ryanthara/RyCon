/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui
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

package de.ryanthara.ja.rycon.gui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * This class implements a simple functionality to center RyCONs widgets.
 * <p>
 * This is done because of there is no simple method in SWT.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up</li>
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class ShellCenter {

    /**
     * Calculates the centered shell location and returns it als <code>Point</code> object.
     * <p>
     * The centered shell location depends on the screen size and the shell size. For calculation
     * these parameters are read from the display and shell objects from the calling widget.
     * The coordinates of the centered shell location represents the upper left corner of the widget.
     *
     * @return centered shell location
     */
    public static Point centerShellOnPrimaryMonitor(Shell parent) {
        Monitor primary = parent.getDisplay().getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = parent.getBounds();

        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        return new Point(x, y);
    }

} // end of ShellCenter
