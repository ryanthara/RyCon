/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.widget.generator
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
package de.ryanthara.ja.rycon.ui.widgets.generate;

import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Warnings;
import de.ryanthara.ja.rycon.ui.widgets.GeneratorWidget;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.ERRORS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.WARNINGS;

/**
 * Instances of this class holds the warning and error messages of the {@link GeneratorWidget}.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public final class ProjectCopyWarnAndErrorMessages implements CopyWarnAndErrorMessage {

    /**
     * Prepares the error message string for the given project number string.
     *
     * @param number project number string
     *
     * @return error message string
     */
    @Override
    public String getErrorMessage(String number) {
        return String.format(ResourceBundleUtils.getLangString(ERRORS, Errors.projectDirCopy), number);
    }

    /**
     * Prepares the warn message string for the given project number string.
     *
     * @param number project number string
     *
     * @return warn message string
     */
    @Override
    public String getWarnMessage(String number) {
        return String.format(ResourceBundleUtils.getLangString(WARNINGS, Warnings.projectDirExists), number);
    }

} // end of ProjectCopyWarnAndErrorMessages
