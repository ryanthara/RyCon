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

import com.swisstopo.geodesy.reframe_lib.IReframe;
import com.swisstopo.geodesy.reframe_lib.Reframe;
import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.PreferenceKey;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.ui.Size;
import de.ryanthara.ja.rycon.ui.custom.*;
import de.ryanthara.ja.rycon.ui.util.ComboHelper;
import de.ryanthara.ja.rycon.ui.util.RadioHelper;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.util.TextCheck;
import de.ryanthara.ja.rycon.ui.widgets.transform.GpsrefRunnable;
import de.ryanthara.ja.rycon.ui.widgets.transform.ReframeRunnable;
import de.ryanthara.ja.rycon.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;

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

    private final String[] acceptableFileSuffixes = new String[]{"*.csv", "*.gsi", "*.txt"};

    private final Shell parent;
    private Shell innerShell;
    private InputFieldsComposite inputFieldsComposite;
    private Path[] files2read;
    private Group groupMethod;
    private Group groupReferenceFrameSource;
    private Group groupReferenceFrameTarget;
    private Group groupProjection;
    private Label progressLabel;
    private ProgressBar progressBar;
    private BottomButtonBar bottomButtonBar;
    private ReframeRunnable reframeRunnable;

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
        if (reframeRunnable != null) {
            reframeRunnable.cancel();
            // Set to null to enter else branch
            reframeRunnable = null;
        } else {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", Status.OK);
            innerShell.dispose();
        }
    }

    @Override
    boolean actionBtnOk() {
        if (TextCheck.isEmpty(inputFieldsComposite.getSourceTextField()) ||
                TextCheck.isEmpty(inputFieldsComposite.getTargetTextField())) {
            return false;
        }

        provideFiles2ReadFromText();

        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperations()) {
                String status;

                final String helper = ResourceBundleUtils.getLangString(MESSAGE, Messages.transformationStatus);

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

    private void provideFiles2ReadFromText() {
        if (files2read.length == 0) {
            files2read = new Path[1];
            files2read[0] = Paths.get(inputFieldsComposite.getSourceTextField().getText());
        } else {
            files2read = TextCheck.checkSourceAndTargetText(inputFieldsComposite, files2read);
        }
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
        innerShell.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.transfer_Shell));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        buildInputFieldsComposite();

        groupMethod = buildComputeMethodChooser(width);

        groupReferenceFrameSource = buildReferenceFrameChooserSource(width);
        groupReferenceFrameTarget = buildReferenceFrameChooserTarget(width);
        groupProjection = buildProjectionChooser(width);

        buildBottomElements(width);

        new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        toggleComputeChooser(groupMethod.getChildren());

        layoutAndPositShell();

        innerShell.open();
    }

    private Group buildComputeMethodChooser(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.transformation_Method));

        GridLayout gridLayout = new GridLayout(2, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        SelectionListener listener = getSelectionListenerForToggleOptions();

        Button computeReframe = new Button(group, SWT.RADIO);
        computeReframe.setSelection(true);
        computeReframe.addSelectionListener(listener);
        computeReframe.setText(ComputeMethod.REFRAME.text);

        Button computeGpsref = new Button(group, SWT.RADIO);
        computeGpsref.addSelectionListener(listener);
        computeGpsref.setText(ComputeMethod.GPSREF.text);

        return group;
    }

    private void buildCopyAndPasteField(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.transformation_GroupCopyAndPaste));

        GridLayout gridLayout = new GridLayout(2, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Text pasteField = new Text(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
        Listener scrollBarListener = event -> {
            Text t = (Text) event.widget;
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
                ResourceBundleUtils.getLangStringFromXml(ADVICE, Advices.pasteCoordinates) + "\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advices.pasteCoordinates2);

        description.setText(helper);
        // description.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advices.pasteCoordinates));

        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        description.setLayoutData(gridData);
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnSource() {
        String[] filterNames = new String[]{
                ResourceBundleUtils.getLangString(FILECHOOSER, FileChoosers.filterNameCsv),
                ResourceBundleUtils.getLangString(FILECHOOSER, FileChoosers.filterNameGsi),
                ResourceBundleUtils.getLangString(FILECHOOSER, FileChoosers.filterNameTxt)
        };

        String filterPath = Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT);

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
                ResourceBundleUtils.getLangString(FILECHOOSER, FileChoosers.clearUpSourceText),
                acceptableFileSuffixes,
                filterNames,
                inputFieldsComposite.getSourceTextField(),
                inputFieldsComposite.getTargetTextField());

        if (files.isPresent()) {
            files2read = files.get();
        } else {
            logger.warn("Can not get the source files to be read.");
        }
    }

    /*
     * This method is used from the class InputFieldsComposite!
     */
    private void actionBtnTarget() {
        String filterPath = Main.pref.getUserPreference(PreferenceKey.DIR_PROJECT);

        Text input = inputFieldsComposite.getTargetTextField();

        // Set the initial filter path according to anything selected or typed in
        if (!TextCheck.isEmpty(input)) {
            if (TextCheck.isDirExists(input)) {
                filterPath = input.getText();
            }
        }

        DirectoryDialogs.showAdvancedDirectoryDialog(innerShell, input,
                DirectoryDialogsTyp.DIR_GENERAL.getText(),
                DirectoryDialogsTyp.DIR_GENERAL.getMessage(),
                filterPath);
    }

    private void buildAdvice(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.advice));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Label tip = new Label(group, SWT.WRAP | SWT.BORDER | SWT.LEFT);

        String text =
                ResourceBundleUtils.getLangStringFromXml(ADVICE, Advices.transformationWidget) + "\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advices.transformationWidget2) + "\n\n" +
                        ResourceBundleUtils.getLangStringFromXml(ADVICE, Advices.transformationWidget3);

        tip.setText(text);

        // tip.setText(ResourceBundleUtils.getLangStringFromXml(ADVICE, Advices.transformationWidget));
        tip.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
    }

    private void buildInputFieldsComposite() {
        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginLeft = 0;

        inputFieldsComposite = new InputFieldsComposite(this, innerShell);
        inputFieldsComposite.setLayout(gridLayout);
    }

    private Group buildProjectionChooser(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.transformation_GroupProjection));

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Combo refProjectionChangeSourceChooser = new Combo(group, SWT.READ_ONLY);

        for (ProjectionChange projectionChange : ProjectionChange.values()) {
            refProjectionChangeSourceChooser.add(projectionChange.text);
            refProjectionChangeSourceChooser.setData(projectionChange.text, projectionChange);
        }

        refProjectionChangeSourceChooser.select(0);
        refProjectionChangeSourceChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTips.projection_change));

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        refProjectionChangeSourceChooser.setLayoutData(gridData);

        return group;
    }

    private Group buildProgressBar(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText("Progress");

        GridLayout gridLayout = new GridLayout(2, false);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        progressBar = new ProgressBar(group, SWT.NONE);
        progressBar.setMinimum(0);
        progressBar.setMaximum(1);
        progressBar.setSelection(0);

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        // gridData.horizontalSpan = 2;

        progressBar.setLayoutData(gridData);

        progressLabel = new Label(group, SWT.NONE | SWT.RIGHT);
        progressLabel.setText("");
        gridData = new GridData(SWT.RIGHT, SWT.FILL, false, true);
        gridData.widthHint = 150;
        progressLabel.setLayoutData(gridData);

        return group;
    }

    private Group buildReferenceFrameChooserSource(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.transformation_GroupReferenceFrameSource));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Combo refFramePlanimetricSourceChooser = new Combo(group, SWT.READ_ONLY);

        for (PlanimetricFrame planimetricFrame : PlanimetricFrame.values()) {
            refFramePlanimetricSourceChooser.add(planimetricFrame.text);
            refFramePlanimetricSourceChooser.setData(planimetricFrame.text, planimetricFrame);
        }

        refFramePlanimetricSourceChooser.select(0);
        refFramePlanimetricSourceChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTips.transformation_planimetricFrameSource));
        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        refFramePlanimetricSourceChooser.setLayoutData(gridData);

        Combo refFrameAltimetricSourceChooser = new Combo(group, SWT.READ_ONLY);

        for (AltimetricFrame altimetricFrame : AltimetricFrame.values()) {
            refFrameAltimetricSourceChooser.add(altimetricFrame.text);
            refFrameAltimetricSourceChooser.setData(altimetricFrame.text, altimetricFrame);
        }

        refFrameAltimetricSourceChooser.select(0);
        refFrameAltimetricSourceChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTips.transformation_altimetricFrameSource));
        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        refFrameAltimetricSourceChooser.setLayoutData(gridData);

        return group;
    }

    private Group buildReferenceFrameChooserTarget(int width) {
        Group group = new Group(innerShell, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.transformation_GroupReferenceFrameTarget));

        GridLayout gridLayout = new GridLayout(1, true);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = width - 24;

        group.setLayout(gridLayout);
        group.setLayoutData(gridData);

        Combo refFramePlanimetricTargetChooser = new Combo(group, SWT.READ_ONLY);

        for (PlanimetricFrame planimetricFrame : PlanimetricFrame.values()) {
            refFramePlanimetricTargetChooser.add(planimetricFrame.text);
            refFramePlanimetricTargetChooser.setData(planimetricFrame.text, planimetricFrame);
        }

        refFramePlanimetricTargetChooser.select(1);
        refFramePlanimetricTargetChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTips.transformation_planimetricFrameTarget));
        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        refFramePlanimetricTargetChooser.setLayoutData(gridData);

        Combo refFrameAltimetricTargetChooser = new Combo(group, SWT.READ_ONLY);

        for (AltimetricFrame altimetricFrame : AltimetricFrame.values()) {
            refFrameAltimetricTargetChooser.add(altimetricFrame.text);
            refFrameAltimetricTargetChooser.setData(altimetricFrame.text, altimetricFrame);
        }

        refFrameAltimetricTargetChooser.select(0);
        refFrameAltimetricTargetChooser.setToolTipText(ResourceBundleUtils.getLangStringFromXml(TOOLTIP, ToolTips.transformation_altimetricFrameTarget));
        gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        refFrameAltimetricTargetChooser.setLayoutData(gridData);

        return group;
    }

    private int fileOperations() {
        int counter = 0;

        int selectedBtn = RadioHelper.getSelectedBtn(groupMethod.getChildren());

        Reframe reframeObj = new Reframe();

        /*
         * Input coordinates: read in a file, got from a text field,
         * or obtained through another method or library...
         * East and North in LV03, usual height (LN02)
         */
        double[] inputCoordinates = new double[]{601000.0, 197500.0, 555.0};
        double[] outputCoordinates = null;

        int count = 999;

        double[][] input = new double[count][3];

        for (int i = 0; i < count; i++) {
            double x = i + 601000.0;
            double y = i + 197500.0;
            double z = i + 555.0;

            input[i] = new double[]{x, y, z};
        }

        switch (Objects.requireNonNull(ComputeMethod.fromIndex(selectedBtn).orElse(null))) {
            case GPSREF:
                IReframe.ProjectionChange projectionChange = getProjectionChange(groupProjection);

                GpsrefRunnable gpsrefRunnable = new GpsrefRunnable(
                        progressBar,
                        progressLabel,
                        bottomButtonBar,
                        input,
                        projectionChange);

                Thread threadGpsref = new Thread(gpsrefRunnable, "GpsrefThread");
                threadGpsref.start();

                try {
                    threadGpsref.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Thread finished");

                break;

            case REFRAME:
                IReframe.PlanimetricFrame planimetricFrameSource = getPlanimetricFrame(groupReferenceFrameSource);
                IReframe.AltimetricFrame altimetricFrameSource = getAltimetricFrame(groupReferenceFrameSource);

                IReframe.PlanimetricFrame planimetricFrameTarget = getPlanimetricFrame(groupReferenceFrameTarget);
                IReframe.AltimetricFrame altimetricFrameTarget = getAltimetricFrame(groupReferenceFrameTarget);

                reframeRunnable = new ReframeRunnable(
                        progressBar,
                        progressLabel,
                        bottomButtonBar,
                        input,
                        planimetricFrameSource,
                        planimetricFrameTarget,
                        altimetricFrameSource,
                        altimetricFrameTarget
                );

                Thread threadReframe = new Thread(reframeRunnable, "ReframeRunnable");
                threadReframe.start();

                System.out.println("Thread started");

                try {
                    threadReframe.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Thread finished");

                break;

            default:
                logger.warn("Unknown filter index for transformation compute method on button");
        }

        System.out.println("outputCoordinates = " + Arrays.toString(outputCoordinates));

        for (Path path : files2read) {
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

        return counter;
    }

    private IReframe.ProjectionChange getProjectionChange(Group group) {
        int selectedItem = ComboHelper.getSelectedItem((Combo) group.getChildren()[0]);

        ProjectionChange projectionChange = Objects.requireNonNull(ProjectionChange.fromIndex(selectedItem).orElse(null));

        return projectionChange.projectionChange;
    }

    private IReframe.AltimetricFrame getAltimetricFrame(Group group) {
        int selectedItemSourcePlaneCoordinates = ComboHelper.getSelectedItem((Combo) group.getChildren()[1]);

        AltimetricFrame altimetricFrame = Objects.requireNonNull(AltimetricFrame.fromIndex(selectedItemSourcePlaneCoordinates).orElse(null));

        return altimetricFrame.altimetricFrame;
    }

    private IReframe.PlanimetricFrame getPlanimetricFrame(Group group) {
        int selectedItemSourceEllipsoidalHeights = ComboHelper.getSelectedItem((Combo) group.getChildren()[0]);

        PlanimetricFrame planimetricFrame = Objects.requireNonNull(PlanimetricFrame.fromIndex(selectedItemSourceEllipsoidalHeights).orElse(null));

        return planimetricFrame.planimetricFrame;
    }

    private SelectionListener getSelectionListenerForToggleOptions() {
        return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                // prevent double fired events
                boolean isSelected = ((Button) e.getSource()).getSelection();
                if (isSelected) {
                    toggleComputeChooser(groupMethod.getChildren());
                }
            }
        };
    }

    private void handleFileInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            inputFieldsComposite.setSourceTextFieldText(files);
        }
    }

    private boolean processFileOperations() {
        int counter = fileOperations();

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGE, Messages.transformationMessage);

            if (counter == 1) {
                message = String.format(StringUtils.getSingularMessage(helper), files2read.length, counter);
            } else {
                message = String.format(StringUtils.getPluralMessage(helper), files2read.length, counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.msgBox_Success), message);

            // set the counter for status bar information
            Main.countFileOps = counter;

            return true;
        } else {
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.msgBox_Error),
                    ResourceBundleUtils.getLangString(ERROR, Errors.transformationFailed));

            return false;
        }
    }

    private void buildComputeGpsrefUI(int width) {
        groupProjection = buildProjectionChooser(width);
    }

    private void buildComputeReframeUI(int width) {
        groupReferenceFrameSource = buildReferenceFrameChooserSource(width);
        groupReferenceFrameTarget = buildReferenceFrameChooserTarget(width);
    }

    private void toggleComputeChooser(Control... childrenMethod) {
        final int width = Size.RyCON_WIDGET_WIDTH.getValue();
        int selectedBtn = RadioHelper.getSelectedBtn(childrenMethod);

        disposeBottomElements();

        ComputeMethod method = Objects.requireNonNull(ComputeMethod.fromIndex(selectedBtn).orElse(null));

        switch (method) {
            case GPSREF:
                buildComputeGpsrefUI(width);
                break;

            case REFRAME:
                buildComputeReframeUI(width);
                break;

            default:
                logger.warn("Unknown filter index for transformation method '{}' on button", method);
        }

        buildBottomElements(width);

        bottomButtonBar = new BottomButtonBar(this, innerShell, BottomButtonBar.OK_AND_EXIT_BUTTON);

        layoutAndPositShell();
    }

    private void buildBottomElements(int width) {
        buildProgressBar(width);
        buildCopyAndPasteField(width);
        buildAdvice(width);
    }

    /*
     * Dispose not needed elements except the first two ones
     */
    private void disposeBottomElements() {
        for (int i = innerShell.getChildren().length - 1; i > 1; i--) {
            innerShell.getChildren()[i].dispose();
        }
    }

    private void layoutAndPositShell() {
        innerShell.pack();
        innerShell.layout(true, true);
        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitorVertically(innerShell));
    }

    private enum ComputeMethod {
        GPSREF(ResourceBundleUtils.getLangString(BUTTON, Buttons.radioBtnComputeGpsref)),
        REFRAME(ResourceBundleUtils.getLangString(BUTTON, Buttons.radioBtnComputeReframe));

        private final String text;

        ComputeMethod(String text) {
            this.text = text;
        }

        /**
         * Returns the {@link ComputeMethod} by index parameter as static access for switch cases.
         *
         * @param index index to return
         * @return ComputeMethod by index
         */
        static Optional<ComputeMethod> fromIndex(int index) {
            assert index < values().length;

            for (ComputeMethod method : values()) {
                if (method.ordinal() == index) {
                    return Optional.of(method);
                }
            }

            return Optional.empty();
        }
    }

    private enum AltimetricFrame {
        ALTIMETRIC_LN02(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.altimetric_LN02), IReframe.AltimetricFrame.LN02),
        ALTIMETRIC_LHN95(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.altimetric_LHN95), IReframe.AltimetricFrame.LHN95),
        ALTIMETRIC_ELLIPSOID(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.altimetric_Ellipsoid), IReframe.AltimetricFrame.Ellipsoid);

        private final String text;
        private final IReframe.AltimetricFrame altimetricFrame;

        AltimetricFrame(String text, IReframe.AltimetricFrame altimetricFrame) {
            this.text = text;
            this.altimetricFrame = altimetricFrame;
        }

        /**
         * Returns the {@link AltimetricFrame} by index parameter as static access for switch cases.
         *
         * @param index index to return
         * @return AltimetricFrame by index
         */
        static Optional<AltimetricFrame> fromIndex(int index) {
            assert index < values().length;

            for (AltimetricFrame altimetricFrame : values()) {
                if (altimetricFrame.ordinal() == index) {
                    return Optional.of(altimetricFrame);
                }
            }

            return Optional.empty();
        }
    }

    private enum PlanimetricFrame {
        PLANIMETRIC_LV03_MILITARY(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.planimetric_LV03_Military), IReframe.PlanimetricFrame.LV03_Military),
        PLANIMETRIC_LV95(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.planimetric_LV95), IReframe.PlanimetricFrame.LV95),
        PLANIMETRIC_LV03_CIVIL(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.planimetric_LV03_Civil), IReframe.PlanimetricFrame.LV03_Civil);

        private final String text;
        private final IReframe.PlanimetricFrame planimetricFrame;

        PlanimetricFrame(String text, IReframe.PlanimetricFrame planimetricFrame) {
            this.text = text;
            this.planimetricFrame = planimetricFrame;
        }

        /**
         * Returns the {@link PlanimetricFrame} by index parameter as static access for switch cases.
         *
         * @param index index to return
         * @return PlanimetricFrame by index
         */
        static Optional<PlanimetricFrame> fromIndex(int index) {
            assert index < values().length;

            for (PlanimetricFrame planimetricFrame : values()) {
                if (planimetricFrame.ordinal() == index) {
                    return Optional.of(planimetricFrame);
                }
            }

            return Optional.empty();
        }
    }

    private enum ProjectionChange {
        PROJECTION_ETRF93_GEOCENTRIC_TO_LV95(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.projection_ETRF93GeocentricToLV95), IReframe.ProjectionChange.ETRF93GeocentricToLV95),
        PROJECTION_ETRF93_GEOGRAPHIC_TO_LV95(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.projection_ETRF93GeographicToLV95), IReframe.ProjectionChange.ETRF93GeographicToLV95),
        PROJECTION_LV95_TO_ETRF93_GEOCENTRIC(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.projection_LV95ToETRF93Geocentric), IReframe.ProjectionChange.LV95ToETRF93Geocentric),
        PROJECTION_LV95_TO_ETRF93_GEOGRAPHIC(ResourceBundleUtils.getLangStringFromXml(DISTINCTTYPE, DistinctTypes.projection_LV95ToETRF93Geographic), IReframe.ProjectionChange.ETRF93GeographicToLV95);

        private final String text;
        private final IReframe.ProjectionChange projectionChange;

        ProjectionChange(String text, IReframe.ProjectionChange projectionChange) {
            this.text = text;
            this.projectionChange = projectionChange;
        }

        /**
         * Returns the {@link ProjectionChange} by index parameter as static access for switch cases.
         *
         * @param index index to return
         * @return ProjectionChange by index
         */
        static Optional<ProjectionChange> fromIndex(int index) {
            assert index < values().length;

            for (ProjectionChange projectionChange : values()) {
                if (projectionChange.ordinal() == index) {
                    return Optional.of(projectionChange);
                }
            }

            return Optional.empty();
        }
    }

}
