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
import de.ryanthara.ja.rycon.data.DefaultKey;
import de.ryanthara.ja.rycon.data.PreferenceKey;
import de.ryanthara.ja.rycon.i18n.Preference;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Text;
import de.ryanthara.ja.rycon.ui.custom.DirectoryDialogsTyp;
import de.ryanthara.ja.rycon.ui.preferences.PreferencesDialog;
import de.ryanthara.ja.rycon.ui.preferences.pref.*;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDialect.*;
import static de.ryanthara.ja.rycon.i18n.ResourceBundle.PREFERENCE;
import static de.ryanthara.ja.rycon.i18n.ResourceBundle.TEXT;

/**
 * The {@code SettingsWidget} is the dialog to change all the preferences of RyCON.
 * <p>
 * With version 2 of RyCON the need for more pref of the modules is fulfilled with this dialog.
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
    public SettingsWidget(Shell parent) {
        this.parent = parent;

        createDialog();
    }

    private void createDialog() {
        PreferencesDialog dialog = new PreferencesDialog(
                parent,
                ResourceBundleUtils.getLangStringFromXml(TEXT, Text.preferencesDialog_Text),
                ResourceBundleUtils.getLangStringFromXml(TEXT, Text.preferencesDialog_Message));

        createTabGeneral(dialog);
        createTabFiles(dialog);
        createTabPaths(dialog);
        createTabFormats(dialog);
        createTabModules(dialog);

        Main.setSettingsWidgetIsOpen(true);

        dialog.open();
    }

    private void createTabFormats(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCE, Preference.category_Formats));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_FormatsTitle));

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.addTraillingZeroes),
                Boolean.valueOf(DefaultKey.ADD_TRAILING_ZEROES.getValue())) {

            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKey.ADD_TRAILING_ZEROES));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.ADD_TRAILING_ZEROES, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.addSpaceAtLineEnd),
                Boolean.valueOf(DefaultKey.GSI_SETTING_LINE_ENDING_WITH_BLANK.getValue())) {

            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKey.GSI_SETTING_LINE_ENDING_WITH_BLANK));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.GSI_SETTING_LINE_ENDING_WITH_BLANK, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceSelection(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.defaultZeissFormat),
                new String[]{R4.name(), R5.name(), REC500.name(), M5.name()},
                DefaultKey.CONVERTER_SETTING_ZEISS_DIALECT.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.CONVERTER_SETTING_ZEISS_DIALECT);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.CONVERTER_SETTING_ZEISS_DIALECT, obj.toString());
            }
        });

        logger.info("Tab 'formats' created.");
    }

    private void createTabGeneral(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCE, Preference.category_General));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_GeneralSettingsTitle));

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.userString),
                DefaultKey.PARAM_USER_STRING.getValue()) {

            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_USER_STRING);
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_USER_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceDouble(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.equalPointsMinimumDistance),
                0.005, 0.10,
                Double.valueOf(DefaultKey.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE.getValue())) {

            public Double getValue() {
                return Double.valueOf(Main.pref.getUserPreference(PreferenceKey.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE, obj.toString());
            }
        });

        logger.info("Tab 'general' created.");
    }

    private void createTabModules(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCE, Preference.category_Modules));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_ClearUpTitle));

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.clearUpIdentifierFreeStation),
                DefaultKey.PARAM_FREE_STATION_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_FREE_STATION_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_FREE_STATION_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.clearUpIdentifierKnownStation),
                DefaultKey.PARAM_KNOWN_STATION_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_KNOWN_STATION_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_KNOWN_STATION_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.clearUpIdentifierStakeOut),
                DefaultKey.PARAM_CONTROL_POINT_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_CONTROL_POINT_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_CONTROL_POINT_STRING, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_ConverterTitle));

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.convertZeroCoordinates),
                Boolean.valueOf(DefaultKey.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE.getValue())) {

            @Override
            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKey.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.ltopUseZenithDistance),
                Boolean.valueOf(DefaultKey.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE.getValue())) {

            @Override
            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKey.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE, obj.toString());
            }
        });

        logger.info("Tab 'modules' created.");
    }

    private void createTabFiles(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCE, Preference.category_Files));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_FileHandling));

        dialog.addPreference(new PreferenceBoolean(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.overwriteExistingFiles),
                Boolean.valueOf(DefaultKey.OVERWRITE_EXISTING.getValue())) {

            public Boolean getValue() {
                return Boolean.valueOf(Main.pref.getUserPreference(PreferenceKey.OVERWRITE_EXISTING));
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.OVERWRITE_EXISTING, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_FileExtensions));

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.fileCompletion_Converter),
                DefaultKey.PARAM_EDIT_STRING.getValue()) {

            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_EDIT_STRING);
            }

            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_EDIT_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.fileCompletion_Splitter),
                DefaultKey.PARAM_CODE_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_CODE_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_CODE_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.fileCompletion_Levelling),
                DefaultKey.PARAM_LEVEL_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_LEVEL_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_LEVEL_STRING, obj.toString());
            }
        });

        dialog.addPreference(new PreferenceString(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.fileCompletion_Ltop),
                DefaultKey.PARAM_LTOP_STRING.getValue()) {

            @Override
            public String getValue() {
                return Main.pref.getUserPreference(PreferenceKey.PARAM_LTOP_STRING);
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.PARAM_LTOP_STRING, obj.toString());
            }
        });

        logger.info("Tab 'files' created.");
    }

    private void createTabPaths(PreferencesDialog dialog) {
        dialog.addCategory(ResourceBundleUtils.getLangString(PREFERENCE, Preference.category_Paths));

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_PathBase));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.path_BaseLabel),
                Paths.get(DefaultKey.DIR_BASE.getValue()),
                DirectoryDialogsTyp.DIR_BASE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_BASE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.DIR_BASE, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_PathAdmin));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.path_AdminFolderLabel),
                Paths.get(DefaultKey.DIR_ADMIN.getValue()),
                DirectoryDialogsTyp.DIR_ADMIN) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_ADMIN));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.DIR_ADMIN, obj.toString());
            }
        });

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.path_AdminTemplateLabel),
                Paths.get(DefaultKey.DIR_ADMIN_TEMPLATE.getValue()),
                DirectoryDialogsTyp.DIR_ADMIN_TEMPLATE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_ADMIN_TEMPLATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.DIR_ADMIN_TEMPLATE, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_PathProject));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.path_ProjectFolderLabel),
                Paths.get(DefaultKey.DIR_PROJECT.getValue()),
                DirectoryDialogsTyp.DIR_PROJECT) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.DIR_PROJECT, obj.toString());
            }
        });

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.path_ProjectTemplateLabel),
                Paths.get(DefaultKey.DIR_PROJECT_TEMPLATE.getValue()),
                DirectoryDialogsTyp.DIR_PROJECT_TEMPLATE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT_TEMPLATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.DIR_PROJECT_TEMPLATE, obj.toString());
            }
        });

        dialog.addGroup(ResourceBundleUtils.getLangString(PREFERENCE, Preference.group_PathBigData));

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.path_BigDataFolderLabel),
                Paths.get(DefaultKey.DIR_BIG_DATA.getValue()),
                DirectoryDialogsTyp.DIR_BIG_DATA) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_BIG_DATA));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.DIR_BIG_DATA, obj.toString());
            }
        });

        dialog.addPreference(new PreferencePath(
                ResourceBundleUtils.getLangString(PREFERENCE, Preference.path_BigDataTemplateLabel),
                Paths.get(DefaultKey.DIR_BIG_DATA_TEMPLATE.getValue()),
                DirectoryDialogsTyp.DIR_BIG_DATA_TEMPLATE) {

            @Override
            public Path getValue() {
                return Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_BIG_DATA_TEMPLATE));
            }

            @Override
            public void setValue(Object obj) {
                Main.pref.setUserPreference(PreferenceKey.DIR_BIG_DATA_TEMPLATE, obj.toString());
            }
        });

        logger.info("Tab 'paths' created.");
    }

}
