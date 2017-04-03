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
package de.ryanthara.ja.rycon.gui.widget;

import de.ryanthara.ja.rycon.Main;
import org.eclipse.swt.widgets.Shell;

import static de.ryanthara.ja.rycon.gui.custom.Status.OK;

/**
 * Instances of this class implements a complete widget and it's functionality.
 * <p>
 * With the TransferWidget of RyCON it is possible to transfer different files from an card reader or folder mounted
 * card reader into a give project structure on the file system. The source structure and the target structure can be
 * configured flexible.
 *
 * @author sebastian
 * @version 8
 * @since 1
 */
public class TransferWidget {

    private Shell innerShell;

    /**
     * Constructs the {@link TransferWidget} without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public TransferWidget() {
        innerShell = null;

        //initUI();
    }

    private void actionBtnCancel() {
        Main.setSubShellStatus(false);
        Main.statusBar.setStatus("", OK);
        innerShell.dispose();
    }

    private int actionBtnOk() {
        return 0;
    }

    /*
     * This method is used from the class BottomButtonBar!
     */
    private void actionBtnOkAndExit() {
        if (actionBtnOk() == 1) {
            Main.setSubShellStatus(false);
            Main.statusBar.setStatus("", OK);

            innerShell.dispose();
        }
    }

} // end of TransferWidget
