/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.custom
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
package de.ryanthara.ja.rycon.ui.custom;

/**
 * This enumeration is used for the status of the {@link StatusBar}.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum Status {

    ERROR, WARNING, OK;

    /**
     * Returns the {@link Status} from index parameter as static access from switch cases.
     *
     * @param index index to return
     * @return Status by index
     */
    public static Status fromIndex(int index) {
        Status selectedStatus = null;

        for (Status status : values()) {
            if (status.ordinal() == index) {
                selectedStatus = status;
            }
        }

        return selectedStatus;
    }

}
