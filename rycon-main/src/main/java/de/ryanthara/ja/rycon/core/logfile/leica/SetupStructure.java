/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.logfile
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

import de.ryanthara.ja.rycon.core.elements.RyObservation;
import de.ryanthara.ja.rycon.core.elements.RyPoint;
import de.ryanthara.ja.rycon.core.elements.RyResidual;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The {@code SetupStructure} implements functions based on the COGO part of
 * the <tt>Leica Geosystems</tt> logfile.txt for <tt>RyCON</tt>.
 * <p>
 * This is used for encapsulating the data and error minimization.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public class SetupStructure extends LeicaLogfileBaseStructure {

    private final ArrayList<String> lines;
    // Results
    private final ArrayList<RyObservation> observationsList;
    private final ArrayList<RyResidual> residualsList;
    private String orientationCorrection;
    private String scale;
    private String standardDeviationEasting;
    private String standardDeviationNorthing;
    private String standardDeviationHeight;
    private String standardDeviationOrientation;
    private RyPoint station;
    private RyObservation observation;

    /**
     * Constructs a new {@code SetupStructure} with a parameter for the lines of the structure.
     *
     * @param lines lines to be analyzed
     */
    public SetupStructure(ArrayList<String> lines) {
        this.lines = lines;
        this.lines.removeAll(Arrays.asList(null, ""));

        observationsList = new ArrayList<>();
        residualsList = new ArrayList<>();
    }

    /**
     * Analyze the SETUP structure of the <tt>Leica Geosystems</tt> logfile.txt and
     * fills the results into the return arrays.
     *
     * @return analysis success
     */
    @Override
    public boolean analyze() {
        boolean success = false;

        super.analyzeHeader(lines);

        SetupMethod setupMethod = null;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // Setup Method "..."
            // ------------------
            if (line.startsWith(Method.SETUP_METHOD.identifier)) {
                setupMethod = SetupMethod.fromIdentifier(line.split("\"")[1]);

                switch (setupMethod) {
                    case KNOWN_AZIMUTH:
                        break;
                    case KNOWN_BACKSIGHT_POINT:
                        break;
                    case ORIENTATION_AND_HEIGHT_TRANSFER:
                        break;
                    case RESECTION:
                        break;
                    case RESECTION_HELMERT:
                        break;
                    case RESECTION_LOCAL:
                        break;
                }
            }

            // Observations
            // ------------
            else if (line.startsWith(Method.POINT_ID.identifier)) {
                switch (setupMethod) {
                    case KNOWN_AZIMUTH:
                    case KNOWN_BACKSIGHT_POINT:
                        // same structure for known azimuth and backsight point
                        String pointId = lines.get(i + 1).split(":")[0].trim();

                        String observationLine = lines.get(i + 1).split(":")[1].trim();
                        String[] elements = observationLine.split("\\s+");

                        String Hz = elements[0];
                        String V = elements[1];
                        String SD = elements[2];
                        String hr = elements[3];
                        String reflectorConstant = elements[4];

                        observation = new RyObservation(pointId, Hz, V, SD, hr, reflectorConstant);
                        break;
                    case ORIENTATION_AND_HEIGHT_TRANSFER:
                    case RESECTION:
                    case RESECTION_HELMERT:
                        // get every observation point
                        for (int j = 1; j < lines.size(); j++) {
                            line = lines.get(i + j);

                            if (line.startsWith(Method.RESIDUALS_OF_POINT.identifier)) {
                                final String[] residuals = line.split(":");
                                pointId = residuals[0].split("\\s+")[3];

                                final String[] deltas = residuals[1].trim().split("\\t");

                                final String dHz = deltas[0].split("=", -1)[1].trim();
                                final String dHeight = deltas[1].split("=", -1)[1].trim();
                                final String dHD = deltas[2].split("=", -1)[1].trim();

                                final String use = residuals[2].trim();

                                residualsList.add(new RyResidual(pointId, dHz, dHeight, dHD, use));
                            } else if (line.startsWith(Method.RESULTS.identifier)) {
                                // continue after observation block
                                i = i + j;
                                break;
                            } else {
                                // skip empty lines
                                if (!line.trim().equals("")) {
                                    final String[] observations = line.split("\\s+");

                                    pointId = observations[0].trim();
                                    Hz = observations[1];
                                    V = observations[2];
                                    SD = observations[3];
                                    hr = observations[4];
                                    reflectorConstant = observations[5];

                                    observationsList.add(new RyObservation(pointId, Hz, V, SD, hr, reflectorConstant));
                                }
                            }
                        }

                        break;
                    case RESECTION_LOCAL:
                        // get the two orientation points
                        for (int j = 1; j < 3; j++) {
                            final String[] observations = lines.get(i + j).split("\\s+");

                            pointId = observations[0].trim();
                            Hz = observations[1];
                            V = observations[2];
                            SD = observations[3];
                            hr = observations[4];
                            reflectorConstant = observations[5];

                            observationsList.add(new RyObservation(pointId, Hz, V, SD, hr, reflectorConstant));
                        }

                        // continue after observation block
                        i = i + 2;

                        break;
                }
            }

            // Results
            // -------
            else if (line.startsWith(Method.STATION_ID.identifier)) {
                // same structure for known azimuth and resection
                final String stationId = line.split(":")[1].trim();

                // get easting, northing and height
                final String resultLine = lines.get(i + 1).trim();
                final String[] elements = resultLine.split("\\s+");

                final String easting = elements[1].trim();
                final String northing = elements[3].trim();
                final String height = elements[5].trim();

                switch (setupMethod) {
                    case KNOWN_AZIMUTH:
                    case KNOWN_BACKSIGHT_POINT:
                        station = new RyPoint(stationId, easting, northing, height);

                        // skip reading next line
                        i++;
                        break;
                    case ORIENTATION_AND_HEIGHT_TRANSFER:
                    case RESECTION:
                    case RESECTION_HELMERT:
                    case RESECTION_LOCAL:
                        final String instrumentHeight = elements[7].trim();
                        station = new RyPoint(stationId, easting, northing, height, instrumentHeight);

                        // skip reading next line
                        i++;
                        break;
                }
            } else if (line.startsWith(Method.ORIENTATION_CORRECTION.identifier) || line.startsWith(Method.ORIENTATION_DOT_CORRECTION.identifier)) {
                orientationCorrection = line.split(":")[1].trim();
            } else if (line.startsWith(Method.SCALE.identifier)) {
                scale = line.split(":")[1].trim();
            } else if (line.startsWith(Method.STANDARD_DEVIATION_EAST.identifier)) {
                standardDeviationEasting = line.split(":")[1].trim();
            } else if (line.startsWith(Method.STANDARD_DEVIATION_NORTH.identifier)) {
                standardDeviationNorthing = line.split(":")[1].trim();
            } else if (line.startsWith(Method.STANDARD_DEVIATION_HEIGHT.identifier)) {
                standardDeviationHeight = line.split(":")[1].trim();
            } else if (line.startsWith(Method.STANDARD_DEVIATION_ORI.identifier)) {
                standardDeviationOrientation = line.split(":")[1].trim();
            }
        }

        if (setupMethod != null) {
            success = true;
        }

        return success;
    }

    /**
     * Returns the observation as {@link RyObservation}.
     *
     * @return observation
     */
    public RyObservation getObservation() {
        return observation;
    }

    /**
     * Returns multiple observations as {@link ArrayList} of {@link RyObservation}.
     *
     * @return multiple observations
     */
    public ArrayList<RyObservation> getObservationsList() {
        return observationsList;
    }

    /**
     * Returns the orientation correction.
     *
     * @return orientation correction
     */
    public String getOrientationCorrection() {
        return orientationCorrection;
    }

    /**
     * Returns multiple residuals as {@link ArrayList} of {@link RyResidual}.
     *
     * @return multiple residuals
     */
    public ArrayList<RyResidual> getResidualsList() {
        return residualsList;
    }

    /**
     * Returns the calculated scale
     *
     * @return calculated scale
     */
    public String getScale() {
        return scale;
    }

    /**
     * Returns the standard deviation for the easting.
     *
     * @return standard deviation easting
     */
    public String getStandardDeviationEasting() {
        return standardDeviationEasting;
    }

    /**
     * Returns the standard deviation for the height.
     *
     * @return standard deviation height
     */
    public String getStandardDeviationHeight() {
        return standardDeviationHeight;
    }

    /**
     * Returns the standard deviation for the northing.
     *
     * @return standard deviation northing
     */
    public String getStandardDeviationNorthing() {
        return standardDeviationNorthing;
    }

    /**
     * Returns the standard deviation for the orientation.
     *
     * @return standard deviation orientation
     */
    public String getStandardDeviationOrientation() {
        return standardDeviationOrientation;
    }

    /**
     * Returns the station as {@link RyPoint}.
     *
     * @return station
     */
    public RyPoint getStation() {
        return station;
    }

    // use original order for enum
    private enum Method {
        // Setup Method
        SETUP_METHOD("Setup Method"),

        // Observations
        POINT_ID("Point ID"),
        RESIDUALS_OF_POINT("Residuals of Point"),

        // Results for 'Resection'
        RESULTS("Results"),
        STATION_ID("Station ID"),
        ORIENTATION_CORRECTION("Ori Corr."),
        ORIENTATION_DOT_CORRECTION("Ori.Corr."),
        SCALE("Scale"),
        STANDARD_DEVIATION_EAST("S. Dev. East"),
        STANDARD_DEVIATION_NORTH("S. Dev. North"),
        STANDARD_DEVIATION_HEIGHT("S. Dev. Height"),
        STANDARD_DEVIATION_ORI("S. Dev. Ori");

        // Results for 'Ori & Ht Transfer'
        /*
        STATION_ID("Station ID"),
        ORIENTATION_CORRECTION("Ori Corr."),
        STANDARD_DEVIATION_HEIGHT("S. Dev. Height"),
        STANDARD_DEVIATION_ORI("S. Dev. Ori");
        */

        private final String identifier;

        Method(String identifier) {
            this.identifier = identifier;
        }
    }

} // end of SetupStructure
