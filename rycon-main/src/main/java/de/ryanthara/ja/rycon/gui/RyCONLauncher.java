/*
 * License: GPL. Copyright 2015- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

import org.eclipse.swt.widgets.Display;

/**
 * RyCONLauncher controls the launch process of RyCON and initialize the Display, a SplashScreen and
 * all of it's services before the main window is shown.
 * <p>
 * This functionality isn't still active.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 6
 */
public class RyCONLauncher {


    /**
     * Constructor which creates a display to show the splash screen.
     */
    public RyCONLauncher() {

        Display display = new Display();

        // splash screen
        SplashScreen splashScreen = new SplashScreen(display);

        // initialization process

        // main window
        new MainApplication();


        while((Display.getCurrent().getShells().length != 0)
                && !Display.getCurrent().getShells()[0].isDisposed())
        {
            if(!display.readAndDispatch())
            {
                display.sleep();
            }
        }

    }

    /**
     * Entry point of the program.
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        // to provide illegal thread access -> https://github.com/udoprog/c10t-swt/issues/1
        // add -XstartOnFirstThread as a java option on VM parameter on OS X
        new RyCONLauncher();
    }
}
