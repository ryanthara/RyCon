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

/**
 * This is the abstract base class for the different <tt>PreferenceXXX</tt> classes of RyCON.
 * <p>
 * The generic parameter <tt>T</tt> holds the data type.
 * <p>
 * The main idea behind this way of preference handler was implemented by Fabian Prasser.
 * See {@link https://github.com/prasser/swtpreferences} for details.
 *
 * @param <T> data typ
 */
abstract class Preference<T> {

    /**
     * Label for the preference.
     */
    private String label;

    /**
     * Parent preference tab
     */
    private PreferenceTab tab;

    /**
     * Default parameter is set to 'null'.
     */
    private T defaultParameter = null;

    /**
     * Constructor.
     *
     * @param label the label
     */
    Preference(String label) {
        this.label = label;

        if (label == null) {
            throw new IllegalArgumentException("label must not be null");
        }
    }

    /**
     * Constructor.
     *
     * @param label            the label
     * @param defaultParameter the default parameter
     */
    Preference(String label, T defaultParameter) {
        this(label);
        this.defaultParameter = defaultParameter;
    }

    /**
     * Retrieves the value from the model.
     *
     * @return value from model
     */
    protected abstract T getValue();

    /**
     * Writes the value to the model.
     *
     * @param obj the model
     */
    protected abstract void setValue(Object obj);

    /**
     * Returns the default parameter.
     *
     * @return default parameter T
     */
    T getDefault() {
        return defaultParameter;
    }

    /**
     * Returns the editor.
     *
     * @return the editor
     */
    abstract Editor<T> getEditor();

    /**
     * Returns the label.
     *
     * @return the label
     */
    String getLabel() {
        return label;
    }

    /**
     * Returns the reference to the tab.
     *
     * @return tab reference
     */
    PreferenceTab getTab() {
        return tab;
    }

    /**
     * Provides the validator
     *
     * @return the validator
     */
    abstract Validator<T> getValidator();

    /**
     * Called when the preference is added to the current tab.
     *
     * @param tab reference to the current tab
     */
    void setPreferenceTab(PreferenceTab tab) {
        this.tab = tab;
    }


} // end of Preference
