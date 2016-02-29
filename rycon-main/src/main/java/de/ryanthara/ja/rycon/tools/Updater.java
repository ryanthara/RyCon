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

package de.ryanthara.ja.rycon.tools;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.Version;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * This class holds all the update functionality for RyCON.
 * <p>
 * This class checks the RyCON website (URL 'https://code.ryanthara.de/RyCON') for a new version.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>3: clean up and improvements </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 3
 */
public class Updater {

    private boolean updateAvailable = false;

    /**
     * Performs the check of the RyCON update website.
     *  
     * @return success
     */
    public boolean checkForUpdate() {
        boolean success = false;

        try {
            URL updateUrl = new URL(Main.RyCON_UPDATE_URL);
            URLConnection con = updateUrl.openConnection();

            if (con.getContentLength() > 0) {
                Scanner scanner = new Scanner(updateUrl.openStream());

                scanner.next();
                String majorMinor = scanner.next();

                String[] segments = majorMinor.split(Pattern.quote("."));
                int majorVersion = Integer.parseInt(segments[0]);
                int minorVersion = Integer.parseInt(segments[1]);

                scanner.next();
                int build = scanner.nextInt();

                scanner.next();
                String date = scanner.next();

                System.out.println(date);
                System.out.println(Version.getBuildDate());

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date programDate = sdf.parse(Version.getBuildDate());
                    Date releaseDate = sdf.parse(date);

                    System.out.println("The local version is older than the online version");
                    System.out.println(programDate.compareTo(releaseDate));

                } catch (ParseException e) {
                    System.err.println("Date String can't be parsed.");
                    e.printStackTrace();
                }
                scanner.close();

                success = true;

                if (Version.getBuildNumber() < build ||
                        (majorVersion < Version.getMajorVersionNumber() && minorVersion < Version.getMinorVersionNumber())) {
                    updateAvailable = true;
                }
            } else {
                System.out.println("Online check failed. Please check your network settings");
            }
        } catch (MalformedURLException e) {
            System.err.println("checkForUpdate() failed: MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("checkForUpdate() failed: IOException");
            e.printStackTrace();
        }

        return success;
    }

    public boolean checkForUpdate2() {
        try {
            String httpsURL = Main.RyCON_UPDATE_URL;
            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                System.out.println(inputLine);
            }

            in.close();
        } catch (Exception e) {
            System.out.println("ERROR");
            System.out.println(e.toString());
        }

        return true;
    }

    /**
     * Picks the latest news from the RyCON website.
     *
     * @return latest news from the update site
     */
    public String getWhatsNew() {
        StringBuilder builder = new StringBuilder();
        
        try {
            URL whatsNewURL = new URL(Main.RyCON_WHATS_NEW_URL);
            BufferedReader in = new BufferedReader(new InputStreamReader(whatsNewURL.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }
            in.close();

        } catch (IOException e) {
            System.err.println("getWhatsNew() failed");
            e.printStackTrace();
        }

        return builder.toString();
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
