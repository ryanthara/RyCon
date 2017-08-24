/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.i18n
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
package de.ryanthara.ja.rycon.i18n;

/**
 * The <tt>ResourceBundles</tt> enumeration holds the paths to all resource bundles
 * for <tt>RyCON</tt>.
 * <p>
 * This enumeration is used for encapsulating the data. The access to the different
 * is done in the class {@link ResourceBundleUtils}.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum ResourceBundles {

    BUTTONS("de/ryanthara/ja/rycon/gui/Buttons"),
    COLUMNS("de/ryanthara/ja/rycon/gui/Columns"),
    CHECKBOXES("de/ryanthara/ja/rycon/gui/CheckBoxes"),
    ERRORS("de/ryanthara/ja/rycon/gui/Errors"),
    FILECHOOSERS("de/ryanthara/ja/rycon/gui/FileChoosers"),
    LABELS("de/ryanthara/ja/rycon/gui/Labels"),
    MESSAGES("de/ryanthara/ja/rycon/gui/Messages"),
    PREFERENCES("de/ryanthara/ja/rycon/gui/Preferences"),
    WARNINGS("de/ryanthara/ja/rycon/gui/Warnings"),
    WORDINDICES("de/ryanthara/ja/rycon/gui/LeicaGSIWordIndices");

    private String bundleName;

    ResourceBundles(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleName() {
        return bundleName;
    }

    @Override
    public String toString() {
        return bundleName;
    }

} // end of ResourceBundles
