/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.widgets.generate
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


/**
 * The {@code WarnAndErrorType} enumeration holds warning and error messages
 * for the {@link de.ryanthara.ja.rycon.ui.widgets.GeneratorWidget}.
 * <p>
 * It uses the inherited {@link CopyWarnAndErrorMessage} for administration,
 * big data and project warning and error messages.
 *
 * @author sebastian
 * @version 1
 * @since 2
 */
public enum WarnAndErrorType {

    ADMIN(new AdminCopyWarnAndErrorMessage()),
    BIG_DATA(new BigDataCopyWarnAndErrorMessage()),
    PROJECT(new ProjectCopyWarnAndErrorMessages());

    private CopyWarnAndErrorMessage errorMessage;

    WarnAndErrorType(CopyWarnAndErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the specified {@link CopyWarnAndErrorMessage}.
     *
     * @return CopyWarnAndErrorMessage reference
     */
    public CopyWarnAndErrorMessage getErrorMessage() {
        return errorMessage;
    }

} // end of WarnAndErrorType
