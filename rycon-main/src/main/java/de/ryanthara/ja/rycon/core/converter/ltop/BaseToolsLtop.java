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
package de.ryanthara.ja.rycon.core.converter.ltop;

import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.data.PreferenceKey;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.LangString;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.util.StringUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.LANG_STRING;

/**
 * Provides basic and helper functions that are used for converting different
 * file formats into text based measurement and coordinate files for LTOP.
 *
 * <p>
 * The methods and helpers are used in other classes of the package
 * {@link de.ryanthara.ja.rycon.core.converter.ltop} lets you
 * <ul>
 * <li>eliminate duplicate points,
 * <li>sort results,
 * <li>prepares the output strings,
 * <li>and write comment lines.
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 8
 */
final class BaseToolsLtop {

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
    static final String cartesianCoordsIdentifier = "$$PK";
    //    private final String ellipsoidCoordsIdentifier = "$$EL";
    static final String measurementLineIdentifier = "$$ME";
    private static final String emptySpace4 = "    ";
    // prevent wrong output with empty strings of defined length for MES files
//    static final String weather = "            ";
//    static final String meanError = "      ";
    private static final String emptySpace6 = "      ";
    private static final String emptySpace8 = "        ";

    /**
     * BaseToolsLtop is non-instantiable.
     */
    private BaseToolsLtop() {
        throw new AssertionError();
    }

    /**
     * Eliminates duplicate points from an ArrayList<String>.
     * <p>
     * Points are identical if the 3D distance is less than 3cm and the point number is the same. The point number is
     * used for find wrong numbered points.
     *
     * @param arrayList unsorted ArrayList<String>
     * @return sorted ArrayList<String>
     */
    // TODO precise and clear up this
    static List<String> eliminateDuplicatePoints(List<RyPoint> arrayList) {
        List<String> result = new ArrayList<>();

        // Set minDistance to default value and try to parse the settings value
        final double minDistance = StringUtils.parseDoubleValue(PreferenceKey.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE);

        // Sort the tree set of RyPoints
        // public int compare(ArrayList<RyPoint> p1, ArrayList<RyPoint> p2) {
        TreeSet<RyPoint> ryPoints = new TreeSet<>((pt1, pt2) -> {
            /*
            Compare at the three distances x, y and z before calculating the slope distance because of reducing
            calculation time and therefore increase the speed.

            Points are equal if they are in a slope distance of 'maxDistance' and have the same number!
             */
            if ((pt1.getSlopeDistance(pt2) < minDistance) && (pt1.getNumber().equalsIgnoreCase(pt2.getNumber()))) {
                return 0;
            } else {
                return 1;
            }
        });

        // Add the ArrayList anc check for duplicate points
        ryPoints.addAll(arrayList);

        // Bring back the points into an ArrayList<String>
        for (RyPoint ryPoint : ryPoints) {
            if (!ryPoint.getPrintLine().trim().equalsIgnoreCase("")) {
                result.add(ryPoint.getPrintLine());
            }
        }

        return List.copyOf(result);
    }

    /**
     * Fills the ArrayList<RyPoint> with {@link RyPoint} objects.
     *
     * @param ryPoints   the ArrayList<RyPoint>
     * @param easting    easting value
     * @param northing   northing value
     * @param height     height value
     * @param resultLine result line as string
     */
    static void fillRyPoints(List<RyPoint> ryPoints, String easting, String northing, String height, String resultLine) {
        double x = Double.NaN, y = Double.NaN, z = Double.NaN;

        if (!easting.trim().equals("")) {
            x = StringUtils.parseDoubleValue(easting);
        }

        if (!northing.trim().equals("")) {
            y = StringUtils.parseDoubleValue(northing);
        }

        if (!height.trim().equals("")) {
            z = StringUtils.parseDoubleValue(height);
        }

        ryPoints.add(new RyPoint(number, x, y, z, resultLine));
    }

    /**
     * Prepares the result string for KOO file output from it's elements, which are given as single parameters.
     *
     * @param number            the point number
     * @param pointType         the point typ
     * @param toleranceCategory the tolerance category_Formats
     * @param easting           the easting coordinate
     * @param northing          the northing coordinate
     * @param height            the height coordinate
     * @param geoid             the geoid
     * @param eta               the eta
     * @param xi                the xi
     * @return prepared result string
     */
    static String prepareStringForKOO(String number, String pointType, String toleranceCategory,
                                      String easting, String northing, String height,
                                      String geoid, String eta, String xi) {

        if (StringUtils.parseBooleanValue(PreferenceKey.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE)) {
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
     * Sorts an ArrayList<String> ascending by 'first token'.
     *
     * @param arrayList unsorted ArrayList<String>
     * @return sorted ArrayList<String>
     */
    static List<String> sortResult(List<String> arrayList) {
        arrayList.sort(String::compareToIgnoreCase);

        return List.copyOf(arrayList);
    }

    /**
     * Write the comment line into a given ArrayList<String>.
     * <p>
     * The following identifiers for the file type are used:
     * - $$ME for measurement file
     * - $$PK for cartesian coordinates
     * - $$EL for geographic coordinates
     *
     * @param result              ArrayList<String> to writer in
     * @param firstLineIdentifier identifier for different file type ($$ME, $$PK or $$EL)
     */
    static void writeCommendLine(List<String> result, String firstLineIdentifier) {
        // insert RyCON version, date and time
        Date d = new Date();
        DateFormat df;
        df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);

        result.add(String.format(firstLineIdentifier + Separator.WHITESPACE.getSign() + ResourceBundleUtils.getLangStringFromXml(LANG_STRING, LangString.commentLine_Ltop), Version.getVersion(), df.format(d)));
    }

}
