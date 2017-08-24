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

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.gui.Sizes;
import de.ryanthara.ja.rycon.gui.custom.BottomButtonBar;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.tools.ShellPositioner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.gui.custom.Status.OK;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;

/**
 * The <tt>PreferencesDialog</tt> is the dialog to change all the preferences of <tt>RyCON</tt>.
 * <p>
 * With version 2 of <tt>RyCON</tt> the need for more preferences of the modules is fulfilled with this dialog.
 * It uses a tabbed structure for different modules and try to provide a clear view on the changeable preferences.
 * <p>
 * The idea to this are inspired by preference dialogs of different applications, like Eclipse, IntelliJ IDEA,
 * AutoCAD and some github stuff like swtpreferences from prasser.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class PreferencesDialog {

    private final static Logger logger = Logger.getLogger(PreferencesDialog.class.getName());

    private Shell innerShell;

    public PreferencesDialog() {
        initUI();
    }

    private void actionBtnCancel() {
        Main.pref.setDefaultSettingsGenerated(false);
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);

        widgetDispose();
    }

    private void initUI() {
        final int height = Sizes.RyCON_WIDGET_HEIGHT.getValue();
        final int width = Sizes.RyCON_WIDGET_WIDTH.getValue() + 205;

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.marginHeight = 5;
        gridLayout.marginWidth = 5;

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
        gridData.heightHint = height;
        gridData.widthHint = width;

        innerShell = new Shell(Main.shell, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.MAX | SWT.TITLE | SWT.APPLICATION_MODAL);
        innerShell.addListener(SWT.Close, event -> actionBtnCancel());
        innerShell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.settingsText));
        innerShell.setSize(width, height);

        innerShell.setLayout(gridLayout);
        innerShell.setLayoutData(gridData);

        final TabFolder tabFolder = new TabFolder(innerShell, SWT.BORDER);

        // add preference tabs here
        final ConverterPreferenceTab converterPreferenceTab = new ConverterPreferenceTab(tabFolder);
        final GeneralPreferenceTab generalPreferenceTab = new GeneralPreferenceTab(tabFolder);
        final PathPreferenceTab pathPreferenceTab = new PathPreferenceTab(tabFolder);
        final TidyUpPreferenceTab tidyUpPreferenceTab = new TidyUpPreferenceTab(tabFolder);

        addTabItem(tabFolder, generalPreferenceTab, SWT.NONE);
        addTabItem(tabFolder, pathPreferenceTab, SWT.NONE);
        // addTabItem(tabFolder, transferPreferenceTab, SWT.NONE);
        addTabItem(tabFolder, tidyUpPreferenceTab, SWT.NONE);
        // addTabItem(tabFolder, codeSplitterPreferenceTab, SWT.NONE);
        // addTabItem(tabFolder, levellingPreferenceTab, SWT.NONE);
        addTabItem(tabFolder, converterPreferenceTab, SWT.NONE);
        // addTabItem(tabFolder, transformerPreferenceTab, SWT.NONE);
        // addTabItem(tabFolder, printerPreferenceTab, SWT.NONE);

        tabFolder.pack();

        new BottomButtonBar(this, innerShell, BottomButtonBar.NO_OK_AND_EXIT_BUTTON);

        innerShell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(innerShell));

        Main.setSubShellStatus(true);

        innerShell.pack();
        innerShell.open();
    }

    private TabItem addTabItem(TabFolder tabFolder, PreferenceTab preferenceTab, int i) {
        TabItem tabItem = new TabItem(tabFolder, i);
        tabItem.setControl(preferenceTab);
        tabItem.setText(preferenceTab.getText());
        tabItem.setToolTipText(preferenceTab.getToolTipText());

        return tabItem;
    }

    private void widgetDispose() {
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    private boolean actionBtnOk() {
        System.out.println("OK hit");
        return false;
    }

} // end of PreferencesDialog
