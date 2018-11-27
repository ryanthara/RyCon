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

import de.ryanthara.ja.rycon.i18n.Buttons;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.BUTTON;

/**
 * Instances of this class implements a custom composite with three buttons ('Cancel', 'OK' and 'OK and EXIT')
 * which is used on the bottom of RyCON's main widgets.
 *
 * @author sebastian
 * @version 3
 * @since 4
 */
public class BottomButtonBar extends Composite {

    /**
     * Member that indicates to use the button "OK and Exit".
     */
    public static final boolean OK_AND_EXIT_BUTTON = true;
    private static final Logger logger = LoggerFactory.getLogger(BottomButtonBar.class.getName());
    private final Object callingObject;
    private final boolean okAndExitButton;
    private Button btnCancel;
    private Button btnOK;
    private Button btnOKAndExit;

    /**
     * Constructs a new instance of this class given a calling object, a parent composite and a style.
     *
     * @param callingObject   reference to the calling object
     * @param parent          parent composite (e.g. the parent shell)
     * @param okAndExitButton usage of the button 'OK and Exit'
     */
    public BottomButtonBar(Object callingObject, Composite parent, boolean okAndExitButton) {
        super(parent, SWT.NONE);

        this.callingObject = callingObject;
        this.okAndExitButton = okAndExitButton;

        createContents();
    }

    /**
     * Give Layout classes or other widgets the option to determine the size of this custom widget.
     * In this case the Layout, of the parent Composite, is able to align its child widgets properly.
     *
     * @param wHint   width
     * @param hHint   height
     * @param changed changed
     * @return result of super() call
     */
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return super.computeSize(wHint, hHint, changed);
    }

    /*
     * Throws an SWTException if the receiver can not be accessed by the caller.
     */
    private void createContents() {
        checkWidget();

        this.setLayout(new FillLayout());

        btnCancel = new Button(this, SWT.NONE);
        btnCancel.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.cancelText));
        btnCancel.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.cancelToolTip));
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnCancel");
            }
        });

        btnOK = new Button(this, SWT.NONE);
        btnOK.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndOpenText));
        btnOK.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndOpenToolTip));
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnOk");
            }
        });

        if (okAndExitButton) {
            btnOKAndExit = new Button(this, SWT.NONE);
            btnOKAndExit.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndExitText));
            btnOKAndExit.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndExitToolTip));
            btnOKAndExit.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    doButtonAction("actionBtnOkAndExit");
                }
            });
        }

        GridData gridData = new GridData(SWT.END, SWT.END, false, true);
        this.setLayoutData(gridData);
    }

    private void doButtonAction(String target) {
        Class<?> clazz = callingObject.getClass();
        try {
            Method method = clazz.getDeclaredMethod(target);
            method.setAccessible(true);

            try {
                method.invoke(callingObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Illegal access or invocation target for calling an action button.", e.getCause());
            }
        } catch (NoSuchMethodException e) {
            logger.error("No such method for calling an action button.", e.getCause());
        }
    }

    /**
     * Disable the ok button.
     */
    public void disableOkButton() {
        btnOK.setEnabled(false);
    }

    /**
     * Disable the ok and exit button if it is present.
     */
    public void disableOkAndExitButton() {
        if (!btnOKAndExit.isDisposed()) {
            btnOKAndExit.setEnabled(false);
        }
    }

    /**
     * Enables the ok button.
     */
    public void enableOkButton() {
        btnOK.setEnabled(true);
    }

    /**
     * Enables the ok and exit button if it is present.
     */
    public void enableOkAndExitButton() {
        if (!btnOKAndExit.isDisposed()) {
            btnOKAndExit.setEnabled(true);
        }
    }
}
