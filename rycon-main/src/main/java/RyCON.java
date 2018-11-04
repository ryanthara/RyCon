/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon
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

import de.ryanthara.ja.rycon.ui.MainApplication;

/**
 * A special approach to show the name RyCON to mac users.
 *
 * <p>
 * The not existing Info.plist file prevent java applications
 * to show their names in the dock and menu bar of macOS X or Mac OS X.
 *
 * <p>
 * For more information see the <a href="https://developer.apple.com/library/mac/documentation/Java/Conceptual/Java14Development/00-Intro/JavaDevelopment.html#//apple_ref/doc/uid/TP40001911-SW1">Java Development Guide for Mac</a> on the web.
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
public class RyCON extends MainApplication {
}
//public class RyCON extends RyCONLauncher {}