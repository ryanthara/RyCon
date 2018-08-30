/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

package de.ryanthara.ja.rycon.ui.util;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ShellPositioner implements a simple functionality to center RyCONs widgets.
 * <p>
 * This is done because of there is no simple method in SWT.
 * <h3>Changes:</h3>
 * <ul>
 * <li>3: add last position and multi monitor support</li>
 * <li>2: code improvements and clean up </li>
 * <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public class ShellPositioner {

    private static final Logger logger = LoggerFactory.getLogger(ShellPositioner.class.getName());

    /**
     * Calculate the centered shell location and return it as {@link Point} object.
     * <p>
     * The centered shell location depends on the screen size and the shell size. To calculate
     * these parameters are reader from the display and shell objects from the calling widgets.
     * The coordinates of the centered shell location represents the upper left corner of the widgets.
     *
     * @param parent the parent shell
     *
     * @return centered shell position on the primary monitor
     */
    public static Point centerShellOnPrimaryMonitor(Shell parent) {
        Monitor primary = parent.getDisplay().getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = parent.getBounds();

        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        return new Point(x, y);
    }

    /**
     * Calculate the vertical centered shell location and return it as {@link Point} object.
     * <p>
     * The centered shell location depends on the screen size and the shell size. To calculate
     * these parameters are reader from the display and shell objects from the calling widgets.
     * The coordinates of the centered shell location represents the upper left corner of the widgets.
     *
     * @param parent the parent shell
     *
     * @return centered shell position on the primary monitor
     */
    public static Point centerShellOnPrimaryMonitorVertically(Shell parent) {
        Monitor primary = parent.getDisplay().getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = parent.getBounds();

        int x = rect.x;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        return new Point(x, y);
    }

    public static Point positShell(Shell shell) {
        Monitor[] monitors = shell.getDisplay().getMonitors();

        final String lastUsedDisplay = Main.pref.getUserPreference(PreferenceKeys.LAST_USED_DISPLAY);

        if (lastUsedDisplay == null || lastUsedDisplay.trim().equals("") || lastUsedDisplay.equals("-1")) {
            return centerShellOnPrimaryMonitor(shell);
        } else {
            try {
                if (monitors.length >= Integer.parseInt(lastUsedDisplay)) {
                    String s;

                    if (lastUsedDisplay.equals("0")) {
                        s = Main.pref.getUserPreference(PreferenceKeys.LAST_POS_PRIMARY_MONITOR);
                    } else {
                        s = Main.pref.getUserPreference(PreferenceKeys.LAST_POS_SECONDARY_MONITOR);
                    }

                    String[] coords = s.split(",");

                    return new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                }
            } catch (NumberFormatException e) {
                logger.warn("Can not parse stored user preference key value '{}' to number for positioning the window on the display. ", lastUsedDisplay, e.getCause());
            }
        }

        return centerShellOnPrimaryMonitor(shell);
    }

} // end of ShellPositioner
