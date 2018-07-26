/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.preferences
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

import de.ryanthara.ja.rycon.ui.widgets.SettingsWidget;

/**
 * This enumeration is used for all the strings in the preference tabs of the {@link SettingsWidget}.
 * <p>
 * Therefore the order of the enum is made by hand and follows the tab order.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum Preferences implements ResourceKeys {
    // categories
    categoryFormats,
    categoryGeneral,
    categoryModules,
    categoryPaths,

    // configurations
    labelNo,
    labelOk,
    labelYes,
    toolTipDefault,
    toolTipUndo,

    // general tab
    generalTabTitle,
    generalTabToolTip,
    groupGeneralSettingsTitle,
    overwriteExistingFiles,
    fileCompletionConverter,
    fileCompletionSplitter,
    fileCompletionLevelling,
    equalPointsMinimumDistance,
    userString,

    // path tab
    pathTabTitle,
    pathTabToolTip,
    pathGroupBase,
    pathBaseLabel,

    pathGroupAdmin,
    pathAdminFolderLabel,
    pathAdminTemplateLabel,

    pathGroupProject,
    pathProjectFolderLabel,
    pathProjectTemplateLabel,

    pathGroupBigData,
    pathBigDataFolderLabel,
    pathBigDataTemplateLabel,

    pathBtnText,
    pathBtnToolTip,

    // file formats tab
    formatsTabTitle,
    formatsTabToolTip,
    groupFormatsTitle,
    addSpaceAtLineEnd,
    defaultZeissFormat,

    // converter tab
    addTraillingZeroes,
    converterTabTitle,
    converterTabToolTip,
    converterTabGroupLTOP,
    converterTabGroupZeiss,

    // modules tab
    modulesTabTitle,
    modulesTabToolTip,

    // clear up group
    modulesTabGroupTitleClearUp,
    clearUpIdentifierFreeStation,
    clearUpIdentifierKnownStation,
    clearUpIdentifierLTOP,
    clearUpIdentifierStakeOut,

    // converter group
    modulesTabGroupTitleConverter,
    convertZeroCoordinates,
    ltopUseZenithDistance,

} // end of Preferences
