/*
 * License: GPL. Copyright 2017- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.widget
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
package de.ryanthara.ja.rycon.ui.widgets;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.core.converter.Separator;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.LangStrings;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Texts;
import de.ryanthara.ja.rycon.ui.Image;
import de.ryanthara.ja.rycon.ui.Size;
import de.ryanthara.ja.rycon.ui.image.ImageConverter;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LANG_STRING;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXT;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;

/**
 * A widget which is used to show the about dialog to users.
 * <p>
 * The {@code AboutWidget} is a simple widget of RyCON
 * which is used to to show basic information about RyCON
 * to the user. Therefore a nice artwork and a basic information
 * structure is used.
 *
 * @author sebastian
 * @version 1
 * @since 2
 */
public class AboutWidget {

    private final Shell parent;
    private Font font;
    private Shell innerShell;

    /**
     * Constructs the {@link AboutWidget} with a parameter for the parent shell.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     *
     * @param parent parent shell
     */
    public AboutWidget(Shell parent) {
        this.parent = parent;

        initUI();
    }

    private void initUI() {
        final int height = Size.RyCON_WIDGET_HEIGHT.getValue() - 205;
        final int width = Size.RyCON_WIDGET_WIDTH.getValue() - 205;

        GridLayout gridLayout = new GridLayout(1, true);

        innerShell = new Shell(parent, SWT.CLOSE | SWT.SHELL_TRIM);
        innerShell.addListener(SWT.Close, event -> actionBtnEscape());
        innerShell.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.about));
        innerShell.setSize(width, height);

        GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, true);

        Label imageLabel = new Label(innerShell, SWT.CENTER);
        imageLabel.setImage(new ImageConverter().convertToImage(Display.getCurrent(), Image.aboutIcon.getPath()));
        imageLabel.setLayoutData(gridData);

        // get app name and version for bold line
        final String appName = ResourceBundleUtils.getLangStringFromXml(LANG_STRING, LangStrings.application_Name);
        final String version = Version.getVersion();
        final String ryconLabelText = appName + Separator.WHITESPACE.getSign() + version;

        Label ryconLabel = new Label(innerShell, SWT.NONE);
        ryconLabel.setText(ryconLabelText);

        FontData fontData = ryconLabel.getFont().getFontData()[0];
        font = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
        ryconLabel.setFont(font);

        // get build number and build date
        Label buildLabel = new Label(innerShell, SWT.NONE);
        buildLabel.setText(Version.getBuildString());

        innerShell.setLayout(gridLayout);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        //innerShell.pack();
        innerShell.open();
    }

    private void actionBtnEscape() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        innerShell.dispose();
        font.dispose();
    }

}
