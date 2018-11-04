/**
 * Classes for enabling the command line interface functionality for RyCON.
 *
 * <p>
 * The core class is the {@link de.ryanthara.ja.rycon.cli.CommandLineInterfaceParser} that
 * lets you
 * <ul>
 * <li>parse arguments from the command line interface,
 * <li>check the parsed elements,
 * <li>and set the values for usage in different parts of RyCON.
 * </ul>
 *
 * <p>
 * The classes let you parses command line arguments.
 *
 * <p>
 * Usage:
 * <pre>
 * CommandLineInterfaceParser parser = new CommandLineInterfaceParser();
 *
 * try {
 *   parser.parseArguments(args);
 * } catch (CommandLineInterfaceException e) {
 *   // handle exception
 * }
 * </pre>
 */
package de.ryanthara.ja.rycon.cli;