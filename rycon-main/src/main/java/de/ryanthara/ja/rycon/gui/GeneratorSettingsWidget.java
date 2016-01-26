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

package de.ryanthara.ja.rycon.gui;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;


/**
 * This class implements a complete widget and it's functionality.
 * <p>
 * The GeneratorSettingsWidget of RyCON is used to set preferences for the generator
 * functionality.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>4: implementation of a new directory structure, code reformat</li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 3
 * @since 1
 */
public class GeneratorSettingsWidget {

    private Shell innerShell = null;
    private Shell shell = null;
    private Text textDefaultPath = null;
    private Text textAdminPath = null;
    private Text textAdminPathTemplateFolder = null;
    private Text textBigDataPath = null;
    private Text textBigDataPathTemplateFolder = null;
    private Text textProjectPath = null;
    private Text textProjectPathTemplateFolder = null;

    /**
     * Class constructor with parameter for the outer shell object.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param shell shell of the parent widget
     */
    public GeneratorSettingsWidget(Shell shell) {
        this.shell = shell;
        initUI();
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
                GuiHelper.showAdvancedDirectoryDialog(innerShell, textDefaultPath, I18N.getFileChooserDirBaseTitle(),
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
                GuiHelper.showAdvancedDirectoryDialog(innerShell, textProjectPath, I18N.getFileChooserDirProjectTitle(),
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
                GuiHelper.showAdvancedDirectoryDialog(innerShell, textProjectPathTemplateFolder, I18N.getFileChooserDirProjectTemplateTitle(),
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
                GuiHelper.showAdvancedDirectoryDialog(innerShell, textAdminPath, I18N.getFileChooserDirAdminTitle(),
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
                GuiHelper.showAdvancedDirectoryDialog(innerShell, textAdminPathTemplateFolder, I18N.getFileChooserDirAdminTemplateTitle(),
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
                GuiHelper.showAdvancedDirectoryDialog(innerShell, textBigDataPath, I18N.getFileChooserDirBigDataTitle(),
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
                GuiHelper.showAdvancedDirectoryDialog(innerShell, textBigDataPathTemplateFolder, I18N.getFileChooserDirBigDataTemplateTitle(),
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


        // tabulator key order
        Control[] controls = new Control[]{
                textDefaultPath, btnDefaultPath,
                textProjectPath, btnProjectPath,
                textProjectPathTemplateFolder, btnProjectPathTemplateFolder,
                textAdminPath, btnAdminPath,
                textAdminPathTemplateFolder, btnAdminPathTemplateFolder,
                textBigDataPath, btnBigDataPath,
                textBigDataPathTemplateFolder, btnBigDataPathTemplateFolder
        };
        innerShell.setTabList(controls);

        createBottomButtons();

        // innerShell.setLayout(gridLayout);
        innerShell.open();
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

    private void actionBtnCancel() {
        shell.setText(I18N.getWidgetTitleGenerator());
        innerShell.dispose();
    }

    // TODO implement functional check
    private void actionBtnOk() {
        // checks for text field inputs and valid directories
        if ((textDefaultPath != null) && (textAdminPath != null) && (textAdminPathTemplateFolder != null) &&
                (textBigDataPath != null) && (textBigDataPathTemplateFolder != null) &&
                (textProjectPath != null) && (textProjectPathTemplateFolder != null)) {

            File checkDirBase = new File(textDefaultPath.getText());
            if (!checkDirBase.exists()) {
                /*
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirBaseNotFound());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
                */
                showMessageBox(innerShell, SWT.ICON_WARNING, I18N.getMsgDirBaseNotFound(), I18N.getMsgBoxTitleWarning());
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_BASE, textDefaultPath.getText());
            }

            File checkDirProject = new File(textProjectPath.getText());
            if (!checkDirProject.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirProjectNotFound());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT, textProjectPath.getText());
            }

            File checkDirProjectDefault = new File(textProjectPathTemplateFolder.getText());
            if (!checkDirProjectDefault.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirProjectDefaultNotFound());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_PROJECT_TEMPLATE, textProjectPathTemplateFolder.getText());
            }

            File checkDirAdmin = new File(textAdminPath.getText());
            if (!checkDirAdmin.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirAdminNotFound());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN, textAdminPath.getText());
            }

            File checkDirAdminTemplate = new File(textAdminPathTemplateFolder.getText());
            if (!checkDirAdminTemplate.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirAdminDefaultNotFound());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_ADMIN_TEMPLATE, textAdminPathTemplateFolder.getText());
            }

            File checkDirBigData = new File(textBigDataPath.getText());
            if (!checkDirBigData.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirBigDataNotFound());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA, textBigDataPath.getText());
            }

            File checkDirBigDataDefault = new File(textBigDataPathTemplateFolder.getText());
            if (!checkDirBigDataDefault.exists()) {
                MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
                msgBox.setMessage(I18N.getMsgDirBigDataDefaultNotFound());
                msgBox.setText(I18N.getMsgBoxTitleWarning());
                msgBox.open();
            } else {
                Main.pref.setUserPref(PreferenceHandler.DIR_BIG_DATA_TEMPLATE, textBigDataPathTemplateFolder.getText());
            }

            Main.statusBar.setStatus(I18N.getStatusSettingsSaved(), StatusBar.OK);
            shell.setText(I18N.getWidgetTitleGenerator());
            innerShell.dispose();
        } else {
            MessageBox msgBox = new MessageBox(innerShell, SWT.ICON_WARNING);
            msgBox.setMessage(I18N.getMsgChooseDirWarning());
            msgBox.setText(I18N.getMsgBoxTitleWarning());
            msgBox.open();
        }
    }

    private void showMessageBox(Shell innerShell, int icon, String text, String message) {
        MessageBox messageBox = new MessageBox(innerShell, icon);
        messageBox.setText(text);
        messageBox.setMessage(message);
        messageBox.open();
    }

} // end of GeneratorSettingsWidget
