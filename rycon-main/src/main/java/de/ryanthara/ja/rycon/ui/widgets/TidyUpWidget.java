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

package de.ryanthara.ja.rycon.ui.widgets;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.core.GsiLtopClean;
import de.ryanthara.ja.rycon.core.GsiTidyUp;
import de.ryanthara.ja.rycon.core.LogfileClean;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;

/**
 * Instances of this class implement a complete widgets and it's functionality.
 * <p>
 * With the TidyUpWidget of RyCON it is possible to clean up coordinate, measurement
 * and <tt>Leica Geosystems</tt> logfile.txt files with a simple 'intelligence'.
 *
 * @author sebastian
 * @version 9
 * @since 1
 */
public class TidyUpWidget extends AbstractWidget {

    private final static Logger logger = Logger.getLogger(TidyUpWidget.class.getName());

    private final String[] acceptableFileSuffixes = {"*.gsi", "*.gsl", "*.txt"};
    private Shell parent;
    private Button chkBoxCleanBlocksByContent;
    private Button chkBoxHoldControlPoints;
    private Button chkBoxHoldStations;
    private Path[] files2read;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;

    /**
     * Constructs the {@link TidyUpWidget} without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public TidyUpWidget(final Shell parent) {
        this.parent = parent;

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
     */
    public void executeDropInjection() {
        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperationsDND()) {
                updateStatus();
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
                updateStatus();
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
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.tidyUpText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createInputFieldsComposite();
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
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameGSI),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameLTOP),
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameLogfileTXT)
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
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.tidyUpSourceText),
                acceptableFileSuffixes,
                filterNames,
                inputFieldsComposite.getSourceTextField(),
                inputFieldsComposite.getTargetTextField());

        if (files.isPresent()) {
            files2read = files.get();
        } else {
            logger.log(Level.SEVERE, "can not get the reader files");
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
        tip.setText(ResourceBundleUtils.getLangString(LABELS, Labels.tipTidyUpWidget));
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

        chkBoxHoldStations = new Button(group, SWT.CHECK);
        chkBoxHoldStations.setSelection(false);
        chkBoxHoldStations.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.holdStationsTidyUp));

        chkBoxHoldControlPoints = new Button(group, SWT.CHECK);
        chkBoxHoldControlPoints.setSelection(false);
        chkBoxHoldControlPoints.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.holdControlPointsTidyUp));

        chkBoxCleanBlocksByContent = new Button(group, SWT.CHECK);
        chkBoxCleanBlocksByContent.setSelection(true);
        chkBoxCleanBlocksByContent.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.cleanBlocksByContent));
    }

    private int fileOperations(boolean holdStations, boolean holdControlPoints) {
        int counter = 0;
        LineReader lineReader;
        final String editString = Main.pref.getUserPreference(PreferenceKeys.PARAM_EDIT_STRING);
        final String ltopString = Main.pref.getUserPreference(PreferenceKeys.PARAM_LTOP_STRING);

        for (Path path : files2read) {
            lineReader = new LineReader(path);

            if (lineReader.readFile(false)) {
                ArrayList<String> readFile = lineReader.getLines();
                ArrayList<String> writeFile;

                final String fileName = path.getFileName().toString();

                if (fileName.toUpperCase().endsWith("GSI")) {
                    GsiTidyUp gsiTidyUp = new GsiTidyUp(readFile);
                    writeFile = gsiTidyUp.processTidyUp(holdStations, holdControlPoints);

                    if (WriteFile2Disk.writeFile2Disk(path, writeFile, editString, ".GSI")) {
                        counter = counter + 1;
                    }
                } else if (fileName.toUpperCase().endsWith(".GSL")) {
                    GsiLtopClean gsiLtopClean = new GsiLtopClean(readFile);
                    writeFile = gsiLtopClean.processLTOPClean();

                    if (WriteFile2Disk.writeFile2Disk(path, writeFile, ltopString, ".GSI")) {
                        counter = counter + 1;
                    }
                } else if (fileName.toUpperCase().endsWith("LOGFILE.TXT")) {
                    LogfileClean logfileClean = new LogfileClean(readFile);
                    if (chkBoxCleanBlocksByContent != null) {
                        writeFile = logfileClean.processTidyUp(chkBoxCleanBlocksByContent.getSelection());
                    } else {
                        writeFile = logfileClean.processTidyUp(true);
                    }

                    if (WriteFile2Disk.writeFile2Disk(path, writeFile, editString, ".txt")) {
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

    private boolean processFileOperations() {
        boolean success;

        final int counter = fileOperations(chkBoxHoldStations.getSelection(), chkBoxHoldControlPoints.getSelection());

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.tidyUpMessage);

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
            final String message = ResourceBundleUtils.getLangString(ERRORS, Errors.tidyUpFailed);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), message);

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

    private void updateStatus() {
        String status;

        final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.tidyUpStatus);

        // use counter to display different text on the status bar
        if (Main.countFileOps == 1) {
            status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), Main.countFileOps);
        } else {
            status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), Main.countFileOps);
        }

        Main.statusBar.setStatus(status, OK);
    }

} // end of TidyUpWidget
