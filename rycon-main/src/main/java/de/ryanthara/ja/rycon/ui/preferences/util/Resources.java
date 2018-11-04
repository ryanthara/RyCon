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

import de.ryanthara.ja.rycon.ui.Image;
import de.ryanthara.ja.rycon.ui.image.ImageConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * This class provides static access to image resources of RyCON's preference editors.
 * <p>
 * Within the {@link de.ryanthara.ja.rycon.ui.preferences.editor.Editor} small images are used on
 * top on the default and undo buttons.
 * <p>
 * The main idea of storing preferences with a MVC preference handler was implemented by Fabian Prasser.
 * See <a href="https://github.com/prasser/swtpreferences">prasser on github</a> for details.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class Resources {

    private static org.eclipse.swt.graphics.Image imageDefault = null;
    private static org.eclipse.swt.graphics.Image imageUndo = null;

    /**
     * Loads an image. Adds a dispose listener that disposes the image when the display is disposed.
     *
     * @param resource image resource
     * @return loaded image
     */
    private static org.eclipse.swt.graphics.Image getImage(String resource) {
        final org.eclipse.swt.graphics.Image image = new ImageConverter().convertToImage(Display.getCurrent(), resource);

        Display.getCurrent().addListener(SWT.Dispose, arg0 -> {
            if (image != null && !image.isDisposed()) {
                image.dispose();
            }
        });

        return image;
    }

    /**
     * Returns the image for the default button of the {@link de.ryanthara.ja.rycon.ui.preferences.editor.Editor}.
     *
     * @return image for the default button
     */
    public static org.eclipse.swt.graphics.Image getImageDefault() {
        if (imageDefault == null || imageDefault.isDisposed()) {
            imageDefault = getImage(Image.btnEditorDefault.getPath());
        }

        return imageDefault;
    }

    /**
     * Returns the image for the undo button of the {@link de.ryanthara.ja.rycon.ui.preferences.editor.Editor}.
     *
     * @return image for the undo button
     */
    public static org.eclipse.swt.graphics.Image getImageUndo() {
        if (imageUndo == null || imageUndo.isDisposed()) {
            imageUndo = getImage(Image.btnEditorUndo.getPath());
        }

        return imageUndo;
    }

}
