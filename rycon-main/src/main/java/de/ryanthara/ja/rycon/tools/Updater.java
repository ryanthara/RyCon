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

import de.ryanthara.ja.rycon.data.DefaultKeys;
import de.ryanthara.ja.rycon.data.Version;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
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
 * Updater holds all the update functionality for RyCON.
 * <p>
 * This class checks the RyCON website (URL 'https://code.ryanthara.de/RyCON') for a new RyCON version.
 * <p>
 * <h3>Changes:</h3>
 * <ul>
 * <li>5: patch level support implemented </li>
 * <li>4: ssl check implemented </li>
 * <li>3: clean up and improvements </li>
 * <li>2: basic improvements </li>
 * <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 5
 * @since 3
 */
public class Updater {

    private boolean updateAvailable = false;

    /**
     * Perform the check of the RyCON update website.
     * <p>
     * Due to JAVA's SSL implementations and it's constrained to store the key in the public keychain,
     * this is a 'hack' to bypass the ssl check easily. This should be done better in a future version.
     *
     * @return success
     */
    // TODO correct return null
    public boolean checkForUpdate() {
        boolean success = false;

        // Create a new trust manager that trust all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.err.println("Can't activate the trust manager for the ssl connection to www.ryanthara.de");
            e.printStackTrace();
        }

        try {
            URL updateUrl = new URL(DefaultKeys.RyCON_UPDATE_URL.getValue());
            URLConnection con = updateUrl.openConnection();

            if (con.getContentLength() > 0) {
                Scanner scanner = new Scanner(updateUrl.openStream());

                scanner.next();
                String majorMinor = scanner.next();

                String[] segments = majorMinor.split(Pattern.quote("."));
                short majorRelease = Short.parseShort(segments[0]);
                short minorRelease = Short.parseShort(segments[1]);
                short patchLevel = Short.parseShort(segments[2]);

                scanner.next();
                short build = scanner.nextShort();

                scanner.next();
                String buildDate = scanner.next();

                int update = 0;

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date programDate = sdf.parse(Version.getBuildDate());
                    Date releaseDate = sdf.parse(buildDate);

                    update = programDate.compareTo(releaseDate);
                } catch (ParseException e) {
                    System.err.println("Date String can't be parsed.");
                    e.printStackTrace();
                }
                scanner.close();

                if (majorRelease > Version.getMajorRelease()) {
                    updateAvailable = true;
                } else if (majorRelease == Version.getMajorRelease() && minorRelease > Version.getMinorRelease()) {
                    updateAvailable = true;
                } else if (majorRelease == Version.getMajorRelease() && minorRelease == Version.getMinorRelease()
                        && patchLevel > Version.getPatchLevel()) {
                    updateAvailable = true;
                } else if (build > Version.getBuildNumber()) {
                    updateAvailable = true;
                } else if (update < 0) {
                    updateAvailable = true;
                }
                success = true;
            } else {
                System.err.println("Online check failed. Please check your network settings");
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

    /**
     * Pick the latest news from a text file on the RyCON website and return the content as {code String}.
     *
     * @return latest news from the update site
     */
    public String getWhatsNew() {
        StringBuilder builder = new StringBuilder();

        try {
            URL whatsNewURL = new URL(DefaultKeys.RyCON_WHATS_NEW_URL.getValue());
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
     * Return true if an update is available.
     * <p>
     * The check for an update is done in {@see checkForUpdate}
     *
     * @return true if an update is available
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

} // end of Updater
