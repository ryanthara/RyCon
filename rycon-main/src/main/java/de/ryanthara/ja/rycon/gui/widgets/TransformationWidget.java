/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.gui.widgets;

import de.ryanthara.ja.rycon.Main;

/**
 * Instances of this class provides functions to transform coordinate files between different coordinate systems
 * or reference frames. Therefore a couple of external free libraries are used.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class TransformationWidget extends AbstractWidget {

    /**
     * Constructs a new instance of this class without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public TransformationWidget() {
        initUI();
        handleFileInjection();
    }

    @Override
    void actionBtnCancel() {

    }

    @Override
    boolean actionBtnOk() {
        return false;
    }

    @Override
    void actionBtnOkAndExit() {

    }

    @Override
    void initUI() {

    }

    private void handleFileInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            System.out.println("to do...");
//            inputFieldsComposite.setSourceTextFieldText(files);
        }
    }

} // end of TransformationWidget
