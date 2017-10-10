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
import de.ryanthara.ja.rycon.ui.preferences.editor.EditorPath;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;
import de.ryanthara.ja.rycon.ui.preferences.validator.ValidatorPath;

import java.nio.file.Path;

/**
 * {@code PreferencePath} is for paths values used in RyCON.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class PreferencePath extends Preference<Path> {

    /**
     * Constructs a new instance of {@code PreferenceInteger} according to the parameter.
     *
     * @param label text string of the preference
     */
    protected PreferencePath(String label) {
        super(label);
    }

    /**
     * Constructs a new instance of {@code PreferenceInteger} according to the parameters.
     *
     * @param label       text string of the preference
     * @param defaultPath default value for the preference
     */
    protected PreferencePath(String label, Path defaultPath) {
        super(label, defaultPath);
    }

    /**
     * Returns the corresponding editor.
     *
     * @return the editor
     */
    @Override
    public Editor<Path> getEditor() {
        return new EditorPath(getDialog(), getDefault());
    }

    /**
     * Returns the corresponding {@link ValidatorPath}.
     *
     * @return corresponding validator
     */
    @Override
    public Validator<Path> getValidator() {
        return new ValidatorPath();
    }

} // end of PreferenceString
