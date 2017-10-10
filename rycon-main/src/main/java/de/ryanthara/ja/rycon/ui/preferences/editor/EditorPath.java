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
import de.ryanthara.ja.rycon.ui.preferences.validator.Validator;
import de.ryanthara.ja.rycon.i18n.Preferences;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.PREFERENCES;

/**
 * {@code EditorPath} is an editor for choosing valid paths.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class EditorPath extends Editor<Path> {

    private Text text;

    /**
     * Creates a new instance.
     *
     * @param dialog      parent dialog
     * @param defaultPath default path for the editor
     */
    public EditorPath(PreferencesDialog dialog, Path defaultPath) {
        super(dialog, null, defaultPath);
    }

    /**
     * Creates a new instance.
     *
     * @param dialog      parent dialog
     * @param validator   for paths
     * @param defaultPath default path for the editor
     */
    public EditorPath(PreferencesDialog dialog, Validator<Path> validator, Path defaultPath) {
        super(dialog, validator, defaultPath);
    }

    /**
     * Creates an according control and its behaviour.
     *
     * @param parent parent composite
     */
    @Override
    public void createControl(Composite parent) {
        parent.setLayout(new GridLayout(5, false));

        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        gridData.grabExcessHorizontalSpace = true;
        gridData.minimumWidth = Size.minimalTextWidthPath.getSize();

        text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        text.setText("");
        text.setLayoutData(gridData);

        final Color black = new Color(text.getDisplay(), 0, 0, 0);
        final Color red = new Color(text.getDisplay(), 255, 0, 0);

        text.addModifyListener(arg0 -> {
            if (isAccepted(text.getText())) {
                text.setForeground(black);
                setValid(true);
            } else {
                text.setForeground(red);
                setValid(false);
            }

            update();
        });

        text.addDisposeListener(arg0 -> {
            black.dispose();
            red.dispose();
        });

        gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);

        Button button = new Button(parent, SWT.PUSH);
        button.setText(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathBtnText));
        button.setToolTipText(ResourceBundleUtils.getLangString(PREFERENCES, Preferences.pathBtnToolTip));
        button.setLayoutData(gridData);

        super.createUndoButton(parent);
        super.createDefaultButton(parent);
        super.update();

    }

    /**
     * Returns the path from the text field of this editor.
     *
     * @return Path
     */
    @Override
    public Path getValue() {
        return Paths.get(text.getText());
    }

    /**
     * Sets the {@code Paths} value for this editor.
     *
     * @param t Paths value to be set
     */
    @Override
    public void setValue(Object t) {
        Path path = (Path) t;
        this.setInitialValue(path);
        this.text.setText(path.toString());
        super.update();
    }

    /**
     * Parses the string to {@code Paths}.
     *
     * @param s string to be parsed
     *
     * @return parsed string
     */
    @Override
    protected Path parse(final String s) {
        return Paths.get(s);
    }

    /**
     * Formats the value for for this editor.
     *
     * @param path value to be formatted
     *
     * @return formatted value
     */
    @Override
    String format(Path path) {
        return path.toString();
    }

} // end of EditorPath
