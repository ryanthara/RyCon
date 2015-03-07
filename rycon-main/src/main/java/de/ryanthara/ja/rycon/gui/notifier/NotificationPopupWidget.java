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

package de.ryanthara.ja.rycon.gui.notifier;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.gui.notifier.caches.ColorCache;
import de.ryanthara.ja.rycon.gui.notifier.caches.FontCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class implements a notification popup and it's functionality.
 * <p>
 * The notification popup is shown on the bottom right corner of the screen,
 * normally above the windows toolbar. This notification popup stays on top
 * of RyCON, doesn't steal the focus or is visible in the task manager.
 * <p>
 * In the free version of RyCON the notification popup stays always open.
 * <p>
 * The implementation is inspired by an article on <a href="http://hexapixel.com/2009/06/30/creating-a-notification-popup-widget">hexapixel.com</a>
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up</li>
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 2
 */
public class NotificationPopupWidget {

    /**
     * Constant which shows a popup 4.5 seconds.
     */
    public static int DISPLAY_TIME = 4500;

    private static int displayTime;
    private static final int FADE_IN_STEP = 30;
    private static final int FADE_OUT_STEP = 8;
    private static final int FADE_TIMER = 50;
    private static final int FINAL_ALPHA = 255;

    private static ArrayList<Shell> activeShells = new ArrayList<Shell>();

    private static Color backgroundGradient = ColorCache.getColor(177, 211, 243);
    private static Color borderColor  = ColorCache.getColor(40, 73, 97);
    private static Color foregroundColor  = ColorCache.getColor(40, 73, 97);
    private static Color foregroundGradient = ColorCache.getColor(226, 239, 249);
    private static Color titleColor = ColorCache.getColor(40, 73, 97);
    private static Image oldImage;

    private static Shell innerShell;

    private static String message = null;
    private static String title = null;

    /**
     * Shows a NotificationPopupWidget which can be set up by a couple of parameters.
     * <p>
     * This popup is used for the non licensed version of RyCON.
     *
     * @param title title of the popup widget
     * @param message message string of the popup widget
     * @param type type which controls the icon of the widget
     * @param time time in ms how long the popup is shown
     */
    public static void notify(String title, String message, NotificationType type, int time) {
        NotificationPopupWidget.title = title;
        NotificationPopupWidget.message = message;

        displayTime = time;

        // creates a shell without focus and no borders
        innerShell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS | SWT.NO_TRIM);
        innerShell.setLayout(new FillLayout());
        innerShell.setForeground(foregroundColor);

        // inherit the background of the children
        innerShell.setBackgroundMode(SWT.INHERIT_DEFAULT);

        // rule 1
        innerShell.addListener(SWT.Dispose, new Listener() {
            @Override
            public void handleEvent(Event event) {
                activeShells.remove(innerShell);
            }
        });

        final Composite composite = new Composite(innerShell, SWT.NONE);

        GridLayout gl = new GridLayout(2, false);
        gl.marginLeft = 5;
        gl.marginTop = 0;
        gl.marginRight = 5;
        gl.marginBottom = 5;

        composite.setLayout(gl);

        // because of the lack of transparency and gradient background of the shell, paint an image in the background
        innerShell.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {

                // why is this surrounded?
                try {
                    // get the size of the drawing area and create an image with these size
                    Rectangle rectangle = innerShell.getClientArea();
                    Image newImage = new Image(Display.getDefault(), Math.max(1, rectangle.width), rectangle.height);

                    // create a GC object which is used for drawing
                    GC gc = new GC(newImage);

                    // fill background with the gradient
                    gc.setForeground(foregroundGradient);
                    gc.setBackground(backgroundGradient);
                    gc.fillGradientRectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);

                    // draw borders
                    gc.setLineWidth(2);
                    gc.setForeground(borderColor);
                    gc.drawRectangle(rectangle.x + 1, rectangle.y + 1, rectangle.width - 2, rectangle.height - 2);

                    // remember rule 1!
                    gc.dispose();

                    innerShell.setBackgroundImage(newImage);

                    if (oldImage != null) {
                        oldImage.dispose();
                    }

                    oldImage = newImage;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        GC gc = new GC(innerShell);

        String lines[] = message.split("\n");
        Point longest = null;

        int normalHeight = gc.stringExtent("THW").y;

        for (String line : lines) {
            Point extent = gc.stringExtent(line);

            if (longest == null) {
                longest = extent;
                continue;
            }

            if (extent.x > longest.x) {
                longest = extent;
            }
        }

        // rule 1
        gc.dispose();

        int minHeight = normalHeight * lines.length;

        createLabels(composite, type);

        // fixed value for minHeight
        // TODO better value handling should be done here later
        minHeight = 100;

        innerShell.setSize(350, minHeight);

        // check shell or monitor for null -> do not show the popup
        if (Display.getDefault().getActiveShell() == null || Display.getDefault().getActiveShell().getMonitor() == null ) {
            return;
        }

        Rectangle clientArea = Display.getDefault().getActiveShell().getMonitor().getClientArea();

        int startX = clientArea.x + clientArea.width - 352;
        int startY = clientArea.y + clientArea.height - 102;

        // move other existing shells up
        if (!activeShells.isEmpty()) {
            ArrayList<Shell> modifiableShells = new ArrayList<Shell>(activeShells);
            Collections.reverse(modifiableShells);

            for (Shell shell : modifiableShells) {
                Point currentLocation = shell.getLocation();
                shell.setLocation(currentLocation.x, currentLocation.y - 100);

                if (currentLocation.y - 100 < 0) {
                    activeShells.remove(shell);
                    shell.dispose();
                }

            }

        }

        innerShell.setLocation(startX, startY);
        innerShell.setAlpha(0);
        innerShell.setVisible(true);

        activeShells.add(innerShell);

        fadeIn(innerShell);
    }

    private static void createLabels(Composite composite, NotificationType type) {
        // sets the labels - CLabel is used because of the gradient
        CLabel imgLabel = new CLabel(composite, SWT.NONE);
        imgLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
        imgLabel.setImage(type.getImage());

        CLabel titleLabel = new CLabel(composite, SWT.NONE);
        titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
        titleLabel.setText(title);
        titleLabel.setForeground(titleColor);

        Font titleFont = titleLabel.getFont();
        FontData titleFontData = titleFont.getFontData()[0];
        titleFontData.setHeight(11);
        titleFontData.setStyle(SWT.BOLD);
        titleLabel.setFont(FontCache.getFont(titleFontData));

        // sets the text on a label
        Label textLabel = new Label(composite, SWT.WRAP);

        Font textFont = textLabel.getFont();
        FontData textFontData = textFont.getFontData()[0];
        textFontData.setHeight(8);
        textFontData.setStyle(SWT.BOLD);
        textLabel.setFont(FontCache.getFont(textFontData));

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;

        textLabel.setForeground(foregroundColor);
        textLabel.setLayoutData(gridData);
        textLabel.setText(String.format(message));

        // sets the url on the bottom
        Link link = new Link(composite, SWT.NONE);
        String url = "<a href=\"http://code.ryanthara.de/RyCON\">RyCON - Homepage</a>";
        link.setText(url);
        link.setSize(400, 100);
        link.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(new URI(Main.getRyCONWebsite()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private static void fadeIn(final Shell shell) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (shell == null || shell.isDisposed()) {
                        return;
                    }

                    int current = shell.getAlpha();
                    current += FADE_IN_STEP;

                    if (current > FINAL_ALPHA) {
                        shell.setAlpha(FINAL_ALPHA);
                        startTimerForFadeOut(shell);
                        return;
                    }

                    shell.setAlpha(current);
                    Display.getDefault().timerExec(FADE_TIMER, this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Display.getDefault().timerExec(FADE_TIMER, runnable);
    }

    private static void fadeOut(final Shell shell) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (shell == null || shell.isDisposed()) {
                        return;
                    }

                    int current = shell.getAlpha();
                    current -= FADE_OUT_STEP;

                    if (current <= 0) {
                        shell.setAlpha(0);

                        if (oldImage != null) {
                            oldImage.dispose();
                        }

                        shell.dispose();
                        activeShells.remove(shell);
                        return;
                    }

                    shell.setAlpha(current);
                    Display.getDefault().timerExec(FADE_TIMER, this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Display.getDefault().timerExec(FADE_TIMER, runnable);
    }

    private static void startTimerForFadeOut(final Shell shell) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (shell == null || shell.isDisposed()) {
                    return;
                }
                fadeOut(shell);
            }
        };

        Display.getDefault().timerExec(displayTime, runnable);
    }

} // end of NotifyPopup
