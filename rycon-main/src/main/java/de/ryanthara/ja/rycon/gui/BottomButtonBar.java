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

package de.ryanthara.ja.rycon.gui;

import de.ryanthara.ja.rycon.i18n.I18N;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BottomButtonBar implements a custom composite with three buttons ('Cancel', 'OK' and 'OK and EXIT')
 * which is used on the bottom of RyCON's widgets.
 *
 * @author sebastian
 * @version 1
 * @since 4
 */
class BottomButtonBar extends Composite {

    private final Object callingObject;

    /**
     * Class constructor with parameters.
     *
     * @param callingObject reference to the calling object
     * @param parent        parent composite (e.g. the parent shell)
     * @param style         style of the composite
     */
    BottomButtonBar(Object callingObject, Composite parent, int style) {
        super(parent, style);
        this.callingObject = callingObject;
        createContents();
    }

    private void createContents() {
        this.setLayout(new FillLayout());

        Button btnCancel = new Button(this, SWT.NONE);
        btnCancel.setText(I18N.getBtnCancelLabel());
        btnCancel.setToolTipText(I18N.getBtnCancelLabelToolTip());
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnCancel");
            }
        });

        Button btnOK = new Button(this, SWT.NONE);
        btnOK.setText(I18N.getBtnOKAndOpenLabel());
        btnOK.setToolTipText(I18N.getBtnOKAndOpenLabelToolTip());
        btnOK.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnOk");
            }
        });

        Button btnOKAndExit = new Button(this, SWT.NONE);
        btnOKAndExit.setText(I18N.getBtnOKAndExitLabel());
        btnOKAndExit.setToolTipText(I18N.getBtnOKAndExitLabelToolTip());
        btnOKAndExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doButtonAction("actionBtnOkAndExit");
            }
        });

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
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

} // end of BottomButtonBar
