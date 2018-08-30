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
import de.ryanthara.ja.rycon.core.splitter.GsiCodeSplit;
import de.ryanthara.ja.rycon.core.splitter.NodeDatCodeSplit;
import de.ryanthara.ja.rycon.core.splitter.TextCodeSplit;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.custom.*;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.util.StringUtils;
import de.ryanthara.ja.rycon.util.check.PathCheck;
import de.ryanthara.ja.rycon.util.check.TextCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;

/**
 * Instances of this class implements a complete widget and it's functionality to split coordinate files by code.
 * <p>
 * The {@link CodeSplitterWidget} of <tt>RyCON</tt> is used to split measurement files (GSI or text format)
 * by code into several files. Each generated file contains only lines of one code. The code is added
 * to the file name of the written file.
 * <p>
 * This version of the CodeSplitterWidget supports the following file types:
 * <ul>
 * <li>Leica GSI format files (GSI8 and GSI16)
 * <li>text files with code (format no, code, x, y, z)
 * </ul>
 *
 * @author sebastian
 * @version 7
 * @since 1
 */
public class CodeSplitterWidget extends AbstractWidget {

    private static final Logger logger = LoggerFactory.getLogger(CodeSplitterWidget.class.getName());

    private final String[] acceptableFileSuffixes = new String[]{"*.gsi", "*.txt", "*.dat"};
    private Shell parent;
    private Button chkBoxInsertCodeColumn, chkBoxWriteCodeZero;
    private Path[] files2read;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;

    /**
     * Constructs the {@link CodeSplitterWidget} without any parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public CodeSplitterWidget(final Shell parent) {
        this.parent = parent;
        this.files2read = new Path[0];

        initUI();
        handleFileInjection();
    }

    /**
     * Constructs the {@link CodeSplitterWidget} for the given files from the drag'n drop operation.
     * <p>
     * The {@link java.nio.file.Path} array of the dropped files will be checked for being valid and not being a directory.
     *
     * @param droppedFiles path array from drop source
     */
    public CodeSplitterWidget(Path... droppedFiles) {
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

                final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.splitFilesStatus);

                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), Main.countFileOps);
                } else {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), Main.countFileOps);
                }

                Main.statusBar.setStatus(status, Status.OK);
            }
        }

    }

    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", Status.OK);
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

                final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.splitFilesStatus);

                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), files2read.length, Main.countFileOps);
                } else {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), files2read.length, Main.countFileOps);
                }

                Main.statusBar.setStatus(status, Status.OK);
            }

            return true;
        }

        return false;
    }

    void actionBtnOkAndExit() {
        if (actionBtnOk()) {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", Status.OK);

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
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.splitterText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createInputFieldsComposite();
        createOptions(width);
        createAdvice(width);

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
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameGsi),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameTxtCode),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameCadwork),
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
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.splitterSourceText),
                acceptableFileSuffixes,
                filterNames,
                inputFieldsComposite.getSourceTextField(),
                inputFieldsComposite.getTargetTextField());

        if (files.isPresent()) {
            files2read = files.get();
        } else {
            logger.warn("Can not get the source files to be read.");
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

    private void createAdvice(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.text));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);

        String text =
                ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.splitterWidget) + "\n\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.splitterWidget2) + "\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.splitterWidget3);

        tip.setText(text);

        // tip.setText(ResourceBundleUtils.getLangString(ADVICE, Advice.splitterWidget));
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
        group.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.general));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        chkBoxInsertCodeColumn = new Button(group, SWT.CHECK);
        chkBoxInsertCodeColumn.setSelection(false);
        chkBoxInsertCodeColumn.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.insertCodeColumn));

        chkBoxWriteCodeZero = new Button(group, SWT.CHECK);
        chkBoxWriteCodeZero.setSelection(false);
        chkBoxWriteCodeZero.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.writeCodeZeroSplitter));
    }

    private int executeSplitGsi(boolean insertCodeColumn, boolean writeFileWithCodeZero, int counter, Path file2read,
                                ArrayList<String> readFile) {

        GsiCodeSplit gsiCodeSplit = new GsiCodeSplit(readFile);
        ArrayList<ArrayList<String>> writeFile = gsiCodeSplit.processCodeSplit(insertCodeColumn, writeFileWithCodeZero);

        Iterator<Integer> codeIterator = gsiCodeSplit.getFoundCodes().iterator();

        // write file by file with one code
        for (ArrayList<String> lines : writeFile) {
            final String codeString = Main.pref.getUserPreference(PreferenceKeys.PARAM_CODE_STRING) + "-" + codeIterator.next();

            if (WriteFile2Disk.writeFile2Disk(file2read, lines, codeString, FileNameExtension.LEICA_GSI.getExtension())) {
                counter = counter + 1;
            }
        }

        return counter;
    }

    private int executeSplitNodeDat(int counter, Path file2read, ArrayList<String> readFile) {
        NodeDatCodeSplit nodeDatCodeSplit = new NodeDatCodeSplit(readFile);
        ArrayList<ArrayList<String>> writeFile = nodeDatCodeSplit.processCodeSplit();

        Iterator<Integer> codeIterator = nodeDatCodeSplit.getFoundCodes().iterator();

        // write file by file with one code
        for (ArrayList<String> lines : writeFile) {
            final String codeString = Main.pref.getUserPreference(PreferenceKeys.PARAM_CODE_STRING) + "-" + codeIterator.next();

            if (WriteFile2Disk.writeFile2Disk(file2read, lines, codeString, FileNameExtension.DAT.getExtension())) {
                counter = counter + 1;
            }
        }

        return counter;
    }

    private int executeSplitTxt(boolean insertCodeColumn, boolean writeFileWithCodeZero, int counter, Path file2read,
                                ArrayList<String> readFile) {

        TextCodeSplit textCodeSplit = new TextCodeSplit(readFile);
        ArrayList<ArrayList<String>> writeFile = textCodeSplit.processCodeSplit(insertCodeColumn, writeFileWithCodeZero);

        Iterator<Integer> codeIterator = textCodeSplit.getFoundCodes().iterator();

        // write file by file with one code
        for (ArrayList<String> lines : writeFile) {
            final String editString = Main.pref.getUserPreference(PreferenceKeys.PARAM_CODE_STRING) + "-" + codeIterator.next();

            if (WriteFile2Disk.writeFile2Disk(file2read, lines, editString, FileNameExtension.TXT.getExtension())) {
                counter = counter + 1;
            }
        }

        return counter;
    }

    private int fileOperations(boolean insertCodeColumn, boolean writeFileWithCodeZero) {
        int counter = 0;

        for (Path path : files2read) {
            // first attempt to ignore logfile.txt files
            if (!path.toString().toLowerCase().contains("logfile.txt")) {
                LineReader lineReader = new LineReader(path);

                if (lineReader.readFile(false)) {
                    ArrayList<String> readFile = lineReader.getLines();

                    // the glob pattern ("glob:*.dat) doesn't work here
                    PathMatcher matcherDAT = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.DAT)");
                    PathMatcher matcherGSI = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.GSI)");
                    PathMatcher matcherTXT = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.TXT)");

                    if (matcherDAT.matches(path)) {
                        counter = executeSplitNodeDat(counter, path, readFile);
                    } else if (matcherGSI.matches(path)) {
                        counter = executeSplitGsi(insertCodeColumn, writeFileWithCodeZero, counter, path, readFile);
                    } else if (matcherTXT.matches(path)) {
                        counter = executeSplitTxt(insertCodeColumn, writeFileWithCodeZero, counter, path, readFile);
                    } else {
                        logger.warn("File format of '{}' are not supported (yet).", path.getFileName());
                    }
                } else {
                    logger.warn("File {} could not be read.", path.toString());
                }
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
        int counter = fileOperations(chkBoxInsertCodeColumn.getSelection(), chkBoxWriteCodeZero.getSelection());

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.splitFilesMessage);

            if (counter == 1) {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), files2read.length, counter);
            } else {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), files2read.length, counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangString(LABELS, Labels.successTextMsgBox), message);

            // set the counter for status bar information
            Main.countFileOps = counter;

            return true;
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.codeSplitFailed));

            return false;
        }
    }

    private boolean processFileOperationsDND() {
        // no code column and writer file for lines without code
        int counter = fileOperations(false, true);

        if (counter > 0) {
            // set the counter for status bar information
            Main.countFileOps = counter;

            return true;
        } else {
            return false;
        }
    }

} // end of CodeSplitterWidget
