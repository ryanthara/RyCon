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
package de.ryanthara.ja.rycon.converter.caplan;

/**
 * This class provides basic and helper functions that are used for converting different
 * file formats into text based Caplan K formatted measurement files.
 * <p>
 * The CAPLAN K file format is a line based and column orientated file format developed
 * by Cremer Programmentwicklung GmbH to store coordinates in different formats.
 * <p>
 * Example file:
 *
 * ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8
 * !-------------------------------------------------------------------------------
 * ! The following data was created by RyCON Build xxx on 2016-09-06.
 * !-------------------------------------------------------------------------------
 *              GB1 7  2612259.5681  1256789.1990    256.90815 |10
 *              GB2 7  2612259.5681  1256789.1990    256.90815 |10
 *             1003 7  2612259.5681  1256789.1990    256.90815 |10|Att1|Att2
 *             1062 7  2612259.5681  1256789.1990    256.90815 |10
 *         TF 1067G 4  2612259.5681  1256789.1990    256.90815 |10
 *         NG 2156U 3  2612259.5681  1256789.1990      0.00000 |10
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class BaseToolsK {
}
