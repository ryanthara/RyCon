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

package de.ryanthara.ja.rycon.ui.preferences.editor;

import de.ryanthara.ja.rycon.ui.preferences.PreferencesDialog;
import de.ryanthara.ja.rycon.ui.preferences.util.Resources;
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;
import de.ryanthara.ja.rycon.i18n.Preferences;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.PREFERENCES;

/**
 * {@code Editor<T>} is an abstract base class for different {@code EditorT<generic data typ></>} classes of RyCON.
 * <p>
 * The subclassed editors exists for different generic data types (Model) like strings, booleans, integer values
 * and represents them with an corresponding control (Control) or needed ui components (View) like text fields
 * or combo boxes.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @param <T> The generic data type of the subclassed editor
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public abstract class Editor<T> {

    private boolean valid = true;

    private Button buttonDefault = null;
    private Button buttonUndo = null;

    private T defaultValue = null;
    private T initialValue = null;
    private Validator<T> validator = null;
    private PreferencesDialog dialog = null;

    /**
     * Constructs a new instance of {@code Editor} according to the parameters.
     *
     * @param dialog       parent dialog
     * @param validator    validator for generic data typ T
     * @param defaultValue default value for generic data type T
     */
    public Editor(PreferencesDialog dialog, Validator<T> validator, T defaultValue) {
        this.dialog = dialog;
        this.validator = validator;
        this.defaultValue = defaultValue;
    }

    /**
     * Creates an according control for the {@code EditorT}.
     *
     * @param parent parent composite
     */
    public abstract void createControl(Composite parent);

    /**
     * Returns the {@code PreferencesDialog} reference.
     *
     * @return the dialog
     */
    public PreferencesDialog getDialog() {
        return dialog;
    }

    /**
     * Returns the current value of the generic data type {@code T}.
     *
     * @return current value
     */
    public abstract T getValue();

    /**
     * Sets the value for the {@code EditorT}.
     *
     * @param t value of generic data type T
     */
    public abstract void setValue(Object t);

    /**
     * Indicates a changed value since the dialog was opened.
     * <p>
     * This is needed to activate the ok button for storing the changes.
     *
     * @return true if a preference value was changed
     */
    public boolean hasChanged() {
        return getInitialValue() != null && (!isValid() || !getInitialValue().equals(getValue()));
    }

    /**
     * Indicates if the current value is valid.
     *
     * @return true if is valid
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Sets the validity for the value.
     *
     * @param valid sets the validity
     */
    void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Creates the default button which restore the default value for the current preference.
     * <p>
     * The default button does not have a text, only an image.
     *
     * @param parent parent composite
     */
    void createDefaultButton(Composite parent) {
        // TODO delete obsolete old code lines and correct grid data usage
        // buttonDefault.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).create());
        GridData gridData = new GridData(); // SWT.LEFT, SWT.TOP);

        buttonDefault = new Button(parent, SWT.PUSH);
        buttonDefault.setImage(Resources.getImageDefault());
        buttonDefault.setToolTipText(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.toolTipDefault));
        buttonDefault.setLayoutData(gridData);
        buttonDefault.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                setValue(defaultValue);
            }
        });
    }

    /**
     * Creates the undo button which undo the last input or control change.
     * <p>
     * The undo button does not have a text, only an image.
     *
     * @param parent parent composite
     */
    void createUndoButton(Composite parent) {
        // TODO delete obsolete old code lines and correct grid data usage
        // buttonUndo.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).create());
        GridData gridData = new GridData(); // SWT.LEFT, SWT.TOP);

        buttonUndo = new Button(parent, SWT.PUSH);
        buttonUndo.setImage(Resources.getImageUndo());
        buttonUndo.setToolTipText(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.toolTipUndo));
        buttonUndo.setLayoutData(gridData);
        buttonUndo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                setValue(getInitialValue());
            }
        });
    }

    /**
     * Formats the value of the generic data type.
     *
     * @param t generic data type value to be formatted
     *
     * @return formatted value
     */
    abstract String format(T t);

    /**
     * Indicates whether the value is accepted or not.
     * <p>
     * The subclassed {@code EditorT} classes parsing the string value {@code s} into different generic data types.
     * Therefore a general {@link Exception} and not a {@link NumberFormatException} is caught.
     *
     * @param s value to be accepted
     *
     * @return true if value is accepted
     */
    boolean isAccepted(String s) {
        try {
            T t = parse(s);

            return t != null && (validator == null || validator.isValid(t));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses the value from the control into the generic data type {@code T}.
     *
     * @param s string to be parsed
     *
     * @return parsed string
     */
    abstract T parse(String s);

    /**
     * Updates the dialog.
     */
    void update() {
        dialog.update();

        buttonUndo.setEnabled(hasChanged() && getInitialValue() != null);

        try {
            buttonDefault.setEnabled(defaultValue != null && !getValue().equals(defaultValue));
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing for combo which is used in EditorSelection
        } catch (NumberFormatException e) {
            buttonDefault.setEnabled(false);
        }
    }

    /**
     * Gets the initial value for the {@code PreferenceT}.
     *
     * @return initial value
     */
    private T getInitialValue() {
        return initialValue;
    }

    /**
     * Sets the initial value for the {@code PreferenceT}.
     *
     * @param t value to be set
     */
    void setInitialValue(T t) {
        if (this.initialValue == null) {
            this.initialValue = t;
        }
    }

    /**
     * {code Size} holds the preferred sizes of the {@link Editor} subclasses.
     */
    public enum Size {
        minimalTextHeight(60),
        minimalTextWidth(60),
        minimalTextWidthPath(450);

        private final int size;

        /**
         * Constructs a new instance of {@code Size} according to the parameter.
         *
         * @param size preferred size
         */
        Size(int size) {
            this.size = size;
        }

        /**
         * Returns the size value.
         *
         * @return size value
         */
        public int getSize() {
            return size;
        }
    } // end of Size

} // end of Editor
