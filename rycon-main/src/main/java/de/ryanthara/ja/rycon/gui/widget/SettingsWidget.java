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
import de.ryanthara.ja.rycon.gui.custom.StatusBar;
import de.ryanthara.ja.rycon.i18n.I18N;
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

/**
 * Instances of this class represents a complete settings widget of RyCON and it's functionality.
 * <p>
 * The settings widget of RyCON is called by hitting the 'P' key in the main window. It shows all of RyCON's settings,
 * which can be changed easily and stored with Java's preference handling system.
 * <p>
 * An additional button adds the functionality to set default values by a simple click.
 *
 * @author sebastian
 * @version 7
 * @since 2
 */
public class SettingsWidget {

    private Button chkBoxUseSpaceAtLineEnd;
    private Button chkBoxEliminateZeroCoordinates;
    private Button chkBoxLTOPUseZenithDistance;
    private Composite compositeZeissRecDialect;
    private Group groupFormat;
    private Group groupGeneral;
    private Group groupConverterWidget;
    private Group groupTidyUpWidget;
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
        identifierLTOPTextField.setText(Main.getParamLTOPString());
        pointIdenticalDistance.setText(Main.getPointIdenticalDistance());

        RadioHelper.selectBtn(compositeZeissRecDialect.getChildren(), 4); // M5 as default value

        Main.pref.setDefaultSettingsGenerated(true);
    }

    private void actionBtnOk() {
        if (!checkForEmptyTextFields() & checkForValidInputs()) {
            if (writeSettings()) {
                if (Main.pref.isDefaultSettingsGenerated()) {
                    MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), I18N.getMsgSettingsDefaultGenerated());
                    Main.pref.setDefaultSettingsGenerated(true);
                } else {
                    MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), I18N.getMsgSettingsSuccess());
                    Main.pref.setDefaultSettingsGenerated(false);
                }
                Main.statusBar.setStatus("", StatusBar.OK);
                Main.setSubShellStatus(false);
                widgetDispose();
            } else {
                MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR, I18N.getMsgBoxTitleError(), I18N.getMsgSettingsError());
            }
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgEmptyTextFieldWarning());
        }
    }

    private boolean checkForEmptyTextFields() {
        return TextCheck.checkIsEmpty(dirBaseTextField) |
                TextCheck.checkIsEmpty(dirProjectTextField) |
                TextCheck.checkIsEmpty(dirProjectTemplateTextField) |
                TextCheck.checkIsEmpty(dirAdminTextField) |
                TextCheck.checkIsEmpty(dirAdminTemplateTextField) |
                TextCheck.checkIsEmpty(dirBigDataTextField) |
                TextCheck.checkIsEmpty(dirBigDataTemplateTextField) |
                TextCheck.checkIsEmpty(identifierCodeStringTextField) |
                TextCheck.checkIsEmpty(identifierEditStringTextField) |
                TextCheck.checkIsEmpty(identifierFreeStationTextField) |
                TextCheck.checkIsEmpty(identifierControlPointTextField) |
                TextCheck.checkIsEmpty(identifierKnownStationTextField) |
                TextCheck.checkIsEmpty(identifierLTOPTextField) |
                TextCheck.checkIsEmpty(pointIdenticalDistance);
    }

    private boolean checkForValidInputs() {
        return TextCheck.checkIsDoubleValue(pointIdenticalDistance);
    }

    private void createAdminDirComposite(Group group) {
        GridData gridData;
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
    }

    private void createAdminDirTemplateComposite(Group group) {
        GridData gridData;
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
    }

    private void createBaseDirComposite(Group group) {
        GridData gridData;
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
    }

    private void createBigDataDirComposite(Group group) {
        GridData gridData;
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
    }

    private void createBigDataTemplateDirComposite(Group group) {
        GridData gridData;
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

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        compositeGeneralAndFormat.setLayoutData(gridData);

        groupGeneral = new Group(compositeGeneralAndFormat, SWT.NONE);
        groupGeneral.setText(I18N.getGroupTitleGeneralSettings());
        groupGeneral.setLayout(new GridLayout(1, false));

        createGroupGeneral(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupGeneral.setLayoutData(gridData2);

        groupFormat = new Group(compositeGeneralAndFormat, SWT.NONE);
        groupFormat.setText(I18N.getGroupTitleFormatSettings());
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
        groupTidyUpWidget.setText(I18N.getGroupTitleTidyUpSettings());
        groupTidyUpWidget.setLayout(new GridLayout(1, false));

        createGroupTidyUp(width / 2);

        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
        groupTidyUpWidget.setLayoutData(gridData2);

        groupConverterWidget = new Group(compositeWidgetSettings, SWT.NONE);
        groupConverterWidget.setText(I18N.getGroupTitleConverterSettings());
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
        chkBoxEliminateZeroCoordinates.setText(I18N.getBtnEliminateZeroCoordinate());

        chkBoxLTOPUseZenithDistance = new Button(composite, SWT.CHECK);
        chkBoxLTOPUseZenithDistance.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
        chkBoxLTOPUseZenithDistance.setText(I18N.getBtnChkBoxLTOPUseZenithDistance());
    }

    private void createGroupConverterComposite2(int width) {
        Composite composite2 = new Composite(groupConverterWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        composite2.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
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

    private void createGroupConverterComposite3(int width) {
        compositeZeissRecDialect = new Composite(groupConverterWidget, SWT.NONE);

        GridLayout gridLayout = new GridLayout(5, false);
        compositeZeissRecDialect.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        gridData.widthHint = width - 24;
        compositeZeissRecDialect.setLayoutData(gridData);

        Label zeissRecDialectLabel = new Label(compositeZeissRecDialect, SWT.NONE);
        zeissRecDialectLabel.setText(I18N.getLabelTextZeissRecDialect());

        Button btnR4 = new Button(compositeZeissRecDialect, SWT.RADIO);
        btnR4.setText(R4.toString());
        Button btnR5 = new Button(compositeZeissRecDialect, SWT.RADIO);
        btnR5.setText(R5.toString());
        Button btnRec500 = new Button(compositeZeissRecDialect, SWT.RADIO);
        btnRec500.setText(REC500.toString());
        Button btnM5 = new Button(compositeZeissRecDialect, SWT.RADIO);
        btnM5.setText(ZeissDialect.M5.toString());

        // try to set the Zeiss Rec dialect from stored settings
        switch (Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT)) {
            case "R4":
                RadioHelper.selectBtn(compositeZeissRecDialect.getChildren(), 1);
                break;
            case "R5":
                RadioHelper.selectBtn(compositeZeissRecDialect.getChildren(), 2);
                break;
            case "REC500":
                RadioHelper.selectBtn(compositeZeissRecDialect.getChildren(), 3);
                break;
            case "M5":
                RadioHelper.selectBtn(compositeZeissRecDialect.getChildren(), 4);
                break;
            default:
                RadioHelper.selectBtn(compositeZeissRecDialect.getChildren(), 4);
                break;
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
        chkBoxUseSpaceAtLineEnd.setText(I18N.getBtnUseSpaceAtLineEnd());
    }

    private void createGroupGeneral(int width) {
        Composite composite = new Composite(groupGeneral, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
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
    }

    private void createProjectDirTemplateComposite(Group group) {
        GridData gridData;
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
    }

    private void createTidyUpFreeStationComposite(Composite composite) {
        GridData gridData;
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
    }

    private void createTidyUpKnownStationComposite(Composite composite) {
        GridData gridData;
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
    }

    private void createTidyUpLTOPComposite(Composite composite) {
        GridData gridData;
        Label ltopLabel = new Label(composite, SWT.NONE);
        ltopLabel.setText(I18N.getLabelTextIdentifierLTOP());

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

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
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
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                    I18N.getFileChooserDirAdminTemplateMessage(), dirAdminTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                    I18N.getFileChooserDirAdminTemplateMessage(), dirBaseTextField.getText());
        }
        dirBigDataTextField.setFocus();
    }

    private void processDirAdminTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                    I18N.getFileChooserDirAdminMessage(), dirAdminTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                    I18N.getFileChooserDirAdminMessage(), dirBaseTextField.getText());
        }
        dirAdminTemplateTextField.setFocus();
    }

    private void processDirBaseTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                    I18N.getFileChooserDirBaseMessage(), dirBaseTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                    I18N.getFileChooserDirBaseMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
        }
        dirProjectTextField.setFocus();
    }

    private void processDirBigDataTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                    I18N.getFileChooserDirBigDataTemplateMessage(), dirBigDataTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                    I18N.getFileChooserDirBigDataTemplateMessage(), dirBaseTextField.getText());
        }
        identifierFreeStationTextField.setFocus();
    }

    private void processDirBigDataTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                    I18N.getFileChooserDirBigDataMessage(), dirBigDataTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                    I18N.getFileChooserDirBigDataMessage(), dirBaseTextField.getText());
        }
        dirBigDataTemplateTextField.setFocus();
    }

    private void processDirProjectTemplateTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                    I18N.getFileChooserDirProjectTemplateMessage(), dirProjectTemplateTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                    I18N.getFileChooserDirProjectTemplateMessage(), dirBaseTextField.getText());
        }
        dirAdminTextField.setFocus();
    }

    private void processDirProjectTextOperations() {
        if (!Main.pref.isDefaultSettingsGenerated()) {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                    I18N.getFileChooserDirProjectMessage(), dirProjectTextField.getText());
        } else {
            DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                    I18N.getFileChooserDirProjectMessage(), dirBaseTextField.getText());
        }
        dirProjectTemplateTextField.setFocus();
    }

    private void widgetDispose() {
        Main.setIsSettingsWidgetOpen(false);
        Main.statusBar.setStatus("", StatusBar.OK);
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
        switch (RadioHelper.getSelectedBtn(compositeZeissRecDialect.getChildren())) {
            case 1:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, R4.name());
                break;
            case 2:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, R5.name());
                break;
            case 3:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, REC500.name());
                break;
            case 4:
                Main.pref.setUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT, M5.name());
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
