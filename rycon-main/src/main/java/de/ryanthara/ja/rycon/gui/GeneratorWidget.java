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
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;

/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * The GeneratorWidget of RyCON is used to generate default paths and subdirectory
 * structure by a given point number.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>3: code improvements and clean up</li>
 *     <li>2: basic improvements
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public class GeneratorWidget {

    private Button chkBoxCreateJobAndProjectFolder;
    private Button chkBoxCreateOnlyJobFolder;
    private Button chkBoxCreateOnlyProjectFolder;
    private Text inputNumber = null;
    private Shell innerShell = null;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public GeneratorWidget() {
        initUI();
    }

    private void initUI() {
        int height = Main.getRyCONWidgetHeight();
        int width = Main.getRyCONWidgetWidth();

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
        innerShell.setText(I18N.getWidgetTitleGenerator());
        innerShell.setSize(width, height);
        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;

        createGroupInputField();
        createGroupOptions(width);
        createDescription(width);
        createBottomButtons();

        innerShell.setLocation(ShellCenter.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private void createGroupInputField() {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleGeneratorNumberInput());

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Main.getRyCONWidgetWidth();
        group.setLayoutData(gridData);

        Label jobAndProjectNumber = new Label(group, SWT.NONE);
        jobAndProjectNumber.setText(I18N.getLabelJobAndProjectNumber());

        inputNumber = new Text(group, SWT.SINGLE | SWT.BORDER);

        // platform independent key handling for ENTER, TAB, ...
        // TODO change bad listener with a better one
        inputNumber.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (!inputNumber.getText().trim().equals("")) {

                    if ((event.stateMask & SWT.SHIFT) == SWT.SHIFT && event.detail == SWT.TRAVERSE_RETURN) {
                        actionBtnOkAndExit();
                    } else if (event.detail == SWT.TRAVERSE_RETURN) {
                        actionBtnOk();
                    }

                }

            }
        });

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        inputNumber.setLayoutData(gridData);
    }

    private void createGroupOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleOptions());

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        SelectionListener selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean isSelected = ((Button) e.getSource()).getSelection();
                if (isSelected) {
                    if (e.getSource().equals(chkBoxCreateJobAndProjectFolder)) {
                        chkBoxCreateOnlyJobFolder.setSelection(false);
                        chkBoxCreateOnlyProjectFolder.setSelection(false);
                    } else if (e.getSource().equals(chkBoxCreateOnlyJobFolder)) {
                        chkBoxCreateJobAndProjectFolder.setSelection(false);
                        chkBoxCreateOnlyProjectFolder.setSelection(false);
                    } else if (e.getSource().equals(chkBoxCreateOnlyProjectFolder)) {
                        chkBoxCreateJobAndProjectFolder.setSelection(false);
                        chkBoxCreateOnlyJobFolder.setSelection(false);
                    }
                }
            }
        };

        chkBoxCreateJobAndProjectFolder = new Button(group, SWT.CHECK);
        chkBoxCreateJobAndProjectFolder.setSelection(true);
        chkBoxCreateJobAndProjectFolder.setText(I18N.getBtnChkBoxCreateJobAndProjectFolder());
        chkBoxCreateJobAndProjectFolder.addSelectionListener(selectionListener);

        chkBoxCreateOnlyJobFolder = new Button(group, SWT.CHECK);
        chkBoxCreateOnlyJobFolder.setSelection(false);
        chkBoxCreateOnlyJobFolder.setText(I18N.getBtnChkBoxCreateOnlyJobFolder());
        chkBoxCreateOnlyJobFolder.addSelectionListener(selectionListener);

        chkBoxCreateOnlyProjectFolder = new Button(group, SWT.CHECK);
        chkBoxCreateOnlyProjectFolder.setSelection(false);
        chkBoxCreateOnlyProjectFolder.setText(I18N.getBtnChkBoxCreateOnlyProjectFolder());
        chkBoxCreateOnlyProjectFolder.addSelectionListener(selectionListener);
    }

    private void createDescription(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleGeneratorNumberInputAdvice());

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setText(I18N.getLabelTipGeneratorWidget());
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createBottomButtons() {
        Composite composite = new Composite(innerShell, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);


        Composite compositeLeft = new Composite(composite, SWT.NONE);
        compositeLeft.setLayout(new FillLayout());

        Button btnSettings = new Button(compositeLeft, SWT.NONE);
        btnSettings.setText(I18N.getBtnSettingsLabel());
        btnSettings.setToolTipText(I18N.getBtnSettingsLabelToolTip());
        btnSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnSettings();
            }
        });

        Label blindTextAsPlaceHolder = new Label(compositeLeft, SWT.NONE);


        Composite compositeRight = new Composite(composite, SWT.NONE);
        compositeRight.setLayout(new FillLayout());

        Button btnCancel = new Button(compositeRight, SWT.NONE);
        btnCancel.setText(I18N.getBtnCancelLabel());
        btnCancel.setToolTipText(I18N.getBtnCancelLabelToolTip());
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCancel();
            }
        });

        Button btnOK = new Button(compositeRight, SWT.NONE);
        btnOK.setText(I18N.getBtnOKAndOpenLabel());
        btnOK.setToolTipText(I18N.getBtnOKAndOpenLabelToolTip());
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnOk();
            }
        });

        Button btnOKAndExit = new Button(compositeRight, SWT.NONE);
        btnOKAndExit.setText(I18N.getBtnOKAndExitLabel());
        btnOKAndExit.setToolTipText(I18N.getBtnOKAndExitLabelToolTip());
        btnOKAndExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnOkAndExit();
            }
        });
    }

    private void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", StatusBar.OK);
        innerShell.dispose();
    }

    private int actionBtnOk() {
        String number = inputNumber.getText();

        if (number.trim().equals("")) {
            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
            msgBox.setMessage(I18N.getMsgEmptyTextFieldWarning());
            msgBox.setText(I18N.getMsgBoxTitleWarning());
            msgBox.open();

            return 0;
        } else {
            if (generateFolders(number)) {
                Main.statusBar.setStatus(String.format(I18N.getStatusJobAndProjectGenerated(), number, number), StatusBar.OK);
            }

            return 1;
        }
    }

    private void actionBtnOkAndExit() {
        switch (actionBtnOk()) {
            case 0:

                break;
            case 1:
                Main.setSubShellStatus(false);
                Main.statusBar.setStatus("", StatusBar.OK);

                innerShell.dispose();
                break;
        }
    }

    private void actionBtnSettings() {
        new GeneratorSettingsWidget(innerShell);
    }

    private boolean generateFolders(String number) {
        boolean success = false;

        if (chkBoxCreateJobAndProjectFolder.getSelection()) {
            boolean jobOK = generateJobFolder(number);
            boolean projectOK = generateProjectFolder(number);

            if (jobOK && projectOK) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(String.format(I18N.getMsgCreateDirJobAndProjectGenerated(), number, number));
                msgBox.setText(I18N.getMsgBoxTitleSuccess());
                msgBox.open();

                success = true;
            } else {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                msgBox.setMessage(I18N.getMsgCreateDirJobAndProjectWarning());
                msgBox.setText(I18N.getMsgBoxTitleError());
                msgBox.open();
            }
        } else if (chkBoxCreateOnlyJobFolder.getSelection()) {
            success = generateJobFolder(number);
        } else if (chkBoxCreateOnlyProjectFolder.getSelection()) {
            success = generateProjectFolder(number);
        }

        return success;
    }

    private boolean generateJobFolder(String number) {
        boolean success = false;

        String jobDir = Main.pref.getUserPref(PreferenceHandler.DIR_JOBS);
        String jobDirTemplate = Main.pref.getUserPref(PreferenceHandler.DIR_JOBS_TEMPLATE);

        File newJobDir = new File(jobDir + File.separator + number);

        if (newJobDir.exists()) {
            Main.statusBar.setStatus("", StatusBar.OK);
            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
            msgBox.setMessage(String.format(I18N.getMsgCreateDirJobExist(), number));
            msgBox.setText(I18N.getMsgBoxTitleWarning());
            msgBox.open();
        } else {
            /* maybe later on with java 8 support in the office
            Path copySourcePathJob = Paths.get(jobDirTemplate);
            Path copySourcePathProject = Paths.get(projectDirTemplate);

            Path copyDestinationPathJob = Paths.get(jobDir + File.separator + number);
            Path copyDestinationPathProject = Paths.get(projectDir + File.separator + number);
            */

            File copySourcePathJob = new File(jobDirTemplate);
            File copyDestinationPathJob = new File(jobDir + File.separator + number);

            try {
                //Files.copy(copySourcePathJob, copyDestinationPathJob);
                //Files.copy(copySourcePathProject, copyDestinationPathProject);

                FileUtils fileUtils = new FileUtils();
                fileUtils.copy(copySourcePathJob, copyDestinationPathJob);

                success = true;
            } catch (IOException e) {
                System.err.println(e.getMessage());

                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                msgBox.setMessage(I18N.getMsgCreateDirJobWarning());
                msgBox.setText(I18N.getMsgBoxTitleError());
                msgBox.open();
                success = false;
            }
        }
        return success;
    }

    private boolean generateProjectFolder(String number) {
        boolean success = false;

        String projectDir = Main.pref.getUserPref(PreferenceHandler.DIR_PROJECTS);
        String projectDirTemplate = Main.pref.getUserPref(PreferenceHandler.DIR_PROJECTS_TEMPLATE);

        File newProjectDir = new File(projectDir + File.separator + number);

        if (newProjectDir.exists()) {
            Main.statusBar.setStatus("", StatusBar.OK);
            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
            msgBox.setMessage(String.format(I18N.getMsgCreateDirProjectExist(), number));
            msgBox.setText(I18N.getMsgBoxTitleWarning());
            msgBox.open();
        } else {
            /* maybe later on with java 8 support in the office
            Path copySourcePathJob = Paths.get(jobDirTemplate);
            Path copySourcePathProject = Paths.get(projectDirTemplate);

            Path copyDestinationPathJob = Paths.get(jobDir + File.separator + number);
            Path copyDestinationPathProject = Paths.get(projectDir + File.separator + number);
            */

            File copySourcePathProject = new File(projectDirTemplate);
            File copyDestinationPathProject = new File(projectDir + File.separator + number);

            try {
                //Files.copy(copySourcePathJob, copyDestinationPathJob);
                //Files.copy(copySourcePathProject, copyDestinationPathProject);

                FileUtils fileUtils = new FileUtils();
                fileUtils.copy(copySourcePathProject, copyDestinationPathProject);

                success = true;
            } catch (IOException e) {
                System.err.println(e.getMessage());

                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                msgBox.setMessage(I18N.getMsgCreateDirProjectWarning());
                msgBox.setText(I18N.getMsgBoxTitleError());
                msgBox.open();
                success = false;
            }

        }
        return success;
    }

}  // end of GeneratorWidget
