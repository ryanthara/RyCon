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

import org.eclipse.swt.widgets.Display;

/**
 * This class starts RyCON.
 * <p>
 * It will be used to implement a clean structure for initialization process and
 * background functionality.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up</li>
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 2
 */
public class RyCONApplication {

    /**
     * Create a static mainApplication object which can be accessed from the splash screen.
     *<p> 
     * Implement this with an event handling mechanism later.
     */
    // TODO implement event handling mechanism
    public static MainApplication mainApplication;

    /**
     * Constructor which initializes the next steps.
     */
    public RyCONApplication() {
        Display display = new Display();
//        mainApplication = new MainApplication(display);
        SplashScreen splashScreen = new SplashScreen(display);
        
        while ((Display.getCurrent().getShells().length != 0) && !Display.getCurrent().getShells()[0].isDisposed()) {
            if(!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args) {
        new RyCONApplication();
    }
    
} // end of RyCONApplication
