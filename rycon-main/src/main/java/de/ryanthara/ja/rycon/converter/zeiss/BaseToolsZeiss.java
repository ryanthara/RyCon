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
package de.ryanthara.ja.rycon.converter.zeiss;

/**
 * This class implements several basic operations for conversion to or from Zeiss REC files.
 * <p>
 * The Zeiss REC file format and it's dialects (R4, R5, REC500 and M5) are a line based and column orientated
 * file format developed by Zeiss to store coordinates and measurement information in text based files.
 * <p>
 * Example file in M5 format:
 *
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9----+---10----+---11----+----12
 * !-------------------------------------------------------------------------------
 * ! Folgende Daten wurden von RyCON Build xxx am 03.02.2016 erzeugt.
 * !-------------------------------------------------------------------------------
 * For M5|Adr     1|TI  Berechn. PunK              |                      |                      |                      |
 * For M5|Adr     2|PI1 2154H                      |th 1.600          m   |                      |                      |
 * For M5|Adr     3|PI1 2154H                      |Hz 34.9078        gon |V1 106.3481       gon |D  89.893         m   |
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class BaseToolsZeiss {

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String R4 = "R4";

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String R5 = "R5";

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String REC500 = "REC500";

    /**
     * Member for indicating R4 dialect for output files.
     */
    public static final String M5 = "M5";


} // end of BaseToolsZeiss
