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

import de.ryanthara.ja.rycon.ui.preferences.editor.Editor;
import de.ryanthara.ja.rycon.ui.preferences.editor.EditorSelection;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;

import java.util.Objects;

/**
 * {@code PreferenceSelection} is for selection values like combo used in RyCON.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class PreferenceSelection extends Preference<String> {

    private final String[] elements;

    /**
     * Constructs a new instance of {@code PreferenceSelection} according to the parameters.
     *
     * @param label    text string of the preference
     * @param elements elements for the combo of the preference
     * @throws NullPointerException     will be thrown if elements is null
     * @throws IllegalArgumentException will be thrown if elements is empty
     */
    protected PreferenceSelection(String label, String... elements) {
        super(label);
        this.elements = elements;

        Objects.requireNonNull(elements, "Element must not be empty");

        if (elements.length == 0) {
            throw new IllegalArgumentException("Element must not be empty");
        }
    }

    /**
     * Constructs a new instance of {@code PreferenceSelection} according to the parameters.
     *
     * @param label        text string of the preference
     * @param elements     elements for the combo of the preference
     * @param defaultValue default value for the preference
     * @throws NullPointerException     will be thrown if elements is null
     * @throws IllegalArgumentException will be thrown if elements is empty
     */
    protected PreferenceSelection(String label, String[] elements, String defaultValue) {
        super(label, defaultValue);
        this.elements = elements;

        Objects.requireNonNull(elements, "Element must not be empty");

        if (elements.length == 0) {
            throw new IllegalArgumentException("Element must not be empty");
        }
    }

    /**
     * Returns the corresponding editor.
     *
     * @return the editor
     */
    @Override
    public Editor<String> getEditor() {
        return new EditorSelection(getDialog(), elements, getDefault());
    }

    /**
     * Returns null because of there is no corresponding {@link Validator}.
     *
     * @return null
     */
    @Override
    public Validator<String> getValidator() {
        return null;
    }

}
