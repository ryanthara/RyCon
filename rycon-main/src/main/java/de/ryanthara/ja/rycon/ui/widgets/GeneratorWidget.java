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
import de.ryanthara.ja.rycon.util.check.PathCheck;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.custom.BottomButtonBar;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.widgets.generate.WarnAndErrorType;
import de.ryanthara.ja.rycon.util.FileUtils;
import de.ryanthara.ja.rycon.util.OpenInFileManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;
import static de.ryanthara.ja.rycon.ui.custom.Status.WARNING;

/**
 * The {@code GeneratorWidget} class represents a complete widget of RyCON,
 * which is used to generate folders and substructures by a given point number.
 * <p>
 * The needed folders will be created based upon a template folder. Afterwards
 * it is opened in the default file manager of the used operating system.
 * <p>
 * Therefore the user has to put the number or text into a text field and
 * take the choice which kind of folders {@code RyCON} has to generate.
 * <p>
 * For better user experience and as note for the user, the recent folders
 * are shown for administration, big data and project folder.
 *
 * @author sebastian
 * @version 6
 * @since 1
 */
public final class GeneratorWidget extends AbstractWidget {

    private final static Logger logger = Logger.getLogger(GeneratorWidget.class.getName());
    private final Shell parent;
    private Button chkBoxCreateAdminFolder;
    private Button chkBoxCreateBigDataFolder;
    private Button chkBoxCreateProjectFolder;
    private Button chkBoxOpenFileManager;
    private Text inputNumber;
    private Shell innerShell;
    private Label adminPath;
    private Label bigDataPath;
    private Label projectPath;

    /**
     * Constructs a new instance with parameter.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public GeneratorWidget(final Shell parent) {
        this.parent = parent;

        initUI();
    }

    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        innerShell.dispose();
    }

    boolean actionBtnOk() {
        String number = inputNumber.getText();

        if (number.trim().equals("")) {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    ResourceBundleUtils.getLangString(WARNINGS, Warnings.emptyTextField));

            return false;
        } else {
            generateFolders(number);

            updateNewestFileTextFields();
        }

        return true;
    }

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
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.generatorText));
        innerShell.setSize(width, height);
        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createGroupInputField();
        createGroupNewestFiles(width);
        createGroupOptions(width);
        createDescription(width);

        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private boolean copyFile(String number, String directoryTemplate, WarnAndErrorType type, Path copyTargetPath) {
        boolean success = false;

        Path copySourcePath = Paths.get(directoryTemplate);

        try {
            FileUtils.copy(copySourcePath, copyTargetPath);

            logger.log(Level.INFO, "file copy successful from " + copySourcePath + " to " + copyTargetPath);

            success = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "error when copying file", e);
            logger.log(Level.SEVERE, e.getMessage());

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR, ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                    type.getErrorMessage().getErrorMessage(number));
        }

        return success;
    }

    private void createDescription(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.adviceText));

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        tip.setText(ResourceBundleUtils.getLangString(LABELS, Labels.tipGeneratorWidget));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createGroupInputField() {
        Group group = new Group(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Sizes.RyCON_WIDGET_WIDTH.getValue();

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label projectNumberLabel = new Label(group, SWT.NONE);
        projectNumberLabel.setText(ResourceBundleUtils.getLangString(LABELS, Labels.projectNumber));

        inputNumber = new Text(group, SWT.SINGLE | SWT.BORDER);

        // platform independent key handling for ENTER, TAB, ...
        // TODO change bad listener with a better one
        inputNumber.addListener(SWT.Traverse, event -> {
            if (!inputNumber.getText().trim().equals("")) {
                if ((event.stateMask & SWT.SHIFT) == SWT.SHIFT && event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnOkAndExit();
                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    actionBtnOk();
                }
            }
        });

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        inputNumber.setLayoutData(gridData);
    }

    private void createGroupNewestFiles(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.newestFolderText));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label adminDescription = new Label(group, SWT.NONE);
        adminDescription.setText(ResourceBundleUtils.getLangString(LABELS, Labels.adminDescription));

        adminPath = new Label(group, SWT.NONE);
        adminPath.setText(getAdminPathString());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        adminPath.setLayoutData(gridData);

        Label bigDataDescription = new Label(group, SWT.NONE);
        bigDataDescription.setText(ResourceBundleUtils.getLangString(LABELS, Labels.bigDataDescription));

        bigDataPath = new Label(group, SWT.NONE);
        bigDataPath.setText(getBigDataPathString());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        bigDataPath.setLayoutData(gridData);

        Label projectDescription = new Label(group, SWT.NONE);
        projectDescription.setText(ResourceBundleUtils.getLangString(LABELS, Labels.projectDescription));

        projectPath = new Label(group, SWT.NONE);
        projectPath.setText(getProjectPathString());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        projectPath.setLayoutData(gridData);
    }

    private void createGroupOptions(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.optionsText));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        chkBoxCreateProjectFolder = new Button(group, SWT.CHECK);
        chkBoxCreateProjectFolder.setSelection(false);
        chkBoxCreateProjectFolder.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.createProjectFolder));

        chkBoxCreateAdminFolder = new Button(group, SWT.CHECK);
        chkBoxCreateAdminFolder.setSelection(true);
        chkBoxCreateAdminFolder.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.createAdminFolder));

        chkBoxCreateBigDataFolder = new Button(group, SWT.CHECK);
        chkBoxCreateBigDataFolder.setSelection(false);
        chkBoxCreateBigDataFolder.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.createBigDataFolder));

        chkBoxOpenFileManager = new Button(group, SWT.CHECK);
        chkBoxOpenFileManager.setSelection(true);
        chkBoxOpenFileManager.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.openFileManager));
    }

    private boolean generateAdminFolder(String number) {
        String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN);
        String dirTemplate = Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN_TEMPLATE);

        return generateFoldersHelper(number, dir, dirTemplate, WarnAndErrorType.ADMIN);
    }

    private boolean generateBigDataFolder(String number) {
        String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA);
        String dirTemplate = Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA_TEMPLATE);

        return generateFoldersHelper(number, dir, dirTemplate, WarnAndErrorType.BIG_DATA);
    }

    private void generateFolders(String number) {
        boolean isAdminFolderGenerated = false;
        boolean isBigDataFolderGenerated = false;
        boolean isProjectFolderGenerated = false;

        if (chkBoxCreateAdminFolder.getSelection()) {
            isAdminFolderGenerated = generateAdminFolder(number);

            renameSpecialFiles(number);
        }

        if (chkBoxCreateBigDataFolder.getSelection()) {
            isBigDataFolderGenerated = generateBigDataFolder(number);
        }

        if (chkBoxCreateProjectFolder.getSelection()) {
            isProjectFolderGenerated = generateProjectFolder(number);
        }

        if (isAdminFolderGenerated || isBigDataFolderGenerated || isProjectFolderGenerated) {
            String message = "";

            if (isAdminFolderGenerated && isBigDataFolderGenerated && isProjectFolderGenerated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGES, Messages.adminAndBigDataAndProjectGenerated), number);
            } else if (isAdminFolderGenerated && isBigDataFolderGenerated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGES, Messages.adminAndBigDataGenerated), number);
            } else if (isAdminFolderGenerated && isProjectFolderGenerated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGES, Messages.adminAndProjectGenerated), number);
            } else if (isBigDataFolderGenerated && isProjectFolderGenerated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGES, Messages.bigDataAndProjectGenerated), number);
            } else if (isAdminFolderGenerated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGES, Messages.adminFolderGenerated), number);
            } else if (isBigDataFolderGenerated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGES, Messages.bigDataFolderGenerated), number);
            } else if (isProjectFolderGenerated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGES, Messages.projectFolderGenerated), number);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangString(LABELS, Labels.informationTextMsgBox), message);

            if (chkBoxOpenFileManager.getSelection()) {
                final String delimiter = FileSystems.getDefault().getSeparator();

                if (isAdminFolderGenerated) {
                    final String path = Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN) + delimiter + number;

                    OpenInFileManager.openFolder(path);
                } else if (isBigDataFolderGenerated) {
                    final String path = Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA) + delimiter + number;

                    OpenInFileManager.openFolder(path);
                } else if (isProjectFolderGenerated) {
                    final String path = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT) + delimiter + number;

                    OpenInFileManager.openFolder(path);
                }
            }
        }
    }

    private boolean generateFoldersHelper(String number, String directory, String directoryTemplate, WarnAndErrorType type) {
        boolean success = false;

        Path copyTargetPath = Paths.get(directory + FileSystems.getDefault().getSeparator() + number);

        if (Files.exists(copyTargetPath)) {
            Main.statusBar.setStatus("", WARNING);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    type.getErrorMessage().getWarnMessage(number));
        } else {
            success = copyFile(number, directoryTemplate, type, copyTargetPath);
        }

        return success;
    }

    private boolean generateProjectFolder(String number) {
        String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT);
        String dirTemplate = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT_TEMPLATE);

        return generateFoldersHelper(number, dir, dirTemplate, WarnAndErrorType.PROJECT);
    }

    private String getAdminPathString() {
        Path adminPath = Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN));

        return FileUtils.getNewestFolder(adminPath);
    }

    private String getBigDataPathString() {
        Path bigDataPath = Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA));

        return FileUtils.getNewestFolder(bigDataPath);
    }

    private String getProjectPathString() {
        Path projectPath = Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT));

        return FileUtils.getNewestFolder(projectPath);
    }

    /**
     * Renames some special files in the admin folder.
     * <p>
     * They are special for my company and may not work for you with a normal usage of {@code RyCON}.
     *
     * @param number number of the generated admin folder
     */
    private void renameSpecialFiles(String number) {
        final String delimiter = FileSystems.getDefault().getSeparator();

        String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN) + delimiter + number;

        // rename 2017_01nn_Arbeitsblatt.docx
        Path organization = Paths.get(dir + delimiter + "01.Organisation");

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(organization);

            for (Path entry : stream) {
                if (entry != null) {
                    if (PathCheck.fileExists(entry) && entry.getFileName().toString().contains("nn_Arbeitsblatt.docx")) {
                        String oldName = entry.getFileName().toString();
                        String newName = number + oldName.substring(9, oldName.length());

                        Path renamed = Paths.get(entry.getParent() + delimiter + newName);

                        Files.move(entry, renamed);

                        logger.log(Level.INFO, "rename of file 'YYYY_01nn_Arbeitsblatt.docx' successful");
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "can not read directory '01.Organisation'");
        }

        // rename 2017_01nn_Aufwandschätzung_0n.xlsx
        Path correspondence = Paths.get(dir + delimiter + "02.Vertrag");

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(correspondence);

            for (Path entry : stream) {
                if (entry != null) {
                    if (PathCheck.fileExists(entry) && entry.getFileName().toString().contains("nn_Aufwandschätzung_")) {
                        String oldName = entry.getFileName().toString();
                        String newName = number + oldName.substring(9, oldName.length());
                        newName = newName.replaceAll("_0n", "_01");

                        Path renamed = Paths.get(entry.getParent() + delimiter + newName);

                        Files.move(entry, renamed);

                        logger.log(Level.INFO, "rename of file 'YYYY_01nn_Aufwandschätzung_0n.xlsx' successful");
                    }
                }
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "can not read directory '02.Vertrag'");
        }
    }

    private void updateNewestFileTextFields() {
        adminPath.setText(getAdminPathString());
        bigDataPath.setText(getBigDataPathString());
        projectPath.setText(getProjectPathString());
    }

}  // end of GeneratorWidget