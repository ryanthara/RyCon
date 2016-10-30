/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

import java.util.ArrayList;

/**
 * This class implements a custom event source object which is used for updating the status bar with an
 * event driven mechanism.
 *
 * @author sebastian
 * @version 1
 * @since 7
 */
public class StatusInformationEventSource {

    // Container for subscribed FireListeners.
    ArrayList<StatusInformationListener> statusInformationListenerList = new ArrayList<>();
    private StatusInformationEvent statusInformationEvent;

    // Add StatusInformationListener
    public void addStatusInformationListener(StatusInformationListener statusInformationListener) {
        statusInformationListenerList.add(statusInformationListener);
    }

    // Remove StatusInformationListener
    public void removeFireListener(StatusInformationListener statusInformationListener) {
        statusInformationListenerList.remove(statusInformationListener);
    }

    // Fire event notifications to StatusInformationListener.
    public void updateStatus() {
        System.out.println("Fire Started in " + statusInformationEvent.getSource().getClass()
                .getSimpleName() + "!");
        for (StatusInformationListener obj : statusInformationListenerList) {
            obj.notification(statusInformationEvent);
        }
    }

    // Initialization Block
    {
        // fireEvent = new FireEvent(this, "start fire", System.currentTimeMillis());
        statusInformationEvent = new StatusInformationEvent(this, "UPDATE");
    }

} // end of StatusInformationEventSource
