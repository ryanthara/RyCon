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
 * {@code EditorDouble} is an editor for double values.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class EditorDouble extends Editor<Double> {

    private Text text;

    /**
     * Constructs a new instance of {@code EditorDouble} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param defaultValue default value for the editor
     */
    public EditorDouble(PreferencesDialog dialog, Double defaultValue) {
        super(dialog, null, defaultValue);
    }

    /**
     * Constructs a new instance of {@code EditorDouble} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param validator    validator for double values
     * @param defaultValue default value for the editor
     */
    public EditorDouble(PreferencesDialog dialog, Validator<Double> validator, Double defaultValue) {
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
     * Returns the {@code Double} value from the text field of this editor.
     *
     * @return Double value
     */
    @Override
    public Double getValue() {
        return parse(text.getText());
    }

    /**
     * Sets the {@code Double} value for this editor.
     *
     * @param t Double value to be set
     */
    @Override
    public void setValue(Object t) {
        this.setInitialValue((Double) t);
        this.text.setText(t.toString());
        super.update();
    }

    /**
     * Formats the value for for this editor.
     *
     * @param d value to be formatted
     *
     * @return formatted value
     */
    @Override
    String format(Double d) {
        return d.toString();
    }

    /**
     * Parses the string to {@code Double}.
     *
     * @param s string to be parsed
     *
     * @return parsed string
     */
    @Override
    Double parse(final String s) {
        return Double.valueOf(s);
    }

} // end of EditorDouble
