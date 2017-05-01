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
package de.ryanthara.ja.rycon.gui.widget;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.check.TextCheck;
import de.ryanthara.ja.rycon.converter.zeiss.ZeissDialect;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
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

/**
 * Instances of this class represents a complete settings widget of RyCON and it's functionality.
 * <p>
 * The settings widget of RyCON is behind button 9 or can be called by hitting the 'P' key in the main window .
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
        Main.setIsSettingsWidgetOpen(true);
    }

    private void actionBtnCancel() {
        Main.pref.setDefaultSettingsGenerated(false);
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        widgetDispose();
    }

    private void actionBtnDefaultSettings() {
        dirBaseTextField.setText(Main.getDirBase());
        dirAdminTextField.setText(Main.getDirAdmin());
        dirAdminTemplateTextField.setText(Main.getDirAdminTemplate());
        dirBigDataTextField.setText(Main.getDirBigData());
        dirBigDataTemplateTextField.setText(Main.getDirBigDataTemplate());
        dirProjectTextField.setText(Main.getDirProject());
        dirProjectTemplateTextField.setText(Main.getDirProjectTemplate());
        identifierCodeStringTextField.setText(Main.getParamCodeString());
        identifierControlPointTextField.setText(Main.getParamControlPointString());
        identifierEditStringTextField.setText(Main.getParamEditString());
        identifierFreeStationTextField.setText(Main.getParamFreeStationString());
        identifierKnownStationTextField.setText(Main.getParamKnownStationString());
        identifierLTOPTextField.setText(Main.getParamLTOPString());
        pointIdenticalDistance.setText(Main.getPointIdenticalDistance());

        RadioHelper.selectBtn(groupZeissRECFormat.getChildren(), 3); // M5 as default value

        Main.pref.setDefaultSettingsGenerated(true);
    }

    private void actionBtnOk() {
        int errorOccurred = Integer.MIN_VALUE;

        if (TextCheck.isEmpty(dirBaseTextField) || !TextCheck.isDirExists(dirBaseTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    Labels.getString("errorTextMsgBox"), Errors.getString("baseDirNotFound"));
        }

        if (TextCheck.isEmpty(dirProjectTextField) || !TextCheck.isDirExists(dirProjectTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    Labels.getString("errorTextMsgBox"), Errors.getString("projectDirNotFound"));
        }

        if (TextCheck.isEmpty(dirProjectTemplateTextField) || !TextCheck.isDirExists(dirProjectTemplateTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    Labels.getString("errorTextMsgBox"), Errors.getString("projectDirDefaultNotFound"));
        }

        if (TextCheck.isEmpty(dirAdminTextField) || !TextCheck.isDirExists(dirAdminTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    Labels.getString("errorTextMsgBox"), Errors.getString("adminDirNotFound"));
        }

        if (TextCheck.isEmpty(dirAdminTemplateTextField) || !TextCheck.isDirExists(dirAdminTemplateTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    Labels.getString("errorTextMsgBox"), Errors.getString("adminDirDefaultNotFound"));
        }

        if (TextCheck.isEmpty(dirBigDataTextField) || !TextCheck.isDirExists(dirBigDataTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    Labels.getString("errorTextMsgBox"), Errors.getString("bigDataDirNotFound"));
        }

        if (TextCheck.isEmpty(dirBigDataTemplateTextField) || !TextCheck.isDirExists(dirBigDataTemplateTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    Labels.getString("errorTextMsgBox"), Errors.getString("bigDataDirDefaultNotFound"));
        }

        if (TextCheck.isEmpty(identifierCodeStringTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("warningTextMsgBox"), Warnings.getString("emptyTextField"));
        }

        if (TextCheck.isEmpty(identifierEditStringTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("warningTextMsgBox"), Warnings.getString("emptyTextField"));
        }

        if (TextCheck.isEmpty(identifierFreeStationTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("warningTextMsgBox"), Warnings.getString("emptyTextField"));
        }

        if (TextCheck.isEmpty(identifierControlPointTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("warningTextMsgBox"), Warnings.getString("emptyTextField"));
        }

        if (TextCheck.isEmpty(identifierKnownStationTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("warningTextMsgBox"), Warnings.getString("emptyTextField"));
        }

        if (TextCheck.isEmpty(identifierLTOPTextField)) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("warningTextMsgBox"), Warnings.getString("emptyTextField"));
        }

        if (TextCheck.isEmpty(pointIdenticalDistance) || !checkForValidInputs()) {
            errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("warningTextMsgBox"), Warnings.getString("emptyTextField"));
        }

        if (errorOccurred == Integer.MIN_VALUE) {
            if (writeSettings()) {
                if (Main.pref.isDefaultSettingsGenerated()) {
                    MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                            Labels.getString("successTextMsgBox"), Messages.getString("settingsDefaultGenerated"));
                    Main.pref.setDefaultSettingsGenerated(true);
                } else {
                    MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                            Labels.getString("successTextMsgBox"), Messages.getString("settingsGenerated"));
                    Main.pref.setDefaultSettingsGenerated(false);
                }
                Main.statusBar.setStatus(Labels.getString("settingsSaved"), OK);
                Main.setSubShellStatus(false);
                widgetDispose();
            } else {
                MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                        Labels.getString("errorTextMsgBox"), Errors.getString("settingsError"));
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
        dirAdminLabel.setText(Labels.getString("adminPath"));
        dirAdminLabel.setLayoutData(new GridData());

        dirAdminTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirAdminTextField.setText(Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN));
        dirAdminTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    processDirAdminTextOperations();
                }
            }
        });

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirAdminTextField.setLayoutData(gridData);

        Button btnDirAdmin = new Button(group, SWT.NONE);
        btnDirAdmin.setText(Buttons.getString("choosePathText"));
        btnDirAdmin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirAdminTextOperations();
            }
        });

        btnDirAdmin.setToolTipText(Buttons.getString("choosePathToolTip"));
        btnDirAdmin.setLayoutData(new GridData());
    }

    private void createAdminDirTemplateComposite(Group group) {
        GridData gridData;
        Label dirAdminTemplateLabel = new Label(group, SWT.NONE);
        dirAdminTemplateLabel.setText(Labels.getString("adminPathDefault"));
        dirAdminTemplateLabel.setLayoutData(new GridData());

        dirAdminTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirAdminTemplateTextField.setText(Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE));
        dirAdminTemplateTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    processDirAdminTemplateTextOperations();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirAdminTemplateTextField.setLayoutData(gridData);

        Button btnDirAdminTemplate = new Button(group, SWT.NONE);
        btnDirAdminTemplate.setText(Buttons.getString("choosePathText"));
        btnDirAdminTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirAdminTemplateTextOperations();
            }
        });

        btnDirAdminTemplate.setToolTipText(Buttons.getString("choosePathToolTip"));
        btnDirAdminTemplate.setLayoutData(new GridData());
    }

    private void createBaseDirComposite(Group group) {
        GridData gridData;
        Label dirBaseLabel = new Label(group, SWT.NONE);
        dirBaseLabel.setText(Labels.getString("defaultPath"));

        dirBaseTextField = new Text(group, SWT.BORDER);
        dirBaseTextField.setText(Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
        dirBaseTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    processDirBaseTextOperations();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirBaseTextField.setLayoutData(gridData);

        Button btnDirBase = new Button(group, SWT.NONE);
        btnDirBase.setText(Buttons.getString("choosePathText"));
        btnDirBase.setToolTipText(Buttons.getString("choosePathToolTip"));
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
        dirBigDataLabel.setText(Labels.getString("bigDataPath"));
        dirBigDataLabel.setLayoutData(new GridData());

        dirBigDataTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirBigDataTextField.setText(Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA));
        dirBigDataTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    processDirBigDataTextOperations();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirBigDataTextField.setLayoutData(gridData);

        Button btnDirBigData = new Button(group, SWT.NONE);
        btnDirBigData.setText(Buttons.getString("choosePathText"));
        btnDirBigData.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBigDataTextOperations();
            }
        });

        btnDirBigData.setToolTipText(Buttons.getString("choosePathToolTip"));
        btnDirBigData.setLayoutData(new GridData());
    }

    private void createBigDataTemplateDirComposite(Group group) {
        GridData gridData;
        Label dirBigDataTemplateLabel = new Label(group, SWT.NONE);
        dirBigDataTemplateLabel.setText(Labels.getString("bigDataPathDefault"));
        dirBigDataTemplateLabel.setLayoutData(new GridData());

        dirBigDataTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirBigDataTemplateTextField.setText(Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE));
        dirBigDataTemplateTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    processDirBigDataTemplateTextOperations();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirBigDataTemplateTextField.setLayoutData(gridData);

        Button btnDirBigDataTemplate = new Button(group, SWT.NONE);
        btnDirBigDataTemplate.setText(Buttons.getString("choosePathText"));
        btnDirBigDataTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBigDataTemplateTextOperations();
            }
        });

        btnDirBigDataTemplate.setToolTipText(Buttons.getString("choosePathToolTip"));
        btnDirBigDataTemplate.setLayoutData(new GridData());
    }

    private void createBottomButtons() {
        Composite compositeBottomBtns = new Composite(innerShell, SWT.NONE);
        compositeBottomBtns.setLayout(new FillLayout());

        Button btnDefaultSettings = new Button(compositeBottomBtns, SWT.NONE);
        btnDefaultSettings.setText(Buttons.getString("defaultSettingsText"));
        btnDefaultSettings.setToolTipText(Buttons.getString("defaultSettingsToolTip"));
        btnDefaultSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnDefaultSettings();
            }
        });

        Button btnCancel = new Button(compositeBottomBtns, SWT.NONE);
        btnCancel.setText(Buttons.getString("cancelText"));
        btnCancel.setToolTipText(Buttons.getString("cancelToolTip"));
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCancel();
            }
        });

        Button btnOk = new Button(compositeBottomBtns, SWT.NONE);
        btnOk.setText(Buttons.getString("okText"));
        btnOk.setToolTipText(Buttons.getString("okToolTip"));
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
        groupGeneral.setText(Labels.getString("settingsGeneralText"));
        groupGeneral.setLayout(new GridLayout(1, false));

        createGroupGeneral(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupGeneral.setLayoutData(gridData2);

        groupFormat = new Group(compositeGeneralAndFormat, SWT.NONE);
        groupFormat.setText(Labels.getString("formatSettingsText"));
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
        groupTidyUpWidget.setText(Labels.getString("tidyUpSettingsText"));
        groupTidyUpWidget.setLayout(new GridLayout(1, false));

        createGroupTidyUp(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupTidyUpWidget.setLayoutData(gridData2);

        groupConverterWidget = new Group(compositeWidgetSettings, SWT.NONE);
        groupConverterWidget.setText(Labels.getString("converterSettingsText"));
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
        chkBoxEliminateZeroCoordinates.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE)));
        chkBoxEliminateZeroCoordinates.setText(CheckBoxes.getString("eliminateZeroCoordinates"));

        chkBoxLTOPUseZenithDistance = new Button(composite, SWT.CHECK);
        chkBoxLTOPUseZenithDistance.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
        chkBoxLTOPUseZenithDistance.setText(CheckBoxes.getString("useZenithDistanceLTOP"));
    }

    private void createGroupConverterComposite2(int width) {
        Composite composite2 = new Composite(groupConverterWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        composite2.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        composite2.setLayoutData(gridData);

        Label minimumPointDistanceLabel = new Label(composite2, SWT.NONE);
        minimumPointDistanceLabel.setText(Labels.getString("minimumPointDistance"));

        pointIdenticalDistance = new Text(composite2, SWT.BORDER);
        pointIdenticalDistance.setText(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE));
        pointIdenticalDistance.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
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
        zeissRecDialectLabel.setText(Labels.getString("zeissRECDialect"));

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
        switch (Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT)) {
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
        chkBoxUseSpaceAtLineEnd.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.GSI_SETTING_LINE_ENDING_WITH_BLANK)));
        chkBoxUseSpaceAtLineEnd.setText(CheckBoxes.getString("useSpaceAtLineEnd"));
    }

    private void createGroupGeneral(int width) {
        Composite composite = new Composite(groupGeneral, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        Label editStringLabel = new Label(composite, SWT.NONE);
        editStringLabel.setText(Labels.getString("editString"));

        identifierEditStringTextField = new Text(composite, SWT.BORDER);
        identifierEditStringTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_EDIT_STRING));
        identifierEditStringTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                }
            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierEditStringTextField.setLayoutData(gridData);

        Label editCodeLabel = new Label(composite, SWT.NONE);
        editCodeLabel.setText(Labels.getString("codeString"));

        identifierCodeStringTextField = new Text(composite, SWT.BORDER);
        identifierCodeStringTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_CODE_STRING));
        identifierCodeStringTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                }
            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierCodeStringTextField.setLayoutData(gridData);
    }

    private void createGroupPaths(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(Labels.getString("pathSettingsText"));

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
        dirProjectLabel.setText(Labels.getString("projectPath"));
        dirProjectLabel.setLayoutData(new GridData());

        dirProjectTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirProjectTextField.setText(Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT));
        dirProjectTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    processDirProjectTextOperations();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirProjectTextField.setLayoutData(gridData);

        Button btnDirProject = new Button(group, SWT.NONE);
        btnDirProject.setText(Buttons.getString("choosePathText"));
        btnDirProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirProjectTextOperations();
            }
        });

        btnDirProject.setToolTipText(Buttons.getString("choosePathToolTip"));
        btnDirProject.setLayoutData(new GridData());
    }

    private void createProjectDirTemplateComposite(Group group) {
        GridData gridData;
        Label dirProjectTemplateLabel = new Label(group, SWT.NONE);
        dirProjectTemplateLabel.setText(Labels.getString("projectPathDefault"));
        dirProjectTemplateLabel.setLayoutData(new GridData());

        dirProjectTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirProjectTemplateTextField.setText(Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE));
        dirProjectTemplateTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    processDirProjectTemplateTextOperations();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirProjectTemplateTextField.setLayoutData(gridData);

        Button btnDirProjectTemplate = new Button(group, SWT.NONE);
        btnDirProjectTemplate.setText(Buttons.getString("choosePathText"));
        btnDirProjectTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirProjectTemplateTextOperations();
            }
        });

        btnDirProjectTemplate.setToolTipText(Buttons.getString("choosePathToolTip"));
        btnDirProjectTemplate.setLayoutData(new GridData());
    }

    private void createTidyUpFreeStationComposite(Composite composite) {
        GridData gridData;
        Label freeStationLabel = new Label(composite, SWT.NONE);
        freeStationLabel.setText(Labels.getString("freeStationIdentifier"));

        identifierFreeStationTextField = new Text(composite, SWT.BORDER);
        identifierFreeStationTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_FREE_STATION_STRING));
        identifierFreeStationTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {
                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }
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
        stationLabel.setText(Labels.getString("stationIdentifier"));

        identifierKnownStationTextField = new Text(composite, SWT.BORDER);
        identifierKnownStationTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_KNOWN_STATION_STRING));
        identifierKnownStationTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

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
        ltopLabel.setText(Labels.getString("ltopIdentifier"));

        identifierLTOPTextField = new Text(composite, SWT.BORDER);
        identifierLTOPTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_LTOP_STRING));
        identifierLTOPTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

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
        stakeOutLabel.setText(Labels.getString("stakeOutIdentifier"));

        identifierControlPointTextField = new Text(composite, SWT.BORDER);
        identifierControlPointTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_CONTROL_POINT_STRING));
        identifierControlPointTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkForEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                }
            }
        });

        gridData = new GridData();
        gridData.widthHint = 50;
        gridData.grabExcessHorizontalSpace = false;
        identifierControlPointTextField.setLayoutData(gridData);
    }

    private void initUI() {
        int height = Main.getRyCONWidgetHeight();
        int width = Main.getRyCONWidgetWidth() + 200;

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell = new Shell(Main.shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                actionBtnCancel();
            }
        });
        innerShell.setText(Labels.getString("settingsText"));
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
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, FileChoosers.getString("dirAdminTemplateTitle"),
                    FileChoosers.getString("dirAdminTemplateMessage"), dirAdminTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, FileChoosers.getString("dirAdminTemplateTitle"),
                    FileChoosers.getString("dirAdminTemplateMessage"), dirBaseTextField.getText());
        }
        dirBigDataTextField.setFocus();
    }

    private void processDirAdminTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, FileChoosers.getString("dirAdminTitle"),
                    FileChoosers.getString("dirAdminMessage"), dirAdminTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, FileChoosers.getString("dirAdminTitle"),
                    FileChoosers.getString("dirAdminMessage"), dirBaseTextField.getText());
        }
        dirAdminTemplateTextField.setFocus();
    }

    private void processDirBaseTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, FileChoosers.getString("dirBaseTitle"),
                    FileChoosers.getString("dirBaseMessage"), dirBaseTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, FileChoosers.getString("dirBaseTitle"),
                    FileChoosers.getString("dirBaseMessage"), Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
        }
        dirProjectTextField.setFocus();
    }

    private void processDirBigDataTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, FileChoosers.getString("dirBigDataTemplateTitle"),
                    FileChoosers.getString("dirBigDataTemplateMessage"), dirBigDataTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, FileChoosers.getString("dirBigDataTemplateTitle"),
                    FileChoosers.getString("dirBigDataTemplateMessage"), dirBaseTextField.getText());
        }
        identifierFreeStationTextField.setFocus();
    }

    private void processDirBigDataTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, FileChoosers.getString("dirBigDataTitle"),
                    FileChoosers.getString("dirBigDataMessage"), dirBigDataTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, FileChoosers.getString("dirBigDataTitle"),
                    FileChoosers.getString("dirBigDataMessage"), dirBaseTextField.getText());
        }
        dirBigDataTemplateTextField.setFocus();
    }

    private void processDirProjectTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, FileChoosers.getString("dirProjectTemplateTitle"),
                    FileChoosers.getString("dirProjectTemplateMessage"), dirProjectTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, FileChoosers.getString("dirProjectTemplateTitle"),
                    FileChoosers.getString("dirProjectTemplateMessage"), dirBaseTextField.getText());
        }
        dirAdminTextField.setFocus();
    }

    private void processDirProjectTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, FileChoosers.getString("dirProjectTitle"),
                    FileChoosers.getString("dirProjectMessage"), dirProjectTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, FileChoosers.getString("dirProjectTitle"),
                    FileChoosers.getString("dirProjectMessage"), dirBaseTextField.getText());
        }
        dirProjectTemplateTextField.setFocus();
    }

    private void widgetDispose() {
        Main.setIsSettingsWidgetOpen(false);
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    private boolean writeSettings() {
        // general settings
        Main.pref.setUserPref(PreferenceHandler.PARAM_CODE_STRING, identifierCodeStringTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.PARAM_EDIT_STRING, identifierEditStringTextField.getText());

        // parameters for module #1 - clean up
        Main.pref.setUserPref(PreferenceHandler.PARAM_CONTROL_POINT_STRING, identifierControlPointTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.PARAM_FREE_STATION_STRING, identifierFreeStationTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.PARAM_KNOWN_STATION_STRING, identifierKnownStationTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.PARAM_LTOP_STRING, identifierLTOPTextField.getText());

        // parameters for module #4 - converter
        Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE, Boolean.toString(chkBoxEliminateZeroCoordinates.getSelection()));
        Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE, Boolean.toString(chkBoxLTOPUseZenithDistance.getSelection()));
        Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE, pointIdenticalDistance.getText());

        // Zeiss Rec dialect
        switch (ZeissDialect.fromIndex(RadioHelper.getSelectedBtn(groupZeissRECFormat.getChildren()))) {
            case R4:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, R4.name());
                break;
            case R5:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, R5.name());
                break;
            case REC500:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, REC500.name());
                break;
            case M5:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, M5.name());
                break;
            default:
                break;
        }

        // paths for module #5 - project generation
        Main.pref.setUserPref(PreferenceHandler.DIR_BASE, dirBaseTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN, dirAdminTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE, dirAdminTemplateTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA, dirBigDataTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE, dirBigDataTemplateTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT, dirProjectTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE, dirProjectTemplateTextField.getText());

        // GSI file format settings
        Main.pref.setUserPref(PreferenceHandler.GSI_SETTING_LINE_ENDING_WITH_BLANK, Boolean.toString(chkBoxUseSpaceAtLineEnd.getSelection()));

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
