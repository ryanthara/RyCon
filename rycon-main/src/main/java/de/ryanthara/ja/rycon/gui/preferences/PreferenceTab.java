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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The <tt>PreferencesTab</tt> is part of the {@link PreferencesDialog} to hold the preferences of <tt>RyCON</tt>.
 * <p>
 * This abstract class represents one tab, that has to be put to the dialog.
 * <p>
 * With version 2 of <tt>RyCON</tt> the need for more preferences of the modules is fulfilled with
 * a new preferences dialog. It uses a tabbed structure for different modules and try to provide
 * a clear view on the changeable preferences.
 * <p>
 * The idea to this are inspired by preference dialogs of different applications, like Eclipse, IntelliJ IDEA,
 * AutoCAD and some github stuff like swtpreferences from prasser.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
abstract class PreferenceTab extends Composite {

    // some Models
    private String category;
    private List<String> categories = new ArrayList<>();
    private Map<Preference<?>, Editor<?>> editors = new HashMap<>();
    private Map<String, List<Preference<?>>> preferences = new HashMap<>();
    private PreferenceTabConfiguration config = new PreferenceTabConfiguration();

    // some Views

    PreferenceTab(Composite parent, int i) {
        super(parent, i);

        //createComposite();
    }

    /**
     * Where the preference content is configured in subclasses.
     */
    public abstract void createContent();

    /**
     * Returns the preference tab's configuration
     *
     * @return the preference tab's configuration
     */
    public PreferenceTabConfiguration getConfiguration() {
        System.out.println("getConfiguration()");

        return config;
    }

    public abstract Image getImage();

    public abstract String getText();

    public abstract String getToolTipText();

    /**
     * Adds a new category to the preference tab.
     * <p>
     * The <tt>category</tt> is an indicator and maybe obsolete, because tab content is built
     * in different composite objects and not in one dialog class.
     *
     * @param label category string
     */
    void addCategory(String label) {
        if (label == null) {
            throw new NullPointerException("Label must not be null");
        }

        this.preferences.put(label, new ArrayList<>());
        this.category = label;
        this.categories.add(label);
    }

    /**
     * Adds a new group to the preference tab.
     *
     * @param text group name
     */
    void addGroup(String text) {
        if (category == null) {
            throw new IllegalStateException("Please create a category first");
        }

        this.preferences.get(category).add(new Group(text));
    }

    /**
     * Adds a new preference to the preference tab and the current group.
     *
     * @param preference preference to add
     */
    void addPreference(Preference<?> preference) {
        if (category == null) {
            throw new IllegalStateException("Please create a category first");
        }

        this.preferences.get(category).add(preference);
        preference.setPreferenceTab(this);
    }

    /**
     * Creates the content of the preference tabs.
     */
    //private void createComposite() {
    void doIt() {
//    private Composite createCategory(final TabFolder folder, String category, List<Preference<?>> preferences) {
        this.setLayout(new GridLayout(4, false));

        final List<Label> labels = new ArrayList<>();

        org.eclipse.swt.widgets.Group current = null;

        for (final Preference<?> e : preferences.get(category)) {
            if (e instanceof Group) {
                current = new org.eclipse.swt.widgets.Group(this, SWT.SHADOW_ETCHED_IN);
                current.setText(e.getLabel());

                // TODO implement LayoutData
                //current.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(4, 1).create());

                current.setLayout(new GridLayout(4, false));
            } else {
                final Label l = new Label(current != null ? current : this, SWT.NONE);
                l.setText(e.getLabel() + ":"); //$NON-NLS-1$
                labels.add(l);
                editors.put(e, e.getEditor());
                editors.get(e).createControl(current != null ? current : this);
                editors.get(e).setValue(e.getValue());
            }
        }

        // Set equal width on labels
        ControlListener listener = new ControlAdapter() {

            /** Call count*/
            int count = 0;
            /** Max width*/
            int maxWidth = 0;

            @Override
            public void controlResized(ControlEvent arg0) {
                maxWidth = Math.max(((Label) arg0.widget).getSize().x, maxWidth);
                if (++count == labels.size()) {
                    GridData gridData = new GridData();
                    gridData.widthHint = maxWidth;
                    for (Label label : labels) {
                        label.setLayoutData(gridData);
                    }
                    layout(true, true);
                }
            }
        };

        // Attach listener
        for (Label label : labels) {
            label.addControlListener(listener);
        }

    }

} // end of PreferenceTab
