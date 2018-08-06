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
package de.ryanthara.ja.rycon.cli;

import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instances of this class provides command line interface functions to {@code RyCON}.
 * <p>
 * {@code RyCON's} options can be set with a few command line arguments. The following arguments
 * will be supported in the current version of {@code RyCON}.
 * <code>
 * --help                      displays a help message on the terminal
 * --debug=[level]             enable debug mode - level could be 'SEVERE WARNING INFO CONFIG FINE FINER FINEST'
 * --locale=[a-zA-Z]           sets the locale to the given value in ISO 639 alpha-2 or alpha-3 language code
 * --file=[input file]         sets the value of input file into the source text field
 * --sourceBtnNumber=[number]  selects the source button by a given number
 * --targetBtnNumber=[number]  selects the target button by a given number
 * </code>
 * <p>
 * The language of {@code RyCON} is set by ISO 639 alpha-2 or alpha-3 language code values.
 * For example use 'en' for english or 'de' for german language.
 * <p>
 * Due to some reasons in the development cycle of {@code RyCON}, the function to parse one file name into
 * the source text field, and the possibility to select radio buttons was implemented. This functionality
 * is available for the {@link de.ryanthara.ja.rycon.ui.widgets.ConverterWidget}.
 *
 * @author sebastian
 * @version 5
 * @since 6
 */
public class CmdLineInterfaceParser {

    private final static Logger logger = Logger.getLogger(CmdLineInterfaceParser.class.getName());
    private int sourceBtnNumber, targetBtnNumber;
    private String alphaLanguageCode, inputFile;
    private Level loggingLevel;

    /**
     * Constructs a new instance of this class.
     */
    public CmdLineInterfaceParser() {
        sourceBtnNumber = -1;
        targetBtnNumber = -1;
        alphaLanguageCode = null;
        inputFile = null;
    }

    /**
     * Returns the parsed input file as String.
     *
     * @return the input file
     */
    public String getInputFile() {
        return inputFile;
    }

    /**
     * Returns the parsed logging level,.
     *
     * @return the logging level
     */
    public Level getLoggingLevel() {
        return loggingLevel;
    }

    /**
     * Returns the parsed alpha-2 or alpha-3 language code as string.
     *
     * @return the alpha-2 or -3 language code
     */
    public String getParsedLanguageCode() {
        return alphaLanguageCode;
    }

    /**
     * Returns the parsed number of the source button that have to be selected.
     *
     * @return select source button number
     */
    public int getSourceBtnNumber() {
        return sourceBtnNumber;
    }

    /**
     * Returns the parsed number of the target button that have to be selected.
     *
     * @return select target button number
     */
    public int getTargetBtnNumber() {
        return targetBtnNumber;
    }

    /**
     * Parses the command line interface arguments of RyCON.
     * <p>
     * The arguments are case sensitive, are only parsed and not checked for being valid or logical.
     *
     * @param args the arguments to be parsed
     */
    public void parseArguments(final String... args) throws CmdLineInterfaceException {
        if (args != null && args.length > 0) {
            for (String s : args) {
                if (s.toLowerCase().equals("--help")) {
                    printHelp();
                } else if (s.toLowerCase().contains("--debug=")) {
                    loggingLevel = parsedLoggingLevel(s.toUpperCase().substring(8, s.length()));
                } else if (s.toLowerCase().contains("--locale=")) {
                    alphaLanguageCode = s.toLowerCase().substring(9, s.length());
                } else if (s.toLowerCase().contains("--file=")) {
                    inputFile = s.substring(7);
                } else if (s.contains("--sourceBtnNumber=")) {
                    sourceBtnNumber = Integer.parseInt(s.substring(18));
                } else if (s.contains("--targetBtnNumber=")) {
                    targetBtnNumber = Integer.parseInt(s.substring(18));
                } else {
                    // TODO correct log level and message text
                    logger.log(Level.FINEST, "incorrect or illegal command line interface input");
                    logger.log(Level.FINEST, s);

                    printUsageAdvice();

                    // TODO is this really necessary?
                    throw new CmdLineInterfaceException();
                }
            }
        }

        logger.log(Level.INFO, "command line arguments parsed successful");
    }

    private Level parsedLoggingLevel(String s) {
        Level level = Level.SEVERE;

        try {
            level = Level.parse(s);
            logger.info("Used logging level: " + level.getName());
        } catch (InvalidParameterException e) {
            logger.log(Level.SEVERE, "Can not parse logging level '" + s + "' from the command line interface", e);
        }

        return level;
    }

    private void printHelp() {
        System.out.println("\n");
        System.out.println("usage: java -jar RyCON_[version].jar [one or more arguments]");
        System.out.println(" --help                     shows this help");
        System.out.println(" --debug=[level]            enable debug mode - level could be 'SEVERE WARNING INFO CONFIG FINE FINER FINEST'");
        System.out.println(" --locale=[language code]   alpha-2 or alpha-3 language code (e.g. en or de");
        System.out.println(" --file=[input files]       sets the value of input files into the source text field");
        System.out.println(" --sourceBtnNumber=[number] selects the source button by a given number");
        System.out.println(" --targetBtnNumber=[number] selects the target button by a given number");
        System.out.println("\n");
    }

    private void printUsageAdvice() {
        String usage = "usage: java -jar RyCON_[].jar --debug[level] --help --locale=[alpha-2 or alpha-3 language code] ";
        usage = usage.concat("--file=[input files] --sourceBtnNumber=[number] --targetBtnNumber=[number] ");

        System.out.println("\n");
        System.out.println(usage);
        System.out.println("\n");
    }

} // end of CmdLineInterfaceParser
