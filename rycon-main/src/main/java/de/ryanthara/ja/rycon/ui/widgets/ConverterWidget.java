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
package de.ryanthara.ja.rycon.ui.widgets;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.core.converter.csv.BaseToolsCsv;
import de.ryanthara.ja.rycon.core.converter.excel.BaseToolsExcel;
import de.ryanthara.ja.rycon.core.converter.text.BaseToolsTxt;
import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDialect;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.custom.*;
import de.ryanthara.ja.rycon.ui.util.RadioHelper;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.widgets.convert.FileFilterIndex;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import de.ryanthara.ja.rycon.ui.widgets.convert.TargetButton;
import de.ryanthara.ja.rycon.ui.widgets.convert.read.*;
import de.ryanthara.ja.rycon.ui.widgets.convert.write.*;
import de.ryanthara.ja.rycon.util.StringUtils;
import de.ryanthara.ja.rycon.util.check.TextCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;
import static de.ryanthara.ja.rycon.ui.widgets.convert.FileFilterIndex.*;

/**
 * Instances of this class implements a complete converter widgets and it's functionality.
 * <p>
 * The ConverterWidget of RyCON is used to convert measurement and coordinate
 * files into different formats. RyCON can be used to convert special formats
 * e.g. coordinate files from governmental services in Switzerland
 *
 * @author sebastian
 * @version 9
 * @since 1
 */
public class ConverterWidget extends AbstractWidget {

    private Button chkBoxCadworkUseZeroHeights;
    private Button chkBoxCSVSemiColonSeparator;
    private Button chkBoxKFormatUseSimpleFormat;
    private Button chkBoxLTOPEliminateDuplicatePoints;
    private Button chkBoxLTOPSortOutputFileByNumber;
    private Button chkBoxSourceContainsCode;
    private Button chkBoxTXTSpaceSeparator;
    private Button chkBoxWriteCodeColumn;
    private Button chkBoxWriteCommentLine;
    private Path[] files2read;
    private Group groupSource;
    private Group groupTarget;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;
    private Shell parent;

    /**
     * Constructs the {@link ConverterWidget} without any parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public ConverterWidget(final Shell parent) {
        this.parent = parent;

        files2read = new Path[0];

        initUI();
        handleCommandLineInterfaceInjection();
    }

    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    boolean actionBtnOk() {
        if (TextCheck.isEmpty(inputFieldsComposite.getSourceTextField()) ||
                TextCheck.isEmpty(inputFieldsComposite.getTargetTextField())) {
            return false;
        }

        if (files2read.length == 0) {
            files2read = new Path[1];
            files2read[0] = Paths.get(inputFieldsComposite.getSourceTextField().getText());
        } else {
            files2read = TextCheck.checkSourceAndTargetText(
                    inputFieldsComposite.getSourceTextField(),
                    inputFieldsComposite.getTargetTextField(), files2read);
        }
        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperations()) {
                String status;

                final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.conversionStatus);

                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), Main.countFileOps);
                } else {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), Main.countFileOps);
                }

                Main.statusBar.setStatus(status, OK);
            }

            return true;
        }

        return false;
    }

    /*
     * This method is used from the class BottomButtonBar!
     */
    void actionBtnOkAndExit() {
        if (actionBtnOk()) {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", OK);

            innerShell.dispose();
        }
    }

    void initUI() {
        int height = Sizes.RyCON_WIDGET_HEIGHT.getValue();
        int width = Sizes.RyCON_WIDGET_WIDTH.getValue();

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell = new Shell(parent, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, event -> actionBtnCancel());
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.converterText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createCompositeSourceTarget();

        createInputFieldsComposite();
        createTxtCsv(width);
        createOptions(width);
        createDescription(width);

        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnSource() {
        FileDialog fileDialog = new FileDialog(innerShell, SWT.MULTI);

        String filterPath = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT);

        // Set the initial filter path according to anything pasted or typed in
        if (!inputFieldsComposite.getSourceTextField().getText().trim().equals("")) {
            Path sourcePath = Paths.get(inputFieldsComposite.getSourceTextField().getText());

            if (Files.isDirectory(sourcePath)) {
                filterPath = inputFieldsComposite.getSourceTextField().getText();
            } else if (Files.isRegularFile(sourcePath)) {
                inputFieldsComposite.setTargetTextFieldText(sourcePath.getFileName().toString());
            }
        }

        fileDialog.setFilterPath(filterPath);
        fileDialog.setText(ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.converterSourceText));

        fileDialog.setFilterExtensions(CSV.getExtensionsArray());
        fileDialog.setFilterNames(CSV.getFilterNamesArray());

        // determine filter index by source group selection
        determineFilterIndex(fileDialog);

        String firstFile = fileDialog.open();

        if (firstFile != null) {
            String[] files = fileDialog.getFileNames();

            files2read = new Path[files.length];

            // displaying file names without path in text field
            String concatString = "";

            String workingDir = fileDialog.getFilterPath();

            for (int i = 0; i < files.length; i++) {
                concatString = concatString.concat(files[i]);
                concatString = concatString.concat(" ");

                files2read[i] = Paths.get(workingDir + FileSystems.getDefault().getSeparator() + files[i]);
            }

            inputFieldsComposite.getTargetTextField().setText(fileDialog.getFilterPath());
            inputFieldsComposite.getSourceTextField().setText(concatString);

            // set the radio buttons
            Control[] childrenSource = groupSource.getChildren();
            Control[] childrenTarget = groupTarget.getChildren();

            toggleRadioButtons(fileDialog, childrenSource, childrenTarget);
        }
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnTarget() {
        String filterPath = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT);

        Text input = inputFieldsComposite.getTargetTextField();

        // Set the initial filter path according to anything selected or typed in
        if (!TextCheck.isEmpty(input)) {
            if (TextCheck.isDirExists(input)) {
                filterPath = input.getText();
            }
        }

        DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, input,
                DirectoryDialogsTypes.DIR_GENERAL.getText(),
                DirectoryDialogsTypes.DIR_GENERAL.getMessage(),
                filterPath);
    }

    private void createCompositeSourceTarget() {
        Composite compositeSourceTarget = new Composite(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

        compositeSourceTarget.setLayout(gridLayout);
        compositeSourceTarget.setLayoutData(gridData);

        groupSource = new Group(compositeSourceTarget, SWT.NONE);
        groupSource.setText(ResourceBundleUtils.getLangString(LABELS, Labels.sourceFormatText));
        groupSource.setLayout(new GridLayout(2, false));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupSource.setLayoutData(gridData);

        groupTarget = new Group(compositeSourceTarget, SWT.NONE);
        groupTarget.setText(ResourceBundleUtils.getLangString(LABELS, Labels.targetFormatText));
        groupTarget.setLayout(new GridLayout(2, false));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupTarget.setLayoutData(gridData);

        SelectionListener selectionListenerSource = getSelectionListener(groupTarget.getChildren(), groupSource.getChildren());
        SelectionListener selectionListenerTarget = getSelectionListener(groupSource.getChildren(), groupTarget.getChildren());

        createRadioButtonsSource(selectionListenerSource, groupSource);
        createRadioButtonsTarget(selectionListenerTarget, groupTarget);
    }

    private void createDescription(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.adviceText));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setText(ResourceBundleUtils.getLangString(LABELS, Labels.tipConverterWidget));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createInputFieldsComposite() {
        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginLeft = 0;

        inputFieldsComposite = new InputFieldsComposite(this, innerShell);
        inputFieldsComposite.setLayout(gridLayout);
    }

    private void createOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.optionsText));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        chkBoxSourceContainsCode = new Button(group, SWT.CHECK);
        chkBoxSourceContainsCode.setSelection(false);
        chkBoxSourceContainsCode.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.sourceContainsCodeChk));

        chkBoxTXTSpaceSeparator = new Button(group, SWT.CHECK);
        chkBoxTXTSpaceSeparator.setSelection(false);
        chkBoxTXTSpaceSeparator.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.separatorTXTSpace));

        chkBoxCSVSemiColonSeparator = new Button(group, SWT.CHECK);
        chkBoxCSVSemiColonSeparator.setSelection(false);
        chkBoxCSVSemiColonSeparator.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.separatorCSVSemiColon));

        chkBoxCadworkUseZeroHeights = new Button(group, SWT.CHECK);
        chkBoxCadworkUseZeroHeights.setSelection(false);
        chkBoxCadworkUseZeroHeights.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.useZeroHeightsCadwork));

        chkBoxKFormatUseSimpleFormat = new Button(group, SWT.CHECK);
        chkBoxKFormatUseSimpleFormat.setSelection(true);
        chkBoxKFormatUseSimpleFormat.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.useSimpleKFormatChk));

        chkBoxLTOPEliminateDuplicatePoints = new Button(group, SWT.CHECK);
        chkBoxLTOPEliminateDuplicatePoints.setSelection(true);
        chkBoxLTOPEliminateDuplicatePoints.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.eliminateDuplicatePointsLTOP));

        chkBoxLTOPSortOutputFileByNumber = new Button(group, SWT.CHECK);
        chkBoxLTOPSortOutputFileByNumber.setSelection(true);
        chkBoxLTOPSortOutputFileByNumber.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.sortOutputFileByNumberLTOP));

        chkBoxWriteCommentLine = new Button(group, SWT.CHECK);
        chkBoxWriteCommentLine.setSelection(false);
        chkBoxWriteCommentLine.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.writeCommentLine));

        chkBoxWriteCodeColumn = new Button(group, SWT.CHECK);
        chkBoxWriteCodeColumn.setSelection(false);
        chkBoxWriteCodeColumn.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.writeCodeColumn));
    }

    private void createRadioButtonsSource(SelectionListener selectionListener, Group group) {
        for (SourceButton button : SourceButton.values()) {
            Button radioBtn = new Button(group, SWT.RADIO);
            radioBtn.addSelectionListener(selectionListener);
            radioBtn.setText(button.getText());

            if (button == SourceButton.GSI16) {
                radioBtn.setSelection(true);
            }
        }
    }

    private void createRadioButtonsTarget(SelectionListener selectionListener, Group group) {
        for (TargetButton button : TargetButton.values()) {
            Button radioBtn = new Button(group, SWT.RADIO);
            radioBtn.addSelectionListener(selectionListener);
            radioBtn.setText(button.getText());

            if (button == TargetButton.TXT) {
                radioBtn.setSelection(true);
            }
        }
    }

    private void createTxtCsv(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.txtCsvGroup));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Button button = new Button(group, SWT.CHECK);
        button.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.txtCsvSpecialFormat));
        Text t = new Text(group, SWT.NONE);
        t.setLayoutData(gridData);


    }

    private void determineFilterIndex(FileDialog fileDialog) {
        int selectedBtnSource = RadioHelper.getSelectedBtn(groupSource.getChildren());

        switch (SourceButton.fromIndex(selectedBtnSource)) {
            case GSI8:
            case GSI16:
                fileDialog.setFilterIndex(GSI.ordinal());
                break;
            case TXT:
                fileDialog.setFilterIndex(TXT.ordinal());
                break;
            case CSV:
                fileDialog.setFilterIndex(CSV.ordinal());
                break;
            case CAPLAN_K:
                fileDialog.setFilterIndex(K.ordinal());
                break;
            case ZEISS_REC:
                fileDialog.setFilterIndex(REC.ordinal());
                break;
            case CADWORK:
                fileDialog.setFilterIndex(DAT.ordinal());
                break;
            case BASEL_STADT:
                fileDialog.setFilterIndex(CSV.ordinal());
                break;
            case BASEL_LANDSCHAFT:
                fileDialog.setFilterIndex(TXT.ordinal());
                break;
            default:
                fileDialog.setFilterIndex(GSI.ordinal());
                System.err.println("ConverterWidget.determineFilterIndex() : set default filter index to " + GSI.toString());
        }
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private SelectionListener getSelectionListener(final Control[] children1, final Control[] children2) {
        return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // control of double fired events
                boolean isSelected = ((Button) e.getSource()).getSelection();
                if (isSelected) {
                    RadioHelper.toggleBtn(children1, children2);
                }
            }
        };
    }

    private void handleCommandLineInterfaceInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            inputFieldsComposite.setSourceTextFieldText(files);
        }

        RadioHelper.selectBtn(groupSource.getChildren(), Main.getCliSourceBtnNumber());
        RadioHelper.selectBtn(groupTarget.getChildren(), Main.getCliTargetBtnNumber());
    }

    private Map<Integer, Reader> prepareReadFileMaps() {
        Map<Integer, Reader> readFileMap = new HashMap<>();
        readFileMap.put(0, new GsiReader(innerShell));
        readFileMap.put(1, new GsiReader(innerShell));
        readFileMap.put(2, new TxtReader(innerShell));
        readFileMap.put(3, new CsvReader(innerShell, chkBoxCSVSemiColonSeparator.getSelection()));
        readFileMap.put(4, new CaplanReader(innerShell));
        readFileMap.put(5, new ZeissReader(innerShell));
        readFileMap.put(6, new CadworkReader(innerShell));
        readFileMap.put(7, new BaselStadtCsvReader(innerShell));
        readFileMap.put(8, new BaselLandschaftTxtReader(innerShell));

        return readFileMap;
    }

    private Map<Integer, Writer> prepareWriteFile(Path path, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
        Map<Integer, Writer> writeFileMap = new HashMap<>();
        writeFileMap.put(0, new GsiWriter(path, readStringFile, readCSVFile, parameter, Main.getGSI8()));
        writeFileMap.put(1, new GsiWriter(path, readStringFile, readCSVFile, parameter, Main.getGSI16()));
        writeFileMap.put(2, new TxtWriter(path, readStringFile, readCSVFile, parameter));
        writeFileMap.put(3, new CsvWriter(path, readStringFile, readCSVFile, parameter));
        writeFileMap.put(4, new CaplanWriter(path, readStringFile, readCSVFile, parameter));
        writeFileMap.put(5, new ZeissWriter(path, readStringFile, readCSVFile, parameter));
        writeFileMap.put(6, new LtopKooWriter(path, readStringFile, readCSVFile, parameter));
        writeFileMap.put(7, new LtopMesWriter(path, readStringFile, parameter));
        writeFileMap.put(8, new ExcelWriter(path, readStringFile, readCSVFile, parameter, BaseToolsExcel.isXLSX));
        writeFileMap.put(9, new ExcelWriter(path, readStringFile, readCSVFile, parameter, BaseToolsExcel.isXLS));
        writeFileMap.put(10, new OdfWriter(path, readStringFile, readCSVFile, parameter));

        return writeFileMap;
    }

    private boolean processFileOperations() {
        boolean success;
        boolean GSIFormat;

        int counter = 0;
        int sourceNumber = RadioHelper.getSelectedBtn(groupSource.getChildren());
        int targetNumber = RadioHelper.getSelectedBtn(groupTarget.getChildren());

        GSIFormat = sourceNumber == 0 ? Main.getGSI8() : Main.getGSI16();

        // prepares a parameter object for reducing parameter field size
        String separatorCSV = chkBoxCSVSemiColonSeparator.getSelection() ? BaseToolsCsv.SEPARATOR_SEMICOLON : BaseToolsCsv.SEPARATOR_COMMA;
        String separatorTXT = chkBoxTXTSpaceSeparator.getSelection() ? BaseToolsTxt.SEPARATOR_SPACE : BaseToolsTxt.SEPARATOR_TAB;
        ZeissDialect dialect = ZeissDialect.valueOf(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT));

        WriteParameter parameter = new WriteParameter(sourceNumber, GSIFormat,
                chkBoxCadworkUseZeroHeights.getSelection(),
                chkBoxKFormatUseSimpleFormat.getSelection(),
                chkBoxLTOPEliminateDuplicatePoints.getSelection(),
                chkBoxLTOPSortOutputFileByNumber.getSelection(),
                chkBoxSourceContainsCode.getSelection(),
                chkBoxWriteCodeColumn.getSelection(),
                chkBoxWriteCommentLine.getSelection(),
                separatorCSV, separatorTXT, dialect);

        Map<Integer, Reader> readFileMap = prepareReadFileMaps();

        for (Path file2read : files2read) {
            boolean readFileSuccess = false;

            List<String[]> readCSVFile = null;
            ArrayList<String> readStringFile = null;

            // reader files (new version)
            if (readFileMap.containsKey(sourceNumber)) {
                if (readFileMap.get(sourceNumber).readFile(file2read)) {
                    if ((readCSVFile = readFileMap.get(sourceNumber).getReadCSVFile()) != null) {
                        readFileSuccess = true;
                    } else if ((readStringFile = readFileMap.get(sourceNumber).getReadStringLines()) != null) {
                        readFileSuccess = true;
                    }
                }
            }

            // writer files (new version)
            if (readFileSuccess) {
                Map<Integer, Writer> writeFileMap = prepareWriteFile(file2read, readStringFile, readCSVFile, parameter);
                if (writeFileMap.containsKey(targetNumber)) {
                    if (writeFileMap.get(targetNumber).writeSpreadsheetDocument()) {
                        counter = counter + 1;
                    } else if (writeFileMap.get(targetNumber).writeStringFile()) {
                        counter = counter + 1;
                    } else if (writeFileMap.get(targetNumber).writeWorkbookFile()) {
                        counter = counter + 1;
                    }
                }
            }
        }

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.conversionMessage);

            if (counter == 1) {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangString(LABELS, Labels.successTextMsgBox), message);

            // set the counter for status bar information
            Main.countFileOps = counter;

            success = true;
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.conversionFailed));

            success = false;
        }

        return success;
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private void toggleRadioButtons(FileDialog fileDialog, Control[] childrenSource, Control[] childrenTarget) {
        switch (FileFilterIndex.fromIndex(fileDialog.getFilterIndex())) {
            case GSI:
                if (RadioHelper.getSelectedBtn(childrenSource) > 1) {
                    RadioHelper.selectBtn(childrenSource, 1);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 0) {
                    RadioHelper.selectBtn(childrenSource, 1);
                } else if (RadioHelper.getSelectedBtn(childrenTarget) == 1) {
                    RadioHelper.selectBtn(childrenSource, 0);
                }
                break;

            case TXT:
                // prevent button change for geodata Basel Landschaft files
                if (RadioHelper.getSelectedBtn(childrenSource) != 8) {
                    RadioHelper.selectBtn(childrenSource, 2);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 2) {
                    RadioHelper.selectBtn(childrenTarget, 1);
                }
                break;

            case CSV:
                // prevent button change for geodata Basel Stadt files
                if (RadioHelper.getSelectedBtn(childrenSource) != 7) {
                    RadioHelper.selectBtn(childrenSource, 3);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 3) {
                    RadioHelper.selectBtn(childrenTarget, 1);
                }
                break;

            case K:
                // prevent button change for CAPLAN K files
                if (RadioHelper.getSelectedBtn(childrenSource) != 4) {
                    RadioHelper.selectBtn(childrenSource, 4);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 4) {
                    RadioHelper.selectBtn(childrenTarget, 1);
                }
                break;

            case DAT:
                // prevent button change for node.dat (cadwork) files
                if (RadioHelper.getSelectedBtn(childrenSource) != 6) {
                    RadioHelper.selectBtn(childrenSource, 6);
                }
                break;

            case REC:
                // prevent button change for Zeiss REC files
                if (RadioHelper.getSelectedBtn(childrenSource) != 5) {
                    RadioHelper.selectBtn(childrenSource, 5);
                }
                break;

            default:
                System.err.println("ConverterWidget.toggleRadioButtons(): Unknown format for buttons");
                break;
        }
    }

} // end of ConverterWidget
