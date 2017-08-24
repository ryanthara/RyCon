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
 * The <tt>PreferenceBoolean</tt> is a preference that uses boolean as data type.
 * <p>
 * The visual representation ist done with a check box.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class PreferenceBoolean extends Preference<Boolean> {

    /**
     * Constructor.
     *
     * @param label the label
     */
    PreferenceBoolean(String label) {
        super(label);
    }

    /**
     * Constructor.
     *
     * @param label          the label
     * @param defaultBoolean the default value for the boolean
     */
    PreferenceBoolean(String label, boolean defaultBoolean) {
        super(label, defaultBoolean);
    }

    /**
     * Returns the editor for the boolean preference.
     *
     * @return
     */
    @Override
    protected Editor<Boolean> getEditor() {
        System.out.println(getTab());
        return new EditorBoolean(getTab(), getDefault());
    }

    @Override
    protected Validator<Boolean> getValidator() {
        return null;
    }


} // end of PreferenceBoolean
