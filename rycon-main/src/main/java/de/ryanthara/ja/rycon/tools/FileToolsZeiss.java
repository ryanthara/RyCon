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
package de.ryanthara.ja.rycon.tools;

/**
 * This class implements several basic operations for conversion to or from Zeiss M5 files.
 * <p>
 * The Zeiss M5 file format and it's dialects are a line based and column orientated file format
 * developed by Zeiss to store coordinates and measurement information in files.
 * <p>
 * Example file:
 *
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9----+---10----+---11----+----12
 * !-------------------------------------------------------------------------------
 * ! Folgende Daten wurden von RyCON Build xxx am 03.02.2016 erzeugt.
 * !-------------------------------------------------------------------------------
 * For M5|Adr     1|TI  Berechn. PunK              |                      |                      |                      |
 * For M5|Adr     2|PI1 2154H                      |th 1.600          m   |                      |                      |
 * For M5|Adr     3|PI1 2154H                      |Hz 34.9078        gon |V1 106.3481       gon |D  89.893         m   |
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 7
 */
public class FileToolsZeiss {

    /**
     *
     */
    public FileToolsZeiss() {}




}
