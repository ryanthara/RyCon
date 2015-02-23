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

package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.Main;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * This class holds all the update functionality for RyCON.
 * <p>
 * This class checks the RyCON website (URL 'http://code.ryanthar.de/RyCON') for a new version.
 * 
 * @version 1
 * @since 3
 * Created by sebastian on 23.02.15.
 */
public class Updater {

    /**
     * Member that indicates an avaible update.
     */
    private boolean updateAvailable = false;

    /**
     * Member that holds the update URL as String.
     */
    private final String url;

    /**
     * Class constructor with the update URL as parameter.
     *
     * @param url URL as String
     */
    public Updater(String url) {
        this.url = url;
    }

    /**
     * Performs the check of the RyCON update website.
     *  
     * @return success
     */
    public boolean checkForUpdate() {
        boolean success = false;

        try {
            URL updateUrl = new URL(url);

            Scanner scanner = new Scanner(updateUrl.openStream());
            
            scanner.next();
            double version = Double.parseDouble(scanner.next());

            scanner.next();
            int build = scanner.nextInt();
            
            scanner.close();
            
            success = true;

            if ((Integer.parseInt(Main.getRyCONBuild().substring(0, 1)) < build) || 
                    (Double.parseDouble(Main.getRyCONVersion()) < version)) {
                updateAvailable = true;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    /**
     * Returns true if an update is available.
     * <p>
     * The check for an update is done in {@see checkForUpdate}
     *
     * @return true if an update is available
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
    
}
