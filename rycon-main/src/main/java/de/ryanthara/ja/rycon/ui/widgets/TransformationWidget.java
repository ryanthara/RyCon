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
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.i18n.Error;
import de.ryanthara.ja.rycon.i18n.Text;
import de.ryanthara.ja.rycon.i18n.ToolTip;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.ui.Size;
import de.ryanthara.ja.rycon.ui.custom.BottomButtonBar;
import de.ryanthara.ja.rycon.ui.custom.InputFieldsComposite;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.custom.Status;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.util.TextCheck;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.*;

/**
 * Instances of this class provides functions to transform coordinate files between different coordinate systems
 * or reference frames. Therefore a couple of external free libraries are used.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TransformationWidget extends AbstractWidget {

    private static final Logger logger = LoggerFactory.getLogger(TransformationWidget.class.getName());

    private final String[] altimetricFrame = {
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.altimetric_LN02),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.altimetric_LHN95),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.altimetric_Ellipsoid),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.altimetric_CHGeo98)
    };
    private final String[] planimetricFrame = {
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.planimetric_LV03_Military),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.planimetric_LV95),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.planimetric_LV03_Civil)
    };
    private final String[] projectionChange = {
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.projection_ETRF93GeocentricToLV95),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.projection_ETRF93GeographicToLV95),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.projection_LV95ToETRF93Geocentric),
            ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTyp.projection_LV95ToETRF93Geographic)
    };

    private final Shell parent;
    private Shell innerShell;
    private InputFieldsComposite inputFieldsComposite;
    private Button chkBoxInsertCodeColumn;
    private Button chkBoxWriteCodeZero;
    private Path[] files2read;

    /**
     * Constructs a new instance of this class without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public TransformationWidget(Shell parent) {
        this.parent = parent;
        this.files2read = new Path[0];

        initUI();
        handleFileInjection();
    }

    @Override
    void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", Status.OK);
        innerShell.dispose();
    }

    @Override
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
                String status;

                final String helper = ResourceBundleUtils.getLangString(MESSAGE, Message.transformationStatus);

                // use counter to display different text on the status bar
                if (Main.countFileOps == 1) {
                    status = String.format(StringUtils.getSingularMessage(helper), files2read.length, Main.countFileOps);
                } else {
                    status = String.format(StringUtils.getPluralMessage(helper), files2read.length, Main.countFileOps);
                }

                Main.statusBar.setStatus(status, Status.OK);
            }

            return true;
        }

        return false;
    }

    @Override
    void actionBtnOkAndExit() {
        if (actionBtnOk()) {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", Status.OK);

            innerShell.dispose();
        }
    }

    @Override
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
        innerShell.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.transfer_Shell));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        createInputFieldsComposite();
        createReferenceFrameChooserSource(width);
        createReferenceFrameChooserTarget(width);
        createProjectionChooser(width);
        createCopyAndPasteField(width);
        createOptions(width);
        createAdvice(width);

        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private void createCopyAndPasteField(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.transformation_GroupCopyAndPaste));

        GridLayout gridLayout = new GridLayout(2, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        org.eclipse.swt.widgets.Text pasteField = new org.eclipse.swt.widgets.Text(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
        Listener scrollBarListener = event -> {
            org.eclipse.swt.widgets.Text t = (org.eclipse.swt.widgets.Text) event.widget;
            Rectangle r1 = t.getClientArea();
            Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
            Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            t.getHorizontalBar().setVisible(r2.width <= p.x);
            t.getVerticalBar().setVisible(r2.height <= p.y);
            if (event.type == SWT.Modify) {
                t.getParent().layout(true);
                t.showSelection();
            }
        };

        pasteField.addListener(SWT.Resize, scrollBarListener);
        pasteField.addListener(SWT.Modify, scrollBarListener);
        pasteField.addModifyListener(e -> {
            System.out.println("modified: " + pasteField.getText());
        });
        // put text field with listener here. The pasted coordinates are transformed immediately.

        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        pasteField.setLayoutData(gridData);

        Label description = new Label(group, SWT.WRAP | SWT.NONE);
        String helper =
                ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.pasteCoordinates) + "\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.pasteCoordinates2);

        description.setText(helper);
        // description.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.pasteCoordinates));

        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        description.setLayoutData(gridData);
    }

    private void createAdvice(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.advice));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);

        String text =
                ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.transformationWidget) + "\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.transformationWidget2) + "\n\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.transformationWidget3);

        tip.setText(text);

        // tip.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advice.transformationWidget));
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
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.generalOptions));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        chkBoxInsertCodeColumn = new Button(group, SWT.CHECK);
        chkBoxInsertCodeColumn.setSelection(false);
        chkBoxInsertCodeColumn.setText(ResourceBundleUtils.getLangString(CHECKBOX, CheckBox.insertCodeColumn));

        chkBoxWriteCodeZero = new Button(group, SWT.CHECK);
        chkBoxWriteCodeZero.setSelection(false);
        chkBoxWriteCodeZero.setText(ResourceBundleUtils.getLangString(CHECKBOX, CheckBox.writeCodeZeroSplitter));
    }

    private void createProjectionChooser(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.transformation_GroupProjection));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        CCombo refProjectionChangeSourceChooser = new CCombo(group, SWT.NONE);
        refProjectionChangeSourceChooser.setEditable(false);
        refProjectionChangeSourceChooser.setItems(projectionChange);
        refProjectionChangeSourceChooser.select(0);
        refProjectionChangeSourceChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTip.projection_change));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        refProjectionChangeSourceChooser.setLayoutData(gridData);


        // input
        // planimetric frame

        // altimetric frame

        // projection change


        // output

        // planimetric frame

        // altimetric frame

        // projection change

    }

    private void createReferenceFrameChooserSource(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.transformation_GroupReferenceFrameSource));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        CCombo refFramePlanimetricSourceChooser = new CCombo(group, SWT.NONE);
        refFramePlanimetricSourceChooser.setEditable(false);
        refFramePlanimetricSourceChooser.setItems(planimetricFrame);
        refFramePlanimetricSourceChooser.select(0);
        refFramePlanimetricSourceChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTip.transformation_planimetricFrameSource));

        CCombo refFrameAltimetricSourceChooser = new CCombo(group, SWT.NONE);
        refFrameAltimetricSourceChooser.setEditable(false);
        refFrameAltimetricSourceChooser.setItems(altimetricFrame);
        refFrameAltimetricSourceChooser.select(0);
        refFrameAltimetricSourceChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTip.transformation_altimetricFrameSource));

        // input
        // planimetric frame

        // altimetric frame

        // projection change


        // output

        // planimetric frame

        // altimetric frame

        // projection change

    }

    private void createReferenceFrameChooserTarget(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Text.transformation_GroupReferenceFrameTarget));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        CCombo refFramePlanimetricTargetChooser = new CCombo(group, SWT.NONE);
        refFramePlanimetricTargetChooser.setEditable(false);
        refFramePlanimetricTargetChooser.setText("Zielsystem Lage");
        refFramePlanimetricTargetChooser.setToolTipText("ZielsystemZielsystem Lage Tooltip");

        CCombo refFrameAltimetricTargetChooser = new CCombo(group, SWT.NONE);
        refFrameAltimetricTargetChooser.setEditable(false);
        refFrameAltimetricTargetChooser.setText("Zielsystem Höhe");
        refFrameAltimetricTargetChooser.setToolTipText("Zielsystem Höhe Tooltip");

        // input
        // planimetric frame

        // altimetric frame

        // projection change


        // output

        // planimetric frame

        // altimetric frame

        // projection change

    }

    private int fileOperations(boolean insertCodeColumn, boolean writeFileWithCodeZero) {
        int counter = 0;

        for (Path path : files2read) {
            // first attempt to ignore logfile.txt files
            if (!path.toString().toLowerCase().contains("logfile.txt")) {
                LineReader lineReader = new LineReader(path);

                if (lineReader.readFile(false)) {
                    java.util.List<String> readFile = lineReader.getLines();

                    // the glob pattern ("glob:*.dat) doesn't work here
                    PathMatcher matcherDAT = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.DAT)");
                    PathMatcher matcherGSI = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.GSI)");
                    PathMatcher matcherTXT = FileSystems.getDefault().getPathMatcher("regex:(?iu:.+\\.TXT)");

                    /*
                    if (matcherDAT.matches(path)) {
                        counter = executeSplitNodeDat(counter, path, readFile);
                    } else if (matcherGSI.matches(path)) {
                        counter = executeSplitGsi(insertCodeColumn, writeFileWithCodeZero, counter, path, readFile);
                    } else if (matcherTXT.matches(path)) {
                        counter = executeSplitTxt(insertCodeColumn, writeFileWithCodeZero, counter, path, readFile);
                    } else {
                        logger.warn("File format of '{}' are not supported (yet).", path.getFileName());
                    }
                    */
                } else {
                    logger.warn("File {} could not be read.", path.toString());
                }
            }
        }

        return counter;
    }

    private void handleFileInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            System.out.println("to do...");
//            inputFieldsComposite.setSourceTextFieldText(files);
        }
    }

    private boolean processFileOperations() {
        int counter = fileOperations(chkBoxInsertCodeColumn.getSelection(), chkBoxWriteCodeZero.getSelection());

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGE, Message.transformationMessage);

            if (counter == 1) {
                message = String.format(StringUtils.getSingularMessage(helper), files2read.length, counter);
            } else {
                message = String.format(StringUtils.getPluralMessage(helper), files2read.length, counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Text.msgBox_Success), message);

            // set the counter for status bar information
            Main.countFileOps = counter;

            return true;
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Text.msgBox_Error),
                    ResourceBundleUtils.getLangString(ERROR, Error.transformationFailed));

            return false;
        }
    }

}
