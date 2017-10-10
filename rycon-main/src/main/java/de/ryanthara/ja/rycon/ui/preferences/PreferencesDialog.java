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

package de.ryanthara.ja.rycon.ui.preferences;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.ui.Sizes;
import de.ryanthara.ja.rycon.ui.preferences.editor.Editor;
import de.ryanthara.ja.rycon.ui.preferences.pref.Preference;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;

/**
 * {@code PreferencesDialog} implements the preferences dialog for RyCON.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class PreferencesDialog {

    private String category;
    private String message;
    private String title;
    private List<String> categories = new ArrayList<>();
    private Map<Preference<?>, Editor<?>> editors = new HashMap<>();
    private Map<String, Image> images = new HashMap<>();
    private Map<String, List<Preference<?>>> preferences = new HashMap<>();
    private Button saveButton;
    private Shell parentShell;
    private Shell innerShell;
    private TabFolder folder;

    /**
     * Constructs a new instance according to the parameters.
     *
     * @param parentShell parent shell
     * @param title       title of the dialog
     * @param message     message of the dialog
     */
    public PreferencesDialog(Shell parentShell, String title, String message) {
        this(parentShell, title, message, false);
    }

    /**
     * Constructs a new instance according to the parameters.
     *
     * @param parentShell parent shell
     * @param title       title of the dialog
     * @param message     message of the dialog
     * @param resizable   is resizable
     */
    public PreferencesDialog(Shell parentShell, String title, String message, boolean resizable) {
        this.title = title;
        this.message = message;
        this.parentShell = parentShell;

        initUI(resizable);
    }

    /**
     * Adds a new category with name to the dialog which is shown as a {@link TabItem}.
     *
     * @param name name of the tab
     */
    public void addCategory(String name) {
        addCategory(name, null);
    }

    /**
     * Adds a new category to the dialog with name and icon which is shown as a {@link TabItem}.
     *
     * @param name  name of the tab
     * @param image image on the tab
     */
    private void addCategory(String name, Image image) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }

        this.category = name;
        this.images.put(name, image);
        this.categories.add(name);
        this.preferences.put(name, new ArrayList<>());
    }

    /**
     * Adds a new {@link Group} to the tab.
     *
     * @param text group text
     */
    public void addGroup(String text) {
        if (category == null) {
            throw new IllegalStateException("Please create a categoryFormats first");
        }

        this.preferences.get(category).add(new de.ryanthara.ja.rycon.ui.preferences.util.Group(text));
    }

    /**
     * Adds a new {@link Preference} to the tab.
     *
     * @param preference preference of the generic data type T
     */
    public void addPreference(Preference<?> preference) {
        if (category == null) {
            throw new IllegalStateException("Please create a categoryFormats first");
        }

        this.preferences.get(category).add(preference);

        preference.setDialog(this);
    }

    /**
     * Opens the {@code PreferencesDialog} and make it visible.
     */
    public void open() {
        createTabs(innerShell);
        createButtonBar(innerShell);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        innerShell.pack();
        innerShell.open();
    }

    /**
     * Updates the save settings button.
     */
    public void update() {
        if (saveButton != null) {
            saveButton.setEnabled(hasChanged());
        }
    }

    /**
     * Creates the button bar with the save settings button.
     *
     * @param parent parent composite
     */
    private void createButtonBar(final Composite parent) {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalIndent = 0;
        gridData.verticalIndent = 0;

        parent.setLayoutData(gridData);

        saveButton = new Button(parent, SWT.NONE);
        saveButton.setText(ResourceBundleUtils.getLangString(LABELS, Labels.preferencesDialogOkButtonText));
        saveButton.setToolTipText(ResourceBundleUtils.getLangString(LABELS, Labels.preferencesDialogOkButtonTooltip));
        saveButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {

                // write preferences only for changed settings
                for (Entry<Preference<?>, Editor<?>> entry : editors.entrySet()) {
                    if (entry.getValue().hasChanged()) {
                        entry.getKey().setValue(entry.getValue().getValue());
                    }
                }

                actionBtnSave();
            }
        });

        saveButton.setEnabled(hasChanged());
    }

    private void actionBtnSave() {
        Main.pref.setDefaultSettingsGenerated(false);
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        widgetDispose();
    }

    /**
     * Creates a category and formats it's child elements.
     *
     * @param folder      parent tab folder
     * @param preferences list of preferences to be set
     *
     * @return built composite
     */
    private Composite createCategory(final TabFolder folder, List<Preference<?>> preferences) {
        final Composite base = new Composite(folder, SWT.NONE);
        base.setLayout(new GridLayout(4, false));

        final List<Label> labels = new ArrayList<>();

        org.eclipse.swt.widgets.Group group = null;

        for (final Preference<?> e : preferences) {
            if (e instanceof de.ryanthara.ja.rycon.ui.preferences.util.Group) {
                group = new org.eclipse.swt.widgets.Group(base, SWT.SHADOW_ETCHED_IN);
                group.setText(e.getLabel());

                GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
                gridData.horizontalSpan = 4;

                group.setLayoutData(gridData);

                group.setLayout(new GridLayout(4, false));
            } else {
                final Label l = new Label(group != null ? group : base, SWT.NONE);
                l.setText(e.getLabel() + ":");
                labels.add(l);

                editors.put(e, e.getEditor());
                editors.get(e).createControl(group != null ? group : base);
                editors.get(e).setValue(e.getValue());
            }
        }

        // Set equal width on labels
        ControlListener listener = new ControlAdapter() {

            int count = 0;
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

                    base.layout(true, true);
                }
            }
        };

        for (Label label : labels) {
            label.addControlListener(listener);
        }

        return base;
    }

    private void createTabs(final Composite parent) {
        folder = new TabFolder(parent, SWT.NONE);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalIndent = 0;
        gridData.verticalIndent = 0;

        folder.setLayoutData(gridData);

        // Build tabs
        for (final String category : categories) {
            final TabItem tab = new TabItem(folder, SWT.NONE);
            tab.setText(category);

            if (images.get(category) != null) {
                tab.setImage(images.get(category));
            }

            final Composite tabC = createCategory(folder, preferences.get(category));
            tab.setControl(tabC);
        }

        // Ugly hack that seems to be needed to force a correct layout on Linux/GTK
        folder.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent arg0) {
                folder.layout(true, true);
                folder.removePaintListener(this);
            }
        });
    }

    /**
     * Returns whether one ore more settings has changed.
     *
     * @return preferences has changed
     */
    private boolean hasChanged() {
        boolean changed = false;

        for (Entry<String, List<Preference<?>>> entry : preferences.entrySet()) {
            for (Preference<?> preference : entry.getValue()) {
                Editor<?> editor = editors.get(preference);
                if (editor != null) {
                    if (!editor.isValid()) {
                        return false;
                    }

                    changed |= editor.hasChanged();
                }
            }
        }

        return changed;
    }

    private void initUI(boolean resizable) {
        final int height = Sizes.RyCON_WIDGET_HEIGHT.getValue();
        final int width = Sizes.RyCON_WIDGET_WIDTH.getValue() + 205;

        int shellStyle = SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE;

        if (resizable) {
            shellStyle = shellStyle | SWT.RESIZE;
        }

        innerShell = new Shell(parentShell, shellStyle);
        innerShell.addListener(SWT.Close, event -> actionBtnSave());
        innerShell.setText(title);
        innerShell.setSize(width, height);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        Label label = new Label(innerShell, SWT.NONE);
        label.setText(message);

        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.minimumHeight = 30;
        gridData.horizontalIndent = 5;
        gridData.verticalIndent = 25;

        label.setLayoutData(gridData);

        Main.setSubShellStatus(true);

        innerShell.forceActive();
    }

    private void widgetDispose() {
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

} // end of PreferencesDialog