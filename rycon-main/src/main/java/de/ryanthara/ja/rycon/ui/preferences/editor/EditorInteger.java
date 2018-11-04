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
 * {@code EditorInteger} is an editor for integer values.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class EditorInteger extends Editor<Integer> {

    private Text text;

    /**
     * Constructs a new instance of {@code EditorInteger} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param defaultValue default value for the editor
     */
    public EditorInteger(PreferencesDialog dialog, Integer defaultValue) {
        super(dialog, null, defaultValue);
    }

    /**
     * Constructs a new instance of {@code EditorInteger} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param validator    used validator for integer values
     * @param defaultValue default value for the editor
     */
    public EditorInteger(PreferencesDialog dialog, Validator<Integer> validator, Integer defaultValue) {
        super(dialog, validator, defaultValue);
    }

    /**
     * Creates an according control and its behaviour.
     *
     * @param parent parent composite
     */
    @Override
    public void createControl(Composite parent) {
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
     * Returns the {@code Integer} value from the text field of this editor.
     *
     * @return Integer value
     */
    @Override
    public Integer getValue() {
        return parse(text.getText());
    }

    /**
     * Sets the {@code Integer} value for this editor.
     *
     * @param t Integer value to be set
     */
    @Override
    public void setValue(Object t) {
        this.setInitialValue((Integer) t);
        this.text.setText(t.toString());
        super.update();
    }

    /**
     * Formats the value for for this editor.
     *
     * @param i value to be formatted
     * @return formatted value
     */
    @Override
    String format(Integer i) {
        return i.toString();
    }

    /**
     * Parses the string to {@code Integer}.
     *
     * @param s string to be parsed
     * @return parsed string
     */
    @Override
    Integer parse(String s) {
        return Integer.valueOf(s);
    }

}
