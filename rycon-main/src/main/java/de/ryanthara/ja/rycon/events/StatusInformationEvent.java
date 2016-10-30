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

package de.ryanthara.ja.rycon.events;

import java.util.EventObject;

/**
 * This class implements a custom event object which is used for updating the status bar with an event driven mechanism.
 *
 * @author sebastian
 * @version 1
 * @since 7
 */
public class StatusInformationEvent extends EventObject {

    private final String statusText;

    /**
     * Constructs a new instance of this class given a calling object and the status text to be shown on the status bar.
     *
     * @param source     calling source object
     * @param statusText text to be shown on the status bar
     */
    public StatusInformationEvent(Object source, String statusText) {
        super(source);
        this.statusText = statusText;
    }

    /**
     * Returns the status text.
     *
     * @return status text
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * Prints the {@link StatusInformationEvent} as text to the console.
     *
     * @return class name, status text and source
     */
    @Override
    public String toString() {
        return this.getClass().getName() + "status text = " + statusText + " on " + source;
    }

} // end of StatusInformationEvent
