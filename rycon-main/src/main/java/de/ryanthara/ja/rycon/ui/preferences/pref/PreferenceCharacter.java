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
import de.ryanthara.ja.rycon.ui.preferences.editor.EditorString;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;
import de.ryanthara.ja.rycon.ui.preferences.validator.ValidatorCharacter;

/**
 * {@code PreferenceCharacter} is for char values used in RyCON.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class PreferenceCharacter extends Preference<String> {

    /**
     * Constructs a new instance of {@code PreferenceCharacter} according to the parameters.
     *
     * @param label text string of the preference
     */
    protected PreferenceCharacter(String label) {
        super(label);
    }

    /**
     * Constructs a new instance of {@code PreferenceCharacter} according to the parameters.
     *
     * @param label        text string of the preference
     * @param defaultValue default value for the preference
     */
    protected PreferenceCharacter(String label, char defaultValue) {
        super(label, String.valueOf(defaultValue));
    }

    /**
     * Returns the corresponding editor.
     *
     * @return corresponding editor
     */
    @Override
    public Editor<String> getEditor() {
        return new EditorString(getDialog(), getValidator(), getDefault());
    }

    /**
     * Returns the corresponding {@link ValidatorCharacter}.
     *
     * @return corresponding validator
     */
    @Override
    public Validator<String> getValidator() {
        return new ValidatorCharacter();
    }

} // end of PreferenceCharacter
