/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.util;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code OpenInFileManager} is used to open files and folders in the default file manager
 * of the used operating system.
 * <p>
 * This is used for example after the generation of new project folders with the generation tool of {@code RyCON}.
 *
 * @author sebastian
 * @version 1
 * @since 2
 */
public class OpenInFileManager {
    private final static Logger logger = Logger.getLogger(OpenInFileManager.class.getName());

    /**
     * Opens a folder with the default file manager of the used operating system.
     *
     * @param path path to open
     */
    public static void openFolder(String path) {
        Desktop desktop = Desktop.getDesktop();

        Path pathToBeOpen = Paths.get(path);

        try {
            desktop.open(pathToBeOpen.toFile());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File " + path + " not found to open folder in file manager. \n" + e.getMessage());
        }
    }

} // end of OpenInFileManager
