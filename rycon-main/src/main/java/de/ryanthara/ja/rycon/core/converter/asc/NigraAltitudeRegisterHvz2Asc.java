/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.asc
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
package de.ryanthara.ja.rycon.core.converter.asc;

import de.ryanthara.ja.rycon.core.converter.Converter;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert a NigraWin/NivNET altitude register until version 3.x (*.HZV)
 * into an ascii file.
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the NigraWin/NivNET altitude register file if present. Otherwise they
 * will be set to local values starting at 0,0 and raise in both axis by a constant value.
 * <p>
 * Until version 3.x the NigraWin/NivNET altitude register uses the file ending (*.HVZ). Since version 4.0 it
 * uses the file ending (*.ASC).
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class NigraAltitudeRegisterHvz2Asc extends Converter {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for the read {@code ArrayList<String>}
     * altitude register from Nigra/NigraWin.
     *
     * @param readStringLines read lines
     */
    public NigraAltitudeRegisterHvz2Asc(ArrayList<String> readStringLines) {
        this.readStringLines = new ArrayList<>(readStringLines);
    }

    /**
     * Converts a read Nigra/NigraWin altitude register file (*.HZV) into an ascii file with
     * pseudo coordinates for x and y.
     * <p>
     * This function based on fixed string positions and substring operations from the standard
     * output of Nigra/NigraWin. This is necessary to find possible x and y coordinates.
     * <p>
     * If they are not present, the algorithm fall back to pseudo coordinates for x and y.
     *
     * @return converted Nigra/NigraWin altitude register format file
     *
     * @see NigraAltitudeRegisterAsc2Asc
     */
    @Override
    public ArrayList<String> convert() {
        // still looks like that the format did not changed relevantly for this function
        NigraAltitudeRegisterAsc2Asc nigraAltitudeRegisterAsc2Asc = new NigraAltitudeRegisterAsc2Asc(readStringLines);

        return nigraAltitudeRegisterAsc2Asc.convert();
    }

} // end of NigraAltitudeRegisterHvz2Asc
