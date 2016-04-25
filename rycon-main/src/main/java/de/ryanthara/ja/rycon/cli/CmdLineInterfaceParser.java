package de.ryanthara.ja.rycon.cli;

/**
 * This class is used to parse command line interface values for RyCON.
 *
 * <p>
 * RyCON options can be set with a few command line arguments. At the moment there
 * are the following arguments supported.
 * <code>
 *     --help              displays a help message on the terminal
 *     --locale=[a-zA-Z]   sets the locale to the given value in ISO 639 alpha-2 or alpha-3 language code
 * </code>
 *
 * <p>
 * The language of RyCON is set by ISO 639 alpha-2 or alpha-3 language code values. For example 'en' for english or
 * 'de' for german.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 6
 */
public class CmdLineInterfaceParser {

    private boolean containsIllegalArgument = false;
    private String alphaLanguageCode;

    /**
     * Returns the parsed alpha-2 or alpha-3 language code as string.
     *
     * @return the alpha-2 or -3 language code
     */
    public String getParsedLanguageCode() {
        return this.alphaLanguageCode;
    }

    /**
     * Parses the command line interface arguments of RyCON and tries to set the locale to a given value.
     *
     * <p>
     * The arguments are only parsed and not controlled for being valid.
     *
     * @param args the arguments to be parsed
     * @throws CmdLineInterfaceException if something goes wrong with the parsing
     */
    public void parseArguments(final String... args) throws CmdLineInterfaceException {
        if (args != null && args.length > 0) {
            for (String s : args) {
                if (s.toLowerCase().equals("--help")) {
                    System.out.println();
                    System.out.println("usage: java -jar RyCON_[version].jar");
                    System.out.println(" --help                     shows this help");
                    System.out.println(" --locale=<language code>   alpha-2 or alpha-3 language code (e.g. en or de");
                    System.out.println();
                } else if (s.toLowerCase().contains("--locale=")) {
                    alphaLanguageCode = s.toLowerCase().substring(9, s.length());
                } else {
                    containsIllegalArgument = true;
                    System.err.println("RyCON: illegal option " + s);
                }
            }

            if (containsIllegalArgument) {
                String usage = "usage: java -jar RyCON_[].jar --help --locale=[alpha-2 or alpha-3 language code]";
                System.err.println(usage);
            }
        }
    }

} // end of CmdLineInterfaceParser
