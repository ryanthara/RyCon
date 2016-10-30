/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.converter.ltop;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.elements.RyPoint;

import java.text.DateFormat;
import java.util.*;

/**
 * BaseToolsLTOP implements basic operations on text based measurement and coordinate files for LTOP.
 * <p>
 * Therefore a couple of methods and helpers are implemented to do the conversions and
 * operations on the given text files.
 *
 * @author sebastian
 * @version 2
 * @since 8
 */
class BaseToolsLTOP {

    // prevent wrong output with empty strings of defined length for KOO and MES files
    static final String number = "          ";
    static final String pointType = "    ";
    static final String toleranceCategory = "  ";
    static final String easting = "            ";
    static final String northing = "            ";
    static final String height = "          ";
    static final String geoid = "        ";
    static final String eta = "      ";
    static final String xi = "      ";
    private static final String emptySpace4 = "    ";
    private static final String emptySpace6 = "      ";
    private static final String emptySpace8 = "        ";

    // prevent wrong output with empty strings of defined length for MES files
//    static final String weather = "            ";
//    static final String meanError = "      ";

    static final String cartesianCoordsIdentifier = "$$PK";
    //    private final String ellipsoidCoordsIdentifier = "$$EL";
    static final String measurementLineIdentifier = "$$ME";

    /**
     * Eliminates duplicate points from an ArrayList<String>.
     * <p>
     * Points are identical if the 3D distance is less than 3cm and the point number is the same. The point number is
     * used for find wrong numbered points.
     *
     * @param arrayList unsorted ArrayList<String>
     *
     * @return sorted ArrayList<String>
     */
    static ArrayList<String> eliminateDuplicatePoints(ArrayList<RyPoint> arrayList) {
        ArrayList<String> result = new ArrayList<>();

        // set minDistance to default value and try to parse the settings value
        double d = 0.03;

        try {
            d = Double.parseDouble(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE));
        } catch (NumberFormatException e) {
            System.err.println("Can't convert maximum distance to double in eliminateDuplicatePoints()");
            e.printStackTrace();
        }

        final double minDistance = d;

        // sort the tree set of RyPoints
        TreeSet<RyPoint> set = new TreeSet<>(new Comparator<RyPoint>() {
            @Override
            //public int compare(ArrayList<RyPoint> p1, ArrayList<RyPoint> p2) {
            public int compare(RyPoint pt1, RyPoint pt2) {
                /*
                Compare at the three distances x, y and z before calculating the slope distance because of reducing
                calculation time and therefore increase the speed.

                Points are equal if they are in a slope distance of 'maxDistance' and have the same number!
                 */
                if ((pt1.getSlopeDistance(pt2) < minDistance) & (pt1.getNumber().equalsIgnoreCase(pt2.getNumber()))) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // add the ArrayList anc check for duplicate points
        set.addAll(arrayList);

        // bring back the points into an ArrayList<String>
        for (RyPoint ryPoint : set) {
            if (!ryPoint.getPrintLine().trim().equalsIgnoreCase("")) {
                result.add(ryPoint.getPrintLine());
            }
        }

        return result;
    }

    /**
     * Fills the ArrayList<RyPoint> with ryPoint objects.
     *
     * @param ryPoints   the ArrayList<RyPoint>
     * @param easting    easting value
     * @param northing   northing value
     * @param height     height value
     * @param resultLine result line as string
     */
    static void fillRyPoints(ArrayList<RyPoint> ryPoints, String easting, String northing, String height, String resultLine) {
        double x = Double.NaN, y = Double.NaN, z = Double.NaN;

        try {
            if (!easting.trim().equals("")) {
                x = Double.parseDouble(easting);
            }
            if (!northing.trim().equals("")) {
                y = Double.parseDouble(northing);
            }
            if (!height.trim().equals("")) {
                z = Double.parseDouble(height);
            }
        } catch (NumberFormatException e) {
            System.err.println("Can't convert string to double in BaseToolsLTOP:fillRyPoints()");
            System.err.println("Wrong line: " + resultLine);
        }

        ryPoints.add(new RyPoint(number, x, y, z, resultLine));
    }

    /**
     * Prepares the result string for KOO file output from it's elements, which are given as single parameters.
     *
     * @param number            the point number
     * @param pointType         the point typ
     * @param toleranceCategory the tolerance category
     * @param easting           the easting coordinate
     * @param northing          the northing coordinate
     * @param height            the height coordinate
     * @param geoid             the geoid
     * @param eta               the eta
     * @param xi                the xi
     *
     * @return prepared result string
     */
    static String prepareStringForKOO(String number, String pointType, String toleranceCategory,
                                      String easting, String northing, String height,
                                      String geoid, String eta, String xi) {

        // check for null coordinate
        if (Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE))) {
            String xyz = northing.trim().concat(easting.trim()).concat(height.trim());
            xyz = xyz.replace('.', '0');

            if (xyz.matches("^[0]+$")) {
                return "";
            }
        }

        return number +
                pointType +
                emptySpace8 +
                toleranceCategory +
                emptySpace8 +
                easting +
                northing +
                emptySpace4 +
                height +
                emptySpace6 +
                geoid +
                emptySpace6 +
                eta +
                xi;
    }

    /**
     * Sorts an ArrayList<String> by 'first token'.
     *
     * @param arrayList unsorted ArrayList<String>
     *
     * @return sorted ArrayList<String>
     */
    static ArrayList<String> sortResult(ArrayList<String> arrayList) {
        Collections.sort(arrayList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        return arrayList;
    }

    /**
     * Write the comment line into a given ArrayList<String>.
     * <p>
     * The following identifiers for the file type are used:
     * - $$ME for measurement file
     * - $$PK for cartesian coordinates
     * - $$EL for geographic coordinates
     *
     * @param result              ArrayList<String> to write in
     * @param firstLineIdentifier identifier for different file type ($$ME, $$PK or $$EL)
     */
    static void writeCommendLine(ArrayList<String> result, String firstLineIdentifier) {
        // insert RyCON version, date and time
        Date d = new Date();
        DateFormat df;
        df = DateFormat.getDateTimeInstance(/* dateStyle */ DateFormat.LONG,
                                            /* timeStyle */ DateFormat.MEDIUM);

        result.add(String.format(firstLineIdentifier + " " + I18N.getStrLTOPCommentLine(), Version.getVersion(), df.format(d)));
    }

}  // end of BaseToolsLTOP
