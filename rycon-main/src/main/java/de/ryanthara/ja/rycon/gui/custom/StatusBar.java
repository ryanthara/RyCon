/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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

package de.ryanthara.ja.rycon.gui.custom;

import de.ryanthara.ja.rycon.events.StatusInformationListener;
import de.ryanthara.ja.rycon.gui.Images;
import de.ryanthara.ja.rycon.tools.ImageConverter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import java.util.Vector;

/**
 * Instances of this class implements a custom status bar and it's functionality.
 * <p>
 * The status bar shows a text which can be changed by method call and an icon
 * which shows three conditions for the actual status of the program. The status bar
 * is shown at the bottom of RyCONs main window.
 * <p>
 * Later on a progress bar should be implemented.
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class StatusBar extends Composite {

    private Image iconError;
    private Image iconOK;
    private Image iconWarning;
    private Label icon;
    private Label message;
    private Vector statusInformationListeners;

    /**
     * Constructs a new instance of this class given it's parent composite and the style.
     * <p>
     * Initializes the Control and its Widgets.
     *
     * @param parent the parent composite
     *
     * @since 1
     */
    @SuppressWarnings("unchecked")
    public StatusBar(Composite parent) {
        super(parent, org.eclipse.swt.SWT.NONE);

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
     * Display a text and the status with an icon on the status bar.
     * <p>
     * The status icon can be set to OK, WARNING, ERROR.
     *
     * @param text   status text to be set
     * @param status status icon chosen by static int value from class
     */
    public void setStatus(String text, Status status) {
        message.setText(text);

        switch (status) {
            case ERROR:
                icon.setImage(iconError);
                break;

            case WARNING:
                icon.setImage(iconWarning);
                break;

            case OK:
                icon.setImage(iconOK);
                break;

            default:
                icon.setImage(iconOK);
                System.err.println("StatusBar.setStatus() : default icon was set!");
        }

        // force a Layout to recalculate the sizes of and reposition children
        // by sending layout() to the parent Composite.
        layout(true);
    }

    /**
     * Add the {@link StatusInformationListener} method.
     *
     * @param listener the {@link StatusInformationListener} to be added
     */
    @SuppressWarnings("unchecked")
    private void addStatusInformationListener(StatusInformationListener listener) {
        statusInformationListeners.addElement(listener);
    }

    private void createContent() {
        /* Throws an SWTException if the receiver can not be accessed by the caller. */
        checkWidget();

        prepareIcons();

        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 5;
        setLayout(formLayout);

        icon = new Label(this, org.eclipse.swt.SWT.NONE);
        message = new Label(this, org.eclipse.swt.SWT.NONE);

        FormData data = new FormData();

        // TODO here comes the progress bar

        data.left = new FormAttachment(0, 0);
        message.setLayoutData(data);

        data = new FormData();
        data.right = new FormAttachment(100, 0);
        icon.setLayoutData(data);

        statusInformationListeners = new Vector();

        addStatusInformationListener(e -> message.setText(e.getStatusText()));

        // dispose not used elements (icons, etc...) to clean up memory
        addDisposeListener(e -> {
            iconError.dispose();
            iconOK.dispose();
            iconWarning.dispose();
        });
    }

    private void prepareIcons() {
        iconError = new ImageConverter().convertToImage(Display.getCurrent(), Images.iconError.getPath());
        iconOK = new ImageConverter().convertToImage(Display.getCurrent(), Images.iconOK.getPath());
        iconWarning = new ImageConverter().convertToImage(Display.getCurrent(), Images.iconWarning.getPath());
    }

} // end of StatusBar
