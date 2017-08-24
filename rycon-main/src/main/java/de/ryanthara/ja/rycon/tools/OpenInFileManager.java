/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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
package de.ryanthara.ja.rycon.tools;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * The <tt>OpenInFileManager</tt> is used to open files and folders in the file managers of the used os.
 * <p>
 * This is used for example after the generation of new project folders with the generation tool of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 1
 * @since 2
 */
public class OpenInFileManager {

    /**
     * Opens a folder with the default file manager of the os.
     *
     * @param path path to open
     */
    public static void openFolder(String path) {
        Desktop desktop = Desktop.getDesktop();

        File dirToOpen;

        try {
            dirToOpen = new File(path);
            desktop.open(dirToOpen);
        } catch (IOException e) {
            System.err.println("File " + path + " not found to open folder in file manager. \n" + e.getMessage());
        }

    }

} // end of OpenInFileManager
