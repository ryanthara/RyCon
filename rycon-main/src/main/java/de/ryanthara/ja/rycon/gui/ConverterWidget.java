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

import com.opencsv.CSVReader;
import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.io.LineReader;
import de.ryanthara.ja.rycon.io.LineWriter;
import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.tools.LeicaGSIFileTools;
import de.ryanthara.ja.rycon.tools.TextFileTools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * The ConverterWidget of RyCON is used to convert measurement and coordinate
 * files into different formats. RyCON can be used to convert special formats
 * e.g. coordinate files from governmental services in switzerland
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class ConverterWidget {

    /**
     * Member for the inner Shell object.
     */
    private Shell innerShell = null;

    /**
     * Member for the checkbox to hold station lines.
     */
    private Button chkBoxTXTSpaceDelimiter;

    /**
     * Member for the checkbox to hold station lines.
     */
    private Button chkBoxCSVSemiColonDelimiter;

    /**
     * Member for the checkbox to write first comment line.
     */
    private Button chkBoxWriteCommentLine;

    /**
     * Member for the destination text field.
     */
    private Text destinationTextField = null;

    /**
     * Member for the files to read.
     */
    private File[] files2read;

    /**
     * Member for a group of radio buttons for the source system.
     */
    private Group groupSource;

    /**
     * Member for a group of radio buttons for the target system.
     */
    private Group groupTarget;

    /**
     * Member for the source text field.
     */
    private Text sourceTextField = null;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public ConverterWidget() {
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
     * Normally the user wants to store the converted file in the same directory like
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
     */
    private void actionBtnOk() {

        // important to prevent of 'Exception in thread "main" org.eclipse.swt.SWTException: Widget is disposed'
        if (!innerShell.isDisposed()) {

            String source = sourceTextField.getText();
            String destination = destinationTextField.getText();

            if (source.trim().equals("") || (destination.trim().equals(""))) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgEmptyTextFieldWarning());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                if (processFileOperations()) {

                    // use counter to display different text on the status bar
                    if (Main.countFileOps == 1) {
                        Main.statusBar.setStatus(String.format(I18N.getStatusConvertSuccess(Main.TEXT_SINGULAR), Main.countFileOps), StatusBar.OK);
                    } else {
                        Main.statusBar.setStatus(String.format(I18N.getStatusConvertSuccess(Main.TEXT_PLURAL), Main.countFileOps), StatusBar.OK);
                    }

                }
            }

        }

    }

    /**
     * Does all the things when hitting the OK and exit button.
     */
    private void actionBtnOkAndExit() {
        Main.setSubShellStatus(false);

        actionBtnOk();

        Main.statusBar.setStatus("", StatusBar.OK);

        innerShell.dispose();
    }

    /**
     * Does all the things when hitting the choose source button.
     */
    private void actionBtnSource() {

        FileDialog fileDialog = new FileDialog(innerShell, SWT.MULTI);
        fileDialog.setFilterPath(Main.pref.getUserPref(PreferenceHandler.DIR_PROJECTS));
        fileDialog.setText(I18N.getFileChooserTidyUpSourceText());
        fileDialog.setFilterExtensions(new String[]{"*.gsi", "*.txt", "*.csv"});
        fileDialog.setFilterNames(new String[]{I18N.getFileChooserFilterNameGSI(), I18N.getFileChooserFilterNameTXT(),
                I18N.getFileChooserFilterNameCSV()});

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

            // set the radio buttons
            Control[] childrenSource = groupSource.getChildren();
            Control[] childrenTarget = groupTarget.getChildren();

            switch (fileDialog.getFilterIndex()) {

                case 0:
                    RadioHelper.selectBtn(childrenSource, 1);
                    RadioHelper.selectBtn(childrenTarget, 2);
                    break;

                case 1:
                    RadioHelper.selectBtn(childrenSource, 2);
                    RadioHelper.selectBtn(childrenTarget, 1);
                    break;

                case 2:
                    RadioHelper.selectBtn(childrenSource, 3);
                    RadioHelper.selectBtn(childrenTarget, 2);
                    break;

            }

        }

    }

    /**
     * Creates the composite with the radio buttons for source and target.
     */
    private void createCompositeSourceTarget() {

        Composite compositeSourceTarget = new Composite(innerShell, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, true);
        compositeSourceTarget.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeSourceTarget.setLayoutData(gridData);

        groupSource = new Group(compositeSourceTarget, SWT.NONE);
        groupSource.setText(I18N.getGroupTitleSourceFileFormat());
        groupSource.setLayout(new GridLayout(1, false));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupSource.setLayoutData(gridData);

        groupTarget = new Group(compositeSourceTarget, SWT.NONE);
        groupTarget.setText(I18N.getGroupTitleTargetFileFormat());
        groupTarget.setLayout(new GridLayout(1, false));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupTarget.setLayoutData(gridData);


        SelectionListener selectionListenerSource = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // control of double fired events
                boolean isSelected = ((Button) e.getSource()).getSelection();
                if (isSelected) {

                    Control[] childrenSource = groupSource.getChildren();
                    Control[] childrenTarget = groupTarget.getChildren();

                    RadioHelper.toggleBtn(childrenSource, childrenTarget);

                }
            }
        };

        SelectionListener selectionListenerTarget = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // control of double fired events
                boolean isSelected = ((Button) e.getSource()).getSelection();
                if (isSelected) {

                    Control[] childrenSource = groupSource.getChildren();
                    Control[] childrenTarget = groupTarget.getChildren();

                    RadioHelper.toggleBtn(childrenTarget, childrenSource);

                }

            }
        };

        String[] formatSource = {"GSI8", "GSI16", "TXT", "CSV", "Basel Stadt CSV"};
        String[] formatTarget = {"GSI8", "GSI16", "TXT", "CSV"};

        // radio buttons for the source formats
        for (int i = 0; i < formatSource.length; i++) {
            Button button = new Button(groupSource, SWT.RADIO);
            button.addSelectionListener(selectionListenerSource);
            button.setText(formatSource[i]);

            if (i == 1) {
                button.setSelection(true);
            }
        }

        // radio buttons for the target formats
        for (int i = 0; i < formatTarget.length; i++) {
            Button button = new Button(groupTarget, SWT.RADIO);
            button.addSelectionListener(selectionListenerTarget);
            button.setText(formatTarget[i]);

            if (i == 0) {
                button.setSelection(true);
            }
        }

    }

    /**
     * Creates the group with the input fields and all its functionality.
     *
     * @param width width of the group
     */
    private void createGroupInputFields(int width) {
        Group groupInputFields = new Group(innerShell, SWT.NONE);
        groupInputFields.setText(I18N.getGroupTitlePathSelection());

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        groupInputFields.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
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

        innerShell.setText(I18N.getWidgetTitleConverter());
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

        // composite for the two columns for the source and target groups
        createCompositeSourceTarget();

        chkBoxTXTSpaceDelimiter = new Button(innerShell, SWT.CHECK);
        chkBoxTXTSpaceDelimiter.setSelection(false);
        chkBoxTXTSpaceDelimiter.setText(I18N.getBtnChkConverterTXTSpaceDelimiter());

        chkBoxCSVSemiColonDelimiter = new Button(innerShell, SWT.CHECK);
        chkBoxCSVSemiColonDelimiter.setSelection(false);
        chkBoxCSVSemiColonDelimiter.setText(I18N.getBtnChkConverterCSVSemiColonDelimiter());

        chkBoxWriteCommentLine = new Button(innerShell, SWT.CHECK);
        chkBoxWriteCommentLine.setSelection(false);
        chkBoxWriteCommentLine.setText(I18N.getBtnChkConverterWriteCommentLine());

        // description for the tidy up field as text on a label
        Group groupDescription = new Group(innerShell, SWT.NONE);
        groupDescription.setText(I18N.getGroupTitleNumberInputAdvice());

        gridLayout = new GridLayout(1, true);
        groupDescription.setLayout(gridLayout);

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        groupDescription.setLayoutData(gridData);

        Label tip = new Label(groupDescription, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
        tip.setText(String.format(I18N.getLabelTipConverterWidget()));

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
        if ((sourceTextField.getText() != null) && (destinationTextField.getText() != null)) {

            int sourceNumber = RadioHelper.getSelectedBtn(groupSource.getChildren());
            int targetNumber = RadioHelper.getSelectedBtn(groupTarget.getChildren());

            boolean GSIFormat;

            if (sourceNumber == 0) {
                GSIFormat = Main.getGSI8();
            } else {
                GSIFormat = Main.getGSI16();
            }

            int counter = 0;

            for (File file2read : files2read) {

                boolean readFileSuccess = false;

                LineReader lineReader;

                List<String[]> readCSVFile = null;
                ArrayList<String> readFile = null;
                ArrayList<String> writeFile;

                LeicaGSIFileTools gsiTools;
                TextFileTools textFileTools;

                String separator;

                // read files
                switch (sourceNumber) {

                    case 0:     // fall through for GSI8 format

                    case 1:     // fall through GSI16 format

                    case 2:     // TXT format (tabulator or space separated)

                        lineReader = new LineReader(file2read);
                        if (lineReader.readFile()) {
                            readFile = lineReader.getLines();
                            readFileSuccess = true;
                        } else {
                            System.err.println("File " + file2read.getName() + " could not be read.");

                            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                            msgBox.setMessage(I18N.getMsgConvertReaderFailed());
                            msgBox.setText(I18N.getMsgBoxTitleError());
                            msgBox.open();

                        }

                        break;

                    case 3:     // CSV format (comma or semicolon separated)

                        char delimiter = ',';
                        if (chkBoxCSVSemiColonDelimiter.getSelection()) {
                            delimiter = ';';
                        }

                        // use opencsv project for reading -> could be done better?
                        try {
                            CSVReader reader = new CSVReader(new FileReader(file2read), delimiter);
                            readCSVFile = reader.readAll();
                            readFileSuccess = true;
                        } catch (IOException e) {

                            System.err.println("File " + file2read.getName() + " could not be read.");

                            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                            msgBox.setMessage(I18N.getMsgConvertReaderCSVFailed());
                            msgBox.setText(I18N.getMsgBoxTitleError());
                            msgBox.open();

                        }

                        break;

                    case 4:     // CSV format from the geo data server 'Basel Stadt' (http://shop.geo.bs.ch/geoshop_app/geoshop/)

                        // use opencsv project for reading -> could be done better?
                        try {
                            CSVReader reader = new CSVReader(new FileReader(file2read), ';', '"', 1); // skip first line
                            readCSVFile = reader.readAll();
                            readFileSuccess = true;
                        } catch (IOException e) {

                            System.err.println("File " + file2read.getName() + " could not be read.");

                            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                            msgBox.setMessage(I18N.getMsgConvertReaderCSVFailed());
                            msgBox.setText(I18N.getMsgBoxTitleError());
                            msgBox.open();

                        }

                        break;

                }

                if (readFileSuccess) {
                    // write files
                    switch (targetNumber) {

                        case 0:     // GSI8 format

                            switch (sourceNumber) {

                                case 0:     // GSI8 format (not possible)

                                    break;

                                case 1:     // GSI16 format

                                    // process file operations
                                    gsiTools = new LeicaGSIFileTools(readFile);
                                    writeFile = gsiTools.processFormatConversionBetweenGSI8AndGSI16(Main.getGSI8());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                                case 2:     // TXT format (tabulator separated)

                                    gsiTools = new LeicaGSIFileTools(readFile);
                                    writeFile = gsiTools.processFormatConversionTXT2GSI(Main.getGSI8());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                                case 3:     // CSV format (comma or semicolon separated)

                                    gsiTools = new LeicaGSIFileTools(readCSVFile);
                                    writeFile = gsiTools.processFormatConversionCSV2GSI(Main.getGSI8());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                                case 4:     // CSV format 'Basel Stadt' (semicolon separated)

                                    gsiTools = new LeicaGSIFileTools(readCSVFile);
                                    writeFile = gsiTools.processFormatConversionCSVBaselStadt2GSI(Main.getGSI8());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                            }

                            break;

                        case 1:     // GSI16 format

                            switch (sourceNumber) {

                                case 0:     // GSI8 format

                                    // process file operations
                                    gsiTools = new LeicaGSIFileTools(readFile);
                                    writeFile = gsiTools.processFormatConversionBetweenGSI8AndGSI16(Main.getGSI16());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                                case 1:     // GSI16 format (not possible)

                                    break;

                                case 2:     // TXT format (space or tabulator separated)

                                    gsiTools = new LeicaGSIFileTools(readFile);
                                    writeFile = gsiTools.processFormatConversionTXT2GSI(Main.getGSI16());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                                case 3:     // CSV format (comma or semicolon separated)

                                    gsiTools = new LeicaGSIFileTools(readCSVFile);
                                    writeFile = gsiTools.processFormatConversionCSV2GSI(Main.getGSI16());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                                case 4:     // CSV format 'Basel Stadt' (semicolon separated)

                                    gsiTools = new LeicaGSIFileTools(readCSVFile);
                                    writeFile = gsiTools.processFormatConversionCSVBaselStadt2GSI(Main.getGSI16());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".GSI")) {
                                        counter++;
                                    }

                                    break;

                            }

                            break;

                        case 2:     // TXT format (space or tabulator separated)

                            switch (sourceNumber) {

                                case 0:     // fall through for GSI8 format

                                case 1:     // GSI16 format

                                    // get separator sign
                                    if (chkBoxTXTSpaceDelimiter.getSelection()) {
                                        separator = Main.getDelimiterSpace();
                                    } else {
                                        separator = Main.getDelimiterTab();
                                    }

                                    // process file operations
                                    gsiTools = new LeicaGSIFileTools(readFile);
                                    writeFile = gsiTools.processFormatConversionGSI2TXT(separator, GSIFormat, chkBoxWriteCommentLine.getSelection());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".TXT")) {
                                        counter++;
                                    }

                                    break;

                                case 2:     // TXT format (not possible)

                                    break;

                                case 3:     // CSV format (comma or semicolon separated)

                                    // get separator sign
                                    if (chkBoxTXTSpaceDelimiter.getSelection()) {
                                        separator = Main.getDelimiterSpace();
                                    } else {
                                        separator = Main.getDelimiterTab();
                                    }

                                    // process file operations
                                    textFileTools = new TextFileTools(readCSVFile);
                                    writeFile = textFileTools.processConversionCSV2TXT(separator);

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".TXT")) {
                                        counter++;
                                    }

                                    break;

                                case 4:     // CSV format 'Basel Stadt' (semicolon separated)

                                    // get separator sign
                                    if (chkBoxTXTSpaceDelimiter.getSelection()) {
                                        separator = Main.getDelimiterSpace();
                                    } else {
                                        separator = Main.getDelimiterTab();
                                    }

                                    textFileTools = new TextFileTools(readCSVFile);
                                    writeFile = textFileTools.processFormatConversionCSVBaselStadt2TXT(separator);

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".TXT")) {
                                        counter++;
                                    }

                                    break;

                            }

                            break;

                        case 3:     // CSV format (comma or semicolon separated)

                            switch (sourceNumber) {

                                case 0:     // fall through for GSI8 format

                                case 1:     // GSI16 format

                                    // get separator sign
                                    if (chkBoxCSVSemiColonDelimiter.getSelection()) {
                                        separator = Main.getDelimiterSemicolon();
                                    } else {
                                        separator = Main.getDelimiterComma();
                                    }

                                    // process file operations
                                    gsiTools = new LeicaGSIFileTools(readFile);
                                    writeFile = gsiTools.processFormatConversionGSI2CSV(separator, chkBoxWriteCommentLine.getSelection());

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".CSV")) {
                                        counter++;
                                    }

                                    break;

                                case 2:     // TXT format (space or tabulator separated)

                                    // get separator sign
                                    if (chkBoxCSVSemiColonDelimiter.getSelection()) {
                                        separator = Main.getDelimiterSemicolon();
                                    } else {
                                        separator = Main.getDelimiterComma();
                                    }

                                    // process file operations
                                    textFileTools = new TextFileTools(readFile);
                                    writeFile = textFileTools.processConversionTXT2CSV(separator);

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".CSV")) {
                                        counter++;
                                    }

                                    break;

                                case 3:     // CSV format (not possible)

                                    break;

                                case 4:     // CSV format 'Basel Stadt' (semicolon separated)

                                    // get separator sign
                                    if (chkBoxCSVSemiColonDelimiter.getSelection()) {
                                        separator = Main.getDelimiterSemicolon();
                                    } else {
                                        separator = Main.getDelimiterComma();
                                    }

                                    textFileTools = new TextFileTools(readCSVFile);
                                    writeFile = textFileTools.processFormatConversionCSVBaselStadt2TXT(separator);

                                    // write file line by line
                                    if (writeFile(file2read, writeFile, ".CSV")) {
                                        counter++;
                                    }

                                    break;

                            }

                            break;

                    }

                }

            }

            if (counter > 0) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_INFORMATION);
                if (counter == 1) {
                    msgBox.setMessage(String.format(I18N.getMsgConvertSuccess(Main.TEXT_SINGULAR), counter));
                } else {
                    msgBox.setMessage(String.format(I18N.getMsgConvertSuccess(Main.TEXT_PLURAL), counter));
                }
                msgBox.setText(I18N.getMsgBoxTitleSuccess());
                msgBox.open();

                // set the counter for status bar information
                Main.countFileOps = counter;
                success = true;
            } else {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_ERROR);
                msgBox.setMessage(I18N.getMsgConvertFailed());
                msgBox.setText(I18N.getMsgBoxTitleError());
                msgBox.open();

                success = false;
            }

        }

        return success;

    }

    /**
     * Helper method for simplify code. The file writing is controlled from here.
     *
     * @param file2read file object for name generation
     * @param writeFile {@code ArrayList<String>} with the lines to be written
     * @param suffix    file suffix
     * @return success of file writing, false when failed
     */
    private boolean writeFile(File file2read, ArrayList<String> writeFile, String suffix) {

        String fileName = file2read.toString().substring(0, file2read.toString().length() - 4) + "_CONV" + suffix;

        // see if file exists
        File file = new File(fileName);

        if (file.exists()) {
            MessageBox mb = new MessageBox(innerShell, SWT.ICON_WARNING | SWT.YES | SWT.NO);

            mb.setMessage(String.format(I18N.getMsgFileExist(), fileName));

            if (mb.open() == SWT.YES) {
                LineWriter lineWriter = new LineWriter(fileName);

                return lineWriter.writeFile(writeFile);
            } else {
                return false;
            }

        } else {
            return new LineWriter(fileName).writeFile(writeFile);
        }

    }

} // end of ConverterWidget
