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

package de.ryanthara.ja.rycon.ui.preferences.editor;

import de.ryanthara.ja.rycon.ui.preferences.PreferencesDialog;
import de.ryanthara.ja.rycon.i18n.Preferences;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.PREFERENCES;

/**
 * {@code EditorBoolean} is an editor for boolean values.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class EditorBoolean extends Editor<Boolean> {

    private Button checkbox;

    /**
     * Constructs a new instance of {@code EditorBoolean} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param defaultValue default value for the editor
     */
    public EditorBoolean(PreferencesDialog dialog, Boolean defaultValue) {
        super(dialog, null, defaultValue);
    }

    /**
     * Creates an according control and its behaviour.
     *
     * @param parent parent composite
     */
    @Override
    public void createControl(final Composite parent) {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);

        checkbox = new Button(parent, SWT.CHECK);
        checkbox.setText(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.labelNo));
        checkbox.setSelection(false);
        checkbox.setLayoutData(gridData);
        checkbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                setValid(true);
                update();
                if (checkbox.getSelection()) {
                    checkbox.setText(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.labelYes));
                } else {
                    checkbox.setText(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.labelNo));
                }
            }
        });

        super.createUndoButton(parent);
        super.createDefaultButton(parent);
        super.update();
    }

    /**
     * Returns the selected checkbox value of this editor.
     *
     * @return checkbox selection
     */
    @Override
    public Boolean getValue() {
        return checkbox.getSelection();
    }


    /**
     * Sets the {@code Boolean} value for this editor.
     *
     * @param t Boolean value to be set
     */
    @Override
    public void setValue(Object t) {
        setInitialValue((Boolean) t);
        checkbox.setSelection((Boolean) t);
        checkbox.setText((Boolean) t ?
                        ResourceBundleUtils.getLangString(PREFERENCES, Preferences.labelYes) :
                        ResourceBundleUtils.getLangString(PREFERENCES, Preferences.labelNo));
        super.update();
    }

    /**
     * Formats the value for for this editor.
     *
     * @param b value to be formatted
     *
     * @return formatted value
     */
    @Override
    String format(Boolean b) {
        return b.toString();
    }

    /**
     * Parses the string to {@code Boolean}.
     *
     * @param s string to be parsed
     *
     * @return parsed string
     */
    @Override
    Boolean parse(final String s) {
        return s.equals(Boolean.TRUE.toString());
    }

} // end of EditorBoolean
