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

import de.ryanthara.ja.rycon.i18n.Preferences;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.PREFERENCES;

/**
 * The <tt>ConverterPreferenceTab</tt> holds all the preferences for the converter module
 * of <tt>RyCON</tt> in a composite.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class ConverterPreferenceTab extends PreferenceTab {

    /**
     * Constructor that matches the super() constructor.
     */
    ConverterPreferenceTab(Composite parent) {
        super(parent, SWT.NONE);

        createContent();
    }

    /**
     * Give Layout classes or other widgets the option to determine the size of this custom widget.
     * In this case the Layout, of the parent Composite, is able to align its child widgets properly.
     *
     * @param wHint   width
     * @param hHint   height
     * @param changed changed
     *
     * @return result of super() call
     */
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return super.computeSize(wHint, hHint, changed);
    }

    /**
     * Where the preference content is configured in subclasses.
     */
    @Override
    public void createContent() {
        /* Throws an SWTException if the receiver can not be accessed by the caller. */
        checkWidget();

        addCategory(ConverterPreferenceTab.class.getSimpleName());

        addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.converterTabGroupLTOP));
        addGroup(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.converterTabGroupZeiss));

        doIt();

        // actual disposal can be handled by the framework itself
        addDisposeListener(disposeEvent -> {

        });

    }

    /**
     * Returns the image of the tab.
     *
     * @return tab image
     */
    @Override
    public Image getImage() {
        return null;
    }

    /**
     * Returns the tab title.
     *
     * @return tab title
     */
    @Override
    public String getText() {
        return ResourceBundleUtils.getLangString(PREFERENCES, Preferences.converterTabTitle);
    }

    /**
     * Returns the tool tip text.
     *
     * @return tool tip text
     */
    @Override
    public String getToolTipText() {
        return ResourceBundleUtils.getLangString(PREFERENCES, Preferences.converterTabToolText);
    }

} // end of ConverterPreferenceTab
