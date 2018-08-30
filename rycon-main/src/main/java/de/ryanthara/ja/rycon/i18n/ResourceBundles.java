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
 * The {@code ResourceBundles} enumeration holds the paths to all resource bundles
 * for <tt>RyCON</tt>.
 * <p>
 * This enumeration is used for encapsulating the data. The access to the different
 * resource bundles is done in the class {@link ResourceBundleUtils}.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum ResourceBundles {

    ADVICE("de/ryanthara/ja/rycon/ui/Advice"),
    BUTTONS("de/ryanthara/ja/rycon/ui/Buttons"),
    COLUMN_NAMES("de/ryanthara/ja/rycon/core/converter/ColumnNames"),
    CHECKBOXES("de/ryanthara/ja/rycon/ui/CheckBoxes"),
    DISTINCT_TYPES("de/ryanthara/ja/rycon/core/transformation/DistinctTypes"),
    ERRORS("de/ryanthara/ja/rycon/ui/Errors"),
    FILECHOOSERS("de/ryanthara/ja/rycon/ui/FileChoosers"),
    LABELS("de/ryanthara/ja/rycon/ui/Labels"),
    MESSAGES("de/ryanthara/ja/rycon/ui/Messages"),
    OPTIONS("de/ryanthara/ja/rycon/ui/Options"),
    PREFERENCES("de/ryanthara/ja/rycon/ui/Preferences"),
    TOOL_TIPS("de/ryanthara/ja/rycon/ui/toolTips"),
    WARNINGS("de/ryanthara/ja/rycon/ui/Warnings"),
    WORDINDICES("de/ryanthara/ja/rycon/core/converter/LeicaGSIWordIndices");

    private final String bundleName;

    ResourceBundles(String bundleName) {
        this.bundleName = bundleName;
    }

    /**
     * Returns the bundle name string.
     *
     * @return bundle name string
     */
    public String getBundleName() {
        return bundleName;
    }

    /**
     * Returns the bundle name and the bundle string.
     *
     * @return concat bundle name string
     */
    @Override
    public String toString() {
        return super.toString() + " " + bundleName;
    }

} // end of ResourceBundles
