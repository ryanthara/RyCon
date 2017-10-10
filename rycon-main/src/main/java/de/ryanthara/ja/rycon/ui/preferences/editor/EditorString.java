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
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * {@code EditorString} is an editor for single line string values.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class EditorString extends Editor<String> {

    private Text text;

    /**
     * Constructs a new instance of {@code EditorString} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param defaultValue default value for the editor
     */
    public EditorString(PreferencesDialog dialog, String defaultValue) {
        super(dialog, null, defaultValue);
    }

    /**
     * Constructs a new instance of {@code EditorString} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param validator    used validator for string values
     * @param defaultValue default value for the editor
     */
    public EditorString(PreferencesDialog dialog, Validator<String> validator, String defaultValue) {
        super(dialog, validator, defaultValue);
    }

    /**
     * Creates an according control and its behaviour.
     *
     * @param parent parent composite
     */
    @Override
    public void createControl(final Composite parent) {
        // GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.minimumWidth = Size.minimalTextWidth.getSize();

        text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        text.setText("");
        text.setLayoutData(gridData);

        final Color black = new Color(text.getDisplay(), 0, 0, 0);
        final Color red = new Color(text.getDisplay(), 255, 0, 0);

        text.addModifyListener(arg0 -> {
            if (isAccepted(text.getText())) {
                text.setForeground(black);
                setValid(true);
            } else {
                text.setForeground(red);
                setValid(false);
            }

            update();
        });

        text.addDisposeListener(arg0 -> {
            black.dispose();
            red.dispose();
        });

        super.createUndoButton(parent);
        super.createDefaultButton(parent);
        super.update();
    }

    /**
     * Returns the single line string from the text field of this editor.
     *
     * @return string from text field
     */
    @Override
    public String getValue() {
        return text.getText();
    }

    /**
     * Sets the single line {@code String} value for this editor.
     *
     * @param t single line string to be set
     */
    @Override
    public void setValue(Object t) {
        this.setInitialValue((String) t);
        this.text.setText((String) t);
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
    String parse(final String s) {
        return s;
    }

} // end of EditorString
