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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.util.ArrayList;


/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * The LevellingWidget of RyCON is used to convert levelling files for cad import.
 * Therefore a GSI based levelling file is prepared to a coordinate file with
 * no, x, y and measured height values. For the x- and y-values are the count line
 * numbers used.
 * <p>
 * On later versions of RyCON there will be support for more levelling formats.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>4: simplification and improvements, extract input fields and bottom button bar into separate classes</li>
 *     <li>3: code improvements and clean up</li>
 *     <li>2: basic improvements
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public class LevellingWidget {

    private Button chkBoxChangePoint = null;
    private File[] files2read = null;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell = null;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public LevellingWidget() {
        initUI();
    }

    private void initUI() {
        int height = Main.getRyCONWidgetHeight();
        int width = Main.getRyCONWidgetWidth();

        innerShell = new Shell(Main.shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);

        innerShell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                actionBtnCancel();
            }
        });

        innerShell.setText(I18N.getWidgetTitleLevelling());
        innerShell.setSize(width, height);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        innerShell.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;
        innerShell.setLayoutData(gridData);

        inputFieldsComposite = new InputFieldsComposite(this, innerShell, SWT.NONE);
        inputFieldsComposite.setLayout(gridLayout);

        createCheckBoxes();
        createDescription(width);

        new BottomButtonBar(this, innerShell, SWT.NONE);

        innerShell.setLocation(ShellCenter.centeredShellLocation(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private void createCheckBoxes() {
        chkBoxChangePoint = new Button(innerShell, SWT.CHECK);
        chkBoxChangePoint.setSelection(true);
        chkBoxChangePoint.setText(I18N.getBtnChkLevellingIgnoreChangePoints());
    }

    private void createDescription(int width) {
        Group groupDescription = new Group(innerShell, SWT.NONE);
        groupDescription.setText(I18N.getGroupTitleNumberInputAdvice());

        GridLayout gridLayout = new GridLayout(1, true);
        groupDescription.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        groupDescription.setLayoutData(gridData);

        Label tip = new Label(groupDescription, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
        tip.setText(String.format(I18N.getLabelTipLevellingWidget()));
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
        DirectoryDialog directoryDialog = new DirectoryDialog(innerShell);
        directoryDialog.setText(I18N.getFileChooserDirBaseTitle());
        directoryDialog.setMessage(I18N.getFileChooserDirBaseMessage());

        // Set the initial filter path according to anything selected or typed in
        if (inputFieldsComposite.getDestinationTextField().getText() == null) {
            directoryDialog.setFilterPath(Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
        } else {
            directoryDialog.setFilterPath(inputFieldsComposite.getDestinationTextField().getText());
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
                inputFieldsComposite.getDestinationTextField().setText(path);
            }
        }
    }

    private int actionBtnOk() {
        files2read = WidgetHelper.checkSourceAndDestinationTextFields(
                inputFieldsComposite.getSourceTextField(),
                inputFieldsComposite.getDestinationTextField(), files2read);

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
        FileDialog fileDialog = new FileDialog(innerShell, SWT.MULTI);
        fileDialog.setFilterPath(Main.pref.getUserPref(PreferenceHandler.DIR_PROJECTS));
        fileDialog.setText(I18N.getFileChooserLevellingSourceText());
        fileDialog.setFilterExtensions(new String[]{"*.gsi"});
        fileDialog.setFilterNames(new String[]{I18N.getFileChooserFilterNameGSI()});

        String firstFile = fileDialog.open();

        if (firstFile != null) {
            String[] files = fileDialog.getFileNames();

            files2read = new File[files.length];

            // hack for displaying file names without path in text field
            String concatString = "";

            String workingDir = fileDialog.getFilterPath();

            //for (String element : files) {
            for (int i = 0; i < files.length; i++) {
                concatString = concatString.concat(files[i]);
                concatString = concatString.concat(" ");

                files2read[i] = new File(workingDir + File.separator + files[i]);
            }

            inputFieldsComposite.getDestinationTextField().setText(fileDialog.getFilterPath());
            inputFieldsComposite.getSourceTextField().setText(concatString);
        }
    }

    private boolean processFileOperations() {
        boolean success;

        int counter = 0;

        LineReader lineReader;

        // read files
        for (File file2read : files2read) {
            lineReader = new LineReader(file2read);

            if (lineReader.readFile()) {
                // read
                ArrayList<String> readFile = lineReader.getLines();

                // processFileOperations
                LeicaGSIFileTools gsiTools = new LeicaGSIFileTools(readFile);
                ArrayList<String> writeFile = gsiTools.processLevelling2Cad(chkBoxChangePoint.getSelection());

                // write
                String file2write = file2read.toString().substring(0, file2read.toString().length() - 4) + "_LEVEL.GSI";
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
                msgBox.setMessage(String.format(I18N.getMsgLevellingSuccess(Main.TEXT_SINGULAR), counter));
            } else {
                msgBox.setMessage(String.format(I18N.getMsgLevellingSuccess(Main.TEXT_PLURAL), counter));
            }

            msgBox.setText(I18N.getMsgBoxTitleSuccess());
            msgBox.open();

            // set the counter for status bar information
            Main.countFileOps = counter;
            success = true;
        } else {
            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
            msgBox.setMessage(String.format(I18N.getMsgLevellingError()));
            msgBox.setText(I18N.getMsgBoxTitleError());
            msgBox.open();
            success = false;
        }

        return success;
    }

} // end of LevellingWidget
