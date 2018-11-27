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

package de.ryanthara.ja.rycon.ui;

import de.ryanthara.ja.rycon.i18n.Buttons;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.BUTTON;

/**
 * {@code UpdateDialog} implements an update dialog for RyCON.
 * <p>
 * If there is a new version of RyCON available this dialog will show a message and
 * a short 'what's new' section. The user can open the default browser of the system
 * and will be redirected to the RyCON update website.
 * <p>
 * The code is inspired by the original MessageDialog code from SWT.
 *
 * @author sebastian
 * @version 2
 * @since 3
 */
public class UpdateDialog extends Dialog {

    /**
     * Member for indicating which button was hit.
     */
    public static final int CLOSE_AND_CONTINUE = 100;
    /**
     * Member for indicating which button was hit.
     */
    public static final int CLOSE_AND_OPEN_BROWSER = 101;
    private static final Logger logger = LoggerFactory.getLogger(UpdateDialog.class.getName());
    private static final int SPACING = 20;
    private static final int BUTTON_WIDTH = 61;
    private static final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
    private static final int MAX_WIDTH = 640;
    private static final int MAX_HEIGHT = 480;
    private final Image image;
    private int returnCode = Integer.MIN_VALUE;
    private Shell shell;
    private String message;
    private String whatsNewInfo;

    /**
     * Constructs a new instance of this class given only its parent.
     * <p>
     * The Constructor passes default styles.
     *
     * @param parent a shell which will be the parent of the new instance
     */
    public UpdateDialog(Shell parent) {
        this(parent, SWT.ICON_INFORMATION | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }

    /**
     * Constructs a new instance of this class given its parent and a style.
     * <p>
     * The Constructor passes default styles.
     *
     * @param parent a shell which will be the parent of the new instance
     * @param style  the style of dialog to construct
     */
    private UpdateDialog(Shell parent, int style) {
        super(parent, style);
        image = parent.getDisplay().getSystemImage(SWT.ICON_INFORMATION);
        message = "";
    }

    /**
     * Opens the dialog and show the content.
     *
     * @return the return code which indicates the user choice
     */
    public int open() {
        shell = new Shell(getParent(), SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
        shell.setText(getText());

        createControls();

        shell.setBounds(computeShellBounds());
        shell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(shell));
        shell.pack();
        shell.open();

        shell.addDisposeListener(disposeEvent -> image.dispose());

        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        return returnCode;
    }

    /**
     * Set the update dialog's message. The message will be visible on the dialog while it is open.
     *
     * @param message the message to be shown
     * @throws java.lang.IllegalArgumentException ERROR_NULL_ARGUMENT - if the string is null
     */
    public void setMessage(String message) {
        if (message == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        this.message = message;
    }

    /**
     * Set the what's new info for displaying.
     *
     * @param whatsNewInfo the info to be set
     * @throws java.lang.IllegalArgumentException ERROR_NULL_ARGUMENT - if the string is null
     */
    public void setWhatsNewInfo(String whatsNewInfo) {
        if (whatsNewInfo == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        this.whatsNewInfo = whatsNewInfo;
    }

    private Rectangle computeShellBounds() {
        Rectangle result = new Rectangle(0, 0, 0, 0);
        Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Rectangle parentSize = getParent().getBounds();
        result.x = (parentSize.width - preferredSize.x) / 2 + parentSize.x;
        result.y = (parentSize.height - preferredSize.y) / 2 + parentSize.y;
        result.width = Math.min(preferredSize.x, MAX_WIDTH);
        result.height = preferredSize.y;

        return result;
    }

    private int convertHorizontalDLUsToPixels(int dlus) {
        GC gc = new GC(shell);
        int charWidth = gc.getFontMetrics().getAverageCharWidth();
        int width = charWidth * dlus + HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2;
        gc.dispose();

        return width / HORIZONTAL_DIALOG_UNIT_PER_CHAR;
    }

    private void createBrowser() {
        final Browser browser;

        try {
            browser = new Browser(shell, SWT.NONE);
        } catch (SWTError e) {
            logger.warn("Can not open the default browser of the system.", e.getCause());

            Display.getCurrent().dispose();

            return;
        }

        browser.setText(whatsNewInfo);
        browser.addLocationListener(new LocationListener() {
            @Override
            public void changed(LocationEvent locationEvent) {
            }

            @Override
            public void changing(LocationEvent event) {
                event.doit = false;
                openDefaultSystemBrowser(event.location);
            }
        });

        GridData browserData = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 2, 1);
        browserData.widthHint = MAX_WIDTH;
        browserData.heightHint = MAX_HEIGHT;
        browser.setLayoutData(browserData);
    }

    private void createButtons() {
        Composite buttonArea = new Composite(shell, SWT.NONE);
        buttonArea.setLayout(new GridLayout(2, true));
        GridData data = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        data.horizontalSpan = 2;
        buttonArea.setLayoutData(data);

        org.eclipse.swt.widgets.Button close = new org.eclipse.swt.widgets.Button(buttonArea, SWT.PUSH);
        close.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndExitText));
        close.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndExitToolTip));
        close.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                UpdateDialog.this.returnCode = CLOSE_AND_CONTINUE;
                shell.close();
            }
        });

        GridData closeBtnData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(BUTTON_WIDTH);
        Point minSize = close.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        closeBtnData.widthHint = Math.max(widthHint, minSize.x);
        close.setLayoutData(closeBtnData);

        org.eclipse.swt.widgets.Button openBrowser = new org.eclipse.swt.widgets.Button(buttonArea, SWT.PUSH);
        openBrowser.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndOpenBrowserText));
        openBrowser.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.okAndOpenBrowserToolTip));
        openBrowser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                UpdateDialog.this.returnCode = CLOSE_AND_OPEN_BROWSER;
                shell.close();
            }
        });

        GridData openBtnData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        widthHint = convertHorizontalDLUsToPixels(BUTTON_WIDTH);
        minSize = openBrowser.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        openBtnData.widthHint = Math.max(widthHint, minSize.x);
        openBrowser.setLayoutData(openBtnData);

        buttonArea.getChildren()[0].forceFocus();
    }

    private void createControls() {
        shell.setLayout(new GridLayout(2, false));
        createImage();
        createText();
        createBrowser();
        createButtons();
    }

    private void createImage() {
        if (image != null) {
            Label imageLabel = new Label(shell, SWT.NONE);
            GridData data = new GridData(SWT.CENTER, SWT.TOP, false, false);
            data.widthHint = image.getBounds().width + SPACING;
            imageLabel.setLayoutData(data);
            imageLabel.setImage(image);
        }
    }

    private void createText() {
        final Label textLabel = new Label(shell, SWT.WRAP);

        // use bold font with one pixel larger
        FontData fontData = textLabel.getFont().getFontData()[0];
        Font font = new Font(getParent().getDisplay(), new FontData(fontData.getName(), fontData.getHeight() + 1, SWT.BOLD));
        textLabel.setFont(font);

        final GridData data = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        int imageWidth = image == null ? 0 : image.getBounds().width;
        int maxTextWidth = MAX_WIDTH - imageWidth - SPACING;
        int maxLineWidth = getMaxMessageLineWidth();

        if (maxLineWidth > maxTextWidth) {
            data.widthHint = maxTextWidth;
        }

        textLabel.setLayoutData(data);
        textLabel.setText(message);

        /*
         * this is necessary for line break because of the label is in a composite and then
         * the line break has to be forced!
         * see: http://book.javanb.com/swt-the-standard-widget-toolkit/ch15lev1sec12.html
         */
        textLabel.addListener(SWT.Resize, event -> {
            Rectangle bounds = textLabel.getBounds();
            data.widthHint = bounds.width;
            shell.layout();
        });

        textLabel.addDisposeListener(disposeEvent -> font.dispose());
    }

    private int getMaxMessageLineWidth() {
        int result = 0;

        StringTokenizer tokenizer = new StringTokenizer(message, "\n");
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();

            // helper label for text width calculation
            Label label = new Label(shell, SWT.NONE);
            GC gc = new GC(label);
            Point size = gc.textExtent(line);  // Tab expansion and carriage return processing are performed.
//            Point size = gc.stringExtent(line); // No tab expansion or carriage return processing will be performed.

            gc.dispose();

            int lineWidth = size.x;
            result = Math.max(result, lineWidth);
        }

        return result;
    }

    private void openDefaultSystemBrowser(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (IOException e) {
            logger.error("Could not open the connection '{}' in the default browser.", uri, e.getCause());
        } catch (URISyntaxException e) {
            logger.error("URI '{}' has the wrong syntax.", uri, e.getCause());
        }
    }

}
