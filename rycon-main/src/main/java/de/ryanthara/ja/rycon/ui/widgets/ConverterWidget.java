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
import de.ryanthara.ja.rycon.core.converter.csv.BaseToolsCsv;
import de.ryanthara.ja.rycon.core.converter.excel.BaseToolsExcel;
import de.ryanthara.ja.rycon.core.converter.text.BaseToolsTxt;
import de.ryanthara.ja.rycon.core.converter.toporail.FileType;
import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDialect;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.custom.*;
import de.ryanthara.ja.rycon.ui.util.RadioHelper;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.widgets.convert.FileFilterIndex;
import de.ryanthara.ja.rycon.ui.widgets.convert.SourceButton;
import de.ryanthara.ja.rycon.ui.widgets.convert.TargetButton;
import de.ryanthara.ja.rycon.ui.widgets.convert.read.*;
import de.ryanthara.ja.rycon.ui.widgets.convert.write.*;
import de.ryanthara.ja.rycon.util.StringUtils;
import de.ryanthara.ja.rycon.util.check.TextCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;
import static de.ryanthara.ja.rycon.ui.widgets.convert.FileFilterIndex.*;

/**
 * Instances of this class implements a complete converter widget and it's functionality.
 * <p>
 * The {@code ConverterWidget} of <tt>RyCON</tt> is used to convert measurement and coordinate
 * files into different formats.
 *
 * @author sebastian
 * @version 10
 * @since 1
 */
public class ConverterWidget extends AbstractWidget {

    private static final Logger logger = LoggerFactory.getLogger(ConverterWidget.class.getName());
    private final Shell parent;
    private Button chkBoxCadworkUseZeroHeights;
    private Button chkBoxCsvSemicolonSeparatorSource;
    private Button chkBoxCsvSemicolonSeparatorTarget;
    private Button chkBoxKFormatUseSimpleCaplanKFormat;
    private Button chkBoxLtopEliminateDuplicatePoints;
    private Button chkBoxSortOutputFileByNumber;
    private Button chkBoxSourceContainsCode;
    private Button chkBoxTxtSpaceSeparator;
    private Button chkBoxWriteCodeColumn;
    private Button chkBoxWriteCommentLine;
    private Button chkBoxWriteZeroHeights;
    private Path[] files2read;
    private Group groupOptionsSource;
    private Group groupOptionsTarget;
    private Group groupSource;
    private Group groupTarget;
    private InputFieldsComposite inputFieldsComposite;
    private Shell innerShell;

    /**
     * Constructs the {@link ConverterWidget} without any parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public ConverterWidget(final Shell parent) {
        this.parent = parent;
        this.files2read = new Path[0];

        initUI();
        handleCommandLineInterfaceInjection();
    }

    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    boolean actionBtnOk() {
        if (TextCheck.isEmpty(inputFieldsComposite.getSourceTextField()) || TextCheck.isEmpty(inputFieldsComposite.getTargetTextField())) {
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
                String status;

                final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.conversionStatus);

                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), Main.countFileOps);
                } else {
                    status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), Main.countFileOps);
                }

                Main.statusBar.setStatus(status, OK);
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
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.converterText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createCompositeSourceTarget();
        createInputFieldsComposite();
        // createTxtCsv(width);
        createOptionsGeneral(width);
        createAdvice(width);
        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        toggleOptionsSource(groupSource.getChildren());
        toggleOptionsTarget(groupTarget.getChildren());

        innerShell.pack();
        innerShell.open();

        updateInnerShell();
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnSource() {
        FileDialog fileDialog = new FileDialog(innerShell, SWT.MULTI);

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

        fileDialog.setFilterPath(filterPath);
        fileDialog.setText(ResourceBundleUtils.getLangString(FILECHOOSERS, FileChoosers.converterSourceText));

        fileDialog.setFilterExtensions(CSV.getExtensionsArray());
        fileDialog.setFilterNames(CSV.getFilterNamesArray());

        // determine filter index by source group selection
        determineFilterIndex(fileDialog);

        String firstFile = fileDialog.open();

        if (firstFile != null) {
            String[] files = fileDialog.getFileNames();

            files2read = new Path[files.length];

            // displaying file names without path in text field
            String concatString = "";

            String workingDir = fileDialog.getFilterPath();

            for (int i = 0; i < files.length; i++) {
                concatString = concatString.concat(files[i]);
                concatString = concatString.concat(" ");

                files2read[i] = Paths.get(workingDir + FileSystems.getDefault().getSeparator() + files[i]);
            }

            inputFieldsComposite.getTargetTextField().setText(fileDialog.getFilterPath());
            inputFieldsComposite.getSourceTextField().setText(concatString);

            // set the radio buttons
            Control[] childrenSource = groupSource.getChildren();
            Control[] childrenTarget = groupTarget.getChildren();

            toggleRadioButtons(fileDialog, childrenSource, childrenTarget);
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

    private void createAdvice(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.text));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);

        String text =
                ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.converterWidget) + "\n\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.converterWidget2) + "\n\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.converterWidget3);

        tip.setText(text);

        // tip.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.converterWidget));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void createCompositeSourceTarget() {
        Composite compositeSourceTarget = new Composite(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout(2, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

        compositeSourceTarget.setLayout(gridLayout);
        compositeSourceTarget.setLayoutData(gridData);

        groupSource = new Group(compositeSourceTarget, SWT.NONE);
        groupSource.setText(ResourceBundleUtils.getLangString(LABELS, Labels.sourceFormatText));
        groupSource.setLayout(new GridLayout(2, false));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupSource.setLayoutData(gridData);

        groupTarget = new Group(compositeSourceTarget, SWT.NONE);
        groupTarget.setText(ResourceBundleUtils.getLangString(LABELS, Labels.targetFormatText));
        groupTarget.setLayout(new GridLayout(2, false));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupTarget.setLayoutData(gridData);

        SelectionListener selectionListenerSource = getSelectionListenerForToggleOptions();
        SelectionListener selectionListenerTarget = getSelectionListenerForToggleOptions();

        createRadioButtonsSource(selectionListenerSource, groupSource);
        createRadioButtonsTarget(selectionListenerTarget, groupTarget);
    }

    private void createInputFieldsComposite() {
        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginLeft = 0;

        inputFieldsComposite = new InputFieldsComposite(this, innerShell);
        inputFieldsComposite.setLayout(gridLayout);
    }

    private void createOptionsGeneral(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.general));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        chkBoxWriteCommentLine = new Button(group, SWT.CHECK);
        chkBoxWriteCommentLine.setSelection(false);
        chkBoxWriteCommentLine.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.writeCommentLine));

        chkBoxWriteCodeColumn = new Button(group, SWT.CHECK);
        chkBoxWriteCodeColumn.setSelection(false);
        chkBoxWriteCodeColumn.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.writeCodeColumn));

        chkBoxWriteZeroHeights = new Button(group, SWT.CHECK);
        chkBoxWriteZeroHeights.setSelection(false);
        chkBoxWriteZeroHeights.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.writeZeroHeights));

        innerShell.layout(true, true);
    }

    private void createOptionsOptionalCadworkSource() {
        groupOptionsSource.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_CadworkSource));

        chkBoxCadworkUseZeroHeights = new Button(groupOptionsSource, SWT.CHECK);
        chkBoxCadworkUseZeroHeights.setSelection(false);
        chkBoxCadworkUseZeroHeights.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.useZeroHeightsCadwork));

        groupOptionsSource.layout(true);
    }

    private void createOptionsOptionalCaplanKTarget() {
        groupOptionsTarget.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_CaplanKTarget));

        chkBoxKFormatUseSimpleCaplanKFormat = new Button(groupOptionsTarget, SWT.CHECK);
        chkBoxKFormatUseSimpleCaplanKFormat.setSelection(true);
        chkBoxKFormatUseSimpleCaplanKFormat.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.useSimpleKFormatChk));

        chkBoxSortOutputFileByNumber = new Button(groupOptionsTarget, SWT.CHECK);
        chkBoxSortOutputFileByNumber.setSelection(false);
        chkBoxSortOutputFileByNumber.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.sortOutputFileByNumber));

        groupOptionsTarget.layout(true);
    }

    private void createOptionsOptionalCsvSource() {
        groupOptionsSource.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_CsvSource));

        chkBoxSourceContainsCode = new Button(groupOptionsSource, SWT.CHECK);
        chkBoxSourceContainsCode.setSelection(false);
        chkBoxSourceContainsCode.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.sourceContainsCodeCsvChk));

        chkBoxCsvSemicolonSeparatorSource = new Button(groupOptionsSource, SWT.CHECK);
        chkBoxCsvSemicolonSeparatorSource.setSelection(false);
        chkBoxCsvSemicolonSeparatorSource.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.separatorCSVSemiColon));

        groupOptionsSource.layout(true);
    }

    private void createOptionsOptionalCsvTarget() {
        groupOptionsTarget.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_CsvTarget));

        chkBoxCsvSemicolonSeparatorTarget = new Button(groupOptionsTarget, SWT.CHECK);
        chkBoxCsvSemicolonSeparatorTarget.setSelection(false);
        chkBoxCsvSemicolonSeparatorTarget.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.separatorCSVSemiColon));

        groupOptionsTarget.layout(true);
    }

    private void createOptionsOptionalLtop() {
        groupOptionsTarget.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_LtopTarget));

        chkBoxLtopEliminateDuplicatePoints = new Button(groupOptionsTarget, SWT.CHECK);
        chkBoxLtopEliminateDuplicatePoints.setSelection(true);
        chkBoxLtopEliminateDuplicatePoints.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.eliminateDuplicatePointsLTOP));

        chkBoxSortOutputFileByNumber = new Button(groupOptionsTarget, SWT.CHECK);
        chkBoxSortOutputFileByNumber.setSelection(false);
        chkBoxSortOutputFileByNumber.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.sortOutputFileByNumber));

        groupOptionsTarget.layout(true);
    }

    private void createOptionsOptionalSource(int width) {
        groupOptionsSource = new Group(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        groupOptionsSource.setLayout(gridLayout);
        groupOptionsSource.setLayoutData(gridData);
    }

    private void createOptionsOptionalTarget(int width) {
        groupOptionsTarget = new Group(innerShell, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        groupOptionsTarget.setLayout(gridLayout);
        groupOptionsTarget.setLayoutData(gridData);
    }

    private void createOptionsOptionalTxtSource() {
        groupOptionsSource.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_TxtSource));

        chkBoxSourceContainsCode = new Button(groupOptionsSource, SWT.CHECK);
        chkBoxSourceContainsCode.setSelection(false);
        chkBoxSourceContainsCode.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.sourceContainsCodeTxtChk));

        groupOptionsSource.layout(true);
    }

    private void createOptionsOptionalTxtTarget() {
        groupOptionsTarget.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_TxtTarget));

        chkBoxTxtSpaceSeparator = new Button(groupOptionsTarget, SWT.CHECK);
        chkBoxTxtSpaceSeparator.setSelection(false);
        chkBoxTxtSpaceSeparator.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.separatorTXTSpace));

        groupOptionsTarget.layout(true);
    }

    private void createRadioButtonsSource(SelectionListener selectionListener, Group group) {
        for (SourceButton button : SourceButton.values()) {
            Button radioBtn = new Button(group, SWT.RADIO);
            radioBtn.addSelectionListener(selectionListener);
            radioBtn.setText(button.getText());

            if (button == SourceButton.GSI16) {
                radioBtn.setSelection(true);
            }
        }
    }

    private void createRadioButtonsTarget(SelectionListener selectionListener, Group group) {
        for (TargetButton button : TargetButton.values()) {
            Button radioBtn = new Button(group, SWT.RADIO);
            radioBtn.addSelectionListener(selectionListener);
            radioBtn.setText(button.getText());

            if (button == TargetButton.TXT) {
                radioBtn.setSelection(true);
            }
        }
    }

    private void createTxtCsv(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.txtCsvGroup));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        GridData gridData1 = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData1.horizontalSpan = 2;

        Button button = new Button(group, SWT.CHECK);
        button.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.txtCsvSpecialFormat));
        button.setLayoutData(gridData1);

        Group group1 = new Group(group, SWT.NONE);
        GridData gridData2 = new GridData(GridData.FILL, GridData.CENTER, true, true);
        group1.setLayout(new GridLayout(2, false));
        group1.setLayoutData(gridData2);

        Group group2 = new Group(group1, SWT.NONE);
        GridData gridData3 = new GridData(GridData.FILL, GridData.CENTER, false, false);
        group2.setLayout(new GridLayout(2, true));
        group2.setLayoutData(gridData3);

        Button buttonAdd = new Button(group2, SWT.NONE);
        buttonAdd.setText("+");

        Button buttonMinus = new Button(group2, SWT.NONE);
        buttonMinus.setText("-");

        /*

        // Configure shell
        shell.setLayout(new GridLayout());

        // Configure standard composite
        Composite standardComposite = new Composite(shell, SWT.NONE);
        standardComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        // Configure scrolled composite
        ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setAlwaysShowScrollBars(true);

        // Add content to scrolled composite
        Composite scrolledContent = new Composite(scrolledComposite, SWT.NONE);
        scrolledContent.setLayout(new GridLayout());
        scrolledComposite.setContent(scrolledContent);

         */


        // this button has a minimum size of 400 x 400. If the window is resized to be big
        // enough to show more than 400 x 400, the button will grow in size. If the window
        // is made too small to show 400 x 400, scrollbars will appear.
        ScrolledComposite scrolledComposite = new ScrolledComposite(group1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setAlwaysShowScrollBars(true);
        scrolledComposite.setExpandHorizontal(true);

        Group group3 = new Group(scrolledComposite, SWT.NONE);
        GridData gridData4 = new GridData(GridData.FILL, GridData.CENTER, true, true);
        group3.setLayout(new GridLayout(15, true));
        group3.setLayoutData(gridData4);

        for (int i = 0; i < 15; i++) {
            CCombo combo = new CCombo(group3, SWT.NONE);
            combo.setText("Combo 1");
            combo.add("item 1");
            combo.add("item 2");

            gridData = new GridData();
            combo.setLayoutData(gridData);
        }

        for (int i = 0; i < 15; i++) {
            CCombo combo2 = new CCombo(group3, SWT.NONE);
            combo2.setText("Combo 2");
            combo2.add("item 3");
            combo2.add("item 4");

            gridData = new GridData();
            combo2.setLayoutData(gridData);
        }

        group3.pack();
        group3.setSize(group3.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));

        scrolledComposite.setContent(group3);
        scrolledComposite.setMinSize(group3.getSize().x, group3.getSize().y);
        scrolledComposite.layout(true);

        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinSize(group3.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));


        buttonAdd.addSelectionListener(new SelectionAdapter() {
            /**
             * Sent when selection occurs in the control.
             * The default behavior is to do nothing.
             *
             * @param e an event containing information about the selection
             */
            @Override
            public void widgetSelected(SelectionEvent e) {

            }
        });

        buttonMinus.addSelectionListener(new SelectionAdapter() {
            /**
             * Sent when selection occurs in the control.
             * The default behavior is to do nothing.
             *
             * @param e an event containing information about the selection
             */
            @Override
            public void widgetSelected(SelectionEvent e) {

            }
        });
    }

    private void createTxtCsv2(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.txtCsvGroup));

        FillLayout fillLayout = new FillLayout(SWT.NONE);
        group.setLayout(fillLayout);

        Button button = new Button(group, SWT.CHECK);
        button.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.txtCsvSpecialFormat));

        // this button has a minimum size of 400 x 400. If the window is resized to be big
        // enough to show more than 400 x 400, the button will grow in size. If the window
        // is made too small to show 400 x 400, scrollbars will appear.
        ScrolledComposite c1 = new ScrolledComposite(group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        Button b1 = new Button(c1, SWT.PUSH);
        b1.setText("fixed size button");
        b1.setSize(800, 200);
        c1.setContent(b1);
    }

    private void createTxtCsvTable(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.txtCsvGroup));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        GridData gridData1 = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData1.horizontalSpan = 2;

        Button button = new Button(group, SWT.CHECK);
        button.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.txtCsvSpecialFormat));
        button.setLayoutData(gridData1);

        Group group1 = new Group(group, SWT.NONE);
        GridData gridData2 = new GridData(GridData.FILL, GridData.CENTER, true, false);
        group1.setLayout(new GridLayout(2, true));
        group1.setLayoutData(gridData2);

        Button buttonAdd = new Button(group1, SWT.NONE);
        buttonAdd.setText("+");

        Button buttonMinus = new Button(group1, SWT.NONE);
        buttonMinus.setText("-");

        Table table = new Table(group1, SWT.HIDE_SELECTION);
        table.setLinesVisible(true);

        GridData gridData3 = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData3.horizontalSpan = 2;
        table.setLayoutData(gridData3);

        for (int i = 0; i < 4; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setWidth(250);
        }

        // create two rows (table items)
        for (int i = 0; i < 2; i++) {
            new TableItem(table, SWT.NONE);
        }

        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++) {
            TableItem item = items[i];
            TableEditor editor = new TableEditor(table);
            CCombo combo = new CCombo(table, SWT.NONE);
            combo.setText("Combo " + i);
            combo.add("item 1");
            combo.add("item 2");
            editor.grabHorizontal = true;
            editor.setEditor(combo, item, 0);
            item.setData("ComboEditor1", editor);
            editor = new TableEditor(table);
            CCombo combo2 = new CCombo(table, SWT.NONE);
            combo2.setText("Combo " + i);
            combo2.add("item 3");
            combo2.add("item 4");
            editor.grabHorizontal = true;
            editor.setEditor(combo2, item, 0);
            item.setData("ComboEditor2", editor);
        }

        buttonAdd.addSelectionListener(new SelectionAdapter() {
            /**
             * Sent when selection occurs in the control.
             * The default behavior is to do nothing.
             *
             * @param e an event containing information about the selection
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println("HIT");

                TableColumn column = new TableColumn(table, SWT.RIGHT);
                column.setText("Column " + table.getColumnCount());
                column.setWidth(250);

                for (int i = 0; i < table.getColumnCount(); i++) {
                    TableEditor tableEditor = new TableEditor(table);

                    CCombo comboColumn = new CCombo(table, SWT.NONE);
                    comboColumn.setText("Column");
                    comboColumn.add("combo item 1");
                    comboColumn.add("combo item 2");

                    tableEditor.grabHorizontal = true;
                    tableEditor.setEditor(comboColumn, table.getItem(0), i);
                }

                for (int i = 0; i < table.getColumnCount(); i++) {
                    TableEditor tableEditor = new TableEditor(table);

                    CCombo comboContext = new CCombo(table, SWT.NONE);
                    comboContext.setText("Context");
                    comboContext.add("combo item 3");
                    comboContext.add("combo item 4");

                    tableEditor.grabHorizontal = true;
                    tableEditor.setEditor(comboContext, table.getItem(1), i);
                }

            }
        });

        table.setRedraw(false);


        buttonMinus.addSelectionListener(new SelectionAdapter() {
            /**
             * Sent when selection occurs in the control.
             * The default behavior is to do nothing.
             *
             * @param e an event containing information about the selection
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                ArrayList<TableColumn> columns = new ArrayList<>();

                for (int i = 0; i < table.getColumnCount() - 1; i++) {
                    columns.add(table.getColumn(i));
                }

                TableItem[] items = table.getItems();
                for (TableItem item : items) {
                    TableEditor editor = (TableEditor) item.getData("ComboEditor1");
                    editor.getEditor().dispose();
                    editor.dispose();
                    editor = (TableEditor) item.getData("ComboEditor2");
                    editor.getEditor().dispose();
                    editor.dispose();
                    table.remove(table.indexOf(item));
                    group1.pack();
                }

                /*
                columns = new ArrayList<>(columns);

                while (table.getColumnCount() > 0) {
                    table.getColumns()[0].dispose();
                }

                System.out.println(columns.size());

                for (TableColumn column : columns) {
                    TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
                    tableColumn.setWidth(150);
                }
                */
            }
        });

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumn(i).pack();
        }

        table.setRedraw(true);

        /*
        Table table1 = new Table(group, SWT.BORDER | SWT.MULTI);
        table1.setLinesVisible(true);
        for (int i = 0; i < 1; i++) {
            TableColumn column = new TableColumn(table1, SWT.NONE);
            column.setWidth(100);
        }
        for (int i = 0; i < 2; i++) {
            new TableItem(table1, SWT.NONE);
        }
        TableItem[] items = table1.getItems();

        for (int i = 0; i < items.length; i++) {
            TableEditor tableEditor = new TableEditor(table1);

            CCombo combo = new CCombo(table1, SWT.NONE);
            combo.setText("CCombo");
            combo.add("combo item 1");
            combo.add("combo item 2");

            tableEditor.grabHorizontal = true;
            tableEditor.setEditor(combo, items[i], 0);
            tableEditor = new TableEditor(table1);

            Text text = new Text(table1, SWT.NONE);
            text.setText("Text");

            tableEditor.grabHorizontal = true;
            tableEditor.setEditor(text, items[i], 1);
            tableEditor = new TableEditor(table1);

            Button button4 = new Button(table1, SWT.CHECK);
            button4.pack();

            tableEditor.minimumWidth = button4.getSize().x;
            tableEditor.horizontalAlignment = SWT.LEFT;
            tableEditor.setEditor(button4, items[i], 2);
        }
        */

        innerShell.layout(true);

    }

    private void determineFilterIndex(FileDialog fileDialog) {
        int selectedBtnSource = RadioHelper.getSelectedBtn(groupSource.getChildren());

        switch (SourceButton.fromIndex(selectedBtnSource)) {
            case GSI8:
            case GSI16:
                fileDialog.setFilterIndex(GSI.ordinal());
                break;
            case TXT:
                fileDialog.setFilterIndex(TXT.ordinal());
                break;
            case CSV:
                fileDialog.setFilterIndex(CSV.ordinal());
                break;
            case CAPLAN_K:
                fileDialog.setFilterIndex(K.ordinal());
                break;
            case ZEISS_REC:
                fileDialog.setFilterIndex(REC.ordinal());
                break;
            case CADWORK:
                fileDialog.setFilterIndex(DAT.ordinal());
                break;
            case BASEL_STADT:
                fileDialog.setFilterIndex(CSV.ordinal());
                break;
            case BASEL_LANDSCHAFT:
                fileDialog.setFilterIndex(TXT.ordinal());
                break;
            case TOPORAIL_MEP:
                fileDialog.setFilterIndex(MEP.ordinal());
                break;
            case TOPORAIL_PTS:
                fileDialog.setFilterIndex(PTS.ordinal());
                break;
            default:
                fileDialog.setFilterIndex(GSI.ordinal());

                logger.warn("Set default filter index to '{}'", GSI.toString());
        }
    }

    private SelectionListener getSelectionListenerForToggleOptions() {
        return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // prevent double fired events
                boolean isSelected = ((Button) e.getSource()).getSelection();
                if (isSelected) {
                    toggleOptionsSource(groupSource.getChildren());
                    toggleOptionsTarget(groupTarget.getChildren());
                }
            }
        };
    }

    private void handleCommandLineInterfaceInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            inputFieldsComposite.setSourceTextFieldText(files);
        }

        RadioHelper.selectBtn(groupSource.getChildren(), Main.getCliSourceBtnNumber());
        RadioHelper.selectBtn(groupTarget.getChildren(), Main.getCliTargetBtnNumber());

        toggleOptionsSource(groupSource.getChildren());
        toggleOptionsTarget(groupTarget.getChildren());

        updateInnerShell();
    }

    private Map<Integer, Reader> prepareReadFileMaps() {
        Map<Integer, Reader> readFileMap = new HashMap<>();
        readFileMap.put(0, new GsiReader(innerShell));
        readFileMap.put(1, new GsiReader(innerShell));
        readFileMap.put(2, new TxtReader(innerShell));
        readFileMap.put(3, new CsvReader(innerShell, chkBoxCsvSemicolonSeparatorSource.getSelection()));
        readFileMap.put(4, new CaplanReader(innerShell));
        readFileMap.put(5, new ZeissReader(innerShell));
        readFileMap.put(6, new CadworkReader(innerShell));
        readFileMap.put(7, new BaselStadtCsvReader(innerShell));
        readFileMap.put(8, new BaselLandschaftTxtReader(innerShell));
        readFileMap.put(9, new ToporailReader(innerShell, FileType.MEP));
        readFileMap.put(10, new ToporailReader(innerShell, FileType.PTS));

        return readFileMap;
    }

    private Map<Integer, Writer> prepareWriteFile(Path readFile, ArrayList<String> readStringFile, List<String[]> readCSVFile, WriteParameter parameter) {
        final Path writeFile = Paths.get(inputFieldsComposite.getTargetTextField().getText() + FileSystems.getDefault().getSeparator() + readFile.getFileName());

        Map<Integer, Writer> writeFileMap = new HashMap<>();
        writeFileMap.put(0, new GsiWriter(writeFile, readStringFile, readCSVFile, parameter, Main.getGsi8()));
        writeFileMap.put(1, new GsiWriter(writeFile, readStringFile, readCSVFile, parameter, Main.getGsi16()));
        writeFileMap.put(2, new TxtWriter(writeFile, readStringFile, readCSVFile, parameter));
        writeFileMap.put(3, new CsvWriter(writeFile, readStringFile, readCSVFile, parameter));
        writeFileMap.put(4, new CaplanWriter(writeFile, readStringFile, readCSVFile, parameter));
        writeFileMap.put(5, new ZeissWriter(writeFile, readStringFile, readCSVFile, parameter));
        writeFileMap.put(6, new LtopKooWriter(writeFile, readStringFile, readCSVFile, parameter));
        writeFileMap.put(7, new LtopMesWriter(writeFile, readStringFile, parameter));
        writeFileMap.put(8, new ToporailWriter(writeFile, readStringFile, readCSVFile, parameter, FileType.MEP));
        writeFileMap.put(9, new ToporailWriter(writeFile, readStringFile, readCSVFile, parameter, FileType.PTS));
        writeFileMap.put(10, new ExcelWriter(writeFile, readStringFile, readCSVFile, parameter, BaseToolsExcel.isXLSX));
        writeFileMap.put(11, new ExcelWriter(writeFile, readStringFile, readCSVFile, parameter, BaseToolsExcel.isXLS));
        writeFileMap.put(12, new OdfWriter(writeFile, readStringFile, readCSVFile, parameter));

        return writeFileMap;
    }

    private boolean processFileOperations() {
        boolean success;

        int counter = 0;
        int sourceNumber = RadioHelper.getSelectedBtn(groupSource.getChildren());
        int targetNumber = RadioHelper.getSelectedBtn(groupTarget.getChildren());

        WriteParameter parameter = prepareWriteParameter();

        Map<Integer, Reader> readFileMap = prepareReadFileMaps();

        for (Path file2read : files2read) {
            boolean readFileSuccess = false;

            List<String[]> readCSVFile = null;
            ArrayList<String> readStringFile = null;

            // read files (new version)
            if (readFileMap.containsKey(sourceNumber)) {
                if (readFileMap.get(sourceNumber).readFile(file2read)) {
                    if ((readCSVFile = readFileMap.get(sourceNumber).getReadCSVFile()) != null) {
                        readFileSuccess = true;
                    } else if ((readStringFile = readFileMap.get(sourceNumber).getReadStringLines()) != null) {
                        readFileSuccess = true;
                    }
                }
            }

            // write files (new version)
            if (readFileSuccess) {
                Map<Integer, Writer> writeFileMap = prepareWriteFile(file2read, readStringFile, readCSVFile, parameter);
                if (writeFileMap.containsKey(targetNumber)) {
                    if (writeFileMap.get(targetNumber).writeSpreadsheetDocument()) {
                        counter = counter + 1;
                    } else if (writeFileMap.get(targetNumber).writeStringFile()) {
                        counter = counter + 1;
                    } else if (writeFileMap.get(targetNumber).writeWorkbookFile()) {
                        counter = counter + 1;
                    }
                }
            }
        }

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.conversionMessage);

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
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.conversionFailed));

            success = false;
        }

        return success;
    }

    /*
     * Prepares a parameter object for reducing parameter field size.
     * <p>
     * With the introduction of variable options for source and target formats this function was extended.
     */
    private WriteParameter prepareWriteParameter() {
        int sourceNumber = RadioHelper.getSelectedBtn(groupSource.getChildren());
        boolean gsiFormat = sourceNumber == 0 ? Main.getGsi8() : Main.getGsi16();
        boolean cadworkUseZeroHeights = false;
        boolean caplanKFormatUseSimpleCaplanKFormat = false;
        boolean ltopEliminateDuplicatePoints = false;
        boolean sortOutputFileByNumber = false;
        boolean sourceContainsCode = false;
        boolean writeCodeColumn = false;
        boolean writeCommentLine = false;
        boolean writeZeroHeights = false;

        if (chkBoxCadworkUseZeroHeights != null) {
            if (!chkBoxCadworkUseZeroHeights.isDisposed()) {
                cadworkUseZeroHeights = chkBoxCadworkUseZeroHeights.getSelection();
            }
        }

        if (chkBoxKFormatUseSimpleCaplanKFormat != null) {
            if (!chkBoxKFormatUseSimpleCaplanKFormat.isDisposed()) {
                caplanKFormatUseSimpleCaplanKFormat = chkBoxKFormatUseSimpleCaplanKFormat.getSelection();
            }
        }

        if (chkBoxLtopEliminateDuplicatePoints != null) {
            if (!chkBoxLtopEliminateDuplicatePoints.isDisposed()) {
                ltopEliminateDuplicatePoints = chkBoxLtopEliminateDuplicatePoints.getSelection();
            }
        }

        if (chkBoxSortOutputFileByNumber != null) {
            if (!chkBoxSortOutputFileByNumber.isDisposed()) {
                sortOutputFileByNumber = chkBoxSortOutputFileByNumber.getSelection();
            }
        }

        if (chkBoxSourceContainsCode != null) {
            if (!chkBoxSourceContainsCode.isDisposed()) {
                sourceContainsCode = chkBoxSourceContainsCode.getSelection();
            }
        }

        if (chkBoxWriteCodeColumn != null) {
            if (!chkBoxWriteCodeColumn.isDisposed()) {
                writeCodeColumn = chkBoxWriteCodeColumn.getSelection();
            }
        }

        if (chkBoxWriteCommentLine != null) {
            if (!chkBoxWriteCommentLine.isDisposed()) {
                writeCommentLine = chkBoxWriteCommentLine.getSelection();
            }
        }

        if (chkBoxWriteZeroHeights != null) {
            if (!chkBoxWriteZeroHeights.isDisposed()) {
                writeZeroHeights = chkBoxWriteZeroHeights.getSelection();
            }
        }

        String separatorCSV = BaseToolsCsv.SEPARATOR_COMMA;
        String separatorTXT = BaseToolsTxt.SEPARATOR_TAB;

        if (chkBoxCsvSemicolonSeparatorTarget != null) {
            if (!chkBoxCsvSemicolonSeparatorTarget.isDisposed()) {
                separatorCSV = chkBoxCsvSemicolonSeparatorTarget.getSelection() ? BaseToolsCsv.SEPARATOR_SEMICOLON : BaseToolsCsv.SEPARATOR_COMMA;
            }
        }

        if (chkBoxTxtSpaceSeparator != null) {
            if (!chkBoxTxtSpaceSeparator.isDisposed()) {
                separatorTXT = chkBoxTxtSpaceSeparator.getSelection() ? BaseToolsTxt.SEPARATOR_SPACE : BaseToolsTxt.SEPARATOR_TAB;
            }
        }

        final ZeissDialect dialect = ZeissDialect.valueOf(Main.pref.getUserPreference(PreferenceKeys.CONVERTER_SETTING_ZEISS_DIALECT));

        return new WriteParameter(sourceNumber, gsiFormat,
                cadworkUseZeroHeights,
                caplanKFormatUseSimpleCaplanKFormat,
                ltopEliminateDuplicatePoints,
                sortOutputFileByNumber,
                sourceContainsCode,
                writeCodeColumn,
                writeCommentLine,
                writeZeroHeights,
                separatorCSV, separatorTXT, dialect);
    }

    private void toggleOptionsSource(Control... childrenSource) {
        final int width = Sizes.RyCON_WIDGET_WIDTH.getValue();
        int selectedBtnSource = RadioHelper.getSelectedBtn(childrenSource);

        // Remove special options if present
        for (int i = 0; i < innerShell.getChildren().length; i++) {
            Control c = innerShell.getChildren()[i];

            if (c.equals(groupOptionsSource)) {
                innerShell.getChildren()[i].dispose();
            }
        }

        // Remove advice text and bottom button bar
        innerShell.getChildren()[innerShell.getChildren().length - 1].dispose();
        innerShell.getChildren()[innerShell.getChildren().length - 1].dispose();

        switch (SourceButton.fromIndex(selectedBtnSource)) {
            case GSI8:
            case GSI16:
                break;
            case TXT:
                createOptionsOptionalSource(width);
                createOptionsOptionalTxtSource();
                break;
            case CSV:
                createOptionsOptionalSource(width);
                createOptionsOptionalCsvSource();
                break;
            case CAPLAN_K:
                break;
            case ZEISS_REC:
                break;
            case CADWORK:
                createOptionsOptionalSource(width);
                createOptionsOptionalCadworkSource();
                break;
            case BASEL_STADT:
                break;
            case BASEL_LANDSCHAFT:
                break;
            case TOPORAIL_MEP:
                break;
            case TOPORAIL_PTS:
                break;
            default:
                logger.warn("Unknown filter index without special options '{}'",
                        SourceButton.fromIndex(selectedBtnSource).toString());
        }

        createAdvice(width);
        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        updateInnerShell();
    }

    private void toggleOptionsTarget(Control... childrenTarget) {
        final int width = Sizes.RyCON_WIDGET_WIDTH.getValue();
        int selectedBtnTarget = RadioHelper.getSelectedBtn(childrenTarget);

        // Remove special options if present
        for (int i = 0; i < innerShell.getChildren().length; i++) {
            Control c = innerShell.getChildren()[i];

            if (c.equals(groupOptionsTarget)) {
                innerShell.getChildren()[i].dispose();
            }
        }

        // Remove advice text and bottom button bar
        innerShell.getChildren()[innerShell.getChildren().length - 1].dispose();
        innerShell.getChildren()[innerShell.getChildren().length - 1].dispose();

        switch (TargetButton.fromIndex(selectedBtnTarget)) {
            case GSI8:
            case GSI16:
                createOptionsOptionalTarget(width);
                createOptionsOptionalGsiTarget();
                break;
            case TXT:
                createOptionsOptionalTarget(width);
                createOptionsOptionalTxtTarget();
                break;
            case CSV:
                createOptionsOptionalTarget(width);
                createOptionsOptionalCsvTarget();
                break;
            case CAPLAN_K:
                createOptionsOptionalTarget(width);
                createOptionsOptionalCaplanKTarget();
                break;
            case ZEISS_REC:
                break;
            case LTOP_KOO:
                createOptionsOptionalTarget(width);
                createOptionsOptionalLtop();
                break;
            case LTOP_MES:
                break;
            case TOPORAIL_MEP:
                break;
            case TOPORAIL_PTS:
                break;
            case EXCEL_XLSX:
                break;
            case EXCEL_XLS:
                break;
            case ODF_ODS:
                break;
            default:
                logger.warn("Unknown filter index without special options '{}'",
                        TargetButton.fromIndex(selectedBtnTarget).toString());
        }

        createAdvice(width);
        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        updateInnerShell();
    }

    private void createOptionsOptionalGsiTarget() {
        groupOptionsTarget.setText(ResourceBundleUtils.getLangStringFromXml(OPTIONS, Options.converter_GsiTarget));

        chkBoxSortOutputFileByNumber = new Button(groupOptionsTarget, SWT.CHECK);
        chkBoxSortOutputFileByNumber.setSelection(false);
        chkBoxSortOutputFileByNumber.setText(ResourceBundleUtils.getLangString(CHECKBOXES, CheckBoxes.sortOutputFileByNumber));

        groupOptionsTarget.layout(true);
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private void toggleRadioButtons(FileDialog fileDialog, Control[] childrenSource, Control[] childrenTarget) {
        switch (FileFilterIndex.fromIndex(fileDialog.getFilterIndex())) {
            case GSI:
                if (RadioHelper.getSelectedBtn(childrenSource) > 1) {
                    RadioHelper.selectBtn(childrenSource, 1);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 0) {
                    RadioHelper.selectBtn(childrenSource, 1);
                } else if (RadioHelper.getSelectedBtn(childrenTarget) == 1) {
                    RadioHelper.selectBtn(childrenSource, 0);
                }
                break;

            case TXT:
                // prevent button change for geodata Basel Landschaft files
                if (RadioHelper.getSelectedBtn(childrenSource) != 8) {
                    RadioHelper.selectBtn(childrenSource, 2);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 2) {
                    RadioHelper.selectBtn(childrenTarget, 1);
                }
                break;

            case CSV:
                // prevent button change for geodata Basel Stadt files
                if (RadioHelper.getSelectedBtn(childrenSource) != 7) {
                    RadioHelper.selectBtn(childrenSource, 3);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 3) {
                    RadioHelper.selectBtn(childrenTarget, 1);
                }
                break;

            case K:
                // prevent button change for CAPLAN K files
                if (RadioHelper.getSelectedBtn(childrenSource) != 4) {
                    RadioHelper.selectBtn(childrenSource, 4);
                }
                if (RadioHelper.getSelectedBtn(childrenTarget) == 4) {
                    RadioHelper.selectBtn(childrenTarget, 1);
                }
                break;

            case DAT:
                // prevent button change for node.dat (cadwork) files
                if (RadioHelper.getSelectedBtn(childrenSource) != 6) {
                    RadioHelper.selectBtn(childrenSource, 6);
                }
                break;

            case REC:
                // prevent button change for Zeiss REC files
                if (RadioHelper.getSelectedBtn(childrenSource) != 5) {
                    RadioHelper.selectBtn(childrenSource, 5);
                }
                break;

            default:
                logger.trace("Can not toggle radio button for unknown format '{}'.", FileFilterIndex.fromIndex(fileDialog.getFilterIndex()));
                break;
        }
    }

    /*
     * Layout the inner shell to make changes happen
     */
    private void updateInnerShell() {
        innerShell.pack();
        innerShell.layout(true, true);
        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitorVertically(innerShell));
    }

} // end of ConverterWidget
