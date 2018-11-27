/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.logfile.leica
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
package de.ryanthara.ja.rycon.core.logfile.leica;

import java.util.Optional;

/**
 * This enumeration holds the setup methods.
 * <p>
 * Known methods (original order from Leica Geosystems Setup program) are:
 * <ul>
 * <li>Set Azimuth</li>
 * <li>Known Backsight Point</li>
 * <li>Orientation &amp; Height Transfer</li>
 * <li>Resection</li>
 * <li>Resection Helmert</li>
 * <li>Local Resection Helmert</li>
 * </ul>
 * <p>
 * The method strings in the logfile.txt file differs from the complete name to some reasons.
 *
 * @author sebastian
 * @version 2
 * @since 27
 */
public enum SetupMethod {

    KNOWN_AZIMUTH("Known Azimuth"),
    KNOWN_BACKSIGHT_POINT("Known BS Point"),
    ORIENTATION_AND_HEIGHT_TRANSFER("Ori & Ht Transfer"),
    RESECTION("Resection"),
    RESECTION_HELMERT("Resection Helmert"),
    RESECTION_LOCAL("Local Resection");

    private final String identifier;

    SetupMethod(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the current setup method from a given identifier string which is caught from the logfile.txt file.
     *
     * @param identifier identifier string from logfile.txt file
     * @return current SetupMethod
     */
    public static Optional<SetupMethod> fromIdentifier(String identifier) {
        for (SetupMethod method : values()) {
            if (method.identifier.equals(identifier)) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the current identifier of the setup method.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the identifier as string.
     *
     * @return the identifier as string
     */
    @Override
    public String toString() {
        return identifier;
    }

}
