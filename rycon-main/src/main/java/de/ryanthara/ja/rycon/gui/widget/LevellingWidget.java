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
import de.ryanthara.ja.rycon.check.FileCheck;
import de.ryanthara.ja.rycon.check.TextCheck;
import de.ryanthara.ja.rycon.converter.gsi.Nigra2GSI;
import de.ryanthara.ja.rycon.core.GSILevelling2Cad;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.gui.custom.*;
import de.ryanthara.ja.rycon.i18n.I18N;
import de.ryanthara.ja.rycon.io.LineReader;
import de.ryanthara.ja.rycon.io.LineWriter;
import de.ryanthara.ja.rycon.tools.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.util.ArrayList;


/**
 * Instances of this class implements a complete widget for process levelling files.
 * <p>
 * The LevellingWidget of RyCON is used to convert levelling files for cad import.
 * Therefore a GSI based levelling file is prepared to a coordinate file with
 * no, x, y and measured height values. For the x- and y-values are the count line
 * numbers used.
 * <p>
 * On later versions of RyCON there will be support for more levelling formats.
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class LevellingWidget {

    private final String[] acceptableFileSuffixes = new String[]{"*.gsi", "*.asc"};
    private Button chkBoxHoldChangePoint;
    private File[] files2read;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;

    /**
     * Constructs a new instance of this class without any parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public LevellingWidget() {
        files2read = new File[0];
        initUI();
        handleFileInjection();
    }

    /**
     * Constructs a new instance of this class given a file array as parameter. This constructor type
     * is used for the drag and drop injection.
     * <p>
     * The file array of the dropped files will be checked for being valid and not being a directory.
     *
     * @param droppedFiles file array from drop source
     */
    public LevellingWidget(File[] droppedFiles) {
        files2read = FileCheck.checkForValidFiles(droppedFiles, acceptableFileSuffixes);
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
                    Main.statusBar.setStatus(String.format(I18N.getStatusPrepareLevelSuccess(Main.TEXT_SINGULAR), Main.countFileOps), StatusBar.OK);
                } else {
                    Main.statusBar.setStatus(String.format(I18N.getStatusPrepareLevelSuccess(Main.TEXT_PLURAL), Main.countFileOps), StatusBar.OK);
                }
            }
        }

        return success;
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
        if (!TextCheck.checkIsEmpty(input)) {
            if (TextCheck.checkDirExists(input)) {
                filterPath = input.getText();
            }
        }

        DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, input, I18N.getFileChooserLevellingSourceText(),
                I18N.getFileChooserLevellingSourceMessage(), filterPath);
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
            files2read = TextCheck.checkSourceAndDestinationText(
                    inputFieldsComposite.getSourceTextField(),
                    inputFieldsComposite.getDestinationTextField(), files2read);
        }

        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperations()) {
                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    Main.statusBar.setStatus(String.format(I18N.getStatusPrepareLevelSuccess(Main.TEXT_SINGULAR), Main.countFileOps), StatusBar.OK);
                } else {
                    Main.statusBar.setStatus(String.format(I18N.getStatusPrepareLevelSuccess(Main.TEXT_PLURAL), Main.countFileOps), StatusBar.OK);
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
        String[] filterNames = new String[]{
                I18N.getFileChooserFilterNameGSI(),
                I18N.getFileChooserFilterNameNIGRA()
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

        files2read = FileDialogs.showAdvancedFileDialog(
                innerShell, SWT.MULTI, filterPath, I18N.getFileChooserSplitterSourceText(), acceptableFileSuffixes,
                filterNames, inputFieldsComposite.getSourceTextField(), inputFieldsComposite.getDestinationTextField());
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
        tip.setText(I18N.getLabelTipLevellingWidget());
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

        chkBoxHoldChangePoint = new Button(group, SWT.CHECK);
        chkBoxHoldChangePoint.setSelection(true);
        chkBoxHoldChangePoint.setText(I18N.getBtnChkLevellingIgnoreChangePoints());
    }

    private int fileOperations(boolean holdChangePoints) {
        int counter = 0;

        for (File file2read : files2read) {
            LineReader lineReader = new LineReader(file2read);

            if (lineReader.readFile()) {
                // read
                ArrayList<String> readFile = lineReader.getLines();

                String[] fileNameAndSuffix = file2read.getName().split("\\.(?=[^.]+$)");

                ArrayList<String> writeFile;

                if (fileNameAndSuffix[1].equalsIgnoreCase("GSI")) {
                    GSILevelling2Cad gsiLevelling2Cad = new GSILevelling2Cad(readFile);
                    writeFile = gsiLevelling2Cad.processLevelling2Cad(holdChangePoints);
                } else if (fileNameAndSuffix[1].equalsIgnoreCase("ASC")) {
                    Nigra2GSI nigra2GSI = new Nigra2GSI(readFile);
                    writeFile = nigra2GSI.convertNIGRA2GSI(Main.getGSI16());
                } else {
                    System.err.println("File " + file2read.getName() + " is not supported (yet).");
                    break;
                }

                String file2write = file2read.toString().substring(0, file2read.toString().length() - 4) + "_LEVEL.GSI";

                LineWriter lineWriter = new LineWriter(file2write);

                if (lineWriter.writeFile(writeFile)) {
                    counter = counter + 1;
                }
            } else {
                System.err.println("File " + file2read.getName() + " could not be read.");
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
        innerShell.setText(I18N.getWidgetTitleLevelling());
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

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private boolean processFileOperations() {
        boolean success;

        int counter = fileOperations(chkBoxHoldChangePoint.getSelection());

        if (counter > 0) {
            String message;

            if (counter == 1) {
                message = String.format(I18N.getMsgLevellingSuccess(Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(I18N.getMsgLevellingSuccess(Main.TEXT_PLURAL), counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), message);

            // set the counter for status bar information
            Main.countFileOps = counter;
            success = true;
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleError(), I18N.getMsgLevellingError());
            success = false;
        }

        return success;
    }

    private boolean processFileOperationsDND() {
        // change points are ignored
        int counter = fileOperations(false);

        if (counter > 0) {
            // set the counter for status bar information
            Main.countFileOps = counter;
            return true;
        } else {
            return false;
        }
    }

} // end of LevellingWidget.java
