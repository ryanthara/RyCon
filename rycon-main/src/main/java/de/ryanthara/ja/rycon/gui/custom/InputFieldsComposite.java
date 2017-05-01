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

package de.ryanthara.ja.rycon.gui.custom;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.check.TextCheck;
import de.ryanthara.ja.rycon.i18n.Buttons;
import de.ryanthara.ja.rycon.i18n.Labels;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private Text destinationTextField;
    private Text sourceTextField;

    /**
     * Constructs a new instance of this class given a calling object, the parent composite and a style.
     *  @param callingObject reference to the calling object
     * @param parent        parent composite (e.g. the parent shell)
     */
    public InputFieldsComposite(Object callingObject, Composite parent) {
        super(parent, SWT.NONE);
        this.callingObject = callingObject;
        createContents();
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

    private void createContents() {
        Group group = new Group(this, SWT.NONE);
        group.setText(Labels.getString("pathSelectionText"));

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;
        gridLayout.numColumns = 3;
        group.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, true);
        gridData.widthHint = Main.getRyCONWidgetWidth();
        group.setLayoutData(gridData);

        // return the buttons is for tabulator key order
        Button btnSource = createSourceComposite(group);
        Button btnDestination = createDestinationComposite(group);

        Control[] tabulatorKeyOrder = new Control[]{
                sourceTextField, btnSource, destinationTextField, btnDestination
        };

        group.setTabList(tabulatorKeyOrder);
    }

    private Button createDestinationComposite(Group group) {
        GridData gridData;
        Label destination = new Label(group, SWT.NONE);
        destination.setText(Labels.getString("destinationText"));
        destination.setLayoutData(new GridData());

        destinationTextField = new Text(group, SWT.SINGLE | SWT.BORDER);
        destinationTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                InputFieldsComposite.this.handleEvent(event, destinationTextField);
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        destinationTextField.setLayoutData(gridData);

        Button btnDestination = new Button(group, SWT.NONE);
        btnDestination.setText(Buttons.getString("choosePathText"));
        btnDestination.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnDestination");
            }
        });

        btnDestination.setToolTipText(Buttons.getString("choosePathToolTip"));
        btnDestination.setLayoutData(new GridData());
        return btnDestination;
    }

    private Button createSourceComposite(Group group) {
        GridData gridData;
        final Label source = new Label(group, SWT.NONE);
        source.setText(Labels.getString("source"));

        sourceTextField = new Text(group, SWT.BORDER);
        sourceTextField.addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // prevent this shortcut for execute when the text fields are empty
                InputFieldsComposite.this.handleEvent(event, sourceTextField);
            }
        });

        /*
        Because of this listener there is a different behaviour of the source
        text field implemented. From injected file names the path is not removed.
         */
        sourceTextField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent modifyEvent) {
                if (TextCheck.isFileExists(sourceTextField)) {
                    Path path = Paths.get(sourceTextField.getText());
                    destinationTextField.setText(path.getParent().toString());
                }
            }
        });

        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        sourceTextField.setLayoutData(gridData);

        Button btnSource = new Button(group, SWT.NONE);
        btnSource.setText(Buttons.getString("chooseFilesText"));
        btnSource.setToolTipText(Buttons.getString("chooseFilesToolTip"));
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

    private void doButtonAction(String destination) {
        Class<?> clazz = callingObject.getClass();
        try {
            Method method = clazz.getDeclaredMethod(destination);
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
        if (!(this.sourceTextField.getText().trim().equals("") || (destinationTextField.getText().trim().equals("")))) {
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
