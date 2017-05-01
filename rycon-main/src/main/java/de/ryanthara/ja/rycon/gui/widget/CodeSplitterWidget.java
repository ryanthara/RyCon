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
package de.ryanthara.ja.rycon.gui.widget;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.check.PathCheck;
import de.ryanthara.ja.rycon.check.TextCheck;
import de.ryanthara.ja.rycon.core.GSICodeSplit;
import de.ryanthara.ja.rycon.core.TextCodeSplit;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.gui.custom.*;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.io.LineReader;
import de.ryanthara.ja.rycon.io.LineWriter;
import de.ryanthara.ja.rycon.tools.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;

import static de.ryanthara.ja.rycon.gui.custom.Status.OK;

/**
 * Instances of this class implements a complete widget and it's functionality to split coordinate files by code.
 * <p>
 * The {@link CodeSplitterWidget} of RyCON is used to split measurement files (GSI or text format)
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
public class CodeSplitterWidget {

    private final String[] acceptableFileSuffixes = new String[]{"*.gsi", "*.txt"};
    private Button chkBoxInsertCodeColumn, chkBoxWriteCodeZero;
    private Path[] files2read;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;

    /**
     * Constructs the {@link CodeSplitterWidget} without any parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public CodeSplitterWidget() {
        files2read = new Path[0];
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
     *
     * @return success of file processing.
     */
    public boolean executeDropInjection() {
        boolean success = false;

        if ((files2read != null) && (files2read.length > 0)) {
            if (success = processFileOperationsDND()) {
                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    Main.statusBar.setStatus(String.format(Messages.prepareString("splitFilesStatus", Main.TEXT_SINGULAR), Main.countFileOps), OK);
                } else {
                    Main.statusBar.setStatus(String.format(Messages.prepareString("splitFilesStatus", Main.TEXT_PLURAL), Main.countFileOps), OK);
                }
            }
        }

        return success;
    }

    private void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnDestination() {
        String filterPath = Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT);

        Text input = inputFieldsComposite.getDestinationTextField();

        // Set the initial filter path according to anything selected or typed in
        if (!TextCheck.isEmpty(input)) {
            if (TextCheck.isDirExists(input)) {
                filterPath = input.getText();
            }
        }

        DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, input, FileChoosers.getString("splitterSourceText"),
                FileChoosers.getString("splitterSourceMessage"), filterPath);
    }

    private int actionBtnOk() {
        if (TextCheck.isEmpty(inputFieldsComposite.getSourceTextField()) ||
                TextCheck.isEmpty(inputFieldsComposite.getDestinationTextField())) {
            return -1;
        }

        if (files2read.length == 0) {
            files2read = new Path[1];
            files2read[0] = Paths.get(inputFieldsComposite.getSourceTextField().getText());
        } else {
            files2read = TextCheck.checkSourceAndDestinationText(
                    inputFieldsComposite.getSourceTextField(),
                    inputFieldsComposite.getDestinationTextField(), files2read);
        }

        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperations()) {

                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    Main.statusBar.setStatus(String.format(Messages.prepareString("splitFilesStatus", Main.TEXT_SINGULAR), Main.countFileOps), OK);
                } else {
                    Main.statusBar.setStatus(String.format(Messages.prepareString("splitFilesStatus", Main.TEXT_PLURAL), Main.countFileOps), OK);
                }
            }

            return 1;
        }
        return 0;
    }

    private void actionBtnOkAndExit() {
        if (actionBtnOk() == 1) {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", OK);

            innerShell.dispose();
        }
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnSource() {
        String[] filterNames = new String[]{
                FileChoosers.getString("filterNameGSI"),
                FileChoosers.getString("filterNameTXT")
        };

        String filterPath = Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT);

        // Set the initial filter path according to anything pasted or typed in
        if (!inputFieldsComposite.getSourceTextField().getText().trim().equals("")) {
            Path sourcePath = Paths.get(inputFieldsComposite.getSourceTextField().getText());

            if (Files.isDirectory(sourcePath)) {
                filterPath = inputFieldsComposite.getSourceTextField().getText();
            } else if (Files.isRegularFile(sourcePath)) {
                inputFieldsComposite.setDestinationTextFieldText(sourcePath.getFileName().toString());
            }
        }

        files2read = FileDialogs.showAdvancedFileDialog(
                innerShell, filterPath, FileChoosers.getString("splitterSourceText"), acceptableFileSuffixes,
                filterNames, inputFieldsComposite.getSourceTextField(), inputFieldsComposite.getDestinationTextField());
    }

    private void createDescription(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(Labels.getString("adviceText"));

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
        tip.setText(Labels.getString("tipSplitterWidget"));
    }

    private void createOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(Labels.getString("optionsText"));

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        chkBoxInsertCodeColumn = new Button(group, SWT.CHECK);
        chkBoxInsertCodeColumn.setSelection(false);
        chkBoxInsertCodeColumn.setText(CheckBoxes.getString("insertCodeColumn"));

        chkBoxWriteCodeZero = new Button(group, SWT.CHECK);
        chkBoxWriteCodeZero.setSelection(false);
        chkBoxWriteCodeZero.setText(CheckBoxes.getString("writeCodeZeroSplitter"));
    }

    private int executeSplitGSI(boolean insertCodeColumn, boolean writeFileWithCodeZero, int counter, Path file2read,
                                ArrayList<String> readFile) {
        GSICodeSplit gsiCodeSplit = new GSICodeSplit(readFile);
        ArrayList<ArrayList<String>> writeFile = gsiCodeSplit.processCodeSplit(insertCodeColumn, writeFileWithCodeZero);

        Iterator<Integer> codeIterator = gsiCodeSplit.getFoundCodes().iterator();

        // write file by file with one code
        for (ArrayList<String> lines : writeFile) {
            int code = codeIterator.next();

            String file2write = file2read.toString().substring(0, file2read.toString().length() - 4) + "_" +
                    Main.getParamCodeString() + "-" + code + ".GSI";

            LineWriter lineWriter = new LineWriter(file2write);

            if (lineWriter.writeFile(lines)) {
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
            int code = codeIterator.next();

            String file2write = file2read.toString().substring(0, file2read.toString().length() - 4) + "_" +
                    Main.getParamCodeString() + "-" + code + ".TXT";

            LineWriter lineWriter = new LineWriter(file2write);

            if (lineWriter.writeFile(lines)) {
                counter = counter + 1;
            }
        }
        return counter;
    }

    private int fileOperations(boolean insertCodeColumn, boolean writeFileWithCodeZero) {
        int counter = 0;

        for (Path path : files2read) {
            LineReader lineReader = new LineReader(path);

            if (lineReader.readFile()) {
                ArrayList<String> readFile = lineReader.getLines();

                // processFileOperations by differ between txt oder gsi files

                // processFileOperations and differ between 'normal' GSI files and LTOP 'GSL' files
                PathMatcher matcherGSI = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.GSI)");
                PathMatcher matcherTXT = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.TXT)");

                if (matcherGSI.matches(path)) {
                    counter = executeSplitGSI(insertCodeColumn, writeFileWithCodeZero, counter, path, readFile);
                } else if (matcherTXT.matches(path)) {
                    counter = executeSplitTxt(insertCodeColumn, writeFileWithCodeZero, counter, path, readFile);
                } else {
                    System.err.println("File format of " + path.getFileName() + " are not supported.");
                }
            } else {
                System.err.println("File " + path.getFileName() + " could not be read.");
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
        innerShell.setText(Labels.getString("splitterText"));
        innerShell.setSize(width, height);
        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;

        inputFieldsComposite = new InputFieldsComposite(this, innerShell);
        inputFieldsComposite.setLayout(gridLayout);

        createOptions(width);
        createDescription(width);

        new BottomButtonBar(this, innerShell);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private boolean processFileOperations() {
        int counter = fileOperations(chkBoxInsertCodeColumn.getSelection(), chkBoxWriteCodeZero.getSelection());

        if (counter > 0) {
            String message;

            if (counter == 1) {
                message = String.format(Messages.prepareString("splitFilesMessage", Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(Messages.prepareString("splitFilesMessage", Main.TEXT_PLURAL), counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    Labels.getString("successTextMsgBox"), message);

            // set the counter for status bar information
            Main.countFileOps = counter;
            return true;
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    Labels.getString("errorTextMsgBox"), Errors.getString("codeSplitFailed"));
            return false;
        }
    }

    private boolean processFileOperationsDND() {
        // no code column and write file for lines without code
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
