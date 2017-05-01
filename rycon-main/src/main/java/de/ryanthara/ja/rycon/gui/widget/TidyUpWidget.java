/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
import de.ryanthara.ja.rycon.core.GSILTOPClean;
import de.ryanthara.ja.rycon.core.GSITidyUp;
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

import static de.ryanthara.ja.rycon.gui.custom.Status.OK;

/**
 * Instances of this class implements a complete widget and it's functionality.
 * <p>
 * With the TidyUpWidget of RyCON it is possible to clean up coordinate and
 * measurement files with a simple 'intelligence'.
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class TidyUpWidget {

    private final String[] acceptableFileSuffixes = new String[]{"*.gsi", "*.gsl"};
    private Button chkBoxHoldControlPoints;
    private Button chkBoxHoldStations;
    private Path[] files2read;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;

    /**
     * Constructs the {@link TidyUpWidget} without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public TidyUpWidget() {
        files2read = new Path[0];
        innerShell = null;

        initUI();
        handleFileInjection();
    }

    /**
     * Class constructor with a file array as parameter. This constructor type
     * is used for the drag and drop injection.
     * <p>
     * The file array of the dropped files will be checked for being valid and not being a directory.
     *
     * @param droppedFiles {@link Path} array from drop source
     */
    public TidyUpWidget(Path... droppedFiles) {
        files2read = PathCheck.getValidFiles(droppedFiles, acceptableFileSuffixes);
        innerShell = null;
    }

    /**
     * Execute the drop action as injection.
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
                    Main.statusBar.setStatus(String.format(Messages.prepareString("tidyUpStatus", Main.TEXT_SINGULAR), Main.countFileOps), OK);
                } else {
                    Main.statusBar.setStatus(String.format(Messages.prepareString("tidyUpStatus", Main.TEXT_PLURAL), Main.countFileOps), OK);
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

        DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, input, FileChoosers.getString("tidyUpSourceTitle"),
                FileChoosers.getString("tidyUpSourceMessage"), filterPath);

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
                    Main.statusBar.setStatus(String.format(Messages.prepareString("tidyUpStatus", Main.TEXT_SINGULAR), Main.countFileOps), OK);
                } else {
                    Main.statusBar.setStatus(String.format(Messages.prepareString("tidyUpStatus", Main.TEXT_PLURAL), Main.countFileOps), OK);
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
                FileChoosers.getString("filterNameLTOP")
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
                innerShell, filterPath, FileChoosers.getString("tidyUpSourceTitle"), acceptableFileSuffixes,
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
        tip.setText(Labels.getString("tipTidyUpWidget"));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(Labels.getString("optionsText"));

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        chkBoxHoldControlPoints = new Button(group, SWT.CHECK);
        chkBoxHoldControlPoints.setSelection(false);
        chkBoxHoldControlPoints.setText(CheckBoxes.getString("HoldControlPointsTidyUp"));

        chkBoxHoldStations = new Button(group, SWT.CHECK);
        chkBoxHoldStations.setSelection(false);
        chkBoxHoldStations.setText(CheckBoxes.getString("HoldStationsTidyUp"));
    }

    private int fileOperations(boolean holdStations, boolean holdControlPoints) {
        int counter = 0;
        LineReader lineReader;
        String editString = Main.pref.getUserPref(PreferenceHandler.PARAM_EDIT_STRING);
        String ltopString = Main.pref.getUserPref(PreferenceHandler.PARAM_LTOP_STRING);

        for (Path path : files2read) {
            lineReader = new LineReader(path);

            if (lineReader.readFile()) {
                ArrayList<String> readFile = lineReader.getLines();
                ArrayList<String> writeFile = null;
                String file2write = null;

                // processFileOperations and differ between 'normal' GSI files and LTOP 'GSL' files (case insensitive)
                PathMatcher matcherGSI = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.GSI)");
                PathMatcher matcherGSL = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.GSL)");

                if (matcherGSL.matches(path)) {
                    GSILTOPClean gsiltopClean = new GSILTOPClean(readFile);
                    writeFile = gsiltopClean.processLTOPClean();
                    file2write = path.toString().substring(0, path.toString().length() - 4) + "_" + ltopString + ".GSI";
                } else if (matcherGSI.matches(path)) {
                    GSITidyUp gsiTidyUp = new GSITidyUp(readFile);
                    writeFile = gsiTidyUp.processTidyUp(holdStations, holdControlPoints);
                    file2write = path.toString().substring(0, path.toString().length() - 4) + "_" + editString + ".GSI";
                }

                // write file line by line
                if (file2write != null) {
                    LineWriter lineWriter = new LineWriter(file2write);
                    if (lineWriter.writeFile(writeFile)) {
                        counter = counter + 1;
                    }
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
        innerShell.setText(Labels.getString("tidyUpText"));
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
        boolean success;

        int counter = fileOperations(chkBoxHoldStations.getSelection(), chkBoxHoldControlPoints.getSelection());

        if (counter > 0) {
            String message;

            if (counter == 1) {
                message = String.format(Messages.prepareString("tidyUpMessage", Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(Messages.prepareString("tidyUpMessage", Main.TEXT_PLURAL), counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    Labels.getString("successTextMsgBox"), message);

            // set the counter for status bar information
            Main.countFileOps = counter;
            success = true;
        } else {
            if (counter == 1) {
                MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                        Labels.getString("errorTextMsgBox"), Errors.prepareString("tidyUpFailed", Main.TEXT_SINGULAR));
            } else {
                MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                        Labels.getString("errorTextMsgBox"), Errors.prepareString("tidyUpFailed", Main.TEXT_PLURAL));
            }

            success = false;
        }

        return success;
    }

    private boolean processFileOperationsDND() {
        int counter = fileOperations(false, false);

        if (counter > 0) {
            // set the counter for status bar information
            Main.countFileOps = counter;
            return true;
        } else {
            return false;
        }
    }

} // end of TidyUpWidget
