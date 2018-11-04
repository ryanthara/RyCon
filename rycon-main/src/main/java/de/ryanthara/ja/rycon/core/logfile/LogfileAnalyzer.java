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
package de.ryanthara.ja.rycon.core.logfile;

import de.ryanthara.ja.rycon.core.logfile.leica.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code LogfileAnalyzer} implements all the general functions to analyze
 * a logfile in different formats.
 * <p>
 * The first version of this tool supports Leica Geosystems logfile.txt files.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
// TODO clear up and make it working
public class LogfileAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(LogfileAnalyzer.class.getName());

    private final List<String> logfile;

    /**
     * Constructs a new instance of this class with the log file to be analyzed as parameter.
     *
     * @param logfile log file to be analyzed
     */
    public LogfileAnalyzer(List<String> logfile) {
        this.logfile = logfile;
    }

    /**
     * Analyzes the Leica Geosystems logfile.txt file and returns the success as indicator.
     *
     * @return true if analysis has finished
     */
    public boolean analyzeLeicaGeosystemsLogfile() {
        boolean success = false;

        List<String> lines = new ArrayList<>();

        String currentIdentifier = null;

        Identifier current = null;

        // clean code
        String date = "";

        for (String line : logfile) {
            // identify a new block
            if (line.startsWith(Identifier.GENERAL.getIdentifier())) {
                // operations are executed when the next block occurs or finally at the end
                if (current != (null)) {
                    //System.out.println(line);

                    doAnalyze(lines, current);

                    lines.clear();
                    // lines.add(line);
                }

                // identify block
                if (line.startsWith(Identifier.COGO.getIdentifier())) {
                    current = Identifier.COGO;
                } else if (line.startsWith(Identifier.REFERENCE_LINE.getIdentifier())) {
                    current = Identifier.REFERENCE_LINE;
                } else if (line.startsWith(Identifier.REFERENCE_PLANE.getIdentifier())) {
                    current = Identifier.REFERENCE_PLANE;
                } else if (line.startsWith(Identifier.SETUP.getIdentifier())) {
                    current = Identifier.SETUP;
                } else if (line.startsWith(Identifier.STAKEOUT.getIdentifier())) {
                    current = Identifier.STAKEOUT;
                }
            }

            // skip empty lines
            if (line.trim().length() > 0) {
                lines.add(line);
            }
        }

        // force last analysis when logfile contains content
        if (current != null) {
            System.out.println("last: " + current.toString());
            doAnalyze(lines, current);
        }

        return success;
    }

    private void doAnalyze(List<String> lines, Identifier current) {
        switch (current) {
            case COGO:
                System.out.println("FOUND CURRENT: COGO");
                CogoStructure cogo = new CogoStructure(lines);
                cogo.analyze();

                /*
                System.out.println(": COGO :" + cogo.getInstrumentType() + "::");
                System.out.println(": COGO :" + cogo.getSerialNo() + "::");
                System.out.println(": COGO :" + cogo.getStoreToJob() + "::");
                System.out.println(": COGO :" + cogo.getApplicationStart() + "::");
                */
                break;
            case SETUP:
                System.out.println("FOUND CURRENT: SETUP");
                SetupStructure setup = new SetupStructure(lines);
                setup.analyze();
                /*
                System.out.println(": SETUP :" + setup.getInstrumentType() + "::");
                System.out.println(": SETUP :" + setup.getSerialNo() + "::");
                System.out.println(": SETUP :" + setup.getStoreToJob() + "::");
                System.out.println(": SETUP :" + setup.getApplicationStart() + "::");
                System.out.println(": SETUP :" + setup.getApplicationStartDate() + "::");
                System.out.println(": SETUP :" + setup.getApplicationStartTime() + "::");
                */
                break;
            case STAKEOUT:
                System.out.println("FOUND CURRENT: STAKEOUT");
                StakeOutStructure stakeOut = new StakeOutStructure(lines);
                stakeOut.analyze();
                /*
                System.out.println(": STAKE OUT :" + stakeOut.getInstrumentType() + "::");
                System.out.println(": STAKE OUT :" + stakeOut.getSerialNo() + "::");
                System.out.println(": STAKE OUT :" + stakeOut.getStoreToJob() + "::");
                System.out.println(": STAKE OUT :" + stakeOut.getApplicationStart() + "::");
                */
                break;
            case REFERENCE_LINE:
                System.out.println("FOUND CURRENT: REFERENCE LINE");
                ReferenceLineStructure referenceLine = new ReferenceLineStructure(lines);
                referenceLine.analyze();
                /*
                System.out.println(": REFERENCE LINE :" + referenceLine.getInstrumentType() + "::");
                System.out.println(": REFERENCE LINE :" + referenceLine.getSerialNo() + "::");
                System.out.println(": REFERENCE LINE :" + referenceLine.getStoreToJob() + "::");
                System.out.println(": REFERENCE LINE :" + referenceLine.getApplicationStart() + "::");
                */
                break;
            case REFERENCE_PLANE:
                System.out.println("FOUND CURRENT: REFERENCE PLANE");
                ReferencePlaneStructure referencePlane = new ReferencePlaneStructure(lines);
                referencePlane.analyze();
            default:
                logger.warn("Found one more token '{}'.", current.getIdentifier());
        }
    }

}
