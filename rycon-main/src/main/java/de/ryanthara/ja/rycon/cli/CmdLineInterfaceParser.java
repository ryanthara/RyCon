package de.ryanthara.ja.rycon.cli;

/**
 * This class provides command line interface functions to RyCON.
 * <p>
 * RyCON's options can be set with a few command line arguments. The following arguments
 * will be supported in the current version of RyCON.
 * <code>
 * --help                displays a help message on the terminal
 * --locale=[a-zA-Z]     sets the locale to the given value in ISO 639 alpha-2 or alpha-3 language code
 * --file=[input file]   sets the value of input file into the source text field
 * </code>
 * <p>
 * The language of RyCON is set by ISO 639 alpha-2 or alpha-3 language code values. For example use 'en' for english
 * or 'de' for german language.
 * <p>
 * Due to some reasons in the development cycle of RyCON, the function to parse one file name into
 * the source text field was implemented.
 *
 * @author sebastian
 * @version 4
 * @since 6
 */
public class CmdLineInterfaceParser {

    private boolean containsIllegalArgument = false;
    private String alphaLanguageCode;
    private String inputFile;

    /**
     * Returns the parsed input file as String.
     *
     * @return the input file
     */
    public String getInputFile() {
        return inputFile;
    }

    /**
     * Returns the parsed alpha-2 or alpha-3 language code as string.
     *
     * @return the alpha-2 or -3 language code
     */
    public String getParsedLanguageCode() {
        return this.alphaLanguageCode;
    }

    /**
     * Parses the command line interface arguments of RyCON.
     * <p>
     * The arguments are only parsed and not checked for being valid or logical.
     *
     * @param args the arguments to be parsed
     *
     * @throws CmdLineInterfaceException if something goes wrong with the parsing
     */
    public void parseArguments(final String... args) throws CmdLineInterfaceException {
        if (args != null && args.length > 0) {
            for (String s : args) {
                if (s.toLowerCase().equals("--help")) {
                    System.out.println();
                    System.out.println("usage: java -jar RyCON_[version].jar");
                    System.out.println(" --help                     shows this help");
                    System.out.println(" --locale=[language code]   alpha-2 or alpha-3 language code (e.g. en or de");
                    System.out.println(" --file=[input files]       sets the value of input files into the source text field");
                    System.out.println(" --sourceBtn=[number]       sets the source button by a given number");
                    System.out.println(" --targetBtn=[number]       sets the target button by a given number");
                    System.out.println();
                } else if (s.toLowerCase().contains("--locale=")) {
                    alphaLanguageCode = s.toLowerCase().substring(9, s.length());
                } else if (s.toLowerCase().contains("--file=")) {
                    inputFile = s.substring(7, s.length());
                } else {
                    containsIllegalArgument = true;
                    System.err.println("RyCON: illegal option " + s);
                }
            }

            if (containsIllegalArgument) {
                String usage = "usage: java -jar RyCON_[].jar --help --locale=[alpha-2 or alpha-3 language code] --file=[input files] ";
                usage = usage.concat("--sourceBtn=[number] --targetBtn=[number] ");
                System.err.println(usage);
            }
        }
    }

} // end of CmdLineInterfaceParser
