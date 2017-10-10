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

package de.ryanthara.ja.rycon.ui.preferences.pref;

import de.ryanthara.ja.rycon.ui.preferences.PreferencesDialog;
import de.ryanthara.ja.rycon.ui.preferences.editor.Editor;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;

/**
 * {@code Preference<T>} is an abstract base class for different {@code PreferenceT<generic data typ></>} classes of RyCON.
 * <p>
 * The subclassed preferences exists for different generic data types (Model) like strings, booleans, integer values
 * and represents them with an corresponding control (Control) or needed ui components (View) like text fields
 * or combo boxes.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @param <T> The generic data type of the subclassed editor
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class Preference<T> {

    private String label;
    private PreferencesDialog dialog;
    private T defaultValue = null;

    /**
     * Constructs a new instance of {@code PreferenceT} according to the parameters.
     *
     * @param label        text string of the preference
     * @param defaultValue default value for the preference
     */
    Preference(String label, T defaultValue) {
        this(label);
        this.defaultValue = defaultValue;
    }

    /**
     * Constructs a new instance of {@code PreferenceT} according to the parameters.
     *
     * @param label text string of the preference
     */
    public Preference(String label) {
        this.label = label;

        if (label == null) {
            throw new IllegalArgumentException("Label must not be null");
        }
    }

    /**
     * Returns the default value, if existing.
     *
     * @return default value
     */
    public T getDefault() {
        return defaultValue;
    }

    /**
     * Returns the editor for the generic data typ.
     *
     * @return editor for generic data typ
     */
    public abstract Editor<T> getEditor();

    /**
     * Returns the label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Provides the validator, if existing.
     *
     * @return current validator
     */
    public abstract Validator<T> getValidator();

    /**
     * Retrieves value from the model.
     *
     * @return value for generic data typ
     */
    public abstract T getValue();

    /**
     * Writes value to the model.
     *
     * @param t value to be set
     */
    public abstract void setValue(Object t);

    /**
     * Returns the dialog.
     *
     * @return current dialog
     */
    PreferencesDialog getDialog() {
        return dialog;
    }

    /**
     * Called when the preference is added to the dialog.
     *
     * @param dialog dialog to add to
     */
    public void setDialog(PreferencesDialog dialog) {
        this.dialog = dialog;
    }

} // end of Preference
