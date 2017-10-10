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
import de.ryanthara.ja.rycon.ui.preferences.editor.EditorText;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;
import de.ryanthara.ja.rycon.ui.preferences.validator.ValidatorString;

/**
 * {@code PreferenceText} is for multi line string values used in RyCON.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class PreferenceText extends Preference<String> {

    /**
     * Constructs a new instance of {@code PreferenceString} according to the parameter.
     *
     * @param label text string of the preference
     */
    public PreferenceText(String label) {
        super(label);
    }

    /**
     * Constructs a new instance of {@code PreferenceString} according to the parameters.
     *
     * @param label        text string of the preference
     * @param defaultValue default value for the preference
     */
    public PreferenceText(String label, String defaultValue) {
        super(label, defaultValue);
    }

    /**
     * Returns the corresponding editor.
     *
     * @return the editor
     */
    @Override
    public Editor<String> getEditor() {
        return new EditorText(getDialog(), getDefault());
    }

    /**
     * Returns the corresponding {@link ValidatorString}.
     *
     * @return corresponding validator
     */
    @Override
    public Validator<String> getValidator() {
        return new ValidatorString();
    }

} // end of PreferenceText
