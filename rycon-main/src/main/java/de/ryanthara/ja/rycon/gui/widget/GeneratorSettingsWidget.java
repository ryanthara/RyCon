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
import de.ryanthara.ja.rycon.check.TextCheck;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.gui.custom.DirectoryDialogs;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.i18n.I18N;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import static de.ryanthara.ja.rycon.gui.custom.Status.OK;


/**
 * Instances of this class implements a complete settings widget and it's functionality for
 * the {@link GeneratorWidget} of RyCON.
 * <p>
 * It is used to set preferences for the {@link GeneratorWidget}'s functionality.
 *
 * @author sebastian
 * @version 4
 * @since 1
 */
public class GeneratorSettingsWidget {

    private Shell innerShell, shell;
    private Text textDefaultPath;
    private Text textAdminPath;
    private Text textAdminPathTemplateFolder;
    private Text textBigDataPath;
    private Text textBigDataPathTemplateFolder;
    private Text textProjectPath;
    private Text textProjectPathTemplateFolder;

    /**
     * Constructs the {@link GeneratorSettingsWidget} for the given outer shell object.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param shell shell of the parent widget
     */
    GeneratorSettingsWidget(Shell shell) {
        this.shell = shell;
        initUI();
        innerShell = null;
    }

    private void actionBtnCancel() {
        shell.setText(I18N.getWidgetTitleGenerator());
        innerShell.dispose();
    }

    private void actionBtnOk() {
        if (!checkForEmptyTexts()) {
            int errorOccurred = Integer.MIN_VALUE;

            if (!TextCheck.isFileExists(textDefaultPath)) {
                errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirBaseNotFound());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_BASE, textDefaultPath.getText());
            }

            if (!TextCheck.isFileExists(textProjectPath)) {
                errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirProjectNotFound());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT, textProjectPath.getText());
            }

            if (!TextCheck.isFileExists(textProjectPathTemplateFolder)) {
                errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirProjectDefaultNotFound());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE, textProjectPathTemplateFolder.getText());
            }

            if (!TextCheck.isFileExists(textAdminPath)) {
                errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirAdminNotFound());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN, textAdminPath.getText());
            }

            if (!TextCheck.isFileExists(textAdminPathTemplateFolder)) {
                errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirAdminDefaultNotFound());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE, textAdminPathTemplateFolder.getText());
            }

            if (!TextCheck.isFileExists(textBigDataPath)) {
                errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirBigDataNotFound());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA, textBigDataPath.getText());
            }

            if (!TextCheck.isFileExists(textBigDataPathTemplateFolder)) {
                errorOccurred = MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgDirBigDataDefaultNotFound());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE, textBigDataPathTemplateFolder.getText());
            }

            if (errorOccurred == Integer.MIN_VALUE) {
                Main.statusBar.setStatus(I18N.getStatusSettingsSaved(), OK);
                shell.setText(I18N.getWidgetTitleGenerator());

                MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION, I18N.getMsgBoxTitleSuccess(), I18N.getMsgSettingsSuccess());

                innerShell.dispose();
            }
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgBoxTitleWarning(), I18N.getMsgChooseDirWarning());
        }
    }

    private boolean checkForEmptyTexts() {
        return TextCheck.isEmpty(textDefaultPath) ||
                TextCheck.isEmpty(textProjectPath) ||
                TextCheck.isEmpty(textProjectPathTemplateFolder) ||
                TextCheck.isEmpty(textAdminPath) ||
                TextCheck.isEmpty(textAdminPathTemplateFolder) ||
                TextCheck.isEmpty(textBigDataPath) ||
                TextCheck.isEmpty(textBigDataPathTemplateFolder);
    }

    private Button createAdminPathComposite(int style) {
        Label adminPath = new Label(innerShell, style);
        adminPath.setText(I18N.getLabelTextDirAdmin());

        textAdminPath = new Text(innerShell, style | SWT.SINGLE);
        textAdminPath.setText(Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN));

        Button btnAdminPath = new Button(innerShell, SWT.PUSH);
        btnAdminPath.setText(I18N.getBtnChoosePath());
        btnAdminPath.setToolTipText(I18N.getBtnChoosePath());
        btnAdminPath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, textAdminPath, I18N.getFileChooserDirAdminTitle(),
                        I18N.getFileChooserDirAdminMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN));
            }
        });

        GridData data4_1 = new GridData();
        adminPath.setLayoutData(data4_1);

        GridData data4_2 = new GridData();
        data4_2.horizontalAlignment = SWT.FILL;
        data4_2.grabExcessHorizontalSpace = true;
        textAdminPath.setLayoutData(data4_2);

        GridData data4_3 = new GridData();
        data4_3.horizontalAlignment = SWT.END;
        btnAdminPath.setLayoutData(data4_3);
        return btnAdminPath;
    }

    private Button createAdminPathTemplateComposite(int style) {
        Label adminPathTemplateFolder = new Label(innerShell, style);
        adminPathTemplateFolder.setText(I18N.getLabelTextDirAdminTemplate());

        textAdminPathTemplateFolder = new Text(innerShell, style | SWT.SINGLE);
        textAdminPathTemplateFolder.setText(Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE));

        Button btnAdminPathTemplateFolder = new Button(innerShell, SWT.PUSH);
        btnAdminPathTemplateFolder.setText(I18N.getBtnChoosePath());
        btnAdminPathTemplateFolder.setToolTipText(I18N.getBtnChoosePath());
        btnAdminPathTemplateFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, textAdminPathTemplateFolder, I18N.getFileChooserDirAdminTemplateTitle(),
                        I18N.getFileChooserDirAdminTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE));
            }
        });

        GridData data5_1 = new GridData();
        adminPathTemplateFolder.setLayoutData(data5_1);

        GridData data5_2 = new GridData();
        data5_2.horizontalAlignment = SWT.FILL;
        data5_2.grabExcessHorizontalSpace = true;
        textAdminPathTemplateFolder.setLayoutData(data5_2);

        GridData data5_3 = new GridData();
        data5_3.horizontalAlignment = SWT.END;
        btnAdminPathTemplateFolder.setLayoutData(data5_3);
        return btnAdminPathTemplateFolder;
    }

    private Button createBigDataPathComposite(int style) {
        Label bigDataPath = new Label(innerShell, style);
        bigDataPath.setText(I18N.getLabelTextDirBigData());

        textBigDataPath = new Text(innerShell, style | SWT.SINGLE);
        textBigDataPath.setText(Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA));

        Button btnBigDataPath = new Button(innerShell, SWT.PUSH);
        btnBigDataPath.setText(I18N.getBtnChoosePath());
        btnBigDataPath.setToolTipText(I18N.getBtnChoosePath());
        btnBigDataPath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, textBigDataPath, I18N.getFileChooserDirBigDataTitle(),
                        I18N.getFileChooserDirBigDataMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA));
            }
        });

        GridData data6_1 = new GridData();
        bigDataPath.setLayoutData(data6_1);

        GridData data6_2 = new GridData();
        data6_2.horizontalAlignment = SWT.FILL;
        data6_2.grabExcessHorizontalSpace = true;
        textBigDataPath.setLayoutData(data6_2);

        GridData data6_3 = new GridData();
        data6_3.horizontalAlignment = SWT.END;
        btnBigDataPath.setLayoutData(data6_3);
        return btnBigDataPath;
    }

    private Button createBigDataPathTemplateComposite(int style) {
        Label bigDataPathTemplateFolder = new Label(innerShell, style);
        bigDataPathTemplateFolder.setText(I18N.getLabelTextDirBigDataTemplate());

        textBigDataPathTemplateFolder = new Text(innerShell, style | SWT.SINGLE);
        textBigDataPathTemplateFolder.setText(Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE));

        Button btnBigDataPathTemplateFolder = new Button(innerShell, SWT.PUSH);
        btnBigDataPathTemplateFolder.setText(I18N.getBtnChoosePath());
        btnBigDataPathTemplateFolder.setToolTipText(I18N.getBtnChoosePath());
        btnBigDataPathTemplateFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, textBigDataPathTemplateFolder, I18N.getFileChooserDirBigDataTemplateTitle(),
                        I18N.getFileChooserDirBigDataTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE));
            }
        });

        GridData data7_1 = new GridData();
        bigDataPathTemplateFolder.setLayoutData(data7_1);

        GridData data7_2 = new GridData();
        data7_2.horizontalAlignment = SWT.FILL;
        data7_2.grabExcessHorizontalSpace = true;
        textBigDataPathTemplateFolder.setLayoutData(data7_2);

        GridData data7_3 = new GridData();
        data7_3.horizontalAlignment = SWT.END;
        btnBigDataPathTemplateFolder.setLayoutData(data7_3);
        return btnBigDataPathTemplateFolder;
    }

    private void createBottomButtons() {
        Composite compositeCancelOK = new Composite(innerShell, SWT.NONE);
        compositeCancelOK.setLayout(new FillLayout());

        Button btnCancel = new Button(compositeCancelOK, SWT.NONE);
        btnCancel.setText(I18N.getBtnCancelLabel());
        btnCancel.setToolTipText(I18N.getBtnCancelLabelToolTip());
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnCancel();
            }
        });

        Button btnOK = new Button(compositeCancelOK, SWT.NONE);
        btnOK.setText(I18N.getBtnOKLabel());
        btnOK.setToolTipText(I18N.getBtnOKToolTip());
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnOk();
            }
        });

        GridData data = new GridData(SWT.END, SWT.END, false, true);
        data.horizontalSpan = 3;
        compositeCancelOK.setLayoutData(data);
    }

    private Button createDefaultPathComposite(int style) {
        Label defaultPath = new Label(innerShell, style);
        defaultPath.setText(I18N.getLabelTextDirBase());

        textDefaultPath = new Text(innerShell, style | SWT.SINGLE);
        textDefaultPath.setText(Main.pref.getUserPref(PreferenceHandler.DIR_BASE));

        Button btnDefaultPath = new Button(innerShell, SWT.PUSH);
        btnDefaultPath.setText(I18N.getBtnChoosePath());
        btnDefaultPath.setToolTipText(I18N.getBtnChoosePath());
        btnDefaultPath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, textDefaultPath, I18N.getFileChooserDirBaseTitle(),
                        I18N.getFileChooserDirBaseMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_BASE));
            }
        });

        GridData data1_1 = new GridData();
        data1_1.verticalIndent = SWT.FILL;
        defaultPath.setLayoutData(data1_1);

        GridData data1_2 = new GridData();
        data1_2.horizontalAlignment = SWT.FILL;
        data1_2.grabExcessHorizontalSpace = true;
        textDefaultPath.setLayoutData(data1_2);

        GridData data1_3 = new GridData();
        data1_3.horizontalAlignment = SWT.END;
        btnDefaultPath.setLayoutData(data1_3);
        return btnDefaultPath;
    }

    private Button createProjectPathComposite(int style) {
        Label projectPath = new Label(innerShell, style);
        projectPath.setText(I18N.getLabelTextDirProject());

        textProjectPath = new Text(innerShell, style | SWT.SINGLE);
        textProjectPath.setText(Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT));

        Button btnProjectPath = new Button(innerShell, SWT.PUSH);
        btnProjectPath.setText(I18N.getBtnChoosePath());
        btnProjectPath.setToolTipText(I18N.getBtnChoosePath());
        btnProjectPath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, textProjectPath, I18N.getFileChooserDirProjectTitle(),
                        I18N.getFileChooserDirProjectMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT));
            }
        });

        GridData data2_1 = new GridData();
        projectPath.setLayoutData(data2_1);

        GridData data2_2 = new GridData();
        data2_2.horizontalAlignment = SWT.FILL;
        data2_2.grabExcessHorizontalSpace = true;
        textProjectPath.setLayoutData(data2_2);

        GridData data2_3 = new GridData();
        data2_3.horizontalAlignment = SWT.END;
        btnProjectPath.setLayoutData(data2_3);
        return btnProjectPath;
    }

    private Button createProjectPathTemplateComposite(int style) {
        Label projectPathTemplateFolder = new Label(innerShell, style);
        projectPathTemplateFolder.setText(I18N.getLabelTextDirProjectTemplate());

        textProjectPathTemplateFolder = new Text(innerShell, style | SWT.SINGLE);
        textProjectPathTemplateFolder.setText(Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE));

        Button btnProjectPathTemplateFolder = new Button(innerShell, SWT.PUSH);
        btnProjectPathTemplateFolder.setText(I18N.getBtnChoosePath());
        btnProjectPathTemplateFolder.setToolTipText(I18N.getBtnChoosePath());
        btnProjectPathTemplateFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, textProjectPathTemplateFolder, I18N.getFileChooserDirProjectTemplateTitle(),
                        I18N.getFileChooserDirProjectTemplateMessage(), Main.pref.getUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE));
            }
        });

        GridData data3_1 = new GridData();
        projectPathTemplateFolder.setLayoutData(data3_1);

        GridData data3_2 = new GridData();
        data3_2.horizontalAlignment = SWT.FILL;
        data3_2.grabExcessHorizontalSpace = true;
        textProjectPathTemplateFolder.setLayoutData(data3_2);

        GridData data3_3 = new GridData();
        data3_3.horizontalAlignment = SWT.END;
        btnProjectPathTemplateFolder.setLayoutData(data3_3);
        return btnProjectPathTemplateFolder;
    }

    private void initUI() {
        int height = Main.getRyCONWidgetHeight();
        int width = Main.getRyCONWidgetWidth() + 150;

        final int style = SWT.BORDER;

        innerShell = new Shell(shell, SWT.SHEET);
        shell.setText(I18N.getWidgetTitleGeneratorSettings());

        innerShell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                actionBtnCancel();
            }
        });
        innerShell.setText(I18N.getWidgetTitleGeneratorSettings());
        innerShell.setSize(width, height);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 20;
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = false;

        innerShell.setLayout(gridLayout);

        Button btnDefaultPath = createDefaultPathComposite(style);

        Button btnProjectPath = createProjectPathComposite(style);
        Button btnProjectPathTemplateFolder = createProjectPathTemplateComposite(style);

        Button btnAdminPath = createAdminPathComposite(style);
        Button btnAdminPathTemplateFolder = createAdminPathTemplateComposite(style);

        Button btnBigDataPath = createBigDataPathComposite(style);
        Button btnBigDataPathTemplateFolder = createBigDataPathTemplateComposite(style);

        Control[] tabulatorKeyOrder = new Control[]{
                textDefaultPath, btnDefaultPath,
                textProjectPath, btnProjectPath,
                textProjectPathTemplateFolder, btnProjectPathTemplateFolder,
                textAdminPath, btnAdminPath,
                textAdminPathTemplateFolder, btnAdminPathTemplateFolder,
                textBigDataPath, btnBigDataPath,
                textBigDataPathTemplateFolder, btnBigDataPathTemplateFolder
        };

        innerShell.setTabList(tabulatorKeyOrder);

        createBottomButtons();

        innerShell.open();
    }

} // end of GeneratorSettingsWidget
