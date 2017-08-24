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

import org.eclipse.swt.widgets.Composite;

/**
 * This is the abstract base class for the different <tt>EditorXXX</tt> classes of RyCON.
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
abstract class Editor<T> {

    /**
     * The validity of the current value.
     */
    private boolean valid = true;

    /**
     * The generic parameter initial value.
     */
    private T initialValue = null;

    /**
     * The validator.
     */
    private Validator<T> validator = null;

    /**
     * The preference tab.
     */
    private PreferenceTab preferenceTab = null;

    /**
     * The default parameter. (Model)
     */
    private T defaultParameter = null;

    /**
     * Creates a new instance.
     *
     * @param tab              the parent {@link PreferenceTab}
     * @param validator        the validator
     * @param defaultParameter the defaultParameter
     */
    public Editor(PreferenceTab tab, Validator<T> validator, T defaultParameter) {
        this.preferenceTab = preferenceTab;
        this.validator = validator;
        this.defaultParameter = defaultParameter;
    }

    /**
     * Parses the value to generic parameter T.
     *
     * @param s string to be parsed
     *
     * @return parsed string
     */
    protected abstract T parse(String s);

    /**
     * Checks if the value is accepted.
     *
     * @param s string to be checked
     *
     * @return true if check is passed successful
     */
    boolean accept(String s) {
        try {
            T t = parse(s);
            if (s == null) {
                return false;
            } else if (validator != null && !validator.isValid(t)) {
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates an according control.
     *
     * @param parent parent composite
     */
    abstract void createControl(Composite parent);

    /**
     * Formats the value.
     *
     * @param t generic parameter
     *
     * @return formatted string
     */
    abstract String format(T t);

    /**
     * Gets the initial value of the generic parameter.
     *
     * @return initial value
     */
    T getInitialValue() {
        return initialValue;
    }

    /**
     * Sets the initial value.
     *
     * @param t generic parameter
     */
    void setInitialValue(T t) {
        if (this.initialValue == null) {
            this.initialValue = t;
        }
    }

    /**
     * Returns the preference tab.
     *
     * @return the preference tab
     */
    PreferenceTab getPreferenceTab() {
        return preferenceTab;
    }

    /**
     * Returns the current parameter value.
     *
     * @return current parameter value
     */
    abstract T getValue();

    /**
     * Sets the current parameter value.
     *
     * @param t parameter value to be set
     */
    abstract void setValue(Object t);

    /**
     * Returns true if the current value is valid.
     *
     * @return true if is valid
     */
    boolean isValid() {
        return this.valid;
    }

    /**
     * Sets the validity parameter.
     *
     * @param valid validity
     */
    void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Updates the preference tab composite.
     */
    void update() {
        preferenceTab.update();

        // TODO implement button handling
        /*
        buttonUndo.setEnabled(isDirty() && getInitialValue() != null);
        try {
            buttonDefault.setEnabled(_default != null && !getValue().equals(_default));
        } catch (Exception e) {
            buttonDefault.setEnabled(false);
        }
        */
    }

} // end of Editor
