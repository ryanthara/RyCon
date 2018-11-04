/**
 * Classes for converting different source file formats into Zeiss REC files.
 *
 * <p>
 * The abstract base class is {@link de.ryanthara.ja.rycon.core.converter.Converter}
 * which defines a general behaviour for all converter classes in this sub package.
 *
 * <p>
 * The {@link de.ryanthara.ja.rycon.core.converter.zeiss.BaseToolsZeiss} contains
 * basic functions which are used in all classes of this package.
 *
 * <p>
 * The classes let you convert to Leica Geosystems GSI format from
 * <ul>
 * <li>Cadwork node.dat format,
 * <li>Caplan K format,
 * <li>comma separated values (csv) format,
 * <li>Geodata Server Basel Stadt (Switzerland) CSV format,
 * <li>Leica Geosystems GSI8 and GSI16 format,
 * <li>ASCII text format,
 * <li>Geodata Server Basel Landschaft (Switzerland) ASCII text format,
 * <li>Zeiss REC format.
 * </ul>
 */
package de.ryanthara.ja.rycon.core.converter.zeiss;