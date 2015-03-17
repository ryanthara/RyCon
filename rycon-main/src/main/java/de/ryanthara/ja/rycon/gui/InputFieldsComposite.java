/*
 * License: GPL. Copyright 2015- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class implements a custom composite with two labels, text fields and buttons for
 * file and directory input used in RyCON's widgets.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 1
 * @since 4
 */
public class InputFieldsComposite extends Composite {

    private final Object callingObject;
    private Text destinationTextField;
    private Text sourceTextField;

    /**
     * Class constructor with parameters.
     *
     * @param callingObject reference to the calling object
     * @param parent parent composite (e.g. the parent shell)
     * @param style style of the composite
     */
    public InputFieldsComposite(Object callingObject, Composite parent, int style) {
        super(parent, style);
        this.callingObject = callingObject;
        createContents();
    }

    private void createContents() {
        Group group = new Group(this, SWT.NONE);
        group.setText(I18N.getGroupTitlePathSelection());

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Main.getRyCONWidgetWidth();
        group.setLayoutData(gridData);

        Label source = new Label(group, SWT.NONE);
        source.setText(I18N.getLabelSource());

        sourceTextField = new Text(group, SWT.BORDER);
        sourceTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!(sourceTextField.getText().trim().equals("") || (destinationTextField.getText().trim().equals("")))) {

                    if (((event.stateMask & SWT.SHIFT) == SWT.SHIFT) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        doButtonAction("actionBtnOk");
                    } else if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        doButtonAction("actionBtnOkAndExit");
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    doButtonAction("actionBtnSource");
                    destinationTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        sourceTextField.setLayoutData(gridData);

        Button btnSource = new Button(group, SWT.NONE);
        btnSource.setText(I18N.getBtnChooseFiles());
        btnSource.setToolTipText(I18N.getBtnChooseFilesToolTip());
        btnSource.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnSource");
            }
        });
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        btnSource.setLayoutData(gridData);

        Label destination = new Label(group, SWT.NONE);
        destination.setText(I18N.getLabelDestination());
        destination.setLayoutData(new GridData());

        destinationTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        destinationTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                if (!(sourceTextField.getText().trim().equals("") || (destinationTextField.getText().trim().equals("")))) {

                    if (((event.stateMask & SWT.SHIFT) == SWT.SHIFT) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        doButtonAction("actionBtnOk");
                    } else if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.detail == SWT.TRAVERSE_RETURN)) {
                        doButtonAction("actionBtnOkAndExit");
                    }

                } else if (event.detail == SWT.TRAVERSE_RETURN) {
                    doButtonAction("actionBtnDestination");
                    sourceTextField.setFocus();
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        destinationTextField.setLayoutData(gridData);

        Button btnDestination = new Button(group, SWT.NONE);
        btnDestination.setText(I18N.getBtnChoosePath());
        btnDestination.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnDestination");
            }
        });

        btnDestination.setToolTipText(I18N.getBtnChoosePathToolTip());
        btnDestination.setLayoutData(new GridData());
    }

    private void doButtonAction(String target) {
        Class<?> clazz = callingObject.getClass();
        try {
            Method method = clazz.getDeclaredMethod(target);
            method.setAccessible(true);

            try {
                method.invoke(callingObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the complete destination text field as an object for full access to it.
     *
     * @return destination text field
     */
    public Text getDestinationTextField() {
        return destinationTextField;
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
     * Sets the text of the destination text field.
     *
     * @param text the text to be set
     */
    public void setDestinationTextFieldText(String text) {
        destinationTextField.setText(text);
    }

    /**
     * Sets the text of the source text field.
     *
     * @param text the text to be set
     */
    public void setSourceTextFieldText(String text) {
        sourceTextField.setText(text);
    }

}
