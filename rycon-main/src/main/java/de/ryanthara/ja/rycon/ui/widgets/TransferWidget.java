/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.widget
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
import de.ryanthara.ja.rycon.core.GsiClearUp;
import de.ryanthara.ja.rycon.core.LogfileClean;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.nio.FileNameExtension;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.nio.WriteFile2Disk;
import de.ryanthara.ja.rycon.nio.filter.FilesFilter;
import de.ryanthara.ja.rycon.nio.filter.GsiFilter;
import de.ryanthara.ja.rycon.nio.filter.TxtFilter;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.custom.BottomButtonBar;
import de.ryanthara.ja.rycon.ui.custom.DirectoryDialogs;
import de.ryanthara.ja.rycon.ui.custom.DirectoryDialogsTypes;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.util.BoundedTreeSet;
import de.ryanthara.ja.rycon.util.FileUtils;
import de.ryanthara.ja.rycon.util.StringUtils;
import de.ryanthara.ja.rycon.util.check.PathCheck;
import de.ryanthara.ja.rycon.util.check.TextCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;

/**
 * A widget which is used to transfer measurement data from a card reader
 * to the file system.
 * <p>
 * The {@code TransferWidget} is a complete widget of RyCON which is used
 * to transfer (copy or move) different files from a card reader or a mounted
 * card reader folder to the file system with a given project structure.
 * <p>
 * This first basic implementation uses the folder structure based on the
 * Leica Geosystems presetting. In a future version of RyCON this will be
 * changed to a more flexible solution with different card structures.
 * <p>
 * The user can choose jobs, export files and different content from the
 * card reader which will be transferred to the file system. It is possible
 * to choose a new project or one of the last used ones.
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class TransferWidget extends AbstractWidget {

    private final static Logger logger = Logger.getLogger(TransferWidget.class.getName());
    private final Shell parent;
    private ArrayList<Path> allJobsFiles;
    private Button chkBoxCleanLogfile;
    private Button chkBoxCleanMeasurementFile;
    private Button chkBoxMoveOption;
    private Shell innerShell;
    private Text cardReaderPath;
    private Text targetProjectPath;
    private TreeSet<Path> allDatas;
    private TreeSet<Path> allExports;
    private List dataList;
    private List exportList;
    private List jobList;
    private List lastUsedProjectsList;
    private BoundedTreeSet<String> lastUsedProjects;
    private int countCopiedFiles;

    /**
     * Constructs the {@link TransferWidget} with a parameter for the parent shell.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public TransferWidget(final Shell parent) {
        this.parent = parent;

        initUI();
    }

    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        innerShell.dispose();
    }

    boolean actionBtnOk() {
        final String storedCardReaderPath = Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER);

        boolean success;

        countCopiedFiles = 0;

        if (checkForAvailableLeicaCardStructure(storedCardReaderPath)) {
            // one or more items selected in a list
            final int number = dataList.getSelectionCount() + exportList.getSelectionCount() + jobList.getSelectionCount();

            if (number > 0) {
                if (checkIsTargetPathValid()) {
                    success = copyMoveAction();

                    if (success) {
                        updateLastUsedProjectsListAndPreferences();

                        checkCardReaderPathAndUpdateListsAndPreferences();

                        Main.statusBar.setStatus(ResourceBundleUtils.getLangString(MESSAGES, Messages.cardReaderFilesCopySuccessful), OK);
                    }

                    String helper, message;

                    if (chkBoxMoveOption.getSelection()) {
                        helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.transferMoveMessage);
                    } else {
                        helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.transferCopyMessage);
                    }

                    if (countCopiedFiles == 1) {
                        message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), countCopiedFiles);
                    } else {
                        message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), countCopiedFiles);
                    }

                    if (countCopiedFiles > 0) {
                        MessageBoxes.showMessageBox(parent, SWT.ICON_INFORMATION,
                                ResourceBundleUtils.getLangString(MESSAGES, Messages.transferText), message);
                    }

                    return success;
                } else {
                    actionBtnChooseProjectPath();
                }
            } else {
                MessageBoxes.showMessageBox(parent, SWT.ICON_INFORMATION,
                        ResourceBundleUtils.getLangString(ERRORS, Errors.transferNoDataSelectedText),
                        ResourceBundleUtils.getLangString(ERRORS, Errors.transferNoDataSelected));
            }
        } else {
            showCardReaderNotExitsWarning();
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
        final int height = Sizes.RyCON_WIDGET_HEIGHT.getValue();
        final int width = Sizes.RyCON_WIDGET_WIDTH.getValue() + 205;

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell = new Shell(parent, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, event -> actionBtnCancel());
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.transferText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createGroupCardReader();
        createGroupChooseData(width);
        createGroupChooseTarget();
        createGroupOptions(width);
        createGroupDescription(width);

        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();

        initCardReading();
    }

    private void actionBtnCardReaderPath() {
        String filterPath = Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER);

        // Set the initial filter path according to anything selected or typed in
        if (!TextCheck.isEmpty(cardReaderPath)) {
            if (TextCheck.isDirExists(cardReaderPath)) {
                filterPath = cardReaderPath.getText();
            }
        }

        DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, cardReaderPath,
                DirectoryDialogsTypes.DIR_CARD_READER.getText(),
                DirectoryDialogsTypes.DIR_CARD_READER.getMessage(),
                filterPath);

        checkCardReaderPathAndUpdateListsAndPreferences();
    }

    private void actionBtnChooseProjectPath() {
        String filterPath = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT);

        // Set the initial filter path according to anything selected or typed in
        if (!TextCheck.isEmpty(targetProjectPath)) {
            if (TextCheck.isDirExists(targetProjectPath)) {
                filterPath = targetProjectPath.getText();
            }
        }

        DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, targetProjectPath,
                DirectoryDialogsTypes.DIR_PROJECT.getText(),
                DirectoryDialogsTypes.DIR_PROJECT.getMessage(),
                filterPath);
    }

    private void checkCardReaderPathAndUpdateListsAndPreferences() {
        final String delimiter = FileSystems.getDefault().getSeparator();

        if (!TextCheck.isEmpty(cardReaderPath) && !cardReaderPath.getText().endsWith(delimiter)) {
            clearListsAndFields();

            if (TextCheck.isDirExists(cardReaderPath)) {
                Main.pref.setUserPreference(PreferenceKeys.DIR_CARD_READER, cardReaderPath.getText());
                readCardFolders(cardReaderPath.getText());
            }
        }
    }

    /**
     * Checks whether the card reader path exists and if a card is inserted.
     * <p>
     * In this version of RyCON the card check is done against a Leica based card structure.
     *
     * @param cardReaderPathString path of the card reader
     *
     * @return true if card reader path exists and given structure is present
     */
    private boolean checkForAvailableLeicaCardStructure(String cardReaderPathString) {
        final Path cardReaderPath = Paths.get(cardReaderPathString);

        if (PathCheck.isValid(cardReaderPath)) {
            /*
             * Check for special folder structure of a Leica Geosystems CF-/SD-Card
             * ./Data - log files
             * ./DBX  - database files
             * ./GSI  - GSI files
             */
            final String separator = FileSystems.getDefault().getSeparator();

            final Path dataPath = Paths.get(cardReaderPathString + separator + "Data");
            final Path dbxPath = Paths.get(cardReaderPathString + separator + "DBX");
            final Path gsiPath = Paths.get(cardReaderPathString + separator + "GSI");

            return PathCheck.isValid(dataPath) && PathCheck.isValid(dbxPath) && PathCheck.isValid(gsiPath);
        }

        return false;
    }

    private boolean checkIsTargetPathValid() {
        final String delimiter = FileSystems.getDefault().getSeparator();

        // last used project list contains items and one of them is selected
        if ((lastUsedProjectsList.getItemCount() > 0) && (lastUsedProjectsList.getSelectionCount() > 0)) {
            final String projectPath = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT);
            final String lastUsedProject = projectPath + delimiter + lastUsedProjectsList.getSelection()[0];
            targetProjectPath.setText(lastUsedProject);

            return true;
        } else {
            // list is empty or no element selected -> use text field
            if (!TextCheck.isEmpty(targetProjectPath)) {
                if (TextCheck.isDirExists(targetProjectPath)) {
                    return true;
                } else {
                    // checks if user entered only a valid project number without path
                    final String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT) + delimiter + targetProjectPath.getText();

                    if (PathCheck.directoryExists(dir)) {
                        targetProjectPath.setText(dir);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void clearListsAndFields() {
        allDatas.clear();
        dataList.removeAll();
        allExports.clear();
        exportList.removeAll();
        allJobsFiles.clear();
        jobList.removeAll();
    }

    private boolean copyFiles(Path source, Path target, boolean overwriteExisting) {
        boolean success = false;

        try {
            if (chkBoxMoveOption.getSelection()) {
                FileUtils.move(source, target, overwriteExisting);
            } else {
                FileUtils.copy(source, target, overwriteExisting);
            }

            countCopiedFiles++;

            success = true;
        } catch (FileAlreadyExistsException e) {
            showFileExistsWarning(target);
        } catch (IOException e) {
            if (source != null && target != null) {
                Path sourceFileName = source.getFileName();
                Path targetFileName = target.getFileName();

                if ((sourceFileName != null) && (targetFileName != null)) {
                    logger.log(Level.SEVERE, "error while copying " + sourceFileName.toString() +
                            " to " + targetFileName.toString(), e);
                } else {
                    logger.log(Level.SEVERE, "Source or target path is null.");
                }
            } else {
                logger.log(Level.SEVERE, "error while copying files, null reference in source or target");
            }
        }

        return success;
    }

    private boolean copyMoveAction() {
        final boolean overwriteExisting = Boolean.parseBoolean(Main.pref.getUserPreference(PreferenceKeys.OVERWRITE_EXISTING));

        return copyMoveExportFiles(overwriteExisting) | copyMoveJobFiles(overwriteExisting) | copyMoveDataFiles(overwriteExisting);
    }

    private boolean copyMoveDataFiles(boolean overwriteExisting) {
        final String delimiter = FileSystems.getDefault().getSeparator();

        final String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER) + delimiter +
                Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_DATA_FILES);

        final String[] selectedDataFiles = dataList.getSelection();

        if (PathCheck.directoryExists(dir)) {
            String destinationPath = targetProjectPath.getText() + delimiter + Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT_LOG_FILES);

            boolean success = false;

            for (String dataFileName : selectedDataFiles) {
                for (Path source : allDatas) {
                    if (source != null) {
                        if (PathCheck.fileExists(source)) {
                            if (source.endsWith(dataFileName)) {
                                Path target;
                                Path p = source.getFileName();

                                if (p != null) {
                                    // insert current date into logfile name
                                    if (source.endsWith("logfile.txt")) {
                                        final String fileName = p.toString();
                                        final LocalDate localDate = LocalDate.now();
                                        final String logFileName = fileName;
                                        final String newFileName = logFileName.replaceAll("logfile.txt", localDate.toString() + "_logfile.txt");

                                        target = Paths.get(destinationPath + delimiter + newFileName);
                                    } else {
                                        target = Paths.get(destinationPath + delimiter + p.toString());
                                    }
                                    success = copyFiles(source, target, overwriteExisting);

                                    if (success) {
                                        Main.pref.setUserPreference(PreferenceKeys.LAST_COPIED_LOGFILE, target.toString());
                                    }

                                    if (chkBoxCleanLogfile.getSelection() && success) {
                                        final String editString = Main.pref.getUserPreference(PreferenceKeys.PARAM_EDIT_STRING);

                                        LineReader lineReader = new LineReader(target);

                                        if (lineReader.readFile(false)) {
                                            LogfileClean logfileClean = new LogfileClean(lineReader.getLines());

                                            ArrayList<String> writeFile = logfileClean.processClean(true);

                                            WriteFile2Disk.writeFile2Disk(target, writeFile, editString, FileNameExtension.TXT.getExtension());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return success;
        }

        return false;
    }

    /*
     * mostly the same code as copyMoveJobFiles
     */
    private boolean copyMoveExportFiles(boolean overwriteExisting) {
        final String delimiter = FileSystems.getDefault().getSeparator();

        final String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER) + delimiter +
                Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_EXPORT_FILES);

        final String[] selectedExports = exportList.getSelection();

        if (PathCheck.directoryExists(dir)) {
            boolean success = false;

            for (String exportedJobName : selectedExports) {
                success = false;

                for (Path source : allExports) {
                    if (source != null) {
                        if (source.endsWith(exportedJobName)) {
                            if (PathCheck.fileExists(source)) {
                                Path p = source.getFileName();

                                if (p != null) {
                                    final String fileName = p.toString();
                                    final String dest = targetProjectPath.getText() + delimiter + Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT_MEASUREMENT_FILES);
                                    final Path target = Paths.get(dest + delimiter + fileName);

                                    success = copyFiles(source, target, overwriteExisting);

                                    if (chkBoxCleanMeasurementFile.getSelection() && success) {
                                        final String editString = Main.pref.getUserPreference(PreferenceKeys.PARAM_EDIT_STRING);

                                        LineReader lineReader = new LineReader(target);

                                        if (lineReader.readFile(true)) {
                                            GsiClearUp gsiClearUp = new GsiClearUp(lineReader.getLines());

                                            ArrayList<String> writeFile = gsiClearUp.processClearUp(false, false);

                                            WriteFile2Disk.writeFile2Disk(target, writeFile, editString, FileNameExtension.LEICA_GSI.getExtension());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return success;

        }

        return false;
    }

    /*
     * mostly the same code as copyMoveExportFiles
     */
    private boolean copyMoveJobFiles(boolean overwriteExisting) {
        final String delimiter = FileSystems.getDefault().getSeparator();

        final String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER) + delimiter +
                Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_JOB_FILES);

        final String[] selectedJobs = jobList.getSelection();

        if (PathCheck.directoryExists(dir)) {
            boolean success = false;

            for (String jobName : selectedJobs) {
                success = false;

                for (Path source : allJobsFiles) {
                    if (source != null) {
                        Path p = source.getFileName();

                        if (p != null) {
                            final String fileName = p.toString();

                            if (fileName.startsWith(jobName)) {
                                if (PathCheck.fileExists(source)) {
                                    final String dest = targetProjectPath.getText() + delimiter + Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT_JOB_FILES);
                                    final Path target = Paths.get(dest + delimiter + fileName);

                                    success = copyFiles(source, target, overwriteExisting);
                                }
                            }
                        }
                    }
                }
            }

            return success;

        }

        return false;
    }

    private void createGroupCardReader() {
        Group group = new Group(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Sizes.RyCON_WIDGET_WIDTH.getValue();

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label cardReaderLabel = new Label(group, SWT.NONE);
        cardReaderLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.cardReaderPath));

        Path cardReader = Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER));

        cardReaderPath = new Text(group, SWT.SINGLE | SWT.BORDER);
        cardReaderPath.setText(cardReader.toString());

        /*
         * platform independent key handling for ENTER to prevent action handling on empty text field
         * and TAB to be used in a normal way to jump over controls
         */
        cardReaderPath.addListener(SWT.Traverse, event -> {
            if (!cardReaderPath.getText().trim().equals("")) {
                if ((event.stateMask & SWT.SHIFT) == SWT.SHIFT && event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnOkAndExit();
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnOk();
                }
            }

            if (event.detail == SWT.TRAVERSE_TAB_NEXT || event.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                event.doit = true;
            }
        });

        // reacts on keyboard or clipboard input for updating the lists
        cardReaderPath.addModifyListener(arg0 -> checkCardReaderPathAndUpdateListsAndPreferences());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        cardReaderPath.setLayoutData(gridData);

        Button btnCardReaderPath = new Button(group, SWT.NONE);
        btnCardReaderPath.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnCardReaderPath.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnCardReaderPath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCardReaderPath();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnCardReaderPath.setLayoutData(gridData);

        Control[] tabulatorKeyOrder = new Control[]{
                cardReaderPath, btnCardReaderPath
        };

        group.setTabList(tabulatorKeyOrder);
    }

    private void createGroupChooseData(int width) {
        final String delimiter = FileSystems.getDefault().getSeparator();

        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.jobProjectText));

        GridLayout gridLayout = new GridLayout(4, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        final int groupHeight = 145;
        final int groupWidth = gridData.widthHint / 4 - 46;

        // DBX folder
        Group jobGroup = new Group(group, SWT.NONE);
        jobGroup.setText(ResourceBundleUtils.getLangString(LABELS, Labels.jobGroupText));

        gridLayout = new GridLayout(1, true);
        jobGroup.setLayout(gridLayout);

        jobList = new List(jobGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        jobList.addListener(SWT.DefaultSelection, e -> {
            for (int aSelection : jobList.getSelectionIndices()) {
                jobList.deselect(aSelection);
            }
        });

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.heightHint = groupHeight;
        gridData.widthHint = groupWidth;

        jobList.setLayoutData(gridData);

        // export folder, here GSI folder
        Group exportGroup = new Group(group, SWT.NONE);
        exportGroup.setText(ResourceBundleUtils.getLangString(LABELS, Labels.exportGroupText));

        gridLayout = new GridLayout(1, true);
        exportGroup.setLayout(gridLayout);

        exportList = new List(exportGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        exportList.addListener(SWT.DefaultSelection, e -> {
            for (int aSelection : exportList.getSelectionIndices()) {
                exportList.deselect(aSelection);
            }
        });

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.heightHint = groupHeight;
        gridData.widthHint = groupWidth;

        exportList.setLayoutData(gridData);

        // data folder
        Group dataGroup = new Group(group, SWT.NONE);
        dataGroup.setText(ResourceBundleUtils.getLangString(LABELS, Labels.dataGroupText));

        gridLayout = new GridLayout(1, true);
        dataGroup.setLayout(gridLayout);

        dataList = new List(dataGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        dataList.addListener(SWT.DefaultSelection, e -> {
            for (int aSelection : dataList.getSelectionIndices()) {
                dataList.deselect(aSelection);
            }
        });

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.heightHint = groupHeight;
        gridData.widthHint = groupWidth;

        dataList.setLayoutData(gridData);

        // last used projects
        Group projectGroup = new Group(group, SWT.NONE);
        projectGroup.setText(ResourceBundleUtils.getLangString(LABELS, Labels.lastUsedProjects));

        gridLayout = new GridLayout(1, true);
        projectGroup.setLayout(gridLayout);

        lastUsedProjectsList = new List(projectGroup, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        lastUsedProjectsList.addListener(SWT.Selection, event -> {
            final String project = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT)
                    + delimiter + lastUsedProjectsList.getSelection()[0];

            targetProjectPath.setText(project);
        });

        loadProjectListFromPreferences();

        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.heightHint = groupHeight;
        gridData.widthHint = groupWidth;

        lastUsedProjectsList.setLayoutData(gridData);
    }

    private void createGroupChooseTarget() {
        final String delimiter = FileSystems.getDefault().getSeparator();

        Group group = new Group(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Sizes.RyCON_WIDGET_WIDTH.getValue();

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label targetProjectLabel = new Label(group, SWT.NONE);
        targetProjectLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.targetProject));

        targetProjectPath = new Text(group, SWT.SINGLE | SWT.BORDER);

        final String baseInput = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT) + delimiter;

        if (lastUsedProjectsList.getItemCount() == 0) {
            targetProjectPath.setText(baseInput);
        } else {
            targetProjectPath.setText(baseInput + lastUsedProjectsList.getItem(0));
        }

        targetProjectPath.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseDown(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseUp(MouseEvent mouseEvent) {
                Text text = (Text) mouseEvent.widget;
                String s = text.getText();

                // select a part of the text for project number input
                text.setSelection(s.lastIndexOf(delimiter) + 1, s.length());
            }
        });

        /*
         * platform independent key handling for ENTER to prevent action handling on empty text field
         * and TAB to be used in a normal way to jump over controls
         */
        targetProjectPath.addListener(SWT.Traverse, event -> {
            if (!targetProjectPath.getText().trim().equals("")) {
                if ((event.stateMask & SWT.SHIFT) == SWT.SHIFT && event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnOkAndExit();
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnOk();
                }
            }

            if (event.detail == SWT.TRAVERSE_TAB_NEXT || event.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                event.doit = true;
            }
        });

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        targetProjectPath.setLayoutData(gridData);

        Button btnChooseProjectPath = new Button(group, SWT.NONE);
        btnChooseProjectPath.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnChooseProjectPath.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnChooseProjectPath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnChooseProjectPath();
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnChooseProjectPath.setLayoutData(gridData);

        Control[] tabulatorKeyOrder = new Control[]{
                targetProjectPath, btnChooseProjectPath
        };

        group.setTabList(tabulatorKeyOrder);
    }

    private void createGroupDescription(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.adviceText));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setText(ResourceBundleUtils.getLangString(LABELS, Labels.tipTransferWidget));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createGroupOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.optionsText));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        chkBoxMoveOption = new Button(group, SWT.CHECK);
        chkBoxMoveOption.setSelection(false);
        chkBoxMoveOption.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.moveTransferWidget));

        chkBoxCleanMeasurementFile = new Button(group, SWT.CHECK);
        chkBoxCleanMeasurementFile.setSelection(false);
        chkBoxCleanMeasurementFile.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.cleanMeasurementFile));

        chkBoxCleanLogfile = new Button(group, SWT.CHECK);
        chkBoxCleanLogfile.setSelection(false);
        chkBoxCleanLogfile.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.cleanLogfile));
    }

    private void initCardReading() {
        final String storedCardReaderPath = Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER);

        if (checkForAvailableLeicaCardStructure(storedCardReaderPath)) {
            readCardFolders(storedCardReaderPath);

            logger.log(Level.INFO, "card reader path successful read");
        } else {
            showCardReaderNotExitsWarning();

            logger.log(Level.INFO, "could not read card reader path");
        }
    }

    private void loadProjectListFromPreferences() {
        lastUsedProjects = new BoundedTreeSet<>(10);

        final String s = Main.pref.getUserPreference(PreferenceKeys.LAST_USED_PROJECTS);
        final String t = s.substring(1, s.length() - 1);

        final String[] strings = t.split(", ");

        for (String projectNumber : strings) {
            if (!projectNumber.trim().equalsIgnoreCase("")) {
                lastUsedProjectsList.add(projectNumber);
            }
        }
    }

    private void readCardFolderData(String cardReaderPath) {
        final String dataFilesDir = cardReaderPath + FileSystems.getDefault().getSeparator() +
                Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_DATA_FILES);

        final ArrayList<Path> dataFileDirContent = FileUtils.listFiles(dataFilesDir, new TxtFilter());

        allDatas = new TreeSet<>();

        if (dataFileDirContent != null) {
            for (Path path : dataFileDirContent) {
                if (PathCheck.fileExists(path)) {
                    allDatas.add(path);
                }
            }

            if (allDatas.size() > 0) {
                for (Path jobFile : allDatas) {
                    if (jobFile != null) {
                        if (PathCheck.fileExists(jobFile)) {
                            Path p = jobFile.getFileName();

                            if (p != null) {
                                final String fileName = p.toString();
                                dataList.add(fileName);
                            }
                        }
                    }
                }
            } else {
                allDatas.clear();
                dataList.removeAll();
            }
        }
    }

    private void readCardFolderExport(String cardReaderPath) {
        final String exportFilesDir = cardReaderPath + FileSystems.getDefault().getSeparator() +
                Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_EXPORT_FILES);

        final ArrayList<Path> exportFileDirContentGSI = FileUtils.listFiles(exportFilesDir, new GsiFilter());

        allExports = new TreeSet<>();

        if (exportFileDirContentGSI != null) {
            for (Path path : exportFileDirContentGSI) {
                if (PathCheck.fileExists(path)) {
                    allExports.add(path);
                }
            }
        }

        final ArrayList<Path> exportFileDirContentTXT = FileUtils.listFiles(exportFilesDir, new TxtFilter());

        if (exportFileDirContentTXT != null) {
            for (Path path : exportFileDirContentTXT) {
                if (PathCheck.fileExists(path)) {
                    allExports.add(path);
                }
            }
        }

        if (allExports.size() > 0) {
            for (Path path : allExports) {
                if (path != null) {
                    if (PathCheck.fileExists(path)) {
                        Path p = path.getFileName();

                        if (p != null) {
                            final String fileName = p.toString();
                            exportList.add(fileName);
                        }
                    }
                }
            }
        } else {
            allExports.clear();
            exportList.removeAll();
        }
    }

    private void readCardFolderJob(String cardReaderPath) {
        final String jobFilesDir = cardReaderPath + FileSystems.getDefault().getSeparator() +
                Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_JOB_FILES);

        final ArrayList<Path> jobFileDirContent = FileUtils.listFiles(jobFilesDir, new FilesFilter());

        TreeSet<String> allJobs = new TreeSet<>();

        allJobsFiles = new ArrayList<>();

        if (jobFileDirContent != null) {
            for (Path path : jobFileDirContent) {
                if (path != null) {
                    Path fileName = path.getFileName();

                    if (fileName != null) {
                        if (PathCheck.fileExists(path)) {
                            int length = fileName.toString().length();
                            allJobs.add(fileName.toString().substring(0, length - 21));
                            allJobsFiles.add(path);
                        }
                    }
                }
            }

            if (allJobs.size() > 0) {
                for (String job : allJobs) {
                    jobList.add(job);
                }
            } else {
                allJobsFiles.clear();
                jobList.removeAll();
            }
        }
    }

    private void readCardFolders(String cardReaderPath) {
        readCardFolderData(cardReaderPath);
        readCardFolderExport(cardReaderPath);
        readCardFolderJob(cardReaderPath);
    }

    private void showCardReaderNotExitsWarning() {
        MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                ResourceBundleUtils.getLangString(ERRORS, Errors.cardReaderPathNotExists));
    }

    private void showFileExistsWarning(Path path) {
        MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                String.format(ResourceBundleUtils.getLangString(WARNINGS, Warnings.fileExists), path.getFileName()));
    }

    /*
     * Update last used project list after copying the selected files. The values are stored in
     * the user pref with the key 'LAST_USED_PROJECTS'.
     */
    private void updateLastUsedProjectsListAndPreferences() {
        final String delimiter = FileSystems.getDefault().getSeparator();

        // get last used project elements from list
        lastUsedProjects.addAll(Arrays.asList(lastUsedProjectsList.getItems()));

        // get last used projects from text field and add to bounded tree set
        final String text = targetProjectPath.getText();
        final String lastUsedProject = text.substring(text.lastIndexOf(delimiter) + 1, text.length());

        lastUsedProjects.add(lastUsedProject);

        // update list
        Object[] projectsHelper = lastUsedProjects.toArray();

        lastUsedProjectsList.removeAll();

        for (Object object : projectsHelper) {
            lastUsedProjectsList.add((String) object);
        }

        // store last used projects to user preferences
        Main.pref.setUserPreference(PreferenceKeys.LAST_USED_PROJECTS, Arrays.toString(lastUsedProjectsList.getItems()));
    }

} // end of TransferWidget
