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
package de.ryanthara.ja.rycon.ui.widgets;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.DefaultKeys;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.Preferences;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.ui.custom.DirectoryDialogsTypes;
import de.ryanthara.ja.rycon.ui.preferences.PreferencesDialog;
import de.ryanthara.ja.rycon.ui.preferences.pref.*;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDialect.*;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.PREFERENCES;

/**
 * The {@code SettingsWidget} is the dialog to change all the preferences of <tt>RyCON</tt>.
 * <p>
 * With version 2 of <tt>RyCON</tt> the need for more pref of the modules is fulfilled with this dialog.
 * It uses a tabbed structure for different modules and try to provide a clear view on the changeable pref.
 * <p>
 * The idea to this are inspired by preference dialogs of different applications, like Eclipse, IntelliJ IDEA,
 * AutoCAD and some github stuff like swtpreferences from prasser.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class SettingsWidget {


    private static final Logger logger = LoggerFactory.getLogger(SettingsWidget.class.getName());
    private final Shell parent;

    /**
     * Creates a new instance.
     *
     * @param parent parent shell
     */
    public SettingsWidget(final Shell parent) {
        this.parent = parent;

        createDialog();
    }

    private void createDialog() {
        PreferencesDialog dialog = new PreferencesDialog(
                parent,
                ResourceBundleUtils.getLangString(LABELS, Labels.preferencesDialogText),
                ResourceBundleUtils.getLangString(LABELS, Labels.preferencesDialogMessage));

        createTabGeneral(dialog);
        createTabPaths(dialog);
        createTabFormats(dialog);
        createTabModules(dialog);

        Main.setSettingsWidgetIsOpen(true);

        dialog.open();
    }

    private void createTabFormats(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.categoryFormats));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.groupFormatsTitle));

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.addTraillingZeroes),
                Boolean.valueOf(DefaultKeys.ADD_TRAILING_ZEROES.getValue())) {

            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKeys.ADD_TRAILING_ZEROES));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.ADD_TRAILING_ZEROES, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.addSpaceAtLineEnd),
                Boolean.valueOf(DefaultKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK.getValue())) {

            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceSelection(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.defaultZeissFormat),
                new String[]{R4.name(), R5.name(), REC500.name(), M5.name()},
                DefaultKeys.CONVERTER_SETTING_ZEISS_DIALECT.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT, obj.toString());
            }
        });

        logger.info("Tab 'formats' created.");
    }

    private void createTabGeneral(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.categoryGeneral));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.groupGeneralSettingsTitle));

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.userString),
                DefaultKeys.PARAM_USER_STRING.getValue()) {

            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_USER_STRING);
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_USER_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.overwriteExistingFiles),
                Boolean.valueOf(DefaultKeys.OVERWRITE_EXISTING.getValue())) {

            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKeys.OVERWRITE_EXISTING));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.OVERWRITE_EXISTING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.fileCompletionConverter),
                DefaultKeys.PARAM_EDIT_STRING.getValue()) {

            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_EDIT_STRING);
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_EDIT_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.fileCompletionSplitter),
                DefaultKeys.PARAM_CODE_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_CODE_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_CODE_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.fileCompletionLevelling),
                DefaultKeys.PARAM_LEVEL_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_LEVEL_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_LEVEL_STRING, obj.toString());
            }
        });


        dialog.addPreference(new PreferenceDouble(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.equalPointsMinimumDistance),
                0.005, 0.10,
                Double.valueOf(DefaultKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE.getValue())) {

            public Double getValue() {
                return Double.valueOf(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE, obj.toString());
            }
        });

        logger.info("Tab 'general' created.");
    }

    private void createTabModules(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.categoryModules));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.modulesTabGroupTitleClearUp));

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.clearUpIdentifierFreeStation),
                DefaultKeys.PARAM_FREE_STATION_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_FREE_STATION_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_FREE_STATION_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.clearUpIdentifierKnownStation),
                DefaultKeys.PARAM_KNOWN_STATION_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_KNOWN_STATION_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_KNOWN_STATION_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.clearUpIdentifierStakeOut),
                DefaultKeys.PARAM_CONTROL_POINT_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.clearUpIdentifierLTOP),
                DefaultKeys.PARAM_LTOP_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKeys.PARAM_LTOP_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.PARAM_LTOP_STRING, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.modulesTabGroupTitleConverter));

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.convertZeroCoordinates),
                Boolean.valueOf(DefaultKeys.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE.getValue())) {

            @Override
            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.ltopUseZenithDistance),
                Boolean.valueOf(DefaultKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE.getValue())) {

            @Override
            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE, obj.toString());
            }
        });

        logger.info("Tab 'modules' created.");
    }

    private void createTabPaths(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.categoryPaths));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathGroupBase));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathBaseLabel),
                Paths.get(DefaultKeys.DIR_BASE.getValue()),
                DirectoryDialogsTypes.DIR_BASE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_BASE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_BASE, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathGroupAdmin));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathAdminFolderLabel),
                Paths.get(DefaultKeys.DIR_ADMIN.getValue()),
                DirectoryDialogsTypes.DIR_ADMIN) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_ADMIN, obj.toString());
            }
        });

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathAdminTemplateLabel),
                Paths.get(DefaultKeys.DIR_ADMIN_TEMPLATE.getValue()),
                DirectoryDialogsTypes.DIR_ADMIN_TEMPLATE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN_TEMPLATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_ADMIN_TEMPLATE, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathGroupProject));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathProjectFolderLabel),
                Paths.get(DefaultKeys.DIR_PROJECT.getValue()),
                DirectoryDialogsTypes.DIR_PROJECT) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_PROJECT, obj.toString());
            }
        });

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathProjectTemplateLabel),
                Paths.get(DefaultKeys.DIR_PROJECT_TEMPLATE.getValue()),
                DirectoryDialogsTypes.DIR_PROJECT_TEMPLATE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT_TEMPLATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_PROJECT_TEMPLATE, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathGroupBigData));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathBigDataFolderLabel),
                Paths.get(DefaultKeys.DIR_BIG_DATA.getValue()),
                DirectoryDialogsTypes.DIR_BIG_DATA) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_BIG_DATA, obj.toString());
            }
        });

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathBigDataTemplateLabel),
                Paths.get(DefaultKeys.DIR_BIG_DATA_TEMPLATE.getValue()),
                DirectoryDialogsTypes.DIR_BIG_DATA_TEMPLATE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA_TEMPLATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_BIG_DATA_TEMPLATE, obj.toString());
            }
        });

        logger.info("Tab 'paths' created.");
    }

} // end of SettingsWidget
