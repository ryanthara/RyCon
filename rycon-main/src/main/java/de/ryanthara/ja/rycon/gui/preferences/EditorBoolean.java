/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.preferences
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
package de.ryanthara.ja.rycon.gui.preferences;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * This is the editor for boolean values.
 * <p>
 * The generic parameter <tt>T</tt> holds the data type.
 * <p>
 * The main idea behind this way of preference handler was implemented by Fabian Prasser.
 * See {@link https://github.com/prasser/swtpreferences} for details.
 *
 * @param <T> data typ
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class EditorBoolean extends Editor<Boolean> {

    private Button checkBox;

    /**
     * Constructor.
     *
     * @param tab              reference to the preference tab
     * @param defaultParameter default parameter
     */
    public EditorBoolean(PreferenceTab tab, boolean defaultParameter) {
        super(tab, null, defaultParameter);
    }

    /**
     * Parses the value to generic parameter T.
     *
     * @param s string to be parsed
     *
     * @return parsed string
     */
    @Override
    protected Boolean parse(String s) {
        if (s.equals(Boolean.TRUE.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates an according control.
     *
     * @param parent parent composite
     */
    @Override
    void createControl(final Composite parent) {
        checkBox = new Button(parent, SWT.CHECK);
        checkBox.setSelection(false);


        System.out.println(super.getPreferenceTab());
        System.out.println("Toll");

        System.out.println("createControl: " + super.getPreferenceTab().getConfiguration().getStringNo());

        checkBox.setText(this.getPreferenceTab().getConfiguration().getStringNo());
        //checkBox.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).indent(0, 0).align(SWT.FILL, SWT.FILL).create());
        checkBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                setValid(true);
                update();
                if (checkBox.getSelection()) {
                    checkBox.setText(getPreferenceTab().getConfiguration().getStringYes());
                } else {
                    checkBox.setText(getPreferenceTab().getConfiguration().getStringNo());
                }
            }
        });

        //super.createUndoButton(parent);
        //super.createDefaultButton(parent);
        super.update();
    }

    /**
     * Formats the value.
     *
     * @param b generic parameter
     *
     * @return formatted string
     */
    @Override
    String format(Boolean b) {
        return b.toString();
    }

    /**
     * Returns the current selection of the check box.
     *
     * @return check box selection
     */
    @Override
    Boolean getValue() {
        return checkBox.getSelection();
    }

    /**
     * Sets the initial value of the check box.
     *
     * @param t value to be set
     */
    @Override
    void setValue(Object t) {
        setInitialValue((Boolean) t);
        checkBox.setSelection((Boolean) t);
        checkBox.setText((Boolean) t ? getPreferenceTab().getConfiguration().getStringYes() : getPreferenceTab().getConfiguration().getStringNo());
        super.update();
    }

} // end of EditorBoolean
