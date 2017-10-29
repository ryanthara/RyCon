/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.core.converter.gsi;

import de.ryanthara.ja.rycon.core.elements.CaplanBlock;
import de.ryanthara.ja.rycon.core.elements.GsiBlock;

import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert a Caplan K formatted coordinate file
 * into Leica GSI8 or GSI16 formatted files.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class Caplan2GSI {

    private ArrayList<String> readStringLines;

    /**
     * Constructs a new instance of this class with a parameter for the reader Caplan K file {@link ArrayList} string.
     *
     * @param readStringLines {@code ArrayList<String>} with lines in Caplan K format
     */
    public Caplan2GSI(ArrayList<String> readStringLines) {
        this.readStringLines = readStringLines;
    }

    /**
     * Converts a CAPLAN K file to GSI8 or GSI16 formatted file.
     * <p>
     * Due to issues data precision is going to be lost.
     *
     * @param isGSI16         true if GSI16 format is used
     * @param writeCodeColumn true if the valency indicator is used as code (WI71)
     *
     * @return converted GSI format file
     */
    public ArrayList<String> convertK2GSI(boolean isGSI16, boolean writeCodeColumn) {
        ArrayList<GsiBlock> blocks;
        ArrayList<ArrayList<GsiBlock>> blocksInLines = new ArrayList<>();

        int lineCounter = 1;

        for (String line : readStringLines) {
            // skip empty lines directly after reading
            if (!line.trim().isEmpty()) {
                blocks = new ArrayList<>();

                CaplanBlock caplanBlock = new CaplanBlock(line);

                if (caplanBlock.getNumber() != null) {
                    blocks.add(new GsiBlock(isGSI16, 11, lineCounter, caplanBlock.getNumber()));
                }

                if ((caplanBlock.getValency() != -1) & writeCodeColumn) {
                    blocks.add(new GsiBlock(isGSI16, 41, Integer.toString(caplanBlock.getValency())));
                }

                if (caplanBlock.getEasting() != null) {
                    blocks.add(new GsiBlock(isGSI16, 81, caplanBlock.getEasting()));
                }

                if (caplanBlock.getNorthing() != null) {
                    blocks.add(new GsiBlock(isGSI16, 82, caplanBlock.getNorthing()));
                }

                if (caplanBlock.getHeight() != null) {
                    blocks.add(new GsiBlock(isGSI16, 83, caplanBlock.getHeight()));
                }

                if ((caplanBlock.getCode() != null) & writeCodeColumn) {
                    blocks.add(new GsiBlock(isGSI16, 71, caplanBlock.getCode()));

                    if (caplanBlock.getAttributes().size() > 0) {
                        for (int i = 1; (i < caplanBlock.getAttributes().size()) & (i < 9); i++) {
                            blocks.add(new GsiBlock(isGSI16, (71 + i), caplanBlock.getAttributes().get(i)));
                            lineCounter = lineCounter + 1;
                        }
                    }
                }

                // check for at least one or more added elements to prevent writing empty lines
                if (blocks.size() > 0) {
                    lineCounter = lineCounter + 1;
                    blocksInLines.add(blocks);
                }
            }
        }

        return BaseToolsGsi.lineTransformation(isGSI16, blocksInLines);
    }

} // end of Caplan2GSI
