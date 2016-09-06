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

package de.ryanthara.ja.rycon.gui;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.tools.SimpleChecker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * SettingsWidget implements a complete widget and it's functionality.
 * <p>
 * With the SettingsWidget of RyCON it is possible to set up the options of RyCON and
 * write the configuration file.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 * <li>5: implementation of a new directory structure, code reformat, optimizations</li>
 * <li>4: defeat bug #1 and #3 </li>
 * <li>3: code improvements and clean up </li>
 * <li>2: basic improvements </li>
 * <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 5
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
    private Shell innerShell = null;
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
    private Text pointIdenticalDistance;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    SettingsWidget() {
        initUI();
        Main.setIsSettingsWidgetOpen(true);
    }

    private void actionBtnCancel() {
        Main.pref.setDefaultSettingsGenerated(false);
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", StatusBar.OK);

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
        pointIdenticalDistance.setText(Main.getPointIdenticalDistance());

        Main.pref.setDefaultSettingsGenerated(true);
    }

    private void actionBtnOk() {
        if (!checkForEmptyTextFields() & checkForValidInputs()) {
            if (writeSettings()) {
                if (Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), I18N.getMsgSettingsDefaultGenerated());
                    Main.pref.setDefaultSettingsGenerated(true);
                } else {
                    GuiHelper.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), I18N.getMsgSettingsSuccess());
                    Main.pref.setDefaultSettingsGenerated(false);
                }
                Main.statusBar.setStatus("", StatusBar.OK);
                Main.setSubShellStatus(false);
                widgetDispose();
            } else {
                GuiHelper.showMessageBox(innerShell, SWT.ICON_ERROR, I18N.getMsgBoxTitleError(), I18N.getMsgSettingsError());
            }
        } else {
            GuiHelper.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgEmptyTextFieldWarning());
        }
    }

    private boolean checkForValidInputs() {
        return SimpleChecker.checkIsDoubleValue(pointIdenticalDistance);
    }

    private boolean checkForEmptyTextFields() {
        return SimpleChecker.checkIsTextEmpty(dirBaseTextField) |
                SimpleChecker.checkIsTextEmpty(dirProjectTextField) |
                SimpleChecker.checkIsTextEmpty(dirProjectTemplateTextField) |
                SimpleChecker.checkIsTextEmpty(dirAdminTextField) |
                SimpleChecker.checkIsTextEmpty(dirAdminTemplateTextField) |
                SimpleChecker.checkIsTextEmpty(dirBigDataTextField) |
                SimpleChecker.checkIsTextEmpty(dirBigDataTemplateTextField) |
                SimpleChecker.checkIsTextEmpty(identifierCodeStringTextField) |
                SimpleChecker.checkIsTextEmpty(identifierEditStringTextField) |
                SimpleChecker.checkIsTextEmpty(identifierFreeStationTextField) |
                SimpleChecker.checkIsTextEmpty(identifierControlPointTextField) |
                SimpleChecker.checkIsTextEmpty(identifierKnownStationTextField) |
                SimpleChecker.checkIsTextEmpty(pointIdenticalDistance);
    }

    private void createBottomButtons() {
        Composite compositeBottomBtns = new Composite(innerShell, SWT.NONE);
        compositeBottomBtns.setLayout(new FillLayout());

        Button btnDefaultSettings = new Button(compositeBottomBtns, SWT.NONE);
        btnDefaultSettings.setText(I18N.getBtnDefaultSettings());
        btnDefaultSettings.setToolTipText(I18N.getBtnDefaultSettingsToolTip());
        btnDefaultSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnDefaultSettings();
            }
        });

        Button btnCancel = new Button(compositeBottomBtns, SWT.NONE);
        btnCancel.setText(I18N.getBtnCancelLabel());
        btnCancel.setToolTipText(I18N.getBtnCancelLabelToolTip());
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCancel();
            }
        });

        Button btnOk = new Button(compositeBottomBtns, SWT.NONE);
        btnOk.setText(I18N.getBtnOKLabel());
        btnOk.setToolTipText(I18N.getBtnOKLabel());
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

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeGeneralAndFormat.setLayoutData(gridData);

        groupGeneral = new Group(compositeGeneralAndFormat, SWT.NONE);
        groupGeneral.setText(I18N.getGroupTitleGeneralSettings());
        groupGeneral.setLayout(new GridLayout(1, false));

        createGroupGeneral(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupGeneral.setLayoutData(gridData2);

        groupFormat = new Group(compositeGeneralAndFormat, SWT.NONE);
        groupFormat.setText(I18N.getGroupTitleFormatSettings());
        groupFormat.setLayout(new GridLayout(1, false));

        createGroupFormat(width / 2);

        gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupFormat.setLayoutData(gridData2);
    }

    private void createCompositeWidgetSettings(int width) {
        Composite compositeWidgetSettings = new Composite(innerShell, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        compositeWidgetSettings.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeWidgetSettings.setLayoutData(gridData);

        groupTidyUpWidget = new Group(compositeWidgetSettings, SWT.NONE);
        groupTidyUpWidget.setText(I18N.getGroupTitleTidyUpSettings());
        groupTidyUpWidget.setLayout(new GridLayout(1, false));

        createGroupTidyUp(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupTidyUpWidget.setLayoutData(gridData2);

        groupConverterWidget = new Group(compositeWidgetSettings, SWT.NONE);
        groupConverterWidget.setText(I18N.getGroupTitleConverterSettings());
        groupConverterWidget.setLayout(new GridLayout(1, false));

        createGroupConverter(width / 2);

        gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupConverterWidget.setLayoutData(gridData2);
    }

    private void createGroupConverter(int width) {
        Composite composite = new Composite(groupConverterWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL_VERTICAL;
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        chkBoxEliminateZeroCoordinates = new Button(composite, SWT.CHECK);
        chkBoxEliminateZeroCoordinates.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE)));
        chkBoxEliminateZeroCoordinates.setText(I18N.getBtnEliminateZeroCoordinate());

        chkBoxLTOPUseZenithDistance = new Button(composite, SWT.CHECK);
        chkBoxLTOPUseZenithDistance.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
        chkBoxLTOPUseZenithDistance.setText(I18N.getBtnChkBoxLTOPUseZenithDistance());

        Composite composite2 = new Composite(groupConverterWidget, SWT.NONE);

        gridLayout = new GridLayout(2, true);
        composite2.setLayout(gridLayout);

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        composite2.setLayoutData(gridData);

        Label minimumPointDistanceLabel = new Label(composite2, SWT.NONE);
        minimumPointDistanceLabel.setText(I18N.getLabelTextMinimumPointDistanceLabel());

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

    private void createGroupFormat(int width) {
        Composite composite = new Composite(groupFormat, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL_VERTICAL;
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        chkBoxUseSpaceAtLineEnd = new Button(composite, SWT.CHECK);
        chkBoxUseSpaceAtLineEnd.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.GSI_SETTING_LINE_ENDING_WITH_BLANK)));
        chkBoxUseSpaceAtLineEnd.setText(I18N.getBtnUseSpaceAtLineEnd());
    }

    private void createGroupGeneral(int width) {
        Composite composite = new Composite(groupGeneral, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        Label editStringLabel = new Label(composite, SWT.NONE);
        editStringLabel.setText(I18N.getLabelTextEditStringLabel());

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
        editCodeLabel.setText(I18N.getLabelTextCodeStringLabel());

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
        group.setText(I18N.getGroupTitlePathSettings());

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        Label dirBaseLabel = new Label(group, SWT.NONE);
        dirBaseLabel.setText(I18N.getLabelTextDirBase());

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
        btnDirBase.setText(I18N.getBtnChoosePath());
        btnDirBase.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirBase.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBaseTextOperations();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnDirBase.setLayoutData(gridData);


        Label dirProjectLabel = new Label(group, SWT.NONE);
        dirProjectLabel.setText(I18N.getLabelTextDirProject());
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
        btnDirProject.setText(I18N.getBtnChoosePath());
        btnDirProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirProjectTextOperations();
            }
        });

        btnDirProject.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirProject.setLayoutData(new GridData());


        Label dirProjectTemplateLabel = new Label(group, SWT.NONE);
        dirProjectTemplateLabel.setText(I18N.getLabelTextDirProjectTemplate());
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
        btnDirProjectTemplate.setText(I18N.getBtnChoosePath());
        btnDirProjectTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirProjectTemplateTextOperations();
            }
        });

        btnDirProjectTemplate.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirProjectTemplate.setLayoutData(new GridData());


        Label dirAdminLabel = new Label(group, SWT.NONE);
        dirAdminLabel.setText(I18N.getLabelTextDirAdmin());
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

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirAdminTextField.setLayoutData(gridData);

        Button btnDirAdmin = new Button(group, SWT.NONE);
        btnDirAdmin.setText(I18N.getBtnChoosePath());
        btnDirAdmin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirAdminTextOperations();
            }
        });

        btnDirAdmin.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirAdmin.setLayoutData(new GridData());


        Label dirAdminTemplateLabel = new Label(group, SWT.NONE);
        dirAdminTemplateLabel.setText(I18N.getLabelTextDirAdminTemplate());
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
        btnDirAdminTemplate.setText(I18N.getBtnChoosePath());
        btnDirAdminTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirAdminTemplateTextOperations();
            }
        });

        btnDirAdminTemplate.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirAdminTemplate.setLayoutData(new GridData());


        Label dirBigDataLabel = new Label(group, SWT.NONE);
        dirBigDataLabel.setText(I18N.getLabelTextDirBigData());
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
        btnDirBigData.setText(I18N.getBtnChoosePath());
        btnDirBigData.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBigDataTextOperations();
            }
        });

        btnDirBigData.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirBigData.setLayoutData(new GridData());


        Label dirBigDataTemplateLabel = new Label(group, SWT.NONE);
        dirBigDataTemplateLabel.setText(I18N.getLabelTextDirBigDataTemplate());
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
        btnDirBigDataTemplate.setText(I18N.getBtnChoosePath());
        btnDirBigDataTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processDirBigDataTemplateTextOperations();
            }
        });

        btnDirBigDataTemplate.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirBigDataTemplate.setLayoutData(new GridData());
    }

    private void createGroupTidyUp(int width) {
        Composite composite = new Composite(groupTidyUpWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        composite.setLayoutData(gridData);

        Label freeStationLabel = new Label(composite, SWT.NONE);
        freeStationLabel.setText(I18N.getLabelTextIdentifierFreeStation());

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

        Label stationLabel = new Label(composite, SWT.NONE);
        stationLabel.setText(I18N.getLabelTextIdentifierStation());

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

        Label stakeOutLabel = new Label(composite, SWT.NONE);
        stakeOutLabel.setText(I18N.getLabelTextIdentifierStakeOutPoint());

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

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell = new Shell(Main.shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                actionBtnCancel();
            }
        });
        innerShell.setText(I18N.getWidgetTitleSettingsWidget());
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
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                    I18N.getFileChooserDirAdminTemplateMessage(), dirAdminTemplateTextField.getText());
        } else {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                    I18N.getFileChooserDirAdminTemplateMessage(), dirBaseTextField.getText());
        }
        dirBigDataTextField.setFocus();
    }

    private void processDirAdminTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                    I18N.getFileChooserDirAdminMessage(), dirAdminTextField.getText());
        } else {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                    I18N.getFileChooserDirAdminMessage(), dirBaseTextField.getText());
        }
        dirAdminTemplateTextField.setFocus();
    }

    private void processDirBaseTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                    I18N.getFileChooserDirBaseMessage(), dirBaseTextField.getText());
        } else {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                    I18N.getFileChooserDirBaseMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
        }
        dirProjectTextField.setFocus();
    }

    private void processDirBigDataTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                    I18N.getFileChooserDirBigDataTemplateMessage(), dirBigDataTemplateTextField.getText());
        } else {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                    I18N.getFileChooserDirBigDataTemplateMessage(), dirBaseTextField.getText());
        }
        identifierFreeStationTextField.setFocus();
    }

    private void processDirBigDataTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                    I18N.getFileChooserDirBigDataMessage(), dirBigDataTextField.getText());
        } else {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                    I18N.getFileChooserDirBigDataMessage(), dirBaseTextField.getText());
        }
        dirBigDataTemplateTextField.setFocus();
    }

    private void processDirProjectTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                    I18N.getFileChooserDirProjectTemplateMessage(), dirProjectTemplateTextField.getText());
        } else {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                    I18N.getFileChooserDirProjectTemplateMessage(), dirBaseTextField.getText());
        }
        dirAdminTextField.setFocus();
    }

    private void processDirProjectTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                    I18N.getFileChooserDirProjectMessage(), dirProjectTextField.getText());
        } else {
            GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                    I18N.getFileChooserDirProjectMessage(), dirBaseTextField.getText());
        }
        dirProjectTemplateTextField.setFocus();
    }

    private void widgetDispose() {
        Main.setIsSettingsWidgetOpen(false);
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

        // parameters for module #4 - converter
        Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ELIMINATE_ZERO_COORDINATE, Boolean.toString(chkBoxEliminateZeroCoordinates.getSelection()));
        Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE, Boolean.toString(chkBoxLTOPUseZenithDistance.getSelection()));
        Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_POINT_IDENTICAL_DISTANCE, pointIdenticalDistance.getText());

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
