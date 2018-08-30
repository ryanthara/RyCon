/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.zfex.gui
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

/**
 * The {@code Images} enumeration holds the paths to all used icons or images of <tt>RyCON</tt>.
 * <p>
 * This enumeration is used for encapsulating the data.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum Images {

    btnAbout("/de/ryanthara/ja/rycon/ui/icons/btn_about.png"),
    btnClean("/de/ryanthara/ja/rycon/ui/icons/btn_clean.png"),
    btnConvert("/de/ryanthara/ja/rycon/ui/icons/btn_convert.png"),
    btnCopy("/de/ryanthara/ja/rycon/ui/icons/btn_copy.png"),
    btnExit("/de/ryanthara/ja/rycon/ui/icons/btn_exit.png"),
    btnLevel("/de/ryanthara/ja/rycon/ui/icons/btn_level.png"),
    btnPrint("/de/ryanthara/ja/rycon/ui/icons/btn_printer.png"),
    btnProject("/de/ryanthara/ja/rycon/ui/icons/btn_project.png"),
    btnReport("/de/ryanthara/ja/rycon/ui/icons/btn_report.png"),
    btnSettings("/de/ryanthara/ja/rycon/ui/icons/btn_settings.png"),
    btnSplit("/de/ryanthara/ja/rycon/ui/icons/btn_code.png"),
    btnTransformation("/de/ryanthara/ja/rycon/ui/icons/btn_transformation.png"),

    iconError("/de/ryanthara/ja/rycon/ui/icons/10-error.png"),
    iconOK("/de/ryanthara/ja/rycon/ui/icons/20-ok.png"),
    iconWarning("/de/ryanthara/ja/rycon/ui/icons/15-warning.png"),

    aboutIcon("/de/ryanthara/ja/rycon/ui/RyCON_blank128x128.png"),
    splashScreen("/de/ryanthara/ja/rycon/ui/RyCON_SplashScreen.png"),
    taskIcon("/de/ryanthara/ja/rycon/ui/RyCON_blank256x256.png"),
    trayIcon64("/de/ryanthara/ja/rycon/ui/RyCON_TrayIcon64x64.png"),

    btnEditorDefault("/de/ryanthara/ja/rycon/ui/icons/default.png"),
    btnEditorUndo("/de/ryanthara/ja/rycon/ui/icons/undo.png");

    private final String path;

    Images(String path) {
        this.path = path;
    }

    /**
     * Returns the image path as string.
     * @return image path
     */
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }

} // end of Images
