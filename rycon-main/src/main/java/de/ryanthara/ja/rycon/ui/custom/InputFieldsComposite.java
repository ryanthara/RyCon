/*
 * License: GPL. Copyright 2015- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

package de.ryanthara.ja.rycon.ui.custom;

import de.ryanthara.ja.rycon.util.check.TextCheck;
import de.ryanthara.ja.rycon.i18n.Buttons;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.ui.Sizes;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.BUTTONS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;

/**
 * Instances of this class implements a custom composite with two labels, two text fields and two buttons for
 * file and directory inputs used in RyCON's widgets.
 *
 * @author sebastian
 * @version 4
 * @since 4
 */
public class InputFieldsComposite extends Composite {

    private final Object callingObject;
    private Text targetTextField;
    private Text sourceTextField;

    /**
     * Constructs a new instance of this class given a calling object, the parent composite and a style.
     *
     * @param callingObject reference to the calling object
     * @param parent        parent composite (e.g. the parent shell)
     */
    public InputFieldsComposite(Object callingObject, Composite parent) {
        super(parent, SWT.NONE);
        this.callingObject = callingObject;
        createContents();
    }

    /**
     * Give Layout classes or other widgets the option to determine the size of this custom widget.
     * In this case the Layout, of the parent Composite, is able to align its child widgets properly.
     *
     * @param wHint   width
     * @param hHint   height
     * @param changed changed
     *
     * @return result of super() call
     */
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return super.computeSize(wHint, hHint, changed);
    }

    /**
     * Returns the complete source text field as an object for full access to it.
     *
     * @return source text field
     */
    public Text getSourceTextField() {
        return sourceTextField;
    }

    /**
     * Returns the complete target text field as an object for full access to it.
     *
     * @return target text field
     */
    public Text getTargetTextField() {
        return targetTextField;
    }

    /**
     * Sets the text of the source text field.
     *
     * @param text the text to be set
     */
    public void setSourceTextFieldText(String text) {
        sourceTextField.setText(text);
    }

    /**
     * Sets the text of the target text field.
     *
     * @param text the text to be set
     */
    public void setTargetTextFieldText(String text) {
        targetTextField.setText(text);
    }

    private void createContents() {
        /* Throws an SWTException if the receiver can not be accessed by the caller. */
        checkWidget();

        Group group = new Group(this, SWT.NONE);
        group.setText(ResourceBundleUtils.getLangString(LABELS, Labels.pathSelectionText));

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Sizes.RyCON_WIDGET_WIDTH.getValue() + 24;
        group.setLayoutData(gridData);

        // return the buttons is for tabulator key order
        Button btnSource = createSourceComposite(group);
        Button btnTarget = createTargetComposite(group);

        Control[] tabulatorKeyOrder = new Control[]{
                sourceTextField, btnSource, targetTextField, btnTarget
        };

        group.setTabList(tabulatorKeyOrder);
    }

    private Button createSourceComposite(Group group) {
        final Label source = new Label(group, SWT.NONE);
        source.setText(ResourceBundleUtils.getLangString(LABELS, Labels.source));

        sourceTextField = new Text(group, SWT.BORDER);
        sourceTextField.addListener(SWT.Traverse, event -> {
            // prevent shortcuts for execute when the text fields are empty
            InputFieldsComposite.this.handleEvent(event, sourceTextField);
        });

        /*
         * The source text field has to use a different behaviour for dropped and
         * dialog selected files. The path from injected files is not removed.
         */
        sourceTextField.addModifyListener(modifyEvent -> {
            if (TextCheck.isFileExists(sourceTextField)) {
                Path path = Paths.get(sourceTextField.getText());
                targetTextField.setText(path.getParent().toString());
            }
        });

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        sourceTextField.setLayoutData(gridData);

        Button btnSource = new Button(group, SWT.NONE);
        btnSource.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.chooseFilesText));
        btnSource.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.chooseFilesToolTip));
        btnSource.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnSource");
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnSource.setLayoutData(gridData);

        return btnSource;
    }

    private Button createTargetComposite(Group group) {
        Label target = new Label(group, SWT.NONE);
        target.setText(ResourceBundleUtils.getLangString(LABELS, Labels.targetText));
        target.setLayoutData(new GridData());

        targetTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        targetTextField.addListener(SWT.Traverse, event -> {
            // prevent shortcuts for execute when the text fields are empty
            InputFieldsComposite.this.handleEvent(event, targetTextField);
        });

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        targetTextField.setLayoutData(gridData);

        Button btnTarget = new Button(group, SWT.NONE);
        btnTarget.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathText));
        btnTarget.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnTarget");
            }
        });

        btnTarget.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.choosePathToolTip));
        btnTarget.setLayoutData(new GridData());

        return btnTarget;
    }

    private void doButtonAction(String target) {
        Class<?> clazz = callingObject.getClass();
        try {
            Method method = clazz.getDeclaredMethod(target);
            method.setAccessible(true);

            try {
                method.invoke(callingObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void handleEvent(Event event, Text text) {
        if (!(this.sourceTextField.getText().trim().equals("") || (targetTextField.getText().trim().equals("")))) {
            if (((event.stateMask & SWT.SHIFT) == SWT.SHIFT) && (event.detail == SWT.TRAVERSE_RETURN)) {
                doButtonAction("actionBtnOk");
            } else if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                doButtonAction("actionBtnOkAndExit");
            }
        } else if (event.detail == SWT.TRAVERSE_RETURN) {
            doButtonAction("actionBtnSource");
            text.setFocus();
        }
    }

} // end of InputFieldsComposite
