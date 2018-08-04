/* * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/) * * This file is part of the package de.ryanthara.ja.rycon.gui.preferences * * This package is free software: you can redistribute it and/or modify it under * the terms of the GNU General Public License as published by the Free Software * Foundation, either version 3 of the License, or (at your option) any later * version. * * This package is distributed in the hope that it will be useful, but WITHOUT * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS * FOR A PARTICULAR PURPOSE.See the GNU General Public License for more details. * * You should have received a copy of the GNU General Public License along with * this package. If not, see <http://www.gnu.org/licenses/>. */

package de.ryanthara.ja.rycon.ui.preferences.editor;

import de.ryanthara.ja.rycon.ui.preferences.PreferencesDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * {@code EditorSelection} is an editor for set valued variables which are shown in a combo.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class EditorSelection extends Editor<String> {

    private final String[] elements;
    private Combo combo;

    /**
     * Constructs a new instance of {@code EditorSelection} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param elements     elements of the combo box
     * @param defaultValue default value for the editor
     */
    public EditorSelection(PreferencesDialog dialog, String[] elements, String defaultValue) {
        super(dialog, null, defaultValue);
        this.elements = elements.clone();
    }

    /**
     * Creates an according control and its behaviour.
     *
     * @param parent parent composite
     */
    @Override
    public void createControl(final Composite parent) {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);

        combo = new Combo(parent, SWT.READ_ONLY);
        combo.setItems(elements);
        combo.setLayoutData(gridData);
        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent arg0) {
                if (combo.getSelectionIndex() >= 0) {
                    setValid(true);
                } else {
                    setValid(false);
                }

                update();
            }
        });

        super.createUndoButton(parent);
        super.createDefaultButton(parent);
        super.update();
    }

    /**
     * Returns the selected element from the combo box of this editor.
     *
     * @return selected element
     */
    @Override
    public String getValue() {
        return elements[combo.getSelectionIndex()];
    }

    /**
     * Sets the selection as {@code String} value for this editor.
     *
     * @param t selection string to be set
     */
    @Override
    public void setValue(Object t) {
        this.setInitialValue((String) t);
        combo.select(indexOf((String) t));
        super.update();
    }

    /**
     * Formats the value for for this editor.
     *
     * @param s value to be formatted
     *
     * @return formatted value
     */
    @Override
    String format(String s) {
        return s;
    }

    /**
     * Returns the not parsed string.
     *
     * @param s string to be parsed
     *
     * @return not parsed string
     */
    @Override
    String parse(String s) {
        return s;
    }

    /**
     * Returns the index of the current selected combo.
     *
     * @param value string to be checked
     *
     * @return index of selected combo
     */
    private int indexOf(final String value) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].equals(value)) {
                return i;
            }
        }

        return -1;
    }

} // end of EditorSelection
