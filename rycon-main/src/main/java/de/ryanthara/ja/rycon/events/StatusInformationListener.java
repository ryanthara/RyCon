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

import java.util.EventListener;

/**
 * This class implements a custom event listener which is used for updating the status bar with
 * an event driven mechanism.
 *
 * @author sebastian
 * @version 2
 * @since 7
 */
public interface StatusInformationListener extends EventListener {

    void notification(StatusInformationEvent event);

} // end of StatusInformationListener
