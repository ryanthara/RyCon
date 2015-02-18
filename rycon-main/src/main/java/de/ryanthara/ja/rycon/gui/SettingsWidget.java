/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import java.io.File;
import java.util.Properties;

/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * With the SettingsWidget of RyCON it is possible to set up the options of RyCON and
 * write the configuration file.
 *
 * @author sebastian
 * @version 1
 * @since 2
 */
public class SettingsWidget {

    /**
     * Member for the inner shell object.
     */
    private Shell innerShell = null;

    /**
     * Member for the text field with the free station identifier
     */
    private Text identifierFreeStationTextField;

    /**
     * Member for the text field with the free station identifier
     */
    private Text identifierControlPointTextField;

    /**
     * Member for the text field with the known station identifier
     */
    private Text identifierStationTextField;

    /**
     * Member for the base dir text field.
     */
    private Text dirBaseTextField;

    /**
     * Member for the jobs dir text field.
     */
    private Text dirJobsTextField;

    /**
     * Member for the jobs template dir text field.
     */
    private Text dirJobsTemplateTextField;

    /**
     * Member for the projects dir text field.
     */
    private Text dirProjectsTextField;

    /**
     * Member for the projects template dir text field.
     */
    private Text dirProjectsTemplateTextField;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public SettingsWidget() {
        initUI();
    }

    /**
     * Does all the things when hitting the cancel button.
     */
    private void actionBtnCancel() {
        Main.setSubShellStatus(false);

        Main.statusBar.setStatus("", StatusBar.OK);

        innerShell.dispose();
    }

    /**
     * Helper for choosing a path via dialogs.
     *
     * @param textField calling text field
     * @param title title of the dialog
     * @param message message of the dialog
     * @param filterPath path of the dialog
     */
    private void actionBtnChoosePath(Text textField, String title, String message, String filterPath) {

        DirectoryDialog directoryDialog = new DirectoryDialog(innerShell);

        directoryDialog.setText(title);
        directoryDialog.setMessage(message);
        directoryDialog.setFilterPath(filterPath);

        String path = directoryDialog.open();

        if (path != null) {

            File checkDirDestination = new File(path);
            if (!checkDirDestination.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirDestinationNotExistWarning());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                textField.setText(path);
            }

        }

    }

    /**
     * Does all the things when hitting the OK button.
     */
    private void actionBtnOk() {

        if (!checkEmptyTextFields()) {
            if (writeSettings()) {
                Main.pref.setDefaultSettingsGenerated(false);
                Main.setSubShellStatus(false);
                Main.statusBar.setStatus("", StatusBar.OK);

                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_INFORMATION);
                msgBox.setMessage(String.format(I18N.getMsgSettingsSuccess()));
                msgBox.setText(I18N.getMsgBoxTitleSuccess());
                msgBox.open();

                innerShell.dispose();
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

    /**
     * Checks the text fields for being empty.
     * @return true if one or more text fields are empty
     */
    private boolean checkEmptyTextFields() {

        return  dirBaseTextField.getText().trim().equals("") ||
                dirJobsTemplateTextField.getText().trim().equals("") ||
                dirJobsTextField.getText().trim().equals("") ||
                dirProjectsTemplateTextField.getText().trim().equals("") ||
                dirProjectsTextField.getText().trim().equals("") ||
                identifierFreeStationTextField.getText().trim().equals("") ||
                identifierControlPointTextField.getText().trim().equals("") ||
                identifierStationTextField.getText().trim().equals("")
        ;

    }

    /**
     * Creates the group for the path settings and all its functionality.
     *
     * @param width width of the group
     */
    private void createGroupPaths(int width) {

        GridLayout gridLayout;
        GridData gridData;

        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitlePathSettings());

        gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        group.setLayout(gridLayout);

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        // base directory
        Label dirBaseLabel = new Label(group, SWT.NONE);
        dirBaseLabel.setText(I18N.getLabelDirBase());

        dirBaseTextField = new Text(group, SWT.BORDER);
        dirBaseTextField.setText(Main.pref.getSingleProperty("DirBase"));
        dirBaseTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnChoosePath(dirBaseTextField, I18N.getFileChooserDirBaseTitle(), I18N.getFileChooserDirBaseMessage(), Main.pref.getSingleProperty("DirBase"));
                    dirJobsTextField.setFocus();
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
                actionBtnChoosePath(dirBaseTextField, I18N.getFileChooserDirBaseTitle(), I18N.getFileChooserDirBaseMessage(), Main.pref.getSingleProperty("DirBase"));
                dirJobsTextField.setFocus();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnDirBase.setLayoutData(gridData);


        // jobs directory
        Label dirJobsLabel = new Label(group, SWT.NONE);
        dirJobsLabel.setText(I18N.getLabelDirJobs());
        dirJobsLabel.setLayoutData(new GridData());

        dirJobsTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirJobsTextField.setText(Main.pref.getSingleProperty("DirJobs"));
        dirJobsTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        System.out.println("MF");
                        actionBtnChoosePath(dirJobsTextField, I18N.getFileChooserDirJobsTitle(), I18N.getFileChooserDirJobsMessage(), dirBaseTextField.getText());
                    } else {
                        actionBtnChoosePath(dirJobsTextField, I18N.getFileChooserDirJobsTitle(), I18N.getFileChooserDirJobsMessage(), Main.pref.getSingleProperty("DirJobs"));
                    }
                    dirJobsTemplateTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirJobsTextField.setLayoutData(gridData);

        Button btnDirJobs = new Button(group, SWT.NONE);
        btnDirJobs.setText(I18N.getBtnChoosePath());
        btnDirJobs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    actionBtnChoosePath(dirJobsTextField, I18N.getFileChooserDirJobsTitle(), I18N.getFileChooserDirJobsMessage(), dirBaseTextField.getText());
                } else {
                    actionBtnChoosePath(dirJobsTextField, I18N.getFileChooserDirJobsTitle(), I18N.getFileChooserDirJobsMessage(), Main.pref.getSingleProperty("DirJobs"));
                }
                dirJobsTemplateTextField.setFocus();
            }
        });

        btnDirJobs.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirJobs.setLayoutData(new GridData());


        // jobs template directory
        Label dirJobsTemplateLabel = new Label(group, SWT.NONE);
        dirJobsTemplateLabel.setText(I18N.getLabelDirJobsTemplate());
        dirJobsTemplateLabel.setLayoutData(new GridData());

        dirJobsTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirJobsTemplateTextField.setText(Main.pref.getSingleProperty("DirJobsTemplate"));
        dirJobsTemplateTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        actionBtnChoosePath(dirJobsTemplateTextField, I18N.getFileChooserDirJobsTemplateTitle(), I18N.getFileChooserDirJobsTemplateMessage(), dirJobsTextField.getText());
                    } else {
                        actionBtnChoosePath(dirJobsTemplateTextField, I18N.getFileChooserDirJobsTemplateTitle(), I18N.getFileChooserDirJobsTemplateMessage(), Main.pref.getSingleProperty("DirJobsTemplate"));
                    }
                    dirProjectsTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirJobsTemplateTextField.setLayoutData(gridData);

        Button btnDirJobsTemplate = new Button(group, SWT.NONE);
        btnDirJobsTemplate.setText(I18N.getBtnChoosePath());
        btnDirJobsTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    actionBtnChoosePath(dirJobsTemplateTextField, I18N.getFileChooserDirJobsTemplateTitle(), I18N.getFileChooserDirJobsTemplateMessage(), dirJobsTextField.getText());
                } else {
                    actionBtnChoosePath(dirJobsTemplateTextField, I18N.getFileChooserDirJobsTemplateTitle(), I18N.getFileChooserDirJobsTemplateMessage(), Main.pref.getSingleProperty("DirJobsTemplate"));
                }
                dirProjectsTextField.setFocus();
            }
        });

        btnDirJobsTemplate.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirJobsTemplate.setLayoutData(new GridData());


        // project directory
        Label dirProjectsLabel = new Label(group, SWT.NONE);
        dirProjectsLabel.setText(I18N.getLabelDirProjects());
        dirProjectsLabel.setLayoutData(new GridData());

        dirProjectsTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirProjectsTextField.setText(Main.pref.getSingleProperty("DirProjects"));
        dirProjectsTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        actionBtnChoosePath(dirProjectsTextField, I18N.getFileChooserDirProjectsTitle(), I18N.getFileChooserDirProjectsMessage(), dirBaseTextField.getText());
                    } else {
                        actionBtnChoosePath(dirProjectsTextField, I18N.getFileChooserDirProjectsTitle(), I18N.getFileChooserDirProjectsMessage(), Main.pref.getSingleProperty("DirProjects"));
                    }
                    dirProjectsTemplateTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirProjectsTextField.setLayoutData(gridData);

        Button btnDirProjects = new Button(group, SWT.NONE);
        btnDirProjects.setText(I18N.getBtnChoosePath());
        btnDirProjects.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!Main.pref.isDefaultSettingsGenerated()) {
                    actionBtnChoosePath(dirProjectsTextField, I18N.getFileChooserDirProjectsTitle(), I18N.getFileChooserDirProjectsMessage(), dirBaseTextField.getText());
                } else {
                    actionBtnChoosePath(dirProjectsTextField, I18N.getFileChooserDirProjectsTitle(), I18N.getFileChooserDirProjectsMessage(), Main.pref.getSingleProperty("DirProjects"));
                }
                dirProjectsTemplateTextField.setFocus();
            }
        });

        btnDirProjects.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirProjects.setLayoutData(new GridData());


        // project template directory
        Label dirProjectsTemplateLabel = new Label(group, SWT.NONE);
        dirProjectsTemplateLabel.setText(I18N.getLabelDirProjectsTemplate());
        dirProjectsTemplateLabel.setLayoutData(new GridData());

        dirProjectsTemplateTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        dirProjectsTemplateTextField.setText(Main.pref.getSingleProperty("DirProjectsTemplate"));
        dirProjectsTemplateTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!checkEmptyTextFields()) {

                    if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    if (!Main.pref.isDefaultSettingsGenerated()) {
                        actionBtnChoosePath(dirProjectsTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(), I18N.getFileChooserDirProjectTemplateMessage(), dirProjectsTextField.getText());
                    } else {
                        actionBtnChoosePath(dirProjectsTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(), I18N.getFileChooserDirProjectTemplateMessage(), Main.pref.getSingleProperty("DirProjectsTemplate"));
                    }
                    dirBaseTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        dirProjectsTemplateTextField.setLayoutData(gridData);

        Button btnDirProjectsTemplate = new Button(group, SWT.NONE);
        btnDirProjectsTemplate.setText(I18N.getBtnChoosePath());
        btnDirProjectsTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnChoosePath(dirProjectsTemplateTextField, I18N.getFileChooserDirProjectTemplateTitle(), I18N.getFileChooserDirProjectTemplateMessage(), dirProjectsTextField.getText());
                dirBaseTextField.setFocus();
            }
        });

        btnDirProjectsTemplate.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDirProjectsTemplate.setLayoutData(new GridData());

    }

    /**
     * Creates the group for the settings of the {@code TidyUpWidget} and all its functionality.
     *
     * @param width width of the group
     * @see de.ryanthara.ja.rycon.gui.TidyUpWidget
     */
    private void createGroupTidyUp(int width) {

        GridLayout gridLayout;
        GridData gridData;

        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleTidyUpSettings());

        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        group.setLayout(gridLayout);

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        // free station identifier
        Label freeStationLabel = new Label(group, SWT.NONE);
        freeStationLabel.setText(I18N.getLabelIdentifierFreeStation());

        identifierFreeStationTextField = new Text(group, SWT.BORDER);
        identifierFreeStationTextField.setText(Main.pref.getSingleProperty("ParamFreeStationString"));
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

        // station identifier
        Label stationLabel = new Label(group, SWT.NONE);
        stationLabel.setText(I18N.getLabelIdentifierStation());

        identifierStationTextField = new Text(group, SWT.BORDER);
        identifierStationTextField.setText(Main.pref.getSingleProperty("ParamStationString"));
        identifierStationTextField.addListener(SWT.Traverse, new Listener() {
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
        identifierStationTextField.setLayoutData(gridData);


        // stake out identifier
        Label stakeOutLabel = new Label(group, SWT.NONE);
        stakeOutLabel.setText(I18N.getLabelIdentifierStakeOutPoint());

        identifierControlPointTextField = new Text(group, SWT.BORDER);
        identifierControlPointTextField.setText(Main.pref.getSingleProperty("ParamControlPointString"));
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

    /**
     * Initializes all the gui of the tidy up widget.
     */
    private void initUI() {

        // golden rectangle cut with an aspect ratio of 1.618:1
        int height = Main.getRyCONWidgetHeight();
        int width = Main.getRyCONWidgetWidth();

        innerShell = new Shell(Main.shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);

        innerShell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                actionBtnCancel();
            }
        });

        innerShell.setText(I18N.getWidgetTitleSettingsWidget());
        innerShell.setSize(width, height);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        innerShell.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;
        innerShell.setLayoutData(gridData);


        createGroupPaths(width);

        createGroupTidyUp(width);


        // buttons on bottom
        Composite compositeBtns = new Composite(innerShell, SWT.NONE);
        compositeBtns.setLayout(new FillLayout());

        Button btnCancel = new Button(compositeBtns, SWT.NONE);
        btnCancel.setText(I18N.getBtnCancelLabel());
        btnCancel.setToolTipText(I18N.getBtnCancelLabelToolTip());

        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCancel();
            }
        });

        Button btnOK = new Button(compositeBtns, SWT.NONE);
        btnOK.setText(I18N.getBtnOKAndOpenLabel());
        btnOK.setToolTipText(I18N.getBtnOKAndOpenLabelToolTip());
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnOk();
            }
        });

        gridData = new GridData(SWT.END, SWT.END, false, true);
        compositeBtns.setLayoutData(gridData);


        // set up position
        ShellCenter shellCenter = new ShellCenter(innerShell);
        innerShell.setLocation(shellCenter.centeredShellLocation());

        Main.setSubShellStatus(true);

        innerShell.forceActive();

        innerShell.pack();
        innerShell.open();

    }

    /**
     * Picks the values from the settings widget and initialize the writing to the properties file.
     *
     * @return success of writing
     */
    private boolean writeSettings() {

        Properties properties = Main.pref.getProperties();

        properties.setProperty("DirBase", dirBaseTextField.getText());
        properties.setProperty("DirJobsTemplate", dirJobsTemplateTextField.getText());
        properties.setProperty("DirJobs", dirJobsTextField.getText());
        properties.setProperty("DirProjects", dirProjectsTextField.getText());
        properties.setProperty("DirProjectsTemplate", dirProjectsTemplateTextField.getText());

        properties.setProperty("ParamControlPointString", identifierControlPointTextField.getText());
        properties.setProperty("ParamFreeStationString", identifierFreeStationTextField.getText());
        properties.setProperty("ParamStationString", identifierStationTextField.getText());

        return Main.pref.setProperties(properties);
    }

}
