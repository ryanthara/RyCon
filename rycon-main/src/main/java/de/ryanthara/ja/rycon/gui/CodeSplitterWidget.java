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

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.io.LineReader;
import de.ryanthara.ja.rycon.io.LineWriter;
import de.ryanthara.ja.rycon.tools.FileToolsLeicaGSI;
import de.ryanthara.ja.rycon.tools.FileToolsText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * The CodeSplitterWidget of RyCON is used to split measurement files (GSI or text format)
 * by code into several files. Each generated file contains only lines of one code. The code
 * is added to the file name of the written file.
 * <p>
 * This version of the CodeSplitterWidget supports the following file types:
 * <ul>
 *     <li>Leica GSI format files (GSI8 and GSI16)
 *     <li>text files with code (format no, code, x, y, z)
 * </ul>
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>5: code improvements, little corrections and clean up </li>
 *     <li>4: simplification and improvements, extract input fields and bottom button bar into separate classes </li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 5
 * @since 1
 */
public class CodeSplitterWidget {

    private Button chkBoxWriteCodeZero;
    private Button chkBoxDropCodeBlock;
    private File[] files2read = new File[0];
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell = null;
    private final String[] acceptableFileSuffixes = new String[]{"*.gsi", "*.txt"};

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public CodeSplitterWidget() {
        initUI();
    }

    /**
     * Class constructor with a file array as parameter. This constructor type
     * is used for the drag and drop injection.
     * <p>
     * The file array of the dropped files will be checked for being valid and not being a directory.
     *
     * @param droppedFiles file array from drop source
     */
    public CodeSplitterWidget(File[] droppedFiles) {
        files2read = WidgetHelper.checkForValidFiles(droppedFiles, acceptableFileSuffixes);
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
                    Main.statusBar.setStatus(String.format(I18N.getStatusCodeSplitSuccess(Main.TEXT_SINGULAR), Main.countFileOps), StatusBar.OK);
                } else {
                    Main.statusBar.setStatus(String.format(I18N.getStatusCodeSplitSuccess(Main.TEXT_PLURAL), Main.countFileOps), StatusBar.OK);
                }
            }

        }

        return success;
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
        innerShell.setText(I18N.getWidgetTitleSplitter());
        innerShell.setSize(width, height);
        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;

        inputFieldsComposite = new InputFieldsComposite(this, innerShell, SWT.NONE);
        inputFieldsComposite.setLayout(gridLayout);

        createOptions(width);
        createDescription(width);

        new BottomButtonBar(this, innerShell, SWT.NONE);

        innerShell.setLocation(ShellCenter.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private void createOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(I18N.getGroupTitleOptions());

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        chkBoxDropCodeBlock = new Button(group, SWT.CHECK);
        chkBoxDropCodeBlock.setSelection(false);
        chkBoxDropCodeBlock.setText(I18N.getBtnChkSplitterIgnoreCodeColumn());

        chkBoxWriteCodeZero = new Button(group, SWT.CHECK);
        chkBoxWriteCodeZero.setSelection(false);
        chkBoxWriteCodeZero.setText(I18N.getBtnChkSplitterWriteCodeZero());
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
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
        tip.setText(I18N.getLabelTipSplitterWidget());
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

        // Set the initial filter path according to anything selected or typed in
        if (!inputFieldsComposite.getDestinationTextField().getText().trim().equals("")) {
            File destinationDir = new File(inputFieldsComposite.getDestinationTextField().getText());
            if (destinationDir.isDirectory()) {
                filterPath = inputFieldsComposite.getDestinationTextField().getText();
            }
        }

        GuiHelper.showAdvancedDirectoryDialog(innerShell, inputFieldsComposite.getDestinationTextField(),
                I18N.getFileChooserSplitterSourceText(), I18N.getFileChooserSplitterSourceMessage(), filterPath);
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
                    Main.statusBar.setStatus(String.format(I18N.getStatusCodeSplitSuccess(Main.TEXT_SINGULAR), Main.countFileOps), StatusBar.OK);
                } else {
                    Main.statusBar.setStatus(String.format(I18N.getStatusCodeSplitSuccess(Main.TEXT_PLURAL), Main.countFileOps), StatusBar.OK);
                }

            }

            return 1;
        }
        return 0;
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

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnSource() {
        String[] filterNames = new String[] {
                I18N.getFileChooserFilterNameGSI(),
                I18N.getFileChooserFilterNameTXT()
        };

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

        files2read = GuiHelper.showAdvancedFileDialog(
                innerShell, SWT.MULTI, filterPath, I18N.getFileChooserSplitterSourceText(), acceptableFileSuffixes,
                filterNames, inputFieldsComposite.getSourceTextField(), inputFieldsComposite.getDestinationTextField());
    }

    private boolean processFileOperations() {
        boolean success;

        int counter = fileOperations(chkBoxDropCodeBlock.getSelection(), chkBoxWriteCodeZero.getSelection());

        if (counter > 0) {
            String message;

            if (counter == 1) {
                message = String.format(I18N.getMsgSplittingSuccess(Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(I18N.getMsgSplittingSuccess(Main.TEXT_PLURAL), counter);
            }

            GuiHelper.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), message);

            // set the counter for status bar information
            Main.countFileOps = counter;
            success = true;
        } else {
            GuiHelper.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleError(), I18N.getMsgSplittingError());
            success = false;
        }

        return success;
    }

    private int fileOperations(boolean createCodeColumn, boolean writeFileWithCodeZero) {
        int counter = 0;
        LineReader lineReader;

        for (File file2read : files2read) {
            lineReader = new LineReader(file2read);

            if (lineReader.readFile()) {
                ArrayList<String> readFile = lineReader.getLines();
                ArrayList<ArrayList<String>> writeFile;

                // processFileOperations by differ between txt oder gsi files
                String suffix = file2read.getName().toLowerCase();

                if (suffix.endsWith(".gsi")) {
                    FileToolsLeicaGSI gsiTools = new FileToolsLeicaGSI(readFile);
                    writeFile = gsiTools.processCodeSplit(createCodeColumn, writeFileWithCodeZero);

                    Iterator<Integer> codeIterator = gsiTools.getFoundCodes().iterator();

                    // write file by file with one code
                    for (ArrayList<String> lines : writeFile) {
                        int code = codeIterator.next();
                        String file2write = file2read.toString().substring(0, file2read.toString().length() - 4) + "_CODE-" + code + ".GSI";
                        LineWriter lineWriter = new LineWriter(file2write);
                        if (lineWriter.writeFile(lines)) {
                            counter++;
                        }
                    }
                } else if (suffix.endsWith(".txt")) {
                    FileToolsText fileToolsText = new FileToolsText(readFile);
                    writeFile = fileToolsText.processCodeSplit(createCodeColumn);

                    Iterator<Integer> codeIterator = fileToolsText.getFoundCodes().iterator();

                    // write file by file with one code
                    for (ArrayList<String> lines : writeFile) {
                        int code = codeIterator.next();
                        String file2write = file2read.toString().substring(0, file2read.toString().length() - 4) + "_CODE-" + code + ".TXT";
                        LineWriter lineWriter = new LineWriter(file2write);
                        if (lineWriter.writeFile(lines)) {
                            counter++;
                        }
                    }
                } else {
                    System.err.println("File format of " + file2read.getName() + " are not supported.");
                }
            } else {
                System.err.println("File " + file2read.getName() + " could not be read.");
            }
        }

        return counter;
    }

    private boolean processFileOperationsDND() {
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
