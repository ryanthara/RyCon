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
import de.ryanthara.ja.rycon.core.LogfileClearUp;
import de.ryanthara.ja.rycon.core.logfile.LogfileAnalyzer;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.custom.BottomButtonBar;
import de.ryanthara.ja.rycon.ui.custom.FileDialogs;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.util.StringUtils;
import de.ryanthara.ja.rycon.util.check.PathCheck;
import de.ryanthara.ja.rycon.util.check.TextCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;

/**
 * Instances of this class provides functions to analyze log files to show an overview sheet
 * with the results of a stake out or reference line measurement.
 * <p>
 * Log files are structured and in a chronological order.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class ReportWidget extends AbstractWidget {

    private final static Logger logger = Logger.getLogger(ReportWidget.class.getName());

    private final String[] acceptableFileSuffixes = {"*.txt"};
    private Shell parent;
    private Path[] files2read;
    private Shell innerShell;
    private Text logfilePath;

    /**
     * Constructs a new instance of this class without parameters.
     *
     * @param parent parent shell
     */
    public ReportWidget(final Shell parent) {
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
    private ReportWidget(Path... droppedFiles) {
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

    @Override
    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    @Override
    boolean actionBtnOk() {
        if (TextCheck.isEmpty(logfilePath)) {
            return false;
        }

        if (files2read.length == 0) {
            files2read = new Path[1];
            files2read[0] = Paths.get(logfilePath.getText());
        }

        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperations()) {
                updateStatus();
            }

            return true;
        }

        return false;
    }

    @Override
    void actionBtnOkAndExit() {
        if (actionBtnOk()) {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", OK);

            innerShell.dispose();
        }
    }

    @Override
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
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.reportText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createInputFieldComposite();

        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private void actionBtnLogfilePath() {
        String[] filterNames = new String[]{
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.filterNameLogfileTxt)
        };

        String filterPath = Main.pref.getUserPreference(PreferenceKeys.LAST_COPIED_LOGFILE);

        // Set the initial filter path according to anything selected or typed in
        if (!TextCheck.isEmpty(logfilePath)) {
            if (TextCheck.isFileExists(logfilePath)) {
                filterPath = logfilePath.getText();
            }
        }

        Optional<Path[]> files = FileDialogs.showAdvancedFileDialog(
                innerShell,
                filterPath,
                ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.fileLogfile),
                acceptableFileSuffixes,
                filterNames,
                logfilePath);

        if (files.isPresent()) {
            files2read = files.get();
        } else {
            logger.log(Level.SEVERE, "can not get the 'logfile.txt' files");
        }
    }

    private void createInputFieldComposite() {
        Group group = new Group(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Sizes.RyCON_WIDGET_WIDTH.getValue();

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label logfilePathLabel = new Label(group, SWT.NONE);
        logfilePathLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.logfilePath));

        Path logfile = Paths.get(Main.pref.getUserPreference(PreferenceKeys.LAST_COPIED_LOGFILE));

        logfilePath = new Text(group, SWT.SINGLE | SWT.BORDER);
        logfilePath.setText(logfile.toString());
        // prevent shortcuts for execute when the text fields are empty
        logfilePath.addListener(SWT.Traverse, this::handleEvent);


        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        logfilePath.setLayoutData(gridData);

        Button btnLogfilePath = new Button(group, SWT.NONE);
        btnLogfilePath.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.chooseLogfilePathText));
        btnLogfilePath.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.chooseLogfilePathText));
        btnLogfilePath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnLogfilePath();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnLogfilePath.setLayoutData(gridData);

        Control[] tabulatorKeyOrder = new Control[]{
                logfilePath, btnLogfilePath
        };

        group.setTabList(tabulatorKeyOrder);
    }

    private int fileOperations() {
        int counter = 0;

        LineReader lineReader;

        for (Path file2read : files2read) {
            if (file2read != null) {
                lineReader = new LineReader(file2read);

                if (lineReader.readFile(false)) {
                    ArrayList<String> readFile = lineReader.getLines();
                    ArrayList<String> cleanFile;
                    Path path = file2read.getFileName();

                    if (path != null) {
                        final String fileName = path.toString();

                        if (fileName.toUpperCase().endsWith("LOGFILE.TXT")) {
                            LogfileClearUp logfileClearUp = new LogfileClearUp(readFile);
                            cleanFile = logfileClearUp.processCleanFull();

                            LogfileAnalyzer logfileAnalyzer = new LogfileAnalyzer(cleanFile);

                            if (logfileAnalyzer.analyzeLeicaGeosystemsLogfile()) {
                                System.out.println("Leica Geosystems logfile.txt analyzed.");

                                counter = counter + 1;
                            }
                        }
                    }
                }
            }
        }

        return counter;
    }

    private void handleEvent(Event event) {
        if (!(this.logfilePath.getText().trim().equals(""))) {
            if (((event.stateMask & SWT.SHIFT) == SWT.SHIFT) && (event.detail == SWT.TRAVERSE_RETURN)) {
                actionBtnOk();
            } else if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                actionBtnOkAndExit();
            }
        }
    }

    private void handleFileInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            logfilePath.setText(files);
        }
    }

    private boolean processFileOperations() {
        boolean success;

        final int counter = fileOperations();

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.reportMessage);

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
            final String message = ResourceBundleUtils.getLangString(ERRORS, Errors.reportFailed);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), message);

            success = false;
        }

        return success;
    }

    private boolean processFileOperationsDND() {
        int counter = fileOperations();

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

        final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.reportStatus);

        // use counter to display different text on the status bar
        if (Main.countFileOps == 1) {
            status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), Main.countFileOps);
        } else {
            status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), Main.countFileOps);
        }

        Main.statusBar.setStatus(status, OK);
    }

} // end of ReportWidget
