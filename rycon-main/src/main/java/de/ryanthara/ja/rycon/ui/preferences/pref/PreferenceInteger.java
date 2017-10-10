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
import de.ryanthara.ja.rycon.ui.preferences.editor.EditorInteger;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;
import de.ryanthara.ja.rycon.ui.preferences.validator.ValidatorInteger;

/**
 * {@code PreferenceInteger} is for integer values used in RyCON.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class PreferenceInteger extends Preference<Integer> {

    /**
     * Validator
     */
    private Validator<Integer> validator = null;

    /**
     * Constructs a new instance of {@code PreferenceInteger} according to the parameter.
     *
     * @param label text string of the preference
     */
    protected PreferenceInteger(String label) {
        super(label);
    }

    /**
     * Constructs a new instance of {@code PreferenceInteger} according to the parameters.
     *
     * @param label text string of the preference
     * @param min   valid minimum value
     * @param max   valid maximum value
     */
    protected PreferenceInteger(String label, int min, int max) {
        super(label);
        this.validator = new ValidatorInteger(min, max);
    }

    /**
     * Constructs a new instance of {@code PreferenceInteger} according to the parameters.
     *
     * @param label        text string of the preference
     * @param min          valid minimum value
     * @param max          valid maximum value
     * @param defaultValue default value for the preference
     */
    protected PreferenceInteger(String label, int min, int max, int defaultValue) {
        super(label, defaultValue);
        this.validator = new ValidatorInteger(min, max);
    }

    /**
     * Returns the corresponding editor.
     *
     * @return the editor
     */
    @Override
    public Editor<Integer> getEditor() {
        return new EditorInteger(getDialog(), getValidator(), getDefault());
    }

    /**
     * Returns the corresponding {@link ValidatorInteger}.
     *
     * @return corresponding validator
     */
    @Override
    public Validator<Integer> getValidator() {
        return validator;
    }

} // end of ValidatorInteger
