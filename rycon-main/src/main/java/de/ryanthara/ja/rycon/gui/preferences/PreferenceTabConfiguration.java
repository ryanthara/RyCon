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
 * The <tt>PreferenceTabConfiguration</tt> is part of the {@link PreferencesDialog} to hold the preferences of <tt>RyCON</tt>.
 * <p>
 * This class holds configuration values for the preference tab.
 * <p>
 * The idea to this are inspired by preference dialogs of different applications, like Eclipse, IntelliJ IDEA,
 * AutoCAD and some github stuff like swtpreferences from prasser.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class PreferenceTabConfiguration {

    /** Layout*/
    private int    minimalTextWidth  = 60;
    /** Layout*/
    private int    minimalTextHeight = 60;
    /** String*/
    private String yes               = "Yes";
    /** String*/
    private String no                = "No";
    /** String*/
    private String ok                = "OK";
    /** String*/
    private String undo              = "Undo changes";
    /** String*/
    private String _default          = "Set to default";

    /**
     * Returns the minimal height of a multi-line text field
     */
    public int getMinimalTextHeight() {
        return minimalTextHeight;
    }

    /**
     * Returns the minimal width of a text field
     */
    public int getMinimalTextWidth() {
        return minimalTextWidth;
    }

    /**
     * Returns the string for "no"
     * @return
     */
    public String getStringNo() {
        return this.no;
    }

    /**
     * Returns the string for "ok"
     * @return
     */
    public String getStringOK() {
        return this.ok;
    }

    /**
     * Returns the string for "yes"
     * @return
     */
    public String getStringYes() {
        return this.yes;
    }

    /**
     * Returns the string for "default"
     * @return
     */
    public String getStringDefault() {
        return this._default;
    }

    /**
     * Returns the string for "undo"
     * @return
     */
    public String getStringUndo() {
        return this.undo;
    }

    /**
     * Sets the minimal height of a multi-line text field
     */
    public void setMinimalTextHeight(int minimalTextHeight) {
        checkPositive(minimalTextHeight);
        this.minimalTextHeight = minimalTextHeight;
    }

    /**
     * Sets the minimal width of a text field
     */
    public void setMinimalTextWidth(int minimalTextWidth) {
        checkPositive(minimalTextWidth);
        this.minimalTextWidth = minimalTextWidth;
    }

    /**
     * Sets the string for "no"
     */
    public void setStringNo(String no) {
        checkNull(no);
        this.no = no;
    }

    /**
     * Sets the string for "ok"
     */
    public void setStringOK(String ok) {
        checkNull(ok);
        this.ok = ok;
    }

    /**
     * Sets the string for "yes"
     */
    public void setStringYes(String yes) {
        checkNull(yes);
        this.yes = yes;
    }

    /**
     * Sets the string for "default"
     */
    public void setStringDefault(String _default) {
        checkNull(_default);
        this._default = _default;
    }

    /**
     * Sets the string for "undo"
     */
    public void setStringUndo(String undo) {
        checkNull(undo);
        this.undo = undo;
    }

    /**
     * Check argument for validity
     * @param arg
     */
    private void checkNull(Object arg) {
        if (arg == null) { throw new NullPointerException("Argument must not be null"); }
    }

    /**
     * Check argument for validity
     * @param arg
     */
    private void checkPositive(int arg) {
        if (arg < 0) { throw new IllegalArgumentException("Argument must be a positive integer"); }
    }

} // end of PreferenceTabConfiguration
