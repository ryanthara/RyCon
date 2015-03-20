/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui
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

package de.ryanthara.ja.rycon.gui;

import de.ryanthara.ja.rycon.tools.ImageConverter;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * This class defines a status bar and it's functionality.
 * <p>
 * The status bar shows a text which can be changed by method call and an icon
 * which shows three conditions for the actual status of the program. The status bar
 * is shown at the bottom of RyCONs main window.
 * <p>
 * Later on a progress bar should be implemented.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class StatusBar extends Composite {

    private Image iconError = null;
    private Image iconOK = null;
    private Image iconWarning = null;
    private Label icon = null;
    private Label message = null;

    /**
     * Constant value for displaying the error icon.
     */
    public static final int ERROR = 10;

    /**
     * Constant value for displaying the ok icon.
     */
    public static final int OK = 20;

    /**
     * Constant value for displaying the warning icon.
     */
    public static final int WARNING = 15;

    /**
     * Constructor with parameters for StatusBar.
     * <p>
     * Initializes the Control and its Widgets.
     *
     * @param parent the parent composite
     * @param style  sorry only SWT.NONE supported
     * @since 1
     */
    public StatusBar(Composite parent, int style) {
        super(parent, style);

        prepareIcons();

        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 5;
        setLayout(formLayout);

        message = new Label(this, style);
        icon = new Label(this, style);

        FormData data = new FormData();

        // TODO here comes the progress bar

        data.left = new FormAttachment(0, 0);
        message.setLayoutData(data);

        data = new FormData();
        data.right = new FormAttachment(100, 0);
        icon.setLayoutData(data);

        // dispose not used elements (icons, etc...) to clean up memory
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                StatusBar.this.dispose();
            }
        });
    }

    private void prepareIcons() {
        iconError = new ImageConverter().convertToImage(Display.getCurrent(), "/de/ryanthara/ja/rycon/gui/icons/10-error.png");
        iconOK = new ImageConverter().convertToImage(Display.getCurrent(), "/de/ryanthara/ja/rycon/gui/icons/20-ok.png");
        iconWarning = new ImageConverter().convertToImage(Display.getCurrent(), "/de/ryanthara/ja/rycon/gui/icons/15-warning.png");
    }

    /**
     * Displays a text and the status with an icon on the status bar.
     * <p>
     * The status icon can be set to OK, WARNING, ERROR.
     *
     * @param text status text to be set
     * @param status status icon chosen by static int value from class
     */
    public void setStatus(String text, int status) {
        message.setText(text);

        switch (status) {
            case 10:
                icon.setImage(iconError);
                break;
            case 15:
                icon.setImage(iconWarning);
                break;
            case 20:
                icon.setImage(iconOK);
                break;
        }

        // force a Layout to recalculate the sizes of and reposition children
        // by sending layout() to the parent Composite.
        layout(true);
    }

} // end of StatusBar
