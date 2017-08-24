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
package de.ryanthara.ja.rycon.gui.widgets;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.gui.Sizes;
import de.ryanthara.ja.rycon.gui.custom.BottomButtonBar;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.gui.widgets.generate.AdminCopyWarnAndErrorMessage;
import de.ryanthara.ja.rycon.gui.widgets.generate.BigDataCopyWarnAndErrorMessage;
import de.ryanthara.ja.rycon.gui.widgets.generate.CopyWarnAndErrorMessage;
import de.ryanthara.ja.rycon.gui.widgets.generate.ProjectCopyWarnAndErrorMessages;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.io.FileUtils;
import de.ryanthara.ja.rycon.tools.OpenInFileManager;
import de.ryanthara.ja.rycon.tools.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.gui.custom.Status.OK;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;

/**
 * Instances of this class implements a complete widgets and it's functionality.
 * <p>
 * The GeneratorWidget of RyCON is used to generate folders and substructures
 * in a default path by a given point number.
 *
 * @author sebastian
 * @version 5
 * @since 1
 */
public class GeneratorWidget extends AbstractWidget {

    private static final int TYPE_PROJECT = 1;
    private static final int TYPE_ADMIN = 2;
    private static final int TYPE_BIG_DATA = 3;
    private final static Logger logger = Logger.getLogger(GeneratorWidget.class.getName());
    private static Map<Integer, CopyWarnAndErrorMessage> messages;
    private Button chkBoxCreateAdminFolder;
    private Button chkBoxCreateBigDataFolder;
    private Button chkBoxCreateProjectFolder;
    private Button chkBoxOpenFileManager;
    private Text inputNumber;
    private Shell innerShell;

    /**
     * Constructs a new instance without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public GeneratorWidget() {
        initMaps();
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
            if (generateFolders(number)) {
                // set status text
                String helper = "";

                if (chkBoxCreateAdminFolder.getSelection() && chkBoxCreateBigDataFolder.getSelection() && chkBoxCreateProjectFolder.getSelection()) {
                    helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.adminAndBigDataAndProjectGenerated);
                } else if (chkBoxCreateAdminFolder.getSelection() && chkBoxCreateBigDataFolder.getSelection()) {
                    helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.adminAndBigDataGenerated);
                } else if (chkBoxCreateAdminFolder.getSelection() && chkBoxCreateProjectFolder.getSelection()) {
                    helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.adminAndProjectGenerated);
                } else if (chkBoxCreateBigDataFolder.getSelection() && chkBoxCreateProjectFolder.getSelection()) {
                    helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.bigDataAndProjectGenerated);
                } else if (chkBoxCreateAdminFolder.getSelection()) {
                    helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.adminFolderGenerated);
                } else if (chkBoxCreateBigDataFolder.getSelection()) {
                    helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.bigDataFolderGenerated);
                } else if (chkBoxCreateProjectFolder.getSelection()) {
                    helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.projectFolderGenerated);
                }

                final String status = String.format(helper, number);

                Main.statusBar.setStatus(status, OK);
            }

            return true;
        }
    }

    void actionBtnOkAndExit() {
        if (actionBtnOk()) {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", OK);

            innerShell.dispose();
        }
    }

    private boolean copyFile(String number, String directoryTemplate, int type, Path copyTargetPath) {
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
                    messages.get(type).getErrorMessage(number));
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

        return generateFoldersHelper(number, dir, dirTemplate, TYPE_ADMIN);
    }

    private boolean generateBigDataFolder(String number) {
        String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA);
        String dirTemplate = Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA_TEMPLATE);

        return generateFoldersHelper(number, dir, dirTemplate, TYPE_BIG_DATA);
    }

    private boolean generateFolders(String number) {
        boolean isAdminFolderGenerated = false;
        boolean isBigDataFolderGenerated = false;
        boolean isProjectFolderGenerated = false;

        if (chkBoxCreateAdminFolder.getSelection()) {
            isAdminFolderGenerated = generateAdminFolder(number);

            // rename special files
            renameSpecialFiles(number);
        }
        if (chkBoxCreateBigDataFolder.getSelection()) {
            isBigDataFolderGenerated = generateBigDataFolder(number);
        }
        if (chkBoxCreateProjectFolder.getSelection()) {
            isProjectFolderGenerated = generateProjectFolder(number);
        }

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

        if (isAdminFolderGenerated & chkBoxOpenFileManager.getSelection()) {
            final String path = Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN) + File.separator + number;

            OpenInFileManager.openFolder(path);
        } else if (isBigDataFolderGenerated & chkBoxOpenFileManager.getSelection()) {
            final String path = Main.pref.getUserPreference(PreferenceKeys.DIR_BIG_DATA) + File.separator + number;

            OpenInFileManager.openFolder(path);
        } else if (isProjectFolderGenerated & chkBoxOpenFileManager.getSelection()) {
            final String path = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT) + File.separator + number;

            OpenInFileManager.openFolder(path);
        }

        return isAdminFolderGenerated & isBigDataFolderGenerated & isProjectFolderGenerated;
    }

    private boolean generateFoldersHelper(String number, String directory, String directoryTemplate, int type) {
        boolean success = false;

        Path copyTargetPath = Paths.get(directory + FileSystems.getDefault().getSeparator() + number);

        if (Files.exists(copyTargetPath)) {
            Main.statusBar.setStatus("", OK);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, ResourceBundleUtils.getLangString(LABELS, Labels.warningTextMsgBox),
                    messages.get(type).getWarnMessage(number));
        } else {
            success = copyFile(number, directoryTemplate, type, copyTargetPath);
        }

        return success;
    }

    private boolean generateProjectFolder(String number) {
        String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT);
        String dirTemplate = Main.pref.getUserPreference(PreferenceKeys.DIR_PROJECT_TEMPLATE);

        return generateFoldersHelper(number, dir, dirTemplate, TYPE_PROJECT);
    }

    private void initMaps() {
        messages = new HashMap<>();
        messages.put(TYPE_PROJECT, new ProjectCopyWarnAndErrorMessages());
        messages.put(TYPE_ADMIN, new AdminCopyWarnAndErrorMessage());
        messages.put(TYPE_BIG_DATA, new BigDataCopyWarnAndErrorMessage());
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

        innerShell = new Shell(Main.shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, event -> actionBtnCancel());
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.generatorText));
        innerShell.setSize(width, height);
        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createGroupInputField();
        createGroupOptions(width);
        createDescription(width);

        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    /**
     * Renames some special objects in the admin folder.
     * <p>
     * They are special for my company and may not works for a normal usage of <tt>RyCON</tt>.
     *
     * @param number number of the generated admin folder
     */
    private void renameSpecialFiles(String number) {
        String dir = Main.pref.getUserPreference(PreferenceKeys.DIR_ADMIN) + File.separator + number;

        // 2017_01nn_Arbeitsblatt.docx
        File organization = new File(dir + File.separator + "01.Organisation");
        File[] listOfFiles = organization.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().contains("nn_Arbeitsblatt.docx")) {
                    String oldName = file.getName();
                    String newName = number + oldName.substring(9, oldName.length());

                    File newFile = new File(file.getParent() + File.separator + newName);

                    boolean success = file.renameTo(newFile);

                    if (success) {
                        logger.log(Level.INFO, "rename of file 'YYYY_01nn_Arbeitsblatt.docx' successful");
                    }
                }
            }
        }

        // 2017_01nn_Aufwandschätzung_0n.xlsx
        File correspondence = new File(dir + File.separator + "02.Vertrag");
        listOfFiles = correspondence.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().contains("nn_Aufwandschätzung_")) {
                    String oldName = file.getName();
                    String newName = number + oldName.substring(9, oldName.length());
                    newName = newName.replaceAll("_0n", "_01");

                    File newFile = new File(file.getParent() + File.separator + newName);

                    boolean success = file.renameTo(newFile);

                    if (success) {
                        logger.log(Level.INFO, "rename of file 'YYYY_01nn_Aufwandschätzung_0n.xlsx' successful");
                    }
                }
            }
        }

    }

}  // end of GeneratorWidget