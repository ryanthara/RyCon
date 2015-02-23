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
import de.ryanthara.ja.rycon.io.LineReader;
import de.ryanthara.ja.rycon.io.LineWriter;
import de.ryanthara.ja.rycon.tools.LeicaGSIFileTools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.util.ArrayList;

/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * With the TidyUpWidget of RyCON it is possible to clean up coordinate and
 * measurement files with a simple 'intelligence'.
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class TidyUpWidget {

    /**
     * Member for the inner Shell object.
     */
    private Shell innerShell = null;

    /**
     * Member for the checkbox to hold station lines.
     */
    private Button chkBoxHoldControlPoints;

    /**
     * Member for the checkbox to hold station lines.
     */
    private Button chkBoxHoldStations;

    /**
     * Member for the destination text field.
     */
    private Text destinationTextField;

    /**
     * Member for the files to read.
     */
    private File[] files2read;

    /**
     * Member for the source text field.
     */
    private Text sourceTextField;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public TidyUpWidget() {
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
     * Does all the things when hitting the button to choose a destination path.
     * <p>
     * Normally the user wants to store the tidied up file in the same directory like
     * the source file was stored. Because of this, the path is set by the source path.
     */
    private void actionBtnDestination() {

        DirectoryDialog directoryDialog = new DirectoryDialog(innerShell);

        directoryDialog.setText(I18N.getFileChooserDirBaseTitle());

        directoryDialog.setMessage(I18N.getFileChooserDirBaseMessage());

        // Set the initial filter path according to anything selected or typed in

        if (destinationTextField.getText() == null) {
            directoryDialog.setFilterPath(Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
        } else {
            directoryDialog.setFilterPath(destinationTextField.getText());
        }

        String path = directoryDialog.open();

        if (path != null) {

            File checkDirDestination = new File(path);
            if (!checkDirDestination.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirDestinationNotExistWarning());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                destinationTextField.setText(path);
            }

        }

    }

    /**
     * Does all the things when hitting the OK button.
     *  
     * @return int value for the 'OK and exit' button handling
     * @since 3
     */
    private int actionBtnOk() {

        String source = sourceTextField.getText();
        String destination = destinationTextField.getText();

        if (source.trim().equals("") || (destination.trim().equals(""))) {
            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
            msgBox.setMessage(I18N.getMsgEmptyTextFieldWarning());
            msgBox.setText(I18N.getMsgBoxTitleWarning());
            msgBox.open();
            
            return 0;
        } else {
            if (processFileOperations()) {

                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    Main.statusBar.setStatus(String.format(I18N.getStatusCleanFileSuccessful(Main.TEXT_SINGULAR), Main.countFileOps), StatusBar.OK);
                } else {
                    Main.statusBar.setStatus(String.format(I18N.getStatusCleanFileSuccessful(Main.TEXT_PLURAL), Main.countFileOps), StatusBar.OK);
                }
            }
            return 1;
        }

    }

    /**
     * Does all the things when hitting the 'OK and exit' button.
     * <p>
     * This button uses the {@code actionBtnOk} method inside.
     */
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

    /**
     * Does all the things when hitting the choose source button.
     */
    private void actionBtnSource() {

        FileDialog fileDialog = new FileDialog(innerShell, SWT.MULTI);
        fileDialog.setFilterPath(Main.pref.getUserPref(PreferenceHandler.DIR_PROJECTS));
        fileDialog.setText(I18N.getFileChooserTidyUpSourceText());
        fileDialog.setFilterExtensions(new String[]{"*.gsi"});
        fileDialog.setFilterNames(new String[]{I18N.getFileChooserFilterNameGSI()});

        String firstFile = fileDialog.open();

        if (firstFile != null) {
            String[] files = fileDialog.getFileNames();

            files2read = new File[files.length];

            // displaying file names without path in text field
            String concatString = "";

            String workingDir = fileDialog.getFilterPath();

            for (int i = 0; i < files.length; i++) {
                concatString = concatString.concat(files[i]);
                concatString = concatString.concat(" ");

                files2read[i] = new File(workingDir + File.separator + files[i]);
            }

            destinationTextField.setText(fileDialog.getFilterPath());
            sourceTextField.setText(concatString);
        }

    }

    /**
     * Creates the group with the input fields and all its functionality.
     *
     * @param width width of the group
     */
    private void createGroupInputFields(int width) {
        GridLayout gridLayout;
        GridData gridData;

        Group groupInputFields = new Group(innerShell, SWT.NONE);
        groupInputFields.setText(I18N.getGroupTitlePathSelection());

        gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        groupInputFields.setLayout(gridLayout);

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        groupInputFields.setLayoutData(gridData);

        Label source = new Label(groupInputFields, SWT.NONE);
        source.setText(I18N.getLabelSource());

        sourceTextField = new Text(groupInputFields, SWT.BORDER);
        sourceTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!(sourceTextField.getText().trim().equals("") || (destinationTextField.getText().trim().equals("")))) {

                    if (((event.stateMask & SWT.SHIFT) == SWT.SHIFT) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    } else if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOkAndExit();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnSource();
                    destinationTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        sourceTextField.setLayoutData(gridData);

        Button btnSource = new Button(groupInputFields, SWT.NONE);
        btnSource.setText(I18N.getBtnChooseFiles());
        btnSource.setToolTipText(I18N.getBtnChooseFilesToolTip());
        btnSource.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnSource();
            }
        });
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnSource.setLayoutData(gridData);

        Label destination = new Label(groupInputFields, SWT.NONE);
        destination.setText(I18N.getLabelDestination());
        destination.setLayoutData(new GridData());

        destinationTextField = new Text(groupInputFields, SWT.SINGLE | SWT.BORDER);
        destinationTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!(sourceTextField.getText().trim().equals("") || (destinationTextField.getText().trim().equals("")))) {

                    if (((event.stateMask & SWT.SHIFT) == SWT.SHIFT) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOk();
                    } else if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        actionBtnOkAndExit();
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnDestination();
                    sourceTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        destinationTextField.setLayoutData(gridData);


        Button btnDestination = new Button(groupInputFields, SWT.NONE);
        btnDestination.setText(I18N.getBtnChoosePath());
        btnDestination.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnDestination();
            }
        });
        btnDestination.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDestination.setLayoutData(new GridData());

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

        innerShell.setText(I18N.getWidgetTitleTidyUp());
        innerShell.setSize(width, height);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        innerShell.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;
        innerShell.setLayoutData(gridData);

        createGroupInputFields(width);

        chkBoxHoldControlPoints = new Button(innerShell, SWT.CHECK);
        chkBoxHoldControlPoints.setSelection(false);
        chkBoxHoldControlPoints.setText(I18N.getBtnChkTidyUpHoldControlPoints());

        chkBoxHoldStations = new Button(innerShell, SWT.CHECK);
        chkBoxHoldStations.setSelection(false);
        chkBoxHoldStations.setText(I18N.getBtnChkTidyUpHoldStations());

        Group groupDescription = new Group(innerShell, SWT.NONE);
        groupDescription.setText(I18N.getGroupTitleNumberInputAdvice());

        gridLayout = new GridLayout(1, true);
        groupDescription.setLayout(gridLayout);

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        groupDescription.setLayoutData(gridData);

        Label tip = new Label(groupDescription, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
        tip.setText(String.format(I18N.getLabelTipTidyUpWidget()));

        Composite compositeBottomBtns = new Composite(innerShell, SWT.NONE);
        compositeBottomBtns.setLayout(new FillLayout());

        Button btnCancel = new Button(compositeBottomBtns, SWT.NONE);
        btnCancel.setText(I18N.getBtnCancelLabel());
        btnCancel.setToolTipText(I18N.getBtnCancelLabelToolTip());

        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCancel();
            }
        });

        Button btnOK = new Button(compositeBottomBtns, SWT.NONE);
        btnOK.setText(I18N.getBtnOKAndOpenLabel());
        btnOK.setToolTipText(I18N.getBtnOKAndOpenLabelToolTip());
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnOk();
            }
        });

        Button btnOKAndExit = new Button(compositeBottomBtns, SWT.NONE);
        btnOKAndExit.setText(I18N.getBtnOKAndExitLabel());
        btnOKAndExit.setToolTipText(I18N.getBtnOKAndExitLabelToolTip());
        btnOKAndExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnOkAndExit();
            }
        });

        gridData = new GridData(SWT.END, SWT.END, false, true);
        compositeBottomBtns.setLayoutData(gridData);


        ShellCenter shellCenter = new ShellCenter(innerShell);
        innerShell.setLocation(shellCenter.centeredShellLocation());

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();

    }

    /**
     * Process all the operations on the chosen tidy up files.
     *
     * @return success
     */
    private boolean processFileOperations() {

        boolean success = false;

        // checks for text field inputs and valid directories
        if ((sourceTextField != null) && (destinationTextField != null)) {

            int counter = 0;

            LineReader lineReader;

            // read files
            for (File file2read : files2read) {
                lineReader = new LineReader(file2read);

                if (lineReader.readFile()) {
                    // read
                    ArrayList<String> readFile = lineReader.getLines();
                    ArrayList<String> writeFile;

                    // processFileOperations
                    LeicaGSIFileTools gsiTools = new LeicaGSIFileTools(readFile);
                    writeFile = gsiTools.processTidyUp(chkBoxHoldStations.getSelection(), chkBoxHoldControlPoints.getSelection());

                    // write file line by line
                    String file2write = file2read.toString().substring(0, file2read.toString().length() - 4) + "_EDIT.GSI";
                    LineWriter lineWriter = new LineWriter(file2write);
                    if (lineWriter.writeFile(writeFile)) {
                        counter++;
                    }

                } else {
                    System.err.println("File " + file2read.getName() + " could not be read.");
                }

            }

            if (counter > 0) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_INFORMATION);

                if (counter == 1) {
                    msgBox.setMessage(String.format(I18N.getMsgTidyUpSuccess(Main.TEXT_SINGULAR), counter));
                } else {
                    msgBox.setMessage(String.format(I18N.getMsgTidyUpSuccess(Main.TEXT_PLURAL), counter));
                }

                msgBox.setText(I18N.getMsgBoxTitleSuccess());
                msgBox.open();

                // set the counter for status bar information
                Main.countFileOps = counter;
                success = true;
            } else {
                success = false;
            }

        }

        return success;

    }

} // end of TidyUpWidget
