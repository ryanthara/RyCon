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

import com.opencsv.CSVReader;
import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.io.LineReader;
import de.ryanthara.ja.rycon.io.LineWriter;
import de.ryanthara.ja.rycon.tools.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
 * <h3>Changes:</h3>
 * <ul>
 *     <li>8: code optimizations, little corrections</li>
 *     <li>7: defeat txt to gsi conversion bug (not listed)</li>
 *     <li>6: implement support for cadwork-files (node.dat) </li>
 *     <li>5: defeat bug #3 </li>
 *     <li>4: simplification and improvements, extract input fields and bottom button bar into separate classes </li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class ConverterWidget {

    private Button chkBoxCadworkUseZeroHeights;
    private Button chkBoxCSVSemiColonSeparator;
    private Button chkBoxKFormatUseSimpleFormat;
    private Button chkBoxSourceContainsCode;
    private Button chkBoxTXTSpaceSeparator;
    private Button chkBoxWriteCodeColumn;
    private Button chkBoxWriteCommentLine;
    private File[] files2read = new File[0];
    private Group groupSource;
    private Group groupTarget;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell = null;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    ConverterWidget() {
        initUI();
    }

    private void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", StatusBar.OK);
        innerShell.dispose();
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnDestination() {
        String filterPath = Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT);

        Text input = inputFieldsComposite.getDestinationTextField();

        // Set the initial filter path according to anything selected or typed in
        if (!TextHelper.checkIsEmpty(input)) {
            if (TextHelper.checkIfDirExists(input)) {
                filterPath = input.getText();
            }
        }

        GuiHelper.showAdvancedDirectoryDialog(innerShell, input, I18N.getFileChooserConverterSourceText(),
                I18N.getFileChooserConverterSourceMessage(), filterPath);
    }

    private int actionBtnOk() {
        if (inputFieldsComposite.getSourceTextField().getText().equals("") &
                inputFieldsComposite.getDestinationTextField().getText().equals("")) {
            return -1;
        }

        if (files2read.length == 0) {
            files2read = new File[1];
            files2read[0] = new File(inputFieldsComposite.getSourceTextField().getText());
        } else {
            files2read = WidgetHelper.checkSourceAndDestinationTextFields(
                    inputFieldsComposite.getSourceTextField(),
                    inputFieldsComposite.getDestinationTextField(), files2read);
        }
        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperations()) {
                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    Main.statusBar.setStatus(String.format(I18N.getStatusConvertSuccess(Main.TEXT_SINGULAR), Main.countFileOps), StatusBar.OK);
                } else {
                    Main.statusBar.setStatus(String.format(I18N.getStatusConvertSuccess(Main.TEXT_PLURAL), Main.countFileOps), StatusBar.OK);
                }
            }
            return 1;
        }
        return 0;
    }

    /*
     * This method is used from the class BottomButtonBar!
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

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnSource() {
        FileDialog fileDialog = new FileDialog(innerShell, SWT.MULTI);

        String filterPath = Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT);

        // Set the initial filter path according to anything pasted or typed in
        if (!inputFieldsComposite.getSourceTextField().getText().trim().equals("")) {
            File sourceFile = new File(inputFieldsComposite.getSourceTextField().getText());
            if (sourceFile.isDirectory()) {
                filterPath = inputFieldsComposite.getSourceTextField().getText();
            } else if (sourceFile.isFile()) {
                inputFieldsComposite.setDestinationTextFieldText(sourceFile.getName());
            }
        }

        fileDialog.setFilterPath(filterPath);
        fileDialog.setText(I18N.getFileChooserTidyUpSourceText());
        fileDialog.setFilterExtensions(new String[]{"*.gsi", "*.txt", "*.csv", "*.K", "*.dat"});
        fileDialog.setFilterNames(new String[]{
                I18N.getFileChooserFilterNameGSI(),
                I18N.getFileChooserFilterNameTXT(),
                I18N.getFileChooserFilterNameCSV(),
                I18N.getFileChooserFilterNameK(),
                I18N.getFileChooserFilterNameCadwork()});

        // determine filter index by source group selection
        int selectedBtnSource = RadioHelper.getSelectedBtn(groupSource.getChildren());
        switch (selectedBtnSource) {
            case 0:
            case 1:
                fileDialog.setFilterIndex(0);   // gsi
                break;
            case 2:
                fileDialog.setFilterIndex(1);   // txt
                break;
            case 3:
                fileDialog.setFilterIndex(2);   // CSV
                break;
            case 4:
                fileDialog.setFilterIndex(3);   // K
                break;
            case 5:
                fileDialog.setFilterIndex(4);   // dat
                break;
            case 6:
                fileDialog.setFilterIndex(2);   // CSV Basel Stadt
                break;
            case 7:
                fileDialog.setFilterIndex(1);   // TXT Basel Landschaft
                break;
        }

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

            inputFieldsComposite.getDestinationTextField().setText(fileDialog.getFilterPath());
            inputFieldsComposite.getSourceTextField().setText(concatString);

            // set the radio buttons
            Control[] childrenSource = groupSource.getChildren();
            Control[] childrenTarget = groupTarget.getChildren();

            switch (fileDialog.getFilterIndex()) {
                case 0: // Leica GSI files
                    RadioHelper.selectBtn(childrenSource, 1);
                    if (RadioHelper.getSelectedBtn(childrenTarget) == 1) {
                        RadioHelper.selectBtn(childrenTarget, 2);
                    }
                    break;
                case 1: // txt files
                    // prevent button change for geodata Basel Landschaft files
                    if (RadioHelper.getSelectedBtn(childrenSource) != 7) {
                        RadioHelper.selectBtn(childrenSource, 2);
                    }
                    if (RadioHelper.getSelectedBtn(childrenTarget) == 2) {
                        RadioHelper.selectBtn(childrenTarget, 1);
                    }
                    break;
                case 2: // CSV files
                    // prevent button change for geodata Basel Stadt files
                    if (RadioHelper.getSelectedBtn(childrenSource) != 6) {
                        RadioHelper.selectBtn(childrenSource, 3);
                    }
                    if (RadioHelper.getSelectedBtn(childrenTarget) == 3) {
                        RadioHelper.selectBtn(childrenTarget, 1);
                    }
                    break;
                case 3: // CAPLAN K files
                    // prevent button change for CAPLAN K files
                    if (RadioHelper.getSelectedBtn(childrenSource) != 4) {
                        RadioHelper.selectBtn(childrenSource, 4);
                    }
                    if (RadioHelper.getSelectedBtn(childrenTarget) == 4) {
                        RadioHelper.selectBtn(childrenTarget, 1);
                    }
                    break;
                case 4: // node.dat files
                    // prevent button change for node.dat (cadwork) files
                    if (RadioHelper.getSelectedBtn(childrenSource) != 5) {
                        RadioHelper.selectBtn(childrenSource, 5);
                    }
                    break;
            }
        }
    }

    private void createCompositeSourceTarget() {
        Composite compositeSourceTarget = new Composite(innerShell, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        compositeSourceTarget.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeSourceTarget.setLayoutData(gridData);

        groupSource = new Group(compositeSourceTarget, SWT.NONE);
        groupSource.setText(I18N.getGroupTitleSourceFileFormat());
        groupSource.setLayout(new GridLayout(2, false));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupSource.setLayoutData(gridData);

        groupTarget = new Group(compositeSourceTarget, SWT.NONE);
        groupTarget.setText(I18N.getGroupTitleTargetFileFormat());
        groupTarget.setLayout(new GridLayout(2, false));

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

        String[] formatSource = {
                "GSI8", "GSI16", "TXT", "CSV", "CAPLAN (.K)", "cadwork (node.dat)",
                "Basel Stadt (.CSV)", "Basel Landschaft (.TXT)"
        };

        String[] formatTarget = {
                "GSI8", "GSI16", "TXT", "CSV", "CAPLAN (.K)", "LTOP (.KOO)", "Excel 2007 (.xlsx)","Excel '97 (.xls)"
        };

        for (int i = 0; i < formatSource.length; i++) {
            Button btnSourceFormats = new Button(groupSource, SWT.RADIO);
            btnSourceFormats.addSelectionListener(selectionListenerSource);
            btnSourceFormats.setText(formatSource[i]);

            if (i == 1) {
                btnSourceFormats.setSelection(true);
            }
        }

        for (int i = 0; i < formatTarget.length; i++) {
            Button btnTargetFormats = new Button(groupTarget, SWT.RADIO);
            btnTargetFormats.addSelectionListener(selectionListenerTarget);
            btnTargetFormats.setText(formatTarget[i]);

            if (i == 2) {
                btnTargetFormats.setSelection(true);
            }
        }
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
        tip.setText(I18N.getLabelTipConverterWidget());
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleOptions());

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        chkBoxSourceContainsCode = new Button(group, SWT.CHECK);
        chkBoxSourceContainsCode.setSelection(false);
        chkBoxSourceContainsCode.setText(I18N.getBtnChkBoxSourceContainsCode());

        chkBoxTXTSpaceSeparator = new Button(group, SWT.CHECK);
        chkBoxTXTSpaceSeparator.setSelection(false);
        chkBoxTXTSpaceSeparator.setText(I18N.getBtnChkConverterTXTSpaceSeparator());

        chkBoxCSVSemiColonSeparator = new Button(group, SWT.CHECK);
        chkBoxCSVSemiColonSeparator.setSelection(false);
        chkBoxCSVSemiColonSeparator.setText(I18N.getBtnChkConverterCSVSemiColonSeparator());

        chkBoxCadworkUseZeroHeights = new Button(group, SWT.CHECK);
        chkBoxCadworkUseZeroHeights.setSelection(false);
        chkBoxCadworkUseZeroHeights.setText(I18N.getBtnChkBoxCadworkUseZeroHeights());

        chkBoxKFormatUseSimpleFormat = new Button(group, SWT.CHECK);
        chkBoxKFormatUseSimpleFormat.setSelection(true);
        chkBoxKFormatUseSimpleFormat.setText(I18N.getBtnChkBoxKFormatUseSimpleFormat());

        chkBoxWriteCommentLine = new Button(group, SWT.CHECK);
        chkBoxWriteCommentLine.setSelection(false);
        chkBoxWriteCommentLine.setText(I18N.getBtnChkConverterWriteCommentLine());

        chkBoxWriteCodeColumn = new Button(group, SWT.CHECK);
        chkBoxWriteCodeColumn.setSelection(false);
        chkBoxWriteCodeColumn.setText(I18N.getBtnChkBoxWriteCodeColumn());
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
        innerShell.setText(I18N.getWidgetTitleConverter());
        innerShell.setSize(width, height);
        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;

        createCompositeSourceTarget();

        inputFieldsComposite = new InputFieldsComposite(this, innerShell, SWT.NONE);
        inputFieldsComposite.setLayout(gridLayout);

        createOptions(width);
        createDescription(width);

        new BottomButtonBar(this, innerShell, SWT.NONE);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private boolean processFileOperations() {
        boolean success;
        boolean GSIFormat;

        int sourceNumber = RadioHelper.getSelectedBtn(groupSource.getChildren());
        int targetNumber = RadioHelper.getSelectedBtn(groupTarget.getChildren());

        if (sourceNumber == 0) {
            GSIFormat = Main.getGSI8();
        } else {
            GSIFormat = Main.getGSI16();
        }

        int counter = 0;

        for (File file2read : files2read) {
            boolean readFileSuccess = false;

            List<String[]> readCSVFile = null;
            ArrayList<String> readFile = null;

            // read files
            switch (sourceNumber) {
                case 0:     // fall through for GSI8 format
                case 1:     // GSI16 format
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderGSIFailed());
                    }
                    break;

                case 2:     // TXT format (tabulator or space separated)
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderTXTFailed());
                    }
                    break;

                case 3:     // CSV format (comma or semicolon separated)
                    char separatorCSV = chkBoxCSVSemiColonSeparator.getSelection() ? ';' : ',';

                    // use opencsv project for reading -> could this be done better?
                    try {
                        CSVReader reader = new CSVReader(new FileReader(file2read), separatorCSV);
                        readCSVFile = reader.readAll();
                        readFileSuccess = true;
                    } catch (IOException e) {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderCSVFailed());
                    }
                    break;

                case 4:     // cadwork node.dat format from the cadwork CAD program
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderCadworkFailed());
                    }
                    break;

                case 5:     // CAPLAN K format file
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderCaplanFailed());
                    }
                    break;

                case 6:     // CSV format from the geodata server 'Basel Stadt' (http://shop.geo.bs.ch/geoshop_app/geoshop/)
                    try {
                        CSVReader reader = new CSVReader(new FileReader(file2read), ';', '"', 1); // skip first line
                        readCSVFile = reader.readAll();
                        readFileSuccess = true;
                    } catch (IOException e) {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderBaselStadtFailed());
                    }
                    break;

                case 7:     // TXT format from the geodata server 'Basel Landschaft' (https://www.geo.bl.ch/)
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderBaselLandschaftFailed());
                    }
                    break;
            }

            if (readFileSuccess) {  // write files
                // helper for conversion
                ArrayList<String> stopOver;
                ArrayList<String> writeFile = null;

                FileToolsCaplanK toolsCaplanK;
                FileToolsCSV toolsCSV;
                FileToolsLeicaGSI toolsLeicaGSI;
                FileToolsLTOP toolsLTOP;
                FileToolsSpreadsheet spreadsheet = null;
                FileToolsText toolsText;

                String separator;

                switch (targetNumber) {
                    case 0:     // target format: GSI8 format
                        switch (sourceNumber) {
                            case 0:     // GSI8 format (not possible)
                                break;

                            case 1:     // GSI16 format
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertGSI8vsGSI16(Main.getGSI8());
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertTXT2GSI(Main.getGSI8(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readCSVFile);
                                writeFile = toolsLeicaGSI.convertCSV2GSI(Main.getGSI8(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 4:     // CAPLAN K format
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertK2GSI(Main.getGSI8());
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertCadwork2GSI(Main.getGSI8(),
                                        chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readCSVFile);
                                writeFile = toolsLeicaGSI.convertCSVBaselStadt2GSI(Main.getGSI8(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertTXTBaselLandschaft2GSI(Main.getGSI8(), chkBoxWriteCodeColumn.getSelection());
                                break;

                        }
                        if (writeFile2Disk(file2read, writeFile, ".GSI")) {
                            counter++;
                        }
                        break;

                    case 1:     // target format: GSI16 format
                        switch (sourceNumber) {
                            case 0:     // GSI8 format
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertGSI8vsGSI16(Main.getGSI16());
                                break;

                            case 1:     // GSI16 format (not possible)
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertTXT2GSI(Main.getGSI16(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readCSVFile);
                                writeFile = toolsLeicaGSI.convertCSV2GSI(Main.getGSI16(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 4:     // CAPLAN K format
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertK2GSI(Main.getGSI16());
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertCadwork2GSI(Main.getGSI16(),
                                        chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readCSVFile);
                                writeFile = toolsLeicaGSI.convertCSVBaselStadt2GSI(Main.getGSI16(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                writeFile = toolsLeicaGSI.convertTXTBaselLandschaft2GSI(Main.getGSI16(), chkBoxWriteCodeColumn.getSelection());
                                break;

                        }
                        if (writeFile2Disk(file2read, writeFile, ".GSI")) {
                            counter++;
                        }
                        break;

                    case 2:     // target format: TXT format (space or tabulator separated)
                        separator = chkBoxTXTSpaceSeparator.getSelection() ? Main.getSeparatorSpace() : Main.getSeparatorTab();

                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                toolsText = new FileToolsText(readFile);
                                writeFile = toolsText.convertGSI2TXT(separator, GSIFormat, chkBoxWriteCommentLine.getSelection());
                                break;

                            case 2:     // TXT format (not possible)
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                toolsText = new FileToolsText(readCSVFile);
                                writeFile = toolsText.convertCSV2TXT(separator);
                                break;

                            case 4:     // CAPLAN K format
                                toolsText = new FileToolsText(readFile);
                                writeFile = toolsText.convertK2TXT(separator, chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(), chkBoxKFormatUseSimpleFormat.getSelection());
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                toolsLeicaGSI = new FileToolsLeicaGSI(readFile);
                                stopOver = toolsLeicaGSI.convertCadwork2GSI(Main.getGSI16(),
                                        chkBoxWriteCodeColumn.getSelection(), chkBoxCadworkUseZeroHeights.getSelection());
                                FileToolsText stopOverFile = new FileToolsText(stopOver);
                                writeFile = stopOverFile.convertGSI2TXT(separator, GSIFormat, chkBoxWriteCommentLine.getSelection());
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                toolsText = new FileToolsText(readCSVFile);
                                writeFile = toolsText.convertCSVBaselStadt2TXT(separator);
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                toolsText = new FileToolsText(readFile);
                                writeFile = toolsText.convertTXTBaselLandschaft2TXT(separator, chkBoxWriteCodeColumn.getSelection());
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".TXT")) {
                            counter++;
                        }
                        break;

                    case 3:     // target format: CSV format (comma or semicolon separated)
                        FileToolsCSV stopOverFile;  // helper for converting

                        separator = chkBoxCSVSemiColonSeparator.getSelection() ? Main.getSeparatorSemicolon() : Main.getSeparatorComma();

                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                toolsCSV = new FileToolsCSV(readFile);
                                writeFile = toolsCSV.convertGSI2CSV(separator, chkBoxWriteCommentLine.getSelection());
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                toolsCSV = new FileToolsCSV(readFile);
                                writeFile = toolsCSV.convertTXT2CSV(separator);
                                break;

                            case 3:     // CSV format (not possible)
                                break;

                            case 4:     // CAPLAN K format
                                toolsText = new FileToolsText(readFile);
                                stopOver = toolsText.convertK2TXT(Main.getSeparatorTab(),
                                        chkBoxWriteCommentLine.getSelection(), chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxKFormatUseSimpleFormat.getSelection());
                                stopOverFile = new FileToolsCSV(stopOver);
                                writeFile = stopOverFile.convertTXT2CSV(separator);
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                toolsCSV = new FileToolsCSV(readFile);
                                writeFile = toolsCSV.convertCadwork2CSV(separator, chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(), chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                toolsCSV = new FileToolsCSV(readCSVFile);
                                writeFile = toolsCSV.convertCSVBaselStadt2CSV(separator);
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                toolsText = new FileToolsText(readFile);
                                stopOver = toolsText.convertTXTBaselLandschaft2TXT(Main.getSeparatorTab(),
                                        chkBoxWriteCodeColumn.getSelection());

                                stopOverFile = new FileToolsCSV(stopOver);
                                writeFile = stopOverFile.convertTXT2CSV(separator);
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".CSV")) {
                            counter++;
                        }
                        break;

                    case 4:     // target format: CAPLAN (.K)
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                toolsCaplanK = new FileToolsCaplanK(readFile);
                                writeFile = toolsCaplanK.convertGSI2KFile(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;
                            case 2:     // TXT format (space or tabulator separated)
                                toolsCaplanK = new FileToolsCaplanK(readFile);
                                writeFile = toolsCaplanK.convertTXT2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;
                            case 3:     // CSV format (comma or semicolon separated)
                                toolsCaplanK = new FileToolsCaplanK(readCSVFile);
                                writeFile = toolsCaplanK.convertCSV2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection());
                                break;

                            case 4:     // CAPLAN K format (not possible)
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                toolsCaplanK = new FileToolsCaplanK(readFile);
                                writeFile = toolsCaplanK.convertCadwork2KFile(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection());
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                toolsCaplanK = new FileToolsCaplanK(readCSVFile);
                                writeFile = toolsCaplanK.convertCSVBaselStadt2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                toolsCaplanK = new FileToolsCaplanK(readFile);
                                writeFile = toolsCaplanK.convertTXTBaselLandschaft2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(), chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".K")) {
                            counter++;
                        }
                        break;

                    case 5:     // target format: LTOP KOO format
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                toolsLTOP = new FileToolsLTOP(readFile);
                                writeFile = toolsLTOP.convertGSI2KOO();
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                toolsLTOP = new FileToolsLTOP(readFile);
                                writeFile = toolsLTOP.convertTXT2KOO();
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                toolsLTOP = new FileToolsLTOP(readCSVFile);
                                writeFile = toolsLTOP.convertCSV2KOO();
                                break;

                            case 4:     // CAPLAN K format
                                toolsLTOP = new FileToolsLTOP(readFile);
                                writeFile = toolsLTOP.convertK2KOO();
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                toolsLTOP = new FileToolsLTOP(readFile);
                                writeFile = toolsLTOP.convertCadwork2KOO(chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                toolsLTOP = new FileToolsLTOP(readCSVFile);
                                writeFile = toolsLTOP.convertCSVBaselStadt2KOO();
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                toolsLTOP = new FileToolsLTOP(readFile);
                                writeFile = toolsLTOP.convertTXTBaselLandschaft2KOO();
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".KOO")) {
                            counter++;
                        }
                        break;

                    case 6:     // target format: Excel 2007 (.xlsx)
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertGSI2XLSX(file2read.getName());
                                break;
                            case 2:     // TXT format (space or tabulator separated)
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertTXT2XLSX();
                                break;
                            case 3:     // CSV format (comma or semicolon separated)
                                spreadsheet = new FileToolsSpreadsheet(readCSVFile);
                                spreadsheet.convertCSV2XLSX();
                                break;

                            case 4:     // CAPLAN K format
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertK2XLSX();
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertCadwork2XLSX();
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                spreadsheet = new FileToolsSpreadsheet(readCSVFile);
                                spreadsheet.convertCSVBaselStadt2XLSX();
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertTXTBaseStadt2XLSX();
                                break;

                        }
                        if (writeSpreadsheet2Disk(file2read, spreadsheet, ".xlsx")) {
                            counter++;
                        }

                        break;

                    case 7:     // target format: Excel 97 (.xls)
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertGSI2XLS(file2read.getName(), chkBoxWriteCommentLine.getSelection());
                                break;
                            case 2:     // TXT format (space or tabulator separated)
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertTXT2XLS();
                                break;
                            case 3:     // CSV format (comma or semicolon separated)
                                spreadsheet = new FileToolsSpreadsheet(readCSVFile);
                                spreadsheet.convertCSV2XLS();
                                break;

                            case 4:     // CAPLAN K format
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertK2XLS();
                                break;

                            case 5:     // cadwork node.dat from cadwork CAD program
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertCadwork2XLS();
                                break;

                            case 6:     // CSV format 'Basel Stadt' (semicolon separated)
                                spreadsheet = new FileToolsSpreadsheet(readCSVFile);
                                spreadsheet.convertCSVBaselStadt2XLS();
                                break;

                            case 7:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                spreadsheet = new FileToolsSpreadsheet(readFile);
                                spreadsheet.convertTXTBaseStadt2XLS();
                                break;
                        }
                        if (writeSpreadsheet2Disk(file2read, spreadsheet, ".xls")) {
                            counter++;
                        }
                        break;

                }
            }
        }

        if (counter > 0) {
            String message;

            if (counter == 1) {
                message = String.format(I18N.getMsgConvertSuccess(Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(I18N.getMsgConvertSuccess(Main.TEXT_PLURAL), counter);
            }

            GuiHelper.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), message);

            // set the counter for status bar information
            Main.countFileOps = counter;
            success = true;
        } else {
            GuiHelper.showMessageBox(innerShell, SWT.ICON_ERROR, I18N.getMsgBoxTitleError(), I18N.getMsgConvertFailed());
            success = false;
        }

        return success;
    }

    private void readErrorHandler(File file2read, String message) {
        System.err.println("File " + file2read.getName() + " could not be read.");
        GuiHelper.showMessageBox(innerShell, SWT.ICON_ERROR, I18N.getMsgBoxTitleError(), message);
    }

    private ArrayList<String> readLineBasedFile(File file2read) {
        LineReader lineReader = new LineReader(file2read);
        if (lineReader.readFile()) {
            return lineReader.getLines();
        } else {
            return null;
        }
    }

    private boolean writeFile2Disk(File file, ArrayList<String> writeFile, String suffix) {
        String fileName = prepareOutputFileName(file, suffix);
        File f = new File(fileName);

        if (f.exists()) {
            int returnValue = GuiHelper.showMessageBox(innerShell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    I18N.getMsgBoxTitleWarning(), String.format(I18N.getMsgFileExist(), fileName));

            if (returnValue == SWT.YES) {
                LineWriter lineWriter = new LineWriter(fileName);

                return lineWriter.writeFile(writeFile);
            } else {
                return false;
            }

        } else {
            return new LineWriter(fileName).writeFile(writeFile);
        }
    }

    private boolean writeSpreadsheet2Disk(File file, FileToolsSpreadsheet spreadsheet, String suffix) {
        String fileName = prepareOutputFileName(file, suffix);
        File f = new File(fileName);

        if (f.exists()) {
            int returnValue = GuiHelper.showMessageBox(innerShell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    I18N.getMsgBoxTitleWarning(), String.format(I18N.getMsgFileExist(), fileName));

            if (returnValue == SWT.YES) {
                if (suffix.equalsIgnoreCase(".xls")) {
                    return spreadsheet.writeXLS(f);
                } else if (suffix.equalsIgnoreCase(".xlsx")) {
                    return spreadsheet.writeXLSX(f);
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } else {
            if (suffix.equalsIgnoreCase(".xls")) {
                return spreadsheet.writeXLS(f);
            } else if (suffix.equalsIgnoreCase(".xlsx")) {
                return spreadsheet.writeXLSX(f);
            } else {
                return false;
            }
        }

    }

    private String prepareOutputFileName(File file, String suffix) {
        return file.toString().substring(0, file.toString().length() - 4) + "_" + Main.getParamEditString() + suffix;
    }

} // end of ConverterWidget
