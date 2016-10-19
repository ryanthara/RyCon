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
import de.ryanthara.ja.rycon.converter.caplan.*;
import de.ryanthara.ja.rycon.converter.csv.*;
import de.ryanthara.ja.rycon.converter.excel.*;
import de.ryanthara.ja.rycon.converter.gsi.*;
import de.ryanthara.ja.rycon.converter.ltop.*;
import de.ryanthara.ja.rycon.converter.odf.*;
import de.ryanthara.ja.rycon.converter.text.*;
import de.ryanthara.ja.rycon.converter.zeiss.*;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.io.LineReader;
import de.ryanthara.ja.rycon.io.LineWriter;
import de.ryanthara.ja.rycon.tools.FileToolsExcel;
import de.ryanthara.ja.rycon.tools.FileToolsODF;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConverterWidget implements a complete widget and it's functionality.
 * <p>
 * The ConverterWidget of RyCON is used to convert measurement and coordinate
 * files into different formats. RyCON can be used to convert special formats
 * e.g. coordinate files from governmental services in Switzerland
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class ConverterWidget {

    private Button chkBoxCadworkUseZeroHeights;
    private Button chkBoxCSVSemiColonSeparator;
    private Button chkBoxKFormatUseSimpleFormat;
    private Button chkBoxLTOPEliminateDuplicatePoints;
    private Button chkBoxLTOPSortOutputFileByNumber;
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
        handleFileInjection();
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
        fileDialog.setFilterExtensions(new String[]{"*.gsi", "*.txt", "*.csv", "*.K", "*.dat", "*.REC"});
        fileDialog.setFilterNames(new String[]{
                I18N.getFileChooserFilterNameGSI(),
                I18N.getFileChooserFilterNameTXT(),
                I18N.getFileChooserFilterNameCSV(),
                I18N.getFileChooserFilterNameK(),
                I18N.getFileChooserFilterNameCadwork(),
                I18N.getFileChooserFilterNameZeiss()});

        // determine filter index by source group selection
        int selectedBtnSource = RadioHelper.getSelectedBtn(groupSource.getChildren());
        switch (selectedBtnSource) {
            case 0:
            case 1:
                fileDialog.setFilterIndex(0);   // Leica GSI
                break;
            case 2:
                fileDialog.setFilterIndex(1);   // text files
                break;
            case 3:
                fileDialog.setFilterIndex(2);   // CSV
                break;
            case 4:
                fileDialog.setFilterIndex(3);   // K
                break;
            case 5:
                fileDialog.setFilterIndex(5);   // Zeiss (*.REC)
                break;
            case 6:
                fileDialog.setFilterIndex(4);   // cadwork node.dat
                break;
            case 7:
                fileDialog.setFilterIndex(2);   // CSV Basel Stadt
                break;
            case 8:
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
                    if (RadioHelper.getSelectedBtn(childrenSource) > 1) {
                        RadioHelper.selectBtn(childrenSource, 1);
                    }
                    if (RadioHelper.getSelectedBtn(childrenTarget) == 0) {
                        RadioHelper.selectBtn(childrenSource, 1);
                    } else if (RadioHelper.getSelectedBtn(childrenTarget) == 1) {
                        RadioHelper.selectBtn(childrenSource, 0);
                    }
                    break;
                case 1: // text files
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
                case 4: // Cadwork node.dat files
                    // prevent button change for node.dat (cadwork) files
                    if (RadioHelper.getSelectedBtn(childrenSource) != 6) {
                        RadioHelper.selectBtn(childrenSource, 6);
                    }
                    break;
                case 5: // Zeiss REC files
                    // prevent button change for Zeiss REC files
                    if (RadioHelper.getSelectedBtn(childrenSource) != 5) {
                        RadioHelper.selectBtn(childrenSource, 5);
                    }
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
                "GSI8", "GSI16", "TXT", "CSV", "CAPLAN (.K)", "Zeiss (.REC)", "cadwork (node.dat)",
                "Basel Stadt (.CSV)", "Basel Landschaft (.TXT)"
        };

        String[] formatTarget = {
                "GSI8", "GSI16", "TXT", "CSV", "CAPLAN (.K)", "Zeiss (.REC)", "LTOP (.KOO)", "LTOP (.MES)",
                "Excel 2007 (.xlsx)", "Excel '97 (.xls)", "Open Document  Format (.ods)"
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

        chkBoxLTOPEliminateDuplicatePoints = new Button(group, SWT.CHECK);
        chkBoxLTOPEliminateDuplicatePoints.setSelection(true);
        chkBoxLTOPEliminateDuplicatePoints.setText(I18N.getBtnChkBoxLTOPEliminateDuplicatePoints());

        chkBoxLTOPSortOutputFileByNumber = new Button(group, SWT.CHECK);
        chkBoxLTOPSortOutputFileByNumber.setSelection(true);
        chkBoxLTOPSortOutputFileByNumber.setText(I18N.getBtnChkBoxLTOPSortOutputFileByNumber());

        chkBoxWriteCommentLine = new Button(group, SWT.CHECK);
        chkBoxWriteCommentLine.setSelection(false);
        chkBoxWriteCommentLine.setText(I18N.getBtnChkConverterWriteCommentLine());

        chkBoxWriteCodeColumn = new Button(group, SWT.CHECK);
        chkBoxWriteCodeColumn.setSelection(false);
        chkBoxWriteCodeColumn.setText(I18N.getBtnChkBoxWriteCodeColumn());
    }

    private void handleFileInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            inputFieldsComposite.setSourceTextFieldText(files);
        }
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

    private String prepareOutputFileName(File file, String suffix) {
        return file.toString().substring(0, file.toString().length() - 4) + "_" + Main.getParamEditString() + suffix;
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

                case 4:     // CAPLAN K format file
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderCaplanFailed());
                    }
                    break;

                case 5:     // Zeiss REC format and it's dialects (R4, R5, REC500 und M5)
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderZeissFailed());
                    }
                    break;

                case 6:     // cadwork node.dat format from the cadwork CAD program
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderCadworkFailed());
                    }
                    break;

                case 7:     // CSV format from the geodata server 'Basel Stadt' (http://shop.geo.bs.ch/geoshop_app/geoshop/)
                    try {
                        CSVReader reader = new CSVReader(new FileReader(file2read), ';', '"', 0); // not skip first line!
                        readCSVFile = reader.readAll();
                        readFileSuccess = true;
                    } catch (IOException e) {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderBaselStadtFailed());
                    }
                    break;

                case 8:     // TXT format from the geodata server 'Basel Landschaft' (https://www.geo.bl.ch/)
                    if ((readFile = readLineBasedFile(file2read)) != null) {
                        readFileSuccess = true;
                    } else {
                        readErrorHandler(file2read, I18N.getMsgConvertReaderBaselLandschaftFailed());
                    }
                    break;
            }

            if (readFileSuccess) {  // write files
                ArrayList<String> writeFile = null;
                String separator;

                switch (targetNumber) {
                    /*
                    Target format: GSI8
                     */
                    case 0:
                        switch (sourceNumber) {
                            case 0:     // GSI8 format (not possible)
                                break;

                            case 1:     // GSI16 format
                                GSI8vsGSI16 gsi8vsGSI16 = new GSI8vsGSI16(readFile);
                                writeFile = gsi8vsGSI16.convertGSI8vsGSI16(Main.getGSI8());
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                TXT2GSI txt2GSI = new TXT2GSI(readFile);
                                writeFile = txt2GSI.convertTXT2GSI(Main.getGSI8(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2GSI csv2GSI = new CSV2GSI(readCSVFile);
                                writeFile = csv2GSI.convertCSV2GSI(Main.getGSI8(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 4:     // CAPLAN K format
                                K2GSI k2GSI = new K2GSI(readFile);
                                writeFile = k2GSI.convertK2GSI(Main.getGSI8());
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2GSI zeiss2GSI = new Zeiss2GSI(readFile);
                                writeFile = zeiss2GSI.convertZeiss2GSI(Main.getGSI8());
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2GSI cadwork2GSI = new Cadwork2GSI(readFile);
                                writeFile = cadwork2GSI.convertCadwork2GSI(Main.getGSI8(),
                                        chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2GSI csvBaselStadt2GSI = new CSVBaselStadt2GSI(readCSVFile);
                                writeFile = csvBaselStadt2GSI.convertCSVBaselStadt2GSI(Main.getGSI8(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2GSI txtBaselLandschaft2GSI = new TXTBaselLandschaft2GSI(readFile);
                                writeFile = txtBaselLandschaft2GSI.convertTXTBaselLandschaft2GSI(Main.getGSI8(),
                                        chkBoxWriteCodeColumn.getSelection());
                                break;

                        }
                        if (writeFile2Disk(file2read, writeFile, ".GSI")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: GSI16
                     */
                    case 1:
                        switch (sourceNumber) {
                            case 0:     // GSI8 format
                                GSI8vsGSI16 gsi8vsGSI16 = new GSI8vsGSI16(readFile);
                                writeFile = gsi8vsGSI16.convertGSI8vsGSI16(Main.getGSI16());
                                break;

                            case 1:     // GSI16 format (not possible)
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                TXT2GSI txt2GSI = new TXT2GSI(readFile);
                                writeFile = txt2GSI.convertTXT2GSI(Main.getGSI16(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2GSI csv2GSI = new CSV2GSI(readCSVFile);
                                writeFile = csv2GSI.convertCSV2GSI(Main.getGSI16(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 4:     // CAPLAN K format
                                K2GSI k2GSI = new K2GSI(readFile);
                                writeFile = k2GSI.convertK2GSI(Main.getGSI16());
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2GSI zeiss2GSI = new Zeiss2GSI(readFile);
                                writeFile = zeiss2GSI.convertZeiss2GSI(Main.getGSI16());
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2GSI cadwork2GSI = new Cadwork2GSI(readFile);
                                writeFile = cadwork2GSI.convertCadwork2GSI(Main.getGSI16(),
                                        chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2GSI csvBaselStadt2GSI = new CSVBaselStadt2GSI(readCSVFile);
                                writeFile = csvBaselStadt2GSI.convertCSVBaselStadt2GSI(Main.getGSI16(),
                                        chkBoxSourceContainsCode.getSelection());
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2GSI txtBaselLandschaft2GSI = new TXTBaselLandschaft2GSI(readFile);
                                writeFile = txtBaselLandschaft2GSI.convertTXTBaselLandschaft2GSI(Main.getGSI16(),
                                        chkBoxWriteCodeColumn.getSelection());
                                break;

                        }
                        if (writeFile2Disk(file2read, writeFile, ".GSI")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: TXT format (space or tabulator separated)
                     */
                    case 2:
                        separator = chkBoxTXTSpaceSeparator.getSelection() ? BaseToolsTXT.SEPARATOR_SPACE : BaseToolsTXT.SEPARATOR_TAB;

                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2TXT gsi2TXT = new GSI2TXT(readFile);
                                writeFile = gsi2TXT.convertGSI2TXT(separator, GSIFormat, chkBoxWriteCommentLine.getSelection());
                                break;

                            case 2:     // TXT format (not possible)
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2TXT csv2TXT = new CSV2TXT(readCSVFile);
                                writeFile = csv2TXT.convertCSV2TXT(separator);
                                break;

                            case 4:     // CAPLAN K format
                                K2TXT k2TXT = new K2TXT(readFile);
                                writeFile = k2TXT.convertK2TXT(separator, chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(), chkBoxKFormatUseSimpleFormat.getSelection());
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2TXT zeiss2TXT = new Zeiss2TXT(readFile);
                                writeFile = zeiss2TXT.convertZeiss2TXT(separator);
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2TXT cadwork2TXT = new Cadwork2TXT(readFile);
                                writeFile = cadwork2TXT.convertCadwork2TXT(separator, chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2TXT csvBaselStadt2TXT = new CSVBaselStadt2TXT(readCSVFile);
                                writeFile = csvBaselStadt2TXT.convertCSVBaselStadt2TXT(separator);
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2TXT txtBaselLandschaft2TXT = new TXTBaselLandschaft2TXT(readFile);
                                writeFile = txtBaselLandschaft2TXT.convertTXTBaselLandschaft2TXT(separator, chkBoxWriteCodeColumn.getSelection());
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".TXT")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: CSV format (comma or semicolon separated)
                     */
                    case 3:
                        separator = chkBoxCSVSemiColonSeparator.getSelection() ? BaseToolsCSV.SEPARATOR_SEMICOLON : BaseToolsCSV.SEPARATOR_COMMA;

                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2CSV gsi2CSV = new GSI2CSV(readFile);
                                writeFile = gsi2CSV.convertGSI2CSV(separator, chkBoxWriteCommentLine.getSelection());
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                TXT2CSV txt2CSV = new TXT2CSV(readFile);
                                writeFile = txt2CSV.convertTXT2CSV(separator);
                                break;

                            case 3:     // CSV format (not possible)
                                break;

                            case 4:     // CAPLAN K format
                                K2CSV k2CSV = new K2CSV(readFile);
                                writeFile = k2CSV.convertK2CSV(separator, chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(), chkBoxKFormatUseSimpleFormat.getSelection());
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2CSV zeiss2CSV = new Zeiss2CSV(readFile);
                                zeiss2CSV.convertZeiss2CSV(separator, chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(), chkBoxKFormatUseSimpleFormat.getSelection());
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2CSV cadwork2CSV = new Cadwork2CSV(readFile);
                                writeFile = cadwork2CSV.convertCadwork2CSV(separator, chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(), chkBoxCadworkUseZeroHeights.getSelection());
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2CSV csvBaselStadt2CSV = new CSVBaselStadt2CSV(readCSVFile);
                                writeFile = csvBaselStadt2CSV.convertCSVBaselStadt2CSV(separator);
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2CSV txtBaselLandschaft2CSV = new TXTBaselLandschaft2CSV(readFile);
                                writeFile = txtBaselLandschaft2CSV.convertTXTBaselLandschaft2CSV(separator);
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".CSV")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: CAPLAN (.K)
                     */
                    case 4:
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2K gsi2K = new GSI2K(readFile);
                                writeFile = gsi2K.convertGSI2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;
                            case 2:     // TXT format (space or tabulator separated)
                                TXT2K txt2K = new TXT2K(readFile);
                                writeFile = txt2K.convertTXT2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;
                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2K csv2K = new CSV2K(readCSVFile);
                                writeFile = csv2K.convertCSV2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection());
                                break;

                            case 4:     // CAPLAN K format (not possible)
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2K zeiss2K = new Zeiss2K(readFile);
                                writeFile = zeiss2K.convertZeiss2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2K cadwork2K = new Cadwork2K(readFile);
                                writeFile = cadwork2K.convertCadwork2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection(),
                                        chkBoxWriteCodeColumn.getSelection());
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2K csvBaselStadt2K = new CSVBaselStadt2K(readCSVFile);
                                writeFile = csvBaselStadt2K.convertCSVBaselStadt2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2K txtBaselLandschaft2K = new TXTBaselLandschaft2K(readFile);
                                writeFile = txtBaselLandschaft2K.convertTXTBaselLandschaft2K(
                                        chkBoxKFormatUseSimpleFormat.getSelection(), chkBoxWriteCodeColumn.getSelection(),
                                        chkBoxWriteCommentLine.getSelection());
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".K")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: Zeiss REC
                     */
                    case 5:
                        String dialect = Main.pref.getUserPref(PreferenceHandler.CONVERTER_SETTING_ZEISS_DIALECT);

                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2Zeiss gsi2Zeiss = new GSI2Zeiss(readFile);
                                writeFile = gsi2Zeiss.convertGSI2REC(dialect);
                                break;
                            case 2:     // TXT format
                                TXT2Zeiss txt2Zeiss = new TXT2Zeiss(readFile);
                                writeFile = txt2Zeiss.convertTXT2REC(dialect);
                                break;
                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2Zeiss csv2Zeiss = new CSV2Zeiss(readCSVFile);
                                writeFile = csv2Zeiss.convertCSV2REC(dialect);
                                break;
                            case 4:     // CAPLAN K format
                                K2Zeiss k2Zeiss = new K2Zeiss(readFile);
                                writeFile = k2Zeiss.convertK2REC(dialect);
                                break;
                            case 5:     // Zeiss REC format and it's dialects (not possible or into dialects?)
                                break;
                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2Zeiss cadwork2Zeiss = new Cadwork2Zeiss(readFile);
                                writeFile = cadwork2Zeiss.convertCadwork2REC(dialect);
                                break;
                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2Zeiss csvBaselStadt2Zeiss = new CSVBaselStadt2Zeiss(readCSVFile);
                                writeFile = csvBaselStadt2Zeiss.convertCSVBaselStadt2REC(dialect);
                                break;
                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2Zeiss txtBaselLandschaft2Zeiss = new TXTBaselLandschaft2Zeiss(readFile);
                                writeFile = txtBaselLandschaft2Zeiss.convertTXTBaselLandschaft2REC(dialect);
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".REC")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: LTOP KOO format
                     */
                    case 6:
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2LTOP gsi2LTOP = new GSI2LTOP(readFile);
                                writeFile = gsi2LTOP.convertGSI2KOO(
                                        chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                TXT2LTOP txt2LTOP = new TXT2LTOP(readFile);
                                writeFile = txt2LTOP.convertTXT2KOO(chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2LTOP csv2LTOP = new CSV2LTOP(readCSVFile);
                                writeFile = csv2LTOP.convertCSV2KOO(chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;

                            case 4:     // CAPLAN K format
                                K2LTOP k2LTOP = new K2LTOP(readFile);
                                writeFile = k2LTOP.convertK2KOO(chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2LTOP zeiss2LTOP = new Zeiss2LTOP(readFile);
                                writeFile = zeiss2LTOP.convertZeiss2KOO(chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2LTOP cadwork2LTOP = new Cadwork2LTOP(readFile);
                                writeFile = cadwork2LTOP.convertCadwork2KOO(chkBoxCadworkUseZeroHeights.getSelection(),
                                        chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2LTOP csvBaselStadt2LTOP = new CSVBaselStadt2LTOP(readCSVFile);
                                writeFile = csvBaselStadt2LTOP.convertCSVBaselStadt2KOO(chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2LTOP txtBaselLandschaft2LTOP = new TXTBaselLandschaft2LTOP(readFile);
                                writeFile = txtBaselLandschaft2LTOP.convertTXTBaselLandschaft2KOO(chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                                        chkBoxLTOPSortOutputFileByNumber.getSelection());
                                break;
                        }
                        if (writeFile2Disk(file2read, writeFile, ".KOO")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: LTOP MES format
                     */
                    case 7:
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2MES gsi2MES = new GSI2MES(readFile);
                                writeFile = gsi2MES.convertGSI2MES(Boolean.parseBoolean(Main.pref.getUserPref(
                                        PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2LTOP zeiss2LTOP = new Zeiss2LTOP(readFile);
                                writeFile = zeiss2LTOP.convertZeiss2MES(Boolean.parseBoolean(Main.pref.getUserPref(
                                        PreferenceHandler.CONVERTER_SETTING_LTOP_USE_ZENITH_DISTANCE)));
                                break;

                        }
                        if (writeFile2Disk(file2read, writeFile, ".MES")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: Excel 2007 (.xlsx)
                     */
                    case 8:
                        Workbook workbook = null;
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2Excel gsi2Excel = new GSI2Excel(readFile);
                                if (gsi2Excel.convertGSI2Excel(BaseToolsExcel.isXLSX, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = gsi2Excel.getWorkbook();
                                }
                                break;
                            case 2:     // TXT format (space or tabulator separated)
                                TXT2Excel txt2Excel = new TXT2Excel(readFile);
                                if (txt2Excel.convertTXT2Excel(BaseToolsExcel.isXLSX, file2read.getName())) {
                                    workbook = txt2Excel.getWorkbook();
                                }
                                break;
                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2Excel csv2Excel = new CSV2Excel(readCSVFile);
                                if (csv2Excel.convertCSV2Excel(BaseToolsExcel.isXLSX, file2read.getName())) {
                                    workbook = csv2Excel.getWorkbook();
                                }
                                break;

                            case 4:     // CAPLAN K format
                                Caplan2Excel caplan2Excel = new Caplan2Excel(readFile);
                                if (caplan2Excel.convertCaplan2Excel(BaseToolsExcel.isXLSX, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = caplan2Excel.getWorkbook();
                                }
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2Excel zeiss2Excel = new Zeiss2Excel(readFile);
                                if (zeiss2Excel.convertZeiss2Excel(BaseToolsExcel.isXLSX, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = zeiss2Excel.getWorkbook();
                                }
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2Excel cadwork2Excel = new Cadwork2Excel(readFile);
                                if (cadwork2Excel.convertCadwork2Excel(BaseToolsExcel.isXLSX, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = cadwork2Excel.getWorkbook();
                                }
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2Excel csvBaselStadt2Excel = new CSVBaselStadt2Excel(readCSVFile);
                                if (csvBaselStadt2Excel.convertCSVBaselStadt2Excel(BaseToolsExcel.isXLSX, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = csvBaselStadt2Excel.getWorkbook();
                                }
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2Excel txtBaselLandschaft2Excel = new TXTBaselLandschaft2Excel(readFile);
                                if (txtBaselLandschaft2Excel.convertTXTBaselLand2Excel(BaseToolsExcel.isXLSX, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = txtBaselLandschaft2Excel.getWorkbook();
                                }
                                break;

                        }
                        if (writeExcel2Disk(file2read, workbook, ".xlsx")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: Excel 97 (.xls)
                     */
                    case 9:
                        workbook = null;
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2Excel gsi2Excel = new GSI2Excel(readFile);
                                if (gsi2Excel.convertGSI2Excel(BaseToolsExcel.isXLS, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = gsi2Excel.getWorkbook();
                                }
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                TXT2Excel txt2Excel = new TXT2Excel(readFile);
                                if (txt2Excel.convertTXT2Excel(BaseToolsExcel.isXLS, file2read.getName())) {
                                    workbook = txt2Excel.getWorkbook();
                                }
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2Excel csv2Excel = new CSV2Excel(readCSVFile);
                                if (csv2Excel.convertCSV2Excel(BaseToolsExcel.isXLS, file2read.getName())) {
                                    workbook = csv2Excel.getWorkbook();
                                }
                                break;

                            case 4:     // CAPLAN K format
                                Caplan2Excel caplan2Excel = new Caplan2Excel(readFile);
                                if (caplan2Excel.convertCaplan2Excel(BaseToolsExcel.isXLS, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = caplan2Excel.getWorkbook();
                                }
                                break;

                            case 5:     // Zeiss REC format and it's dialects
                                Zeiss2Excel zeiss2Excel = new Zeiss2Excel(readFile);
                                if (zeiss2Excel.convertZeiss2Excel(BaseToolsExcel.isXLS, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = zeiss2Excel.getWorkbook();
                                }
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2Excel cadwork2Excel = new Cadwork2Excel(readFile);
                                if (cadwork2Excel.convertCadwork2Excel(BaseToolsExcel.isXLS, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = cadwork2Excel.getWorkbook();
                                }
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2Excel csvBaselStadt2Excel = new CSVBaselStadt2Excel(readCSVFile);
                                if (csvBaselStadt2Excel.convertCSVBaselStadt2Excel(BaseToolsExcel.isXLS, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = csvBaselStadt2Excel.getWorkbook();
                                }
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2Excel txtBaselLandschaft2Excel = new TXTBaselLandschaft2Excel(readFile);

                                if (txtBaselLandschaft2Excel.convertTXTBaselLand2Excel(BaseToolsExcel.isXLS, file2read.getName(),
                                        chkBoxWriteCommentLine.getSelection())) {
                                    workbook = txtBaselLandschaft2Excel.getWorkbook();
                                }
                                break;
                        }
                        if (writeExcel2Disk(file2read, workbook, ".xls")) {
                            counter = counter + 1;
                        }
                        break;

                    /*
                    Target format: Open Document Format (ODF spreadsheet format .ODS)
                     */
                    case 10:
                        SpreadsheetDocument spreadsheetDocument = null;
                        switch (sourceNumber) {
                            case 0:     // fall through for GSI8 format
                            case 1:     // GSI16 format
                                GSI2ODF gsi2ODF = new GSI2ODF(readFile);
                                if (gsi2ODF.convertGSI2ODS(file2read.getName(), chkBoxWriteCommentLine.getSelection())) {
                                    spreadsheetDocument = gsi2ODF.getSpreadsheetDocument();
                                }
                                break;

                            case 2:     // TXT format (space or tabulator separated)
                                TXT2ODF txt2ODF = new TXT2ODF(readFile);
                                if (txt2ODF.convertTXT2ODS(file2read.getName())) {
                                    spreadsheetDocument = txt2ODF.getSpreadsheetDocument();
                                }
                                break;

                            case 3:     // CSV format (comma or semicolon separated)
                                CSV2ODF csv2ODF = new CSV2ODF(readCSVFile);
                                if (csv2ODF.convertCSV2ODS(file2read.getName())) {
                                    spreadsheetDocument = csv2ODF.getSpreadsheetDocument();
                                }
                                break;

                            case 4:     // CAPLAN K format
                                K2ODF k2ODF = new K2ODF(readFile);
                                if (k2ODF.convertCaplan2ODS(file2read.getName(), chkBoxWriteCommentLine.getSelection())) {
                                    spreadsheetDocument = k2ODF.getSpreadsheetDocument();
                                }
                                break;

                            case 5:     // Zeiss M5 format and it's dialects
                                Zeiss2ODF zeiss2ODF = new Zeiss2ODF(readFile);
                                if (zeiss2ODF.convertZeiss2ODS(file2read.getName(), chkBoxWriteCommentLine.getSelection())) {
                                    spreadsheetDocument = zeiss2ODF.getSpreadsheetDocument();
                                }
                                break;

                            case 6:     // cadwork node.dat from cadwork CAD program
                                Cadwork2ODF cadwork2ODF = new Cadwork2ODF(readFile);
                                if (cadwork2ODF.convertCadwork2ODS(file2read.getName(), chkBoxWriteCommentLine.getSelection())) {
                                    spreadsheetDocument = cadwork2ODF.getSpreadsheetDocument();
                                }
                                break;

                            case 7:     // CSV format 'Basel Stadt' (semicolon separated)
                                CSVBaselStadt2ODF csvBaselStadt2ODF = new CSVBaselStadt2ODF(readCSVFile);
                                if (csvBaselStadt2ODF.convertCSVBaselStadt2ODS(file2read.getName(), chkBoxWriteCommentLine.getSelection())) {
                                    spreadsheetDocument = csvBaselStadt2ODF.getSpreadsheetDocument();
                                }
                                break;

                            case 8:     // TXT format 'Basel Landschaft' (different column based text files for LFP and HFP points)
                                TXTBaselLandschaft2ODF txtBaselLandschaft2ODF = new TXTBaselLandschaft2ODF(readFile);
                                if (txtBaselLandschaft2ODF.convertTXTBaselLandschaft2ODS(file2read.getName(), chkBoxWriteCommentLine.getSelection())) {
                                    spreadsheetDocument = txtBaselLandschaft2ODF.getSpreadsheetDocument();
                                }
                                break;
                        }
                        if (writeODS2Disk(file2read, spreadsheetDocument, ".ods")) {
                            counter = counter + 1;
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

    private boolean writeExcel2Disk(File file, Workbook workbook, String suffix) {
        String fileName = prepareOutputFileName(file, suffix);
        File f = new File(fileName);
        FileToolsExcel fileToolsExcel = new FileToolsExcel(workbook);

        if (f.exists()) {
            int returnValue = GuiHelper.showMessageBox(innerShell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    I18N.getMsgBoxTitleWarning(), String.format(I18N.getMsgFileExist(), fileName));

            if (returnValue == SWT.YES) {
                if (suffix.equalsIgnoreCase(".xls")) {
                    return fileToolsExcel.writeXLS(f);
                } else
                    return suffix.equalsIgnoreCase(".xlsx") && fileToolsExcel.writeXLSX(f);
            } else {
                return false;
            }
        } else {
            if (suffix.equalsIgnoreCase(".xls")) {
                return fileToolsExcel.writeXLS(f);
            } else
                return suffix.equalsIgnoreCase(".xlsx") && fileToolsExcel.writeXLSX(f);
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

    private boolean writeODS2Disk(File file, SpreadsheetDocument spreadsheetDocument, String suffix) {
        String fileName = prepareOutputFileName(file, suffix);
        File f = new File(fileName);
        FileToolsODF fileToolsODF = new FileToolsODF(spreadsheetDocument);

        if (f.exists()) {
            int returnValue = GuiHelper.showMessageBox(innerShell, SWT.ICON_WARNING | SWT.YES | SWT.NO,
                    I18N.getMsgBoxTitleWarning(), String.format(I18N.getMsgFileExist(), fileName));

            return returnValue == SWT.YES && fileToolsODF.writeODS(fileName);
        } else {
            return fileToolsODF.writeODS(fileName);
        }
    }

} // end of ConverterWidget
