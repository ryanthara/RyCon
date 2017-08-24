/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui
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
package de.ryanthara.ja.rycon.gui.widgets;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.check.TextCheck;
import de.ryanthara.ja.rycon.converter.zeiss.ZeissDialect;
import de.ryanthara.ja.rycon.data.DefaultKeys;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.gui.Sizes;
import de.ryanthara.ja.rycon.gui.custom.DirectoryDialogs;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.tools.RadioHelper;
import de.ryanthara.ja.rycon.tools.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import static de.ryanthara.ja.rycon.converter.zeiss.ZeissDialect.*;
import static de.ryanthara.ja.rycon.gui.custom.Status.OK;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;

/**
 * Instances of this class represents a complete settings widgets of RyCON and it's functionality.
 * <p>
 * The settings widgets of RyCON is behind button 9 or can be called by hitting the 'P' key in the main window .
 * It shows all of RyCON's settings, which can be changed easily and stored with Java's preference handling system.
 * <p>
 * An additional button adds the functionality to set default values by a simple click.
 *
 * @author sebastian
 * @version 8
 * @since 2
 */
public class SettingsWidget {

    private Button chkBoxUseSpaceAtLineEnd;
    private Button chkBoxEliminateZeroCoordinates;
    private Button chkBoxLTOPUseZenithDistance;
    private Button chkBoxOverwriteExistingFiles;
    private Group groupFormat;
    private Group groupGeneral;
    private Group groupConverterWidget;
    private Group groupTidyUpWidget;
    private Group groupZeissRECFormat;
    private Shell innerShell;
    private Text dirBaseTextField;
    private Text dirAdminTextField;
    private Text dirAdminTemplateTextField;
    private Text dirBigDataTextField;
    private Text dirBigDataTemplateTextField;
    private Text dirProjectTextField;
    private Text dirProjectTemplateTextField;
    private Text identifierCodeStringTextField;
    private Text identifierControlPointTextField;
    private Text identifierEditStringTextField;
    private Text identifierFreeStationTextField;
    private Text identifierKnownStationTextField;
    private Text identifierLTOPTextField;
    private Text pointIdenticalDistance;

    /**
     * Constructs a new instance of this class without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public SettingsWidget() {
        initUI();
    }

    private void actionBtnCancel() {
        Main.pref.setDefaultSettingsGenerated(false);
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        widgetDispose();
    }

    private void actionBtnDefaultSettings() {
        dirBaseTextField.setText(DefaultKeys.DIR_BASE.getValue());
        dirAdminTextField.setText(DefaultKeys.DIR_ADMIN.getValue());
        dirAdminTemplateTextField.setText(DefaultKeys.DIR_ADMIN_TEMPLATE.getValue());
        dirBigDataTextField.setText(DefaultKeys.DIR_BIG_DATA.getValue());
        dirBigDataTemplateTextField.setText(DefaultKeys.DIR_BIG_DATA_TEMPLATE.getValue());
        dirProjectTextField.setText(DefaultKeys.DIR_PROJECT.getValue());
        dirProjectTemplateTextField.setText(DefaultKeys.DIR_PROJECT_TEMPLATE.getValue());
        identifierCodeStringTextField.setText(DefaultKeys.PARAM_CODE_STRING.getValue());
        identifierControlPointTextField.setText(DefaultKeys.PARAM_CONTROL_POINT_STRING.getValue());
        identifierEditStringTextField.setText(DefaultKeys.PARAM_EDIT_STRING.getValue());
        identifierFreeStationTextField.setText(DefaultKeys.PARAM_FREE_STATION_STRING.getValue());
        identifierKnownStationTextField.setText(DefaultKeys.PARAM_KNOWN_STATION_STRING.getValue());
        identifierLTOPTextField.setText(DefaultKeys.PARAM_LTOP_STRING.getValue());
        pointIdenticalDistance.setText(DefaultKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE.getValue());

        RadioHelper.selectBtn(groupZeissRECFormat.getChildren(), 3); // M5 as default value

        Main.pref.setDefaultSettingsGenerated(true);
    }

    private void actionBtnOk() {
        int errorOccurred = Integer.MIN_VALUE;

        if (TextCheck.isEmpty(dirBaseTextField) || !TextCheck.isDirExists(dirBaseTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), ResourceBundleUtils.getLangString(ERRORS, Errors.baseDirNotFound));
        }

        if (TextCheck.isEmpty(dirProjectTextField) || !TextCheck.isDirExists(dirProjectTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), ResourceBundleUtils.getLangString(ERRORS, Errors.projectDirNotFound));
        }

        if (TextCheck.isEmpty(dirProjectTemplateTextField) || !TextCheck.isDirExists(dirProjectTemplateTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), ResourceBundleUtils.getLangString(ERRORS, Errors.projectDirDefaultNotFound));
        }

        if (TextCheck.isEmpty(dirAdminTextField) || !TextCheck.isDirExists(dirAdminTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), ResourceBundleUtils.getLangString(ERRORS, Errors.adminDirNotFound));
        }

        if (TextCheck.isEmpty(dirAdminTemplateTextField) || !TextCheck.isDirExists(dirAdminTemplateTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), ResourceBundleUtils.getLangString(ERRORS, Errors.adminDirDefaultNotFound));
        }

        if (TextCheck.isEmpty(dirBigDataTextField) || !TextCheck.isDirExists(dirBigDataTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), ResourceBundleUtils.getLangString(ERRORS, Errors.bigDataDirNotFound));
        }

        if (TextCheck.isEmpty(dirBigDataTemplateTextField) || !TextCheck.isDirExists(dirBigDataTemplateTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), ResourceBundleUtils.getLangString(ERRORS, Errors.bigDataDirDefaultNotFound));
        }

        if (TextCheck.isEmpty(identifierCodeStringTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));
        }

        if (TextCheck.isEmpty(identifierEditStringTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));
        }

        if (TextCheck.isEmpty(identifierFreeStationTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));
        }

        if (TextCheck.isEmpty(identifierControlPointTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));
        }

        if (TextCheck.isEmpty(identifierKnownStationTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));
        }

        if (TextCheck.isEmpty(identifierLTOPTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));
        }

        if (TextCheck.isEmpty(pointIdenticalDistance) || !checkForValidInputs()) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));
        }

        if (errorOccurred == Integer.MIN_VALUE) {
            if (writeSettings()) {
                if (Main.pref.isDefaultSettingsGenerated()) {
                    MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                            ResourceBundleUtils.getLangString(LABELS, Labels.successTextMsgBox),
                            ResourceBundleUtils.getLangString(MESSAGES, Messages.settingsDefaultGenerated));
                    Main.pref.setDefaultSettingsGenerated(true);
                } else {
                    MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                            ResourceBundleUtils.getLangString(LABELS, Labels.successTextMsgBox),
                            ResourceBundleUtils.getLangString(MESSAGES, Messages.settingsGenerated));
                    Main.pref.setDefaultSettingsGenerated(false);
                }
                Main.statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.settingsSaved), OK);
                Main.setSubShellStatus(false);
                widgetDispose();
            } else {
                MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                        ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                        ResourceBundleUtils.getLangString(ERRORS, Errors.settingsError));
            }
        }

    }

    private boolean checkForEmptyTextFields() {
        return TextCheck.isEmpty(dirBaseTextField) |
                TextCheck.isEmpty(dirProjectTextField) |
                TextCheck.isEmpty(dirProjectTemplateTextField) |
                TextCheck.isEmpty(dirAdminTextField) |
                TextCheck.isEmpty(dirAdminTemplateTextField) |
                TextCheck.isEmpty(dirBigDataTextField) |
                TextCheck.isEmpty(dirBigDataTemplateTextField) |
                TextCheck.isEmpty(identifierCodeStringTextField) |
                TextCheck.isEmpty(identifierEditStringTextField) |
                TextCheck.isEmpty(identifierFreeStationTextField) |
                TextCheck.isEmpty(identifierControlPointTextField) |
                TextCheck.isEmpty(identifierKnownStationTextField) |
                TextCheck.isEmpty(identifierLTOPTextField) |
                TextCheck.isEmpty(pointIdenticalDistance);
    }

    private boolean checkForValidInputs() {
        return TextCheck.isDoubleValue(pointIdenticalDistance);
    }

    private void createAdminDirComposite(Group group) {
        Label dirAdminLabel = new Label(group, SWT.NONE);
        dirAdminLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.adminPath));
        dirAdminLabel.setLayoutData(new GridData());

        dirAdminTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirAdminTextField.setText(Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN));
        dirAdminTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            } else if (event.detail == SWT.TRAVERSE_RETURN) {
                processDirAdminTextOperations();
            }
        });

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirAdminTextField.setLayoutData(gridData);

        Button btnDirAdmin = new Button(group, SWT.NONE);
        btnDirAdmin.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnDirAdmin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirAdminTextOperations();
            }
        });

        btnDirAdmin.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnDirAdmin.setLayoutData(new GridData());
    }

    private void createAdminDirTemplateComposite(Group group) {
        GridData gridData;
        Label dirAdminTemplateLabel = new Label(group, SWT.NONE);
        dirAdminTemplateLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.adminPathDefault));
        dirAdminTemplateLabel.setLayoutData(new GridData());

        dirAdminTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirAdminTemplateTextField.setText(Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN_TEMPLATE));
        dirAdminTemplateTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            } else if (event.detail == SWT.TRAVERSE_RETURN) {
                processDirAdminTemplateTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirAdminTemplateTextField.setLayoutData(gridData);

        Button btnDirAdminTemplate = new Button(group, SWT.NONE);
        btnDirAdminTemplate.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnDirAdminTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirAdminTemplateTextOperations();
            }
        });

        btnDirAdminTemplate.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnDirAdminTemplate.setLayoutData(new GridData());
    }

    private void createBaseDirComposite(Group group) {
        GridData gridData;
        Label dirBaseLabel = new Label(group, SWT.NONE);
        dirBaseLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.defaultPath));

        dirBaseTextField = new Text(group, SWT.BORDER);
        dirBaseTextField.setText(Main.pref.getUserPreference(PreferenceKeys.DIR_BASE));
        dirBaseTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            } else if (event.detail == SWT.TRAVERSE_RETURN) {
                processDirBaseTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirBaseTextField.setLayoutData(gridData);

        Button btnDirBase = new Button(group, SWT.NONE);
        btnDirBase.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnDirBase.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnDirBase.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBaseTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnDirBase.setLayoutData(gridData);
    }

    private void createBigDataDirComposite(Group group) {
        GridData gridData;
        Label dirBigDataLabel = new Label(group, SWT.NONE);
        dirBigDataLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.bigDataPath));
        dirBigDataLabel.setLayoutData(new GridData());

        dirBigDataTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirBigDataTextField.setText(Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA));
        dirBigDataTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            } else if (event.detail == SWT.TRAVERSE_RETURN) {
                processDirBigDataTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirBigDataTextField.setLayoutData(gridData);

        Button btnDirBigData = new Button(group, SWT.NONE);
        btnDirBigData.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnDirBigData.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBigDataTextOperations();
            }
        });

        btnDirBigData.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnDirBigData.setLayoutData(new GridData());
    }

    private void createBigDataTemplateDirComposite(Group group) {
        GridData gridData;
        Label dirBigDataTemplateLabel = new Label(group, SWT.NONE);
        dirBigDataTemplateLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.bigDataPathDefault));
        dirBigDataTemplateLabel.setLayoutData(new GridData());

        dirBigDataTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirBigDataTemplateTextField.setText(Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA_TEMPLATE));
        dirBigDataTemplateTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            } else if (event.detail == SWT.TRAVERSE_RETURN) {
                processDirBigDataTemplateTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirBigDataTemplateTextField.setLayoutData(gridData);

        Button btnDirBigDataTemplate = new Button(group, SWT.NONE);
        btnDirBigDataTemplate.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnDirBigDataTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBigDataTemplateTextOperations();
            }
        });

        btnDirBigDataTemplate.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnDirBigDataTemplate.setLayoutData(new GridData());
    }

    private void createBottomButtons() {
        Composite compositeBottomBtns = new Composite(innerShell, SWT.NONE);
        compositeBottomBtns.setLayout(new FillLayout());

        Button btnDefaultSettings = new Button(compositeBottomBtns, SWT.NONE);
        btnDefaultSettings.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.defaultSettingsText));
        btnDefaultSettings.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.defaultSettingsToolTip));
        btnDefaultSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnDefaultSettings();
            }
        });

        Button btnCancel = new Button(compositeBottomBtns, SWT.NONE);
        btnCancel.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.cancelText));
        btnCancel.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.cancelToolTip));
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCancel();
            }
        });

        Button btnOk = new Button(compositeBottomBtns, SWT.NONE);
        btnOk.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.okText));
        btnOk.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.okToolTip));
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnOk();
            }
        });

        GridData gridData = new GridData(SWT.END, SWT.END, false, true);
        compositeBottomBtns.setLayoutData(gridData);
    }

    private void createCompositeGeneralAndFormatSettings(int width) {
        Composite compositeGeneralAndFormat = new Composite(innerShell, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        compositeGeneralAndFormat.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        compositeGeneralAndFormat.setLayoutData(gridData);

        groupGeneral = new Group(compositeGeneralAndFormat, SWT.NONE);
        groupGeneral.setText(ResourceBundleUtils.getLangString(LABELS, Labels.settingsGeneralText));
        groupGeneral.setLayout(new GridLayout(1, false));

        createGroupGeneral(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupGeneral.setLayoutData(gridData2);

        groupFormat = new Group(compositeGeneralAndFormat, SWT.NONE);
        groupFormat.setText(ResourceBundleUtils.getLangString(LABELS, Labels.formatSettingsText));
        groupFormat.setLayout(new GridLayout(1, false));

        createGroupFormat(width / 2);

        gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupFormat.setLayoutData(gridData2);
    }

    private void createCompositeWidgetSettings(int width) {
        Composite compositeWidgetSettings = new Composite(innerShell, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        compositeWidgetSettings.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        compositeWidgetSettings.setLayoutData(gridData);

        groupTidyUpWidget = new Group(compositeWidgetSettings, SWT.NONE);
        groupTidyUpWidget.setText(ResourceBundleUtils.getLangString(LABELS, Labels.tidyUpSettingsText));
        groupTidyUpWidget.setLayout(new GridLayout(1, false));

        createGroupTidyUp(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupTidyUpWidget.setLayoutData(gridData2);

        groupConverterWidget = new Group(compositeWidgetSettings, SWT.NONE);
        groupConverterWidget.setText(ResourceBundleUtils.getLangString(LABELS, Labels.converterSettingsText));
        groupConverterWidget.setLayout(new GridLayout(1, false));

        createGroupConverter(width / 2);

        gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupConverterWidget.setLayoutData(gridData2);
    }

    private void createGroupConverter(int width) {
        createGroupConverterComposite1(width);
        createGroupConverterComposite2(width);
        createGroupConverterComposite3(width);
    }

    private void createGroupConverterComposite1(int width) {
        Composite composite = new Composite(groupConverterWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL_VERTICAL;
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        chkBoxEliminateZeroCoordinates = new Button(composite, SWT.CHECK);
        chkBoxEliminateZeroCoordinates.setSelection(Boolean.parseBoolean(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE)));
        chkBoxEliminateZeroCoordinates.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.eliminateZeroCoordinates));

        chkBoxLTOPUseZenithDistance = new Button(composite, SWT.CHECK);
        chkBoxLTOPUseZenithDistance.setSelection(Boolean.parseBoolean(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
        chkBoxLTOPUseZenithDistance.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.useZenithDistanceLTOP));
    }

    private void createGroupConverterComposite2(int width) {
        Composite composite2 = new Composite(groupConverterWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        composite2.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        composite2.setLayoutData(gridData);

        Label minimumPointDistanceLabel = new Label(composite2, SWT.NONE);
        minimumPointDistanceLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.minimumPointDistance));

        pointIdenticalDistance = new Text(composite2, SWT.BORDER);
        pointIdenticalDistance.setText(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE));
        pointIdenticalDistance.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        pointIdenticalDistance.setLayoutData(gridData);
    }

    private void createGroupConverterComposite3(int width) {
        Composite compositeZeissRecDialect = new Composite(groupConverterWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout(5, false);
        compositeZeissRecDialect.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        compositeZeissRecDialect.setLayoutData(gridData);

        Label zeissRecDialectLabel = new Label(compositeZeissRecDialect, SWT.NONE);
        zeissRecDialectLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.zeissRECDialect));

        groupZeissRECFormat = new Group(compositeZeissRecDialect, SWT.NONE);
        groupZeissRECFormat.setLayout(new GridLayout(4, false));

        Button btnR4 = new Button(groupZeissRECFormat, SWT.RADIO);
        btnR4.setText(R4.toString());
        Button btnR5 = new Button(groupZeissRECFormat, SWT.RADIO);
        btnR5.setText(R5.toString());
        Button btnRec500 = new Button(groupZeissRECFormat, SWT.RADIO);
        btnRec500.setText(REC500.toString());
        Button btnM5 = new Button(groupZeissRECFormat, SWT.RADIO);
        btnM5.setText(ZeissDialect.M5.toString());

        // try to set the Zeiss Rec dialect from stored settings
        switch (Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT)) {
            case "R4":
                RadioHelper.selectBtn(groupZeissRECFormat.getChildren(), 0);
                break;
            case "R5":
                RadioHelper.selectBtn(groupZeissRECFormat.getChildren(), 1);
                break;
            case "REC500":
                RadioHelper.selectBtn(groupZeissRECFormat.getChildren(), 2);
                break;
            case "M5":
                RadioHelper.selectBtn(groupZeissRECFormat.getChildren(), 3);
                break;
            default:
                RadioHelper.selectBtn(groupZeissRECFormat.getChildren(), 3);
                System.err.println("SettingsWidget.createGroupConverterComposite3() : set Zeiss dialect to M5");
        }
    }

    private void createGroupFormat(int width) {
        Composite composite = new Composite(groupFormat, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL_VERTICAL;
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        chkBoxUseSpaceAtLineEnd = new Button(composite, SWT.CHECK);
        chkBoxUseSpaceAtLineEnd.setSelection(Boolean.parseBoolean(Main.pref.getUserPreference(PreferenceKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK)));
        chkBoxUseSpaceAtLineEnd.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.useSpaceAtLineEnd));
    }

    private void createGroupGeneral(int width) {
        Composite composite = new Composite(groupGeneral, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        Label editStringLabel = new Label(composite, SWT.NONE);
        editStringLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.editString));

        identifierEditStringTextField = new Text(composite, SWT.BORDER);
        identifierEditStringTextField.setText(Main.pref.getUserPreference(PreferenceKeys.PARAM_EDIT_STRING));
        identifierEditStringTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierEditStringTextField.setLayoutData(gridData);

        Label editCodeLabel = new Label(composite, SWT.NONE);
        editCodeLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.codeString));

        identifierCodeStringTextField = new Text(composite, SWT.BORDER);
        identifierCodeStringTextField.setText(Main.pref.getUserPreference(PreferenceKeys.PARAM_CODE_STRING));
        identifierCodeStringTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierCodeStringTextField.setLayoutData(gridData);

        chkBoxOverwriteExistingFiles = new Button(composite, SWT.CHECK);
        chkBoxOverwriteExistingFiles.setSelection(Boolean.parseBoolean(Main.pref.getUserPreference(PreferenceKeys.OVERWRITE_EXISTING)));
        chkBoxOverwriteExistingFiles.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.overWriteExistingFiles));
    }

    private void createGroupPaths(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.pathSettingsText));

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        createBaseDirComposite(group);

        createProjectDirComposite(group);
        createProjectDirTemplateComposite(group);

        createAdminDirComposite(group);
        createAdminDirTemplateComposite(group);

        createBigDataDirComposite(group);
        createBigDataTemplateDirComposite(group);
    }

    private void createGroupTidyUp(int width) {
        Composite composite = new Composite(groupTidyUpWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        createTidyUpFreeStationComposite(composite);
        createTidyUpKnownStationComposite(composite);
        createTidyUpStakeOutComposite(composite);
        createTidyUpLTOPComposite(composite);
    }

    private void createProjectDirComposite(Group group) {
        GridData gridData;
        Label dirProjectLabel = new Label(group, SWT.NONE);
        dirProjectLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.projectPath));
        dirProjectLabel.setLayoutData(new GridData());

        dirProjectTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirProjectTextField.setText(Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT));
        dirProjectTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            } else if (event.detail == SWT.TRAVERSE_RETURN) {
                processDirProjectTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirProjectTextField.setLayoutData(gridData);

        Button btnDirProject = new Button(group, SWT.NONE);
        btnDirProject.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnDirProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirProjectTextOperations();
            }
        });

        btnDirProject.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnDirProject.setLayoutData(new GridData());
    }

    private void createProjectDirTemplateComposite(Group group) {
        GridData gridData;
        Label dirProjectTemplateLabel = new Label(group, SWT.NONE);
        dirProjectTemplateLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.projectPathDefault));
        dirProjectTemplateLabel.setLayoutData(new GridData());

        dirProjectTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirProjectTemplateTextField.setText(Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT_TEMPLATE));
        dirProjectTemplateTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            } else if (event.detail == SWT.TRAVERSE_RETURN) {
                processDirProjectTemplateTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirProjectTemplateTextField.setLayoutData(gridData);

        Button btnDirProjectTemplate = new Button(group, SWT.NONE);
        btnDirProjectTemplate.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnDirProjectTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirProjectTemplateTextOperations();
            }
        });

        btnDirProjectTemplate.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnDirProjectTemplate.setLayoutData(new GridData());
    }

    private void createTidyUpFreeStationComposite(Composite composite) {
        GridData gridData;
        Label freeStationLabel = new Label(composite, SWT.NONE);
        freeStationLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.freeStationIdentifier));

        identifierFreeStationTextField = new Text(composite, SWT.BORDER);
        identifierFreeStationTextField.setText(Main.pref.getUserPreference(PreferenceKeys.PARAM_FREE_STATION_STRING));
        identifierFreeStationTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {
                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }
            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierFreeStationTextField.setLayoutData(gridData);
    }

    private void createTidyUpKnownStationComposite(Composite composite) {
        GridData gridData;
        Label stationLabel = new Label(composite, SWT.NONE);
        stationLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.stationIdentifier));

        identifierKnownStationTextField = new Text(composite, SWT.BORDER);
        identifierKnownStationTextField.setText(Main.pref.getUserPreference(PreferenceKeys.PARAM_KNOWN_STATION_STRING));
        identifierKnownStationTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {

                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }

            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierKnownStationTextField.setLayoutData(gridData);
    }

    private void createTidyUpLTOPComposite(Composite composite) {
        GridData gridData;
        Label ltopLabel = new Label(composite, SWT.NONE);
        ltopLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.ltopIdentifier));

        identifierLTOPTextField = new Text(composite, SWT.BORDER);
        identifierLTOPTextField.setText(Main.pref.getUserPreference(PreferenceKeys.PARAM_LTOP_STRING));
        identifierLTOPTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {

                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }

            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierLTOPTextField.setLayoutData(gridData);
    }

    private void createTidyUpStakeOutComposite(Composite composite) {
        GridData gridData;
        Label stakeOutLabel = new Label(composite, SWT.NONE);
        stakeOutLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.stakeOutIdentifier));

        identifierControlPointTextField = new Text(composite, SWT.BORDER);
        identifierControlPointTextField.setText(Main.pref.getUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING));
        identifierControlPointTextField.addListener(SWT.Traverse, event -> {
            // prevent this shortcut for execute when the text fields are empty
            if (!checkForEmptyTextFields()) {

                if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                    actionBtnOk();
                }

            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierControlPointTextField.setLayoutData(gridData);
    }

    private void initUI() {
        int height = Sizes.RyCON_WIDGET_HEIGHT.getValue();
        int width = Sizes.RyCON_WIDGET_WIDTH.getValue() + 205;

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell = new Shell(Main.shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, event -> actionBtnCancel());
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.settingsText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createGroupPaths(width);

        createCompositeGeneralAndFormatSettings(width);
        createCompositeWidgetSettings(width);

        createBottomButtons();

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.forceActive();

        innerShell.pack();
        innerShell.open();
    }

    private void processDirAdminTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminTemplateTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminTemplateMessage),
                    dirAdminTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminTemplateTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminTemplateMessage),
                    dirBaseTextField.getText());
        }
        dirBigDataTextField.setFocus();
    }

    private void processDirAdminTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminMessage),
                    dirAdminTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirAdminMessage),
                    dirBaseTextField.getText());
        }
        dirAdminTemplateTextField.setFocus();
    }

    private void processDirBaseTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBaseTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBaseTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBaseMessage),
                    dirBaseTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBaseTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBaseTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBaseMessage),
                    Main.pref.getUserPreference(PreferenceKeys.DIR_BASE));
        }
        dirProjectTextField.setFocus();
    }

    private void processDirBigDataTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataTemplateTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataTemplateMessage),
                    dirBigDataTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataTemplateTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataTemplateMessage),
                    dirBaseTextField.getText());
        }
        identifierFreeStationTextField.setFocus();
    }

    private void processDirBigDataTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataMessage),
                    dirBigDataTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirBigDataMessage),
                    dirBaseTextField.getText());
        }
        dirBigDataTemplateTextField.setFocus();
    }

    private void processDirProjectTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectTemplateTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectTemplateMessage),
                    dirProjectTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectTemplateTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectTemplateMessage),
                    dirBaseTextField.getText());
        }
        dirAdminTextField.setFocus();
    }

    private void processDirProjectTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectMessage),
                    dirProjectTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTextField,
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectTitle),
                    ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.dirProjectMessage),
                    dirBaseTextField.getText());
        }
        dirProjectTemplateTextField.setFocus();
    }

    private void widgetDispose() {
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    private boolean writeSettings() {
        // general settings
        Main.pref.setUserPreference(PreferenceKeys.PARAM_CODE_STRING, identifierCodeStringTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.PARAM_EDIT_STRING, identifierEditStringTextField.getText());

        // parameters for module #1 - clean up
        Main.pref.setUserPreference(PreferenceKeys.PARAM_CONTROL_POINT_STRING, identifierControlPointTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.PARAM_FREE_STATION_STRING, identifierFreeStationTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.PARAM_KNOWN_STATION_STRING, identifierKnownStationTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.PARAM_LTOP_STRING, identifierLTOPTextField.getText());

        // parameters for module #4 - converter
        Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE, Boolean.toString(chkBoxEliminateZeroCoordinates.getSelection()));
        Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE, Boolean.toString(chkBoxLTOPUseZenithDistance.getSelection()));
        Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE, pointIdenticalDistance.getText());

        // Zeiss Rec dialect
        switch (ZeissDialect.fromIndex(RadioHelper.getSelectedBtn(groupZeissRECFormat.getChildren()))) {
            case R4:
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT, R4.name());
                break;
            case R5:
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT, R5.name());
                break;
            case REC500:
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT, REC500.name());
                break;
            case M5:
                Main.pref.setUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT, M5.name());
                break;
            default:
                break;
        }

        // paths for module #5 - project generation
        Main.pref.setUserPreference(PreferenceKeys.DIR_BASE, dirBaseTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.DIR_ADMIN, dirAdminTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.DIR_ADMIN_TEMPLATE, dirAdminTemplateTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.DIR_BIG_DATA, dirBigDataTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.DIR_BIG_DATA_TEMPLATE, dirBigDataTemplateTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.DIR_PROJECT, dirProjectTextField.getText());
        Main.pref.setUserPreference(PreferenceKeys.DIR_PROJECT_TEMPLATE, dirProjectTemplateTextField.getText());

        // GSI file format settings
        Main.pref.setUserPreference(PreferenceKeys.GSI_SETTING_LINE_ENDING_WITH_BLANK,
                Boolean.toString(chkBoxUseSpaceAtLineEnd.getSelection()));

        // TODO implement write setting success and checks for valid dirs
        // TODO(maybe with a listener construction?)

        /*

        // Register where??
        PreferenceChangeListener listener = new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                System.out.println("SETTINGS CHANGED");
            }
        };

         */

        return true;
    }

} // end of SettingsWidget.java
