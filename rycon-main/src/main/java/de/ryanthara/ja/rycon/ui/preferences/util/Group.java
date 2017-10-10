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

package de.ryanthara.ja.rycon.ui.preferences.util;

import de.ryanthara.ja.rycon.ui.preferences.editor.Editor;
import de.ryanthara.ja.rycon.ui.preferences.pref.Preference;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;

/**
 * {@code Group} is used as a visual separator in the different tabs of the
 * {@link de.ryanthara.ja.rycon.ui.preferences.PreferencesDialog} of RyCON.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class Group extends Preference<String> {

    /**
     * Constructs a new instance of {@code Group} according to the parameter.
     *
     * @param text group text
     */
    public Group(String text) {
        super(text);
    }

    /**
     * Returns the current editor.
     *
     * @return null
     */
    @Override
    public Editor<String> getEditor() {
        return null;
    }

    /**
     * Returns the current validator.
     *
     * @return null
     */
    @Override
    public Validator<String> getValidator() {
        return null;
    }

    /**
     * Returns the current value.
     *
     * @return null
     */
    @Override
    public String getValue() {
        return null;
    }

    /**
     * Sets the current value for this.
     * <p>
     * This is empty by design.
     *
     * @param t value to be set
     */
    @Override
    public void setValue(Object t) {
        // Empty by design
    }

} // end of Group
