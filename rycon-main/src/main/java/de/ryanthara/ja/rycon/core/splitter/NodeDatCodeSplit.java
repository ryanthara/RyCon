/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.splitter
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
package de.ryanthara.ja.rycon.core.splitter;

import de.ryanthara.ja.rycon.core.elements.RyBlock;
import de.ryanthara.ja.rycon.util.SortHelper;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class NodeDatCodeSplit {
    private final ArrayList<String> readStringLines;
    private final TreeSet<Integer> foundCodes;

    /**
     * Constructs a new instance of this class given a read line based text file with a specified format.
     *
     * @param arrayList {@code ArrayList<String>} with lines in text format
     */
    public NodeDatCodeSplit(ArrayList<String> arrayList) {
        this.readStringLines = arrayList;
        foundCodes = new TreeSet<>();
    }

    /**
     * Returns the found codes as an integer array.
     * <p>
     * This is necessary because of the elimination of the code in the string line.
     *
     * @return found codes as {@code TreeSet<Integer>}
     */
    public TreeSet<Integer> getFoundCodes() {
        return foundCodes;
    }

    /**
     * Splits a 'node.dat' text file from cadwork into separate files by code.
     * <p>
     * A separate file is generated for every existing code. Lines without code will get the pseudo code '987789'.
     * <tt>RyCON</tt> need a text file format that is no, code, x, y, z and divided by blank or tab.
     * <p>
     * A 'node.dat' file is a node list export file from cadwork cad program with the following structure.
     * Usually the units are in metre. A code is always set, a name only if the knots are named.
     * <p>
     * {@code No    X   Y   Z   Code    Name}
     *
     * @return converted {@code ArrayList<ArrayList<String>>} for writing
     */
    public ArrayList<ArrayList<String>> processCodeSplit() {
        StringTokenizer stringTokenizer;

        ArrayList<String> header = new ArrayList<>();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<RyBlock> linesWithCode = new ArrayList<>();

        // check for content
        if (readStringLines.get(1).contains("Knotenliste in [m]")) {
            for (int i = 0; i < readStringLines.size(); i++) {
                String line = readStringLines.get(i);

                // write first six lines into a header which is added to every code file
                if (i < 6) {
                    header.add(line);
                } else {
                    // line six till the end contains the points
                    stringTokenizer = new StringTokenizer(line);

                    /*
                     * a line without name contains 5 tokens (no x y z code)
                     * a line with name contains 6 or more tokens (no x y z code 'name with spaces')
                     */
                    stringTokenizer.nextToken();    // number
                    stringTokenizer.nextToken();    // X
                    stringTokenizer.nextToken();    // Y
                    stringTokenizer.nextToken();    // Z

                    String code = stringTokenizer.nextToken();
                    foundCodes.add(Integer.parseInt(code));

                    linesWithCode.add(new RyBlock(Integer.parseInt(code), line));
                }
            }

            SortHelper.sortByCode(linesWithCode);

            if (linesWithCode.size() > 0) {
                // helpers for generating a new array for every found code
                int code = linesWithCode.get(0).getNumber();
                ArrayList<String> lineStorage = new ArrayList<>(header);

                // fill in the sorted textBlocks into an ArrayList<ArrayList<String>> for writing
                for (RyBlock ryBlock : linesWithCode) {
                    if (code == ryBlock.getNumber()) {
                        lineStorage.add(ryBlock.getString());
                    } else {
                        result.add(lineStorage);
                        lineStorage = new ArrayList<>(header);
                        lineStorage.add(ryBlock.getString());
                    }

                    code = ryBlock.getNumber();
                }

                // insert last element
                result.add(lineStorage);
            }
        }

        return result;
    }

} // end of NodeDatCodeSplit
