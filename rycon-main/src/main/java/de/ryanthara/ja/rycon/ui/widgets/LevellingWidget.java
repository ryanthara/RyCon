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
import de.ryanthara.ja.rycon.core.converter.asc.*;
import de.ryanthara.ja.rycon.core.converter.csv.Asc2Csv;
import de.ryanthara.ja.rycon.core.converter.gsi.Asc2Gsi;
import de.ryanthara.ja.rycon.core.converter.text.Asc2Txt;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.custom.*;
import de.ryanthara.ja.rycon.ui.util.RadioHelper;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.util.StringUtils;
import de.ryanthara.ja.rycon.util.check.PathCheck;
import de.ryanthara.ja.rycon.util.check.TextCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.Main.countFileOps;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;

/**
 * Instances of this class implements a complete widget and it's functionality for processing levelling files.
 * <p>
 * The {@link LevellingWidget} of RyCON is used to convert levelling files or height lists (GSI or text format)
 * for CAD import. Each generated file contains only lines of one code. The code is added
 * to the file name of the written file.
 * <p>
 * This version of the CodeSplitterWidget supports the following file types:
 * <ul>
 * <li>Leica GSI format files (GSI8 and GSI16)
 * <li>text files with code (format no, code, x, y, z)
 * </ul>
 * <p>
 * The LevellingWidget of RyCON is used to convert levelling or height files for cad import. Therefore a GSI based
 * levelling file is prepared to a coordinate file with no, x, y and measured height values. For the x- and y-values
 * are the count line numbers used.
 * <p>
 * On later versions of RyCON there will be support for more levelling formats.
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class LevellingWidget extends AbstractWidget {

    private final static Logger logger = Logger.getLogger(LevellingWidget.class.getName());

    private final String[] acceptableFileSuffixes = new String[]{"*.gsi", "*.lev", "*.asc", "*.asc", "*.hvz", "*.ber", "*.aus"};
    private Button chkBoxCsvSemicolonSeparator;
    private Button chkBoxIgnoreChangePoints;
    private Button chkBoxTxtSpaceSeparator;
    private Path[] files2read;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;
    private Shell parent;
    private Group radio;

    /**
     * Constructs a new instance of this class without any parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public LevellingWidget(final Shell parent) {
        this.parent = parent;

        files2read = new Path[0];

        initUI();
        handleFileInjection();
    }

    /**
     * Constructs a new instance of this class given a {@link Path} array as parameter. This constructor type
     * is used for the drag and drop injection.
     * <p>
     * The {@link Path} array of the dropped files will be checked for being valid and not being a directory.
     *
     * @param droppedFiles {@link Path} array from drop source
     */
    public LevellingWidget(Path... droppedFiles) {
        files2read = PathCheck.getValidFiles(droppedFiles, acceptableFileSuffixes);
    }

    /**
     * Executes the drop action as injection.
     * <p>
     * The file processing will be done without a graphical user interface
     * and the result is only shown on the status bar.
     */
    public void executeDropInjection() {
        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperationsDND()) {
                String status;

                final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.levellingStatus);

                // use counter to display different text on the status bar
                if (countFileOps == 1) {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), countFileOps);
                } else {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), countFileOps);
                }

                Main.statusBar.setStatus(status, OK);
            }
        }
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

                final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.levellingStatus);

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
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.levellingText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createInputFieldComposite();
        createOutputFormat(width);
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
        String[] filterNames = new String[]{
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameGsiLevel),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameLeicaObservations),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameLeicaProtocol),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameNigraAltitudeRegisterAsc),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameNigraAltitudeRegisterHvz),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameNigraCalculations),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameNivNetOutput),
        };

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

        Optional<Path[]> files = FileDialogs.showAdvancedFileDialog(
                innerShell,
                filterPath,
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.levellingSourceText),
                acceptableFileSuffixes,
                filterNames,
                inputFieldsComposite.getSourceTextField(),
                inputFieldsComposite.getTargetTextField());

        if (files.isPresent()) {
            files2read = files.get();
        } else {
            logger.log(Level.SEVERE, "can't get the reader files");
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

    private void createDescription(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.adviceText));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setText(ResourceBundleUtils.getLangString(LABELS, Labels.tipLevellingWidget));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createInputFieldComposite() {
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

        chkBoxIgnoreChangePoints = new Button(group, SWT.CHECK);
        chkBoxIgnoreChangePoints.setSelection(true);
        chkBoxIgnoreChangePoints.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.levellingIgnoreChangePoints));

        chkBoxCsvSemicolonSeparator = new Button(group, SWT.CHECK);
        chkBoxCsvSemicolonSeparator.setSelection(false);
        chkBoxCsvSemicolonSeparator.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.separatorCSVSemiColon));

        chkBoxTxtSpaceSeparator = new Button(group, SWT.CHECK);
        chkBoxTxtSpaceSeparator.setSelection(false);
        chkBoxTxtSpaceSeparator.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.separatorTXTSpace));
    }

    private void createOutputFormat(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.outputFormatText));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        radio = new Group(group, SWT.NONE);
        radio.setLayout(new RowLayout(SWT.HORIZONTAL));

        Button radioBtnGsi8 = new Button(radio, SWT.RADIO);
        radioBtnGsi8.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.radioBtnLevelGsi8));

        Button radioBtnGsi16 = new Button(radio, SWT.RADIO);
        radioBtnGsi16.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.radioBtnLevelGsi16));

        Button radioBtnAsc = new Button(radio, SWT.RADIO);
        radioBtnAsc.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.radioBtnLevelAsc));

        Button radioBtnCsv = new Button(radio, SWT.RADIO);
        radioBtnCsv.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.radioBtnLevelCsv));

        Button radioBtnTxt = new Button(radio, SWT.RADIO);
        radioBtnTxt.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.radioBtnLevelTxt));

        radioBtnGsi16.setSelection(true);
    }

    private int fileOperations(boolean ignoreChangePoints) {
        int counter = 0;
        final String levelString = Main.pref.getUserPreference(PreferenceKeys.PARAM_LEVEL_STRING);

        for (Path path : files2read) {
            if (PathCheck.fileExists(path)) {
                PathMatcher matcherLev = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.LEV)");

                ArrayList<String> ascFile = null;
                ArrayList<String> writeFile = null;

                // read xml based Leica Geosystems observations
                if (matcherLev.matches(path)) {
                    LeicaObservations2Asc leicaObservations2Asc = new LeicaObservations2Asc(path, ignoreChangePoints);
                    ascFile = leicaObservations2Asc.convert();
                } else {
                    // read line based text files in different formats
                    LineReader lineReader = new LineReader(path);

                    if (lineReader.readFile(false)) {
                        ArrayList<String> readFile = lineReader.getLines();

                        // the glob pattern ("glob:*.dat) doesn't work here
                        PathMatcher matcherAsc = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.ASC)");
                        PathMatcher matcherAus = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.AUS)");
                        PathMatcher matcherBer = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.BER)");
                        PathMatcher matcherGsi = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.GSI)");
                        PathMatcher matcherHvz = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.HVZ)");

                        /*
                         * Read different file formats and writes ASC format with the full number of digits
                         * as default to save the precision of the levelling.
                         */
                        if (matcherAsc.matches(path)) {
                            // differ between Leica Geosystems AG levelling protocol and Nigra/NigraWin altitude register file
                            if (readFile.get(1).contains("Leica Geosystems AG --- ")) {
                                LeicaProtocol2Asc leicaProtocol2Asc = new LeicaProtocol2Asc(readFile, ignoreChangePoints);
                                ascFile = leicaProtocol2Asc.convert();
                            } else if (readFile.get(2).contains("NigraWin - Nivellement, ")) {
                                // NigraWin/NivNET altitude register new version since version 4.0?
                                NigraAltitudeRegisterAsc2Asc nigraWinAsc2Asc = new NigraAltitudeRegisterAsc2Asc(readFile);
                                ascFile = nigraWinAsc2Asc.convert();
                            }
                        } else if (matcherAus.matches(path)) {
                            NivNet2Aus nivNet2Aus = new NivNet2Aus(readFile);
                            ascFile = nivNet2Aus.convert();
                        } else if (matcherBer.matches(path)) {
                            // NigraWin calculation file
                            NigraCalculation2Asc nigraCalculation2Asc = new NigraCalculation2Asc(readFile);
                            ascFile = nigraCalculation2Asc.convert();
                        } else if (matcherGsi.matches(path)) {
                            // Leica Geosystems GSI Levelling files
                            Gsi2Asc gsi2Asc = new Gsi2Asc(readFile, ignoreChangePoints);
                            ascFile = gsi2Asc.convert();
                        } else if (matcherHvz.matches(path)) {
                            // NigraWin/NivNET altitude register old version until version 3.x?
                            NigraAltitudeRegisterHvz2Asc nigraWinHvz2Asc = new NigraAltitudeRegisterHvz2Asc(readFile);
                            ascFile = nigraWinHvz2Asc.convert();
                        } else {
                            logger.log(Level.SEVERE, "File " + path.getFileName() + " is not supported (yet).");
                            break;
                        }
                    }
                }

                // convert the ASC lines to the chosen output format
                String fileNameExtension = "";

                if (ascFile != null) {
                    switch (RadioHelper.getSelectedBtn(radio.getChildren())) {
                        // GSI8
                        case 0:
                            Asc2Gsi asc2Gsi8 = new Asc2Gsi(ascFile, Main.getGSI8());
                            writeFile = asc2Gsi8.convert();
                            fileNameExtension = FileNameExtension.LEICA_GSI.getExtension();
                            break;
                        // GSI16
                        case 1:
                            Asc2Gsi asc2Gsi16 = new Asc2Gsi(ascFile, Main.getGSI16());
                            writeFile = asc2Gsi16.convert();
                            fileNameExtension = FileNameExtension.LEICA_GSI.getExtension();
                            break;
                        // ASC
                        case 2:
                            Asc2Asc asc2Asc = new Asc2Asc(ascFile);
                            writeFile = asc2Asc.convert();
                            fileNameExtension = FileNameExtension.ASC.getExtension();
                            break;
                        // CSV
                        case 3:
                            Asc2Csv asc2Csv = new Asc2Csv(ascFile, chkBoxCsvSemicolonSeparator.getSelection());
                            writeFile = asc2Csv.convert();
                            fileNameExtension = FileNameExtension.CSV.getExtension();
                            break;
                        // TXT
                        case 4:
                            Asc2Txt asc2Txt = new Asc2Txt(ascFile, chkBoxTxtSpaceSeparator.getSelection());
                            writeFile = asc2Txt.convert();
                            fileNameExtension = FileNameExtension.TXT.getExtension();
                            break;
                        default:
                            break;
                    }

                    if (WriteFile2Disk.writeFile2Disk(path, writeFile, levelString, fileNameExtension)) {
                        counter = counter + 1;
                    }
                }
            } else {
                logger.log(Level.SEVERE, "File " + path.getFileName() + " could not be read.");
            }
        }

        return counter;
    }

    private void handleFileInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            inputFieldsComposite.setSourceTextFieldText(files);
        }
    }

    private boolean processFileOperations() {
        boolean success;

        int counter = fileOperations(chkBoxIgnoreChangePoints.getSelection());

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.levellingMessage);

            if (counter == 1) {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangString(LABELS, Labels.successTextMsgBox), message);

            // set the counter for status bar information
            countFileOps = counter;
            success = true;
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.levellingPreparationFailed));

            success = false;
        }

        return success;
    }

    private boolean processFileOperationsDND() {
        // change points are ignored
        int counter = fileOperations(false);

        if (counter > 0) {
            // set the counter for status bar information
            countFileOps = counter;
            return true;
        } else {
            return false;
        }
    }

} // end of LevellingWidget.java
