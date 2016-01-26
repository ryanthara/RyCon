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
import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * With the SettingsWidget of RyCON it is possible to set up the options of RyCON and
 * write the configuration file.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>5: implementation of a new directory structure, code reformat, optimizations</li>
 *     <li>4: defeat bug #1 and #3 </li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 5
 * @since 2
 */
public class SettingsWidget {

    private Button chkBoxUseSpaceAtLineEnd;
    private Shell innerShell = null;
    private Text dirBaseTextField;
    private Text dirAdminTextField;
    private Text dirAdminTemplateTextField;
    private Text dirBigDataTextField;
    private Text dirBigDataTemplateTextField;
    private Text dirProjectTextField;
    private Text dirProjectTemplateTextField;
    private Text identifierControlPointTextField;
    private Text identifierFreeStationTextField;
    private Text identifierKnownStationTextField;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public SettingsWidget() {
        initUI();
        Main.setIsSettingsWidgetOpen(true);
    }

    private void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", StatusBar.OK);

        widgetDispose();
    }

    private void widgetDispose() {
        Main.setIsSettingsWidgetOpen(false);
        innerShell.dispose();
    }

    private void actionBtnDefaultSettings() {
        Main.pref.createDefaultSettings();
        Main.pref.setDefaultSettingsGenerated(true);
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", StatusBar.OK);
        
        MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_INFORMATION);
        msgBox.setMessage(I18N.getMsgSettingsDefaultGenerated());
        msgBox.setText(I18N.getMsgBoxTitleSuccess());
        msgBox.open();

        widgetDispose();
    }

    private void actionBtnOk() {
        if (!checkEmptyTextFields()) {
            if (writeSettings()) {
                Main.pref.setDefaultSettingsGenerated(false);
                Main.setSubShellStatus(false);
                Main.statusBar.setStatus("", StatusBar.OK);

                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_INFORMATION);
                msgBox.setMessage(I18N.getMsgSettingsSuccess());
                msgBox.setText(I18N.getMsgBoxTitleSuccess());
                msgBox.open();

                widgetDispose();
            } else {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                msgBox.setMessage(I18N.getMsgSettingsError());
                msgBox.setText(I18N.getMsgBoxTitleError());
                msgBox.open();
            }
        } else {
            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
            msgBox.setMessage(I18N.getMsgEmptyTextFieldWarning());
            msgBox.setText(I18N.getMsgBoxTitleWarning());
            msgBox.open();
        }
    }

    // TODO implement better checks for valid directories
    private boolean checkEmptyTextFields() {
        return  dirBaseTextField.getText().trim().equals("") ||
                dirAdminTextField.getText().trim().equals("") ||
                dirAdminTemplateTextField.getText().trim().equals("") ||
                dirBigDataTextField.getText().trim().equals("") ||
                dirBigDataTemplateTextField.getText().trim().equals("") ||
                dirProjectTextField.getText().trim().equals("") ||
                dirProjectTemplateTextField.getText().trim().equals("") ||
                identifierFreeStationTextField.getText().trim().equals("") ||
                identifierControlPointTextField.getText().trim().equals("") ||
                identifierKnownStationTextField.getText().trim().equals("")
        ;
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
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                                I18N.getFileChooserDirBaseMessage(), dirBaseTextField.getText());
                    } else {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                                I18N.getFileChooserDirBaseMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
                    }
                    dirProjectTextField.setFocus();
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
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                            I18N.getFileChooserDirBaseMessage(), dirBaseTextField.getText());
                } else {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBaseTextField, I18N.getFileChooserDirBaseTitle(),
                            I18N.getFileChooserDirBaseMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
                }
                dirProjectTextField.setFocus();
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
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                                I18N.getFileChooserDirProjectMessage(), dirProjectTextField.getText());
                    } else {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                                I18N.getFileChooserDirProjectMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT));
                    }
                    dirProjectTextField.setFocus();
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
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                            I18N.getFileChooserDirProjectMessage(), dirBaseTextField.getText());
                } else {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTextField, I18N.getFileChooserDirProjectTitle(),
                            I18N.getFileChooserDirProjectMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT));
                }
                dirProjectTemplateTextField.setFocus();
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
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                                I18N.getFileChooserDirProjectTemplateMessage(), dirProjectTemplateTextField.getText());
                    } else {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                                I18N.getFileChooserDirProjectTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE));
                    }
                    dirAdminTextField.setFocus();
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
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                            I18N.getFileChooserDirProjectTemplateMessage(), dirProjectTemplateTextField.getText());
                } else {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirProjectTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(),
                            I18N.getFileChooserDirProjectTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE));
                }
                dirBaseTextField.setFocus();
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
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                                I18N.getFileChooserDirAdminMessage(), dirAdminTextField.getText());
                    } else {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                                I18N.getFileChooserDirAdminMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN));
                    }
                    dirAdminTemplateTextField.setFocus();
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
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                            I18N.getFileChooserDirAdminMessage(), dirAdminTextField.getText());
                } else {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTextField, I18N.getFileChooserDirAdminTitle(),
                            I18N.getFileChooserDirAdminMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN));
                }
                dirAdminTemplateTextField.setFocus();
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
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                                I18N.getFileChooserDirAdminTemplateMessage(), dirAdminTemplateTextField.getText());
                    } else {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                                I18N.getFileChooserDirAdminTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE));
                    }
                    dirBigDataTextField.setFocus();
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
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                            I18N.getFileChooserDirAdminTemplateMessage(), dirAdminTemplateTextField.getText());
                } else {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirAdminTemplateTextField, I18N.getFileChooserDirAdminTemplateTitle(),
                            I18N.getFileChooserDirAdminTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE));
                }
                dirBigDataTextField.setFocus();
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
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                                I18N.getFileChooserDirBigDataMessage(), dirBigDataTextField.getText());
                    } else {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                                I18N.getFileChooserDirBigDataMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA));
                    }
                    dirBigDataTemplateTextField.setFocus();
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
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                            I18N.getFileChooserDirBigDataMessage(), dirBigDataTextField.getText());
                } else {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTextField, I18N.getFileChooserDirBigDataTitle(),
                            I18N.getFileChooserDirBigDataMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA));
                }
                dirBigDataTemplateTextField.setFocus();
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
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                                I18N.getFileChooserDirBigDataTemplateMessage(), dirBigDataTemplateTextField.getText());
                    } else {
                        GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                                I18N.getFileChooserDirBigDataTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE));
                    }
                    identifierFreeStationTextField.setFocus();
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
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                            I18N.getFileChooserDirBigDataTemplateMessage(), dirBigDataTemplateTextField.getText());
                } else {
                    GuiHelper.showAdvancedDirectoryDialog(innerShell, dirBigDataTemplateTextField, I18N.getFileChooserDirBigDataTemplateTitle(),
                            I18N.getFileChooserDirBigDataTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE));
                }
                identifierFreeStationTextField.setFocus();
            }
        });

        btnDirBigDataTemplate.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirBigDataTemplate.setLayoutData(new GridData());
    }

    private void createGroupGSIFormat(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleGSISettings());

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        chkBoxUseSpaceAtLineEnd = new Button(group, SWT.CHECK);
        chkBoxUseSpaceAtLineEnd.setSelection(Boolean.parseBoolean(Main.pref.getUserPref(PreferenceHandler.GSI_SETTING_LINE_ENDING_WITH_BLANK)));
        chkBoxUseSpaceAtLineEnd.setText(I18N.getBtnUseSpaceAtLineEnd());
    }

    private void createGroupTidyUp(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleTidyUpSettings());

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        Label freeStationLabel = new Label(group, SWT.NONE);
        freeStationLabel.setText(I18N.getLabelTextIdentifierFreeStation());

        identifierFreeStationTextField = new Text(group, SWT.BORDER);
        identifierFreeStationTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_FREE_STATION_STRING));
        identifierFreeStationTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

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

        Label stationLabel = new Label(group, SWT.NONE);
        stationLabel.setText(I18N.getLabelTextIdentifierStation());

        identifierKnownStationTextField = new Text(group, SWT.BORDER);
        identifierKnownStationTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_KNOWN_STATION_STRING));
        identifierKnownStationTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

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

        Label stakeOutLabel = new Label(group, SWT.NONE);
        stakeOutLabel.setText(I18N.getLabelTextIdentifierStakeOutPoint());

        identifierControlPointTextField = new Text(group, SWT.BORDER);
        identifierControlPointTextField.setText(Main.pref.getUserPref(PreferenceHandler.PARAM_CONTROL_POINT_STRING));
        identifierControlPointTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

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
        createGroupGSIFormat(width);
        createGroupTidyUp(width);
        createBottomButtons();

        innerShell.setLocation(ShellCenter.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.forceActive();

        innerShell.pack();
        innerShell.open();
    }

    private boolean writeSettings() {
        Main.pref.setUserPref(PreferenceHandler.DIR_BASE, dirBaseTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN, dirAdminTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE, dirAdminTemplateTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA, dirBigDataTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE, dirBigDataTemplateTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT, dirProjectTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE, dirProjectTemplateTextField.getText());

        Main.pref.setUserPref(PreferenceHandler.GSI_SETTING_LINE_ENDING_WITH_BLANK, Boolean.toString(chkBoxUseSpaceAtLineEnd.getSelection()));

        Main.pref.setUserPref(PreferenceHandler.PARAM_CONTROL_POINT_STRING, identifierControlPointTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.PARAM_FREE_STATION_STRING, identifierFreeStationTextField.getText());
        Main.pref.setUserPref(PreferenceHandler.PARAM_KNOWN_STATION_STRING, identifierKnownStationTextField.getText());
        
        // TODO implement write setting success
        
        return true;
    }

} // end of SettingsWidget.java
