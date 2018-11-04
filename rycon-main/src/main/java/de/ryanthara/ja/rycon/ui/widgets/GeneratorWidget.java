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
import de.ryanthara.ja.rycon.data.PreferenceKey;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.i18n.Error;
import de.ryanthara.ja.rycon.nio.util.FileUtils;
import de.ryanthara.ja.rycon.nio.util.check.PathCheck;
import de.ryanthara.ja.rycon.ui.Size;
import de.ryanthara.ja.rycon.ui.custom.BottomButtonBar;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.widgets.generate.error.WarnAndErrorType;
import de.ryanthara.ja.rycon.util.OpenInFileManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;
import static de.ryanthara.ja.rycon.ui.custom.Status.WARNING;

/**
 * The {@code GeneratorWidget} class represents a complete widget of RyCON,
 * which is used to generate folders and substructures by a given project number.
 * <p>
 * The needed folders will be created based upon a template folder. Afterwards
 * it could be opened in the default file manager of the used operating system.
 * <p>
 * Therefore the user has to put the number or text into a text field and
 * take the choice which kind of folders RyCON has to generate.
 * It's possible to create two or more folders at the same time, when the
 * folder names are split by a semicolon sign (';').
 * <p>
 * For better user experience and as note for the user, the recent folders
 * are shown for administration, big data and project folder.
 * <p>
 * There are some special renaming functions for word and excel files which
 * are used in my company.
 *
 * @author sebastian
 * @version 7
 * @since 1
 */
public final class GeneratorWidget extends AbstractWidget {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorWidget.class.getName());

    private final int ADMIN_FOLDER = 1;
    private final int PROJECT_FOLDER = 2;
    private final Shell parent;
    private Button chkBoxCreateAdminFolder;
    private Button chkBoxCreateBigDataFolder;
    private Button chkBoxCreateProjectFolder;
    private Button chkBoxOpenFileManager;
    private org.eclipse.swt.widgets.Text inputNumber;
    private Shell innerShell;
    private Label adminPath;
    private Label bigDataPath;
    private Label projectPath;

    /**
     * Constructs a new instance with a reference for the parent shell.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public GeneratorWidget(Shell parent) {
        this.parent = parent;

        initUI();
    }

    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        innerShell.dispose();
    }

    boolean actionBtnOk() {
        String projectNumber = inputNumber.getText();

        if (projectNumber.trim().equals("")) {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Text.msgBox_Warning),
                    ResourceBundleUtils.getLangString(ResourceBundle.WARNING, Warning.emptyTextField));

            return false;
        } else {
            createFolders(projectNumber);
            updateRecentFoldersTextFields();
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
        int height = Size.RyCON_WIDGET_HEIGHT.getValue();
        int width = Size.RyCON_WIDGET_WIDTH.getValue();

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell = new Shell(parent, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, event -> actionBtnCancel());
        innerShell.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generator_Shell));
        innerShell.setSize(width, height);
        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createGroupInputField();
        createGroupRecentFolders(width);
        createGroupOptions(width);
        createAdvice(width);

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
            success = true;

            logger.info("File copy successful from '{}' to '{}'", copySourcePath, copyTargetPath);
        } catch (IOException e) {
            logger.error("Error when copying file '{}'.", copySourcePath.toString(), e.getCause());

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Text.msgBox_Error),
                    type.getErrorMessage().getErrorMessage(number));
        }

        return success;
    }

    private void createAdvice(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.advice));

        GridLayout gridLayout = new GridLayout(1, true);
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);
        String text =
                ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.generatorWidget) + "\n\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.generatorWidget2) + "\n\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.generatorWidget3);

        tip.setText(text);

        // tip.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.generatorWidget));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createFolders(String projectNumbers) {
        boolean areAdminFoldersCreated = false;
        boolean areBigDataFoldersCreated = false;
        boolean areProjectFoldersCreated = false;

        final String[] numbers = projectNumbers.split(";");

        // create all folders first
        for (String number : numbers) {
            if (!number.trim().equals("")) {
                if (chkBoxCreateAdminFolder.getSelection()) {
                    areAdminFoldersCreated = generateAdminFolder(number.trim());
                    renameSpecialFiles(number, ADMIN_FOLDER);
                }

                if (chkBoxCreateBigDataFolder.getSelection()) {
                    areBigDataFoldersCreated = generateBigDataFolder(number.trim());
                }

                if (chkBoxCreateProjectFolder.getSelection()) {
                    areProjectFoldersCreated = generateProjectFolder(number.trim());
                    renameSpecialFiles(number, PROJECT_FOLDER);
                }
            }
        }

        // show success message after folder creation
        if (areAdminFoldersCreated || areBigDataFoldersCreated || areProjectFoldersCreated) {
            String helper = "\n";

            for (String number : numbers) {
                if (!number.trim().equals("")) {
                    helper = helper.concat(number.trim() + "\n");
                }
            }

            String message;

            if (areAdminFoldersCreated && areBigDataFoldersCreated && areProjectFoldersCreated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGE, Message.adminAndBigDataAndProjectsCreated), helper);
            } else if (areAdminFoldersCreated && areBigDataFoldersCreated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGE, Message.adminAndBigDatasCreated), helper);
            } else if (areAdminFoldersCreated && areProjectFoldersCreated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGE, Message.adminAndProjectsCreated), helper);
            } else if (areBigDataFoldersCreated && areProjectFoldersCreated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGE, Message.bigDataAndProjectsCreated), helper);
            } else if (areAdminFoldersCreated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGE, Message.adminFoldersCreated), helper);
            } else if (areBigDataFoldersCreated) {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGE, Message.bigDataFoldersCreated), helper);
            } else {
                message = String.format(ResourceBundleUtils.getLangString(MESSAGE, Message.projectFoldersCreated), helper);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Text.msgBox_Information), message);

            // open every created folder in the file viewer
            if (chkBoxOpenFileManager.getSelection()) {
                for (String number : numbers) {
                    if (!number.trim().equals("")) {
                        openFolder(number.trim(), areAdminFoldersCreated, areBigDataFoldersCreated, areProjectFoldersCreated);
                    }
                }
            }
        }
    }

    private void createGroupInputField() {
        Group group = new Group(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Size.RyCON_WIDGET_WIDTH.getValue();

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label projectNumberLabel = new Label(group, SWT.NONE);
        projectNumberLabel.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generator_ProjectNumber));

        inputNumber = new org.eclipse.swt.widgets.Text(group, SWT.SINGLE | SWT.BORDER);

        /*
         * platform independent key handling for ENTER to prevent action handling on empty text field
         */
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
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generalOptions));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        chkBoxCreateAdminFolder = new Button(group, SWT.CHECK);
        chkBoxCreateAdminFolder.setSelection(true);
        chkBoxCreateAdminFolder.setText(ResourceBundleUtils.getLangString(CHECKBOX, CheckBox.createAdminFolder));

        chkBoxCreateBigDataFolder = new Button(group, SWT.CHECK);
        chkBoxCreateBigDataFolder.setSelection(false);
        chkBoxCreateBigDataFolder.setText(ResourceBundleUtils.getLangString(CHECKBOX, CheckBox.createBigDataFolder));

        chkBoxCreateProjectFolder = new Button(group, SWT.CHECK);
        chkBoxCreateProjectFolder.setSelection(false);
        chkBoxCreateProjectFolder.setText(ResourceBundleUtils.getLangString(CHECKBOX, CheckBox.createProjectFolder));

        chkBoxOpenFileManager = new Button(group, SWT.CHECK);
        chkBoxOpenFileManager.setSelection(true);
        chkBoxOpenFileManager.setText(ResourceBundleUtils.getLangString(CHECKBOX, CheckBox.openFileManager));
    }

    private void createGroupRecentFolders(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generator_GroupRecentFolders));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label adminDescription = new Label(group, SWT.NONE);
        adminDescription.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generator_AdminDescription));

        adminPath = new Label(group, SWT.NONE);
        adminPath.setText(getAdminPathString());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        adminPath.setLayoutData(gridData);

        Label bigDataDescription = new Label(group, SWT.NONE);
        bigDataDescription.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generator_BigDataDescription));

        bigDataPath = new Label(group, SWT.NONE);
        bigDataPath.setText(getBigDataPathString());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        bigDataPath.setLayoutData(gridData);

        Label projectDescription = new Label(group, SWT.NONE);
        projectDescription.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generator_ProjectDescription));

        projectPath = new Label(group, SWT.NONE);
        projectPath.setText(getProjectPathString());

        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        projectPath.setLayoutData(gridData);
    }

    private boolean generateAdminFolder(String number) {
        final String dir = Main.pref.getUserPreference(PreferenceKey.DIR_ADMIN);
        final String dirTemplate = Main.pref.getUserPreference(PreferenceKey.DIR_ADMIN_TEMPLATE);

        /*
         * Check admin dir and admin template dir for identity. They must not be equal,
         * because of the recursive file copy operations to fetch all the sub folders and
         * nested files in there.
         */
        if (dir.equals(dirTemplate)) {
            final String message = ResourceBundleUtils.getLangString(MESSAGE, Message.adminDirEqualityMessage);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(ERROR, Error.adminDirEquality), message);

            return false;
        } else {
            return generateFoldersHelper(number, dir, dirTemplate, WarnAndErrorType.ADMIN);
        }
    }

    private boolean generateBigDataFolder(String number) {
        final String dir = Main.pref.getUserPreference(PreferenceKey.DIR_BIG_DATA);
        final String dirTemplate = Main.pref.getUserPreference(PreferenceKey.DIR_BIG_DATA_TEMPLATE);

        /*
         * Check big data dir and big data template dir for identity. They must not be equal,
         * because of the recursive file copy operations to fetch all the sub folders and
         * nested files in there.
         */
        if (dir.equals(dirTemplate)) {
            final String message = ResourceBundleUtils.getLangString(MESSAGE, Message.bigDataDirEqualityMessage);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(ERROR, Error.bigDataDirEquality), message);

            return false;
        } else {
            return generateFoldersHelper(number, dir, dirTemplate, WarnAndErrorType.BIG_DATA);
        }
    }

    private boolean generateFoldersHelper(String number, String directory, String directoryTemplate, WarnAndErrorType type) {
        boolean success = false;

        Path copyTargetPath = Paths.get(directory + FileSystems.getDefault().getSeparator() + number);

        if (Files.exists(copyTargetPath)) {
            Main.statusBar.setStatus("", WARNING);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Text.msgBox_Warning),
                    type.getErrorMessage().getWarnMessage(number));
        } else {
            success = copyFile(number, directoryTemplate, type, copyTargetPath);
        }

        return success;
    }

    private boolean generateProjectFolder(String number) {
        final String dir = Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT);
        final String dirTemplate = Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT_TEMPLATE);

        /*
         * Check project dir and project template dir for identity. They must not be equal,
         * because of the recursive file copy operations to fetch all the sub folders and
         * nested files in there.
         */
        if (dir.equals(dirTemplate)) {
            final String message = ResourceBundleUtils.getLangString(MESSAGE, Message.projectDirEqualityMessage);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(ERROR, Error.projectDirEquality), message);

            return false;
        } else {
            return generateFoldersHelper(number, dir, dirTemplate, WarnAndErrorType.PROJECT);
        }
    }

    private String getAdminPathString() {
        Path adminPath = Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_ADMIN));

        return FileUtils.getRecentFolder(adminPath);
    }

    private String getBigDataPathString() {
        Path bigDataPath = Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_BIG_DATA));

        return FileUtils.getRecentFolder(bigDataPath);
    }

    private String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return now.format(df);
    }

    private String getProjectPathString() {
        Path projectPath = Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT));

        return FileUtils.getRecentFolder(projectPath);
    }

    private String getUserString() {
        return Main.pref.getUserPreference(PreferenceKey.PARAM_USER_STRING);
    }

    private void openFolder(String number, boolean isAdminFolderGenerated, boolean isBigDataFolderGenerated, boolean isProjectFolderGenerated) {
        final String delimiter = FileSystems.getDefault().getSeparator();

        if (isAdminFolderGenerated) {
            final String path = Main.pref.getUserPreference(PreferenceKey.DIR_ADMIN) + delimiter + number;

            OpenInFileManager.openFolder(path);
        }
        if (isBigDataFolderGenerated) {
            final String path = Main.pref.getUserPreference(PreferenceKey.DIR_BIG_DATA) + delimiter + number;

            OpenInFileManager.openFolder(path);
        }
        if (isProjectFolderGenerated) {
            final String path = Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT) + delimiter + number;

            OpenInFileManager.openFolder(path);
        }
    }

    /*
     * rename 20YY_0nnn_Aufwandschätzung_0n.xlsx
     */
    private void renameContractFiles(String number, String delimiter, String dir) {
        Path correspondence = Paths.get(dir + delimiter + "02.Vertrag");

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(correspondence);

            for (Path entry : stream) {
                if (entry != null) {
                    Path path = entry.getFileName();

                    if (path != null) {
                        final String fileName = path.toString();

                        if (PathCheck.fileExists(entry) && fileName.contains("nn_Aufwandschätzung_")) {
                            String oldName = path.toString();
                            String newName = number + oldName.substring(oldName.indexOf("nn_") + 2);
                            newName = newName.replaceAll("_0n", "_01");

                            Path renamed = Paths.get(entry.getParent() + delimiter + newName);

                            // Change date and number in excel sheet
                            if (PathCheck.fileExists(entry)) {
                                //try {
                                FileInputStream fileInputStream = new FileInputStream(new File(entry.toString()));

                                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                                XSSFRow row;
                                XSSFCell cell;
                                XSSFSheet spreadsheet;

                                // get the sheets
                                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                                    spreadsheet = workbook.getSheetAt(i);

                                    for (Iterator<Row> rowIterator = spreadsheet.rowIterator(); rowIterator.hasNext(); ) {
                                        row = (XSSFRow) rowIterator.next();

                                        for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
                                            cell = (XSSFCell) cellIterator.next();

                                            if (cell.getCellTypeEnum().equals(CellType.STRING)) {
                                                switch (cell.getStringCellValue()) {
                                                    case "Auftrag-Nr.":
                                                        XSSFRow rowNumber = spreadsheet.getRow(row.getRowNum());
                                                        XSSFCell cellNumber = rowNumber.getCell(cell.getColumnIndex() + 1);

                                                        cellNumber.setCellValue(number);
                                                        break;
                                                    case "Bearbeiter/in":
                                                        XSSFRow rowUserName = spreadsheet.getRow(row.getRowNum() + 1);
                                                        XSSFCell cellUserName = rowUserName.getCell(cell.getColumnIndex());

                                                        cellUserName.setCellValue(getUserString());
                                                        break;
                                                    case "Datum":
                                                        XSSFRow rowDate = spreadsheet.getRow(row.getRowNum() + 1);
                                                        XSSFCell cellBelow = rowDate.getCell(cell.getColumnIndex());

                                                        cellBelow.setCellValue(getDate());
                                                        break;
                                                    default:

                                                }
                                            }
                                        }
                                    }
                                }

                                // close file input stream
                                fileInputStream.close();

                                // write new excel file
                                FileOutputStream out = new FileOutputStream(renamed.toFile());
                                workbook.write(out);
                                out.close();

                                // delete the old one
                                if (PathCheck.fileExists(entry) && PathCheck.fileExists(renamed)) {
                                    Files.delete(entry);
                                }
                            }

                            // Files.move(entry, renamed);

                            logger.info("Rename of file 'YYYY_01nn_Aufwandschätzung_0n.xlsx' successful");
                        }
                    }
                }
            }

        } catch (IOException e) {
            logger.warn("Can not read directory '02.Vertrag'", e.getCause());
        }
    }

    /*
     * rename 20YY_0nnn_Arbeitsblatt.docx
     */
    private void renameOrganisationFiles(String number, String delimiter, int folderType, String dir) {
        Path organization = Paths.get(dir + delimiter + "01.Organisation");

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(organization);

            for (Path entry : stream) {
                if (entry != null) {
                    Path path = entry.getFileName();

                    if (path != null) {
                        final String fileName = path.toString();

                        if (PathCheck.fileExists(entry) && fileName.contains("nn_Arbeitsblatt.docx")) {
                            String oldName = path.toString();
                            String newName = number + oldName.substring(oldName.indexOf("nn_") + 2);

                            Path renamed = Paths.get(entry.getParent() + delimiter + newName);

                            // Files.move(entry, renamed);

                            // Change date and number in word file
                            if (PathCheck.fileExists(entry)) {
                                FileInputStream fileInputStream = new FileInputStream(new File(entry.toString()));

                                XWPFDocument document = new XWPFDocument(fileInputStream);

                                List<XWPFTable> tables = document.getTables();
                                XWPFTable projectTable = tables.get(1);
                                XWPFTableRow firstRow = projectTable.getRow(0);
                                XWPFTableCell rightCell = firstRow.getCell(1);
                                List<XWPFParagraph> paragraphs = rightCell.getParagraphs();

                                Map<String, String> replacements = new HashMap<>();

                                // prepare the replacement strings
                                for (XWPFParagraph paragraph : paragraphs) {
                                    String[] split = paragraph.getParagraphText().split("\t");

                                    if (split.length > 1) {
                                        switch (split[0]) {
                                            case "Offerte Nr.:":
                                                if (folderType == ADMIN_FOLDER) {
                                                    replacements.put(split[1], number);
                                                }
                                                break;
                                            case "Auftrag Nr.:":
                                                if (folderType == PROJECT_FOLDER) {
                                                    replacements.put(split[1], number);
                                                }
                                                break;
                                            case "Visum:":
                                                replacements.put(split[1], getUserString());
                                                break;
                                            case "Datum:":
                                                replacements.put(split[1], getDate());
                                                break;
                                            default:
                                        }
                                    }
                                }

                                replaceInParagraphs(replacements, rightCell.getParagraphs());

                                /*
                                 * XWPFParagraph paragraph = (XWPFParagraph)element;
                                 *
                                 * if(paragraph.getStyleID()!=null){
                                 *      XWPFStyles styles= output.createStyles();
                                 *      XWPFStyles stylesDoc2= source.getStyles();
                                 *      styles.addStyle(stylesDoc2.getStyle(paragraph.getStyleID()));
                                 * }
                                 *
                                 * XWPFParagraph x = output.createParagraph();
                                 * x.setStyle(((XWPFParagraph) element).getStyle());
                                 * XWPFRun runX = x.createRun();
                                 * runX.setText(((XWPFParagraph) element).getText());
                                 */

                                // close file input stream
                                fileInputStream.close();

                                // write new word file
                                FileOutputStream out = new FileOutputStream(renamed.toFile());
                                document.write(out);
                                out.close();

                                // delete the old one
                                if (PathCheck.fileExists(entry) && PathCheck.fileExists(renamed)) {
                                    Files.delete(entry);
                                }
                            }

                            logger.info("Rename of file 'YYYY_01nn_Arbeitsblatt.docx' successful");
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Can not read directory '01.Organisation'", e.getCause());
        }
    }

    /**
     * Renames some special files in the admin folder.
     * <p>
     * They are special for my company and may not work for you with a normal usage of RyCON.
     *
     * @param number     number of the generated admin folder
     * @param folderType differ between admin or project folder
     */
    private void renameSpecialFiles(String number, int folderType) {
        final String delimiter = FileSystems.getDefault().getSeparator();
        String dir = ".";

        switch (folderType) {
            case ADMIN_FOLDER:
                dir = Main.pref.getUserPreference(PreferenceKey.DIR_ADMIN) + delimiter + number;
                // renameOrganisationFiles(number, delimiter, folderType, dir);
                // renameContractFiles(number, delimiter, dir);
                break;
            case PROJECT_FOLDER:
                dir = Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT) + delimiter + number;
                break;
            default:
                break;
        }

        renameOrganisationFiles(number, delimiter, folderType, dir);
        renameContractFiles(number, delimiter, dir);
    }

    private void replaceInParagraphs(Map<String, String> replacements, List<XWPFParagraph> xwpfParagraphs) {
        // long count = 0;
        for (XWPFParagraph paragraph : xwpfParagraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (Map.Entry<String, String> replPair : replacements.entrySet()) {
                String find = replPair.getKey();
                String repl = replPair.getValue();
                TextSegement found = paragraph.searchText(find, new PositionInParagraph());
                if (found != null) {
                    // count++;
                    if (found.getBeginRun() == found.getEndRun()) {
                        // whole search string is in one Run
                        XWPFRun run = runs.get(found.getBeginRun());
                        String runText = run.getText(run.getTextPosition());
                        String replaced = runText.replace(find, repl);
                        run.setText(replaced, 0);
                    } else {
                        // The search string spans over more than one Run
                        // Put the Strings together
                        StringBuilder b = new StringBuilder();
                        for (int runPos = found.getBeginRun(); runPos <= found.getEndRun(); runPos++) {
                            XWPFRun run = runs.get(runPos);
                            b.append(run.getText(run.getTextPosition()));
                        }
                        String connectedRuns = b.toString();
                        String replaced = connectedRuns.replace(find, repl);

                        // The first Run receives the replaced String of all connected Runs
                        XWPFRun partOne = runs.get(found.getBeginRun());
                        partOne.setText(replaced, 0);
                        // Removing the text in the other Runs.
                        for (int runPos = found.getBeginRun() + 1; runPos <= found.getEndRun(); runPos++) {
                            XWPFRun partNext = runs.get(runPos);
                            partNext.setText("", 0);
                        }
                    }
                }
            }
        }
    }

    private void updateRecentFoldersTextFields() {
        adminPath.setText(getAdminPathString());
        bigDataPath.setText(getBigDataPathString());
        projectPath.setText(getProjectPathString());
    }

}
