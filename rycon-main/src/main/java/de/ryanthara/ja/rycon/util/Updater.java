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

package de.ryanthara.ja.rycon.util;

import de.ryanthara.ja.rycon.data.ApplicationKey;
import de.ryanthara.ja.rycon.data.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles the update functionality for RyCON.
 * <p>
 * Therefore it checks the RyCON website (URL 'https://code.ryanthara.de/RyCON')
 * for a new RyCON version.
 *
 * @author sebastian
 * @version 5
 * @since 3
 */
public class Updater {

    private static final Logger logger = LoggerFactory.getLogger(Updater.class.getName());

    private boolean isUpdateAvailable = false;

    /**
     * Default constructor which init the logging handler.
     */
    public Updater() {
    }

    /**
     * Performs the check of the RyCON update website.
     * <p>
     * Due to JAVA's SSL implementations and it's constrained to store the key in the public keychain,
     * this is a 'hack' to bypass the ssl check easily. This should be done better in a future version.
     *
     * @return success
     */
    // TODO correct return null
    public boolean checkForUpdate() {
        TrustManager[] trustAllCerts = getTrustManagers();
        activateTrustManager(trustAllCerts);

        try {
            URL updateUrl = new URL(ApplicationKey.RyCON_UPDATE_URL.getValue());
            HttpsURLConnection huc = (HttpsURLConnection) updateUrl.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();

            if (huc.getResponseCode() == 200) { // document found on server
                if (huc.getContentLength() > 0) {
                    Scanner scanner = new Scanner(updateUrl.openStream(), StandardCharsets.UTF_8);

                    scanner.next();
                    String majorMinor = scanner.next();

                    String[] segments = majorMinor.split(Pattern.quote("."));
                    short majorRelease = StringUtils.parseShort(segments[0]);
                    short minorRelease = StringUtils.parseShort(segments[1]);
                    short patchLevel = StringUtils.parseShort(segments[2]);

                    scanner.next();
                    short build = scanner.nextShort();

                    scanner.next();
                    String buildDate = scanner.next();

                    int update = 0;

                    update = parseDate(buildDate, update);

                    scanner.close();

                    if (majorRelease > Version.getMajorRelease()) {
                        isUpdateAvailable = true;
                    } else if (majorRelease == Version.getMajorRelease() && minorRelease > Version.getMinorRelease()) {
                        isUpdateAvailable = true;
                    } else if (majorRelease == Version.getMajorRelease() && minorRelease == Version.getMinorRelease()
                            && patchLevel > Version.getPatchLevel()) {
                        isUpdateAvailable = true;
                    } else if (build > Version.getBuildNumber()) {
                        isUpdateAvailable = true;
                    } else if (update < 0) {
                        isUpdateAvailable = true;
                    }

                    return true;
                }
            } else if (huc.getResponseCode() == 404) { // document not found on server
                logger.warn("Can not found the 'what's new document on server. Error 404");
            }
        } catch (MalformedURLException e) {
            logger.warn("Update failed because of a wrong URL format.");
        } catch (IOException e) {
            logger.warn("IOException caused from no active internet connection.");
        }

        return false;
    }

    private int parseDate(String buildDate, int update) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date programDate = sdf.parse(Version.getBuildDate());
            Date releaseDate = sdf.parse(buildDate);

            update = programDate.compareTo(releaseDate);
        } catch (ParseException e) {
            logger.error("Can not parse the date string '{}'.", buildDate);
        }
        return update;
    }

    private void activateTrustManager(TrustManager... trustAllCerts) {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            logger.warn("Can not activate the trust manager for the ssl connection to www.ryanthara.de", e.getCause());
        }
    }

    private TrustManager[] getTrustManagers() {
        /*
         * In the context of a fast and simple solution,
         * we decided to use a simple approach
         * to achieve a secure connection,
         * by accepting all certificates.
         */
        return new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
    }

    /**
     * Picks the latest news from a text file on the RyCON website and return the content as {@code String}.
     *
     * @return latest news from the update site
     */
    public String getWhatsNew() {
        StringBuilder builder = new StringBuilder();

        try {
            // TODO add ssl connection here
            URL whatsNewURL = new URL(ApplicationKey.RyCON_WHATS_NEW_URL.getValue());
            BufferedReader in = new BufferedReader(new InputStreamReader(whatsNewURL.openStream(), StandardCharsets.UTF_8));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            logger.warn("Can not get the 'what's new document.", e.getCause());
        }

        return builder.toString();
    }

    /**
     * Returns true if an update is available.
     *
     * <p>
     * The check for an update is done in {@code checkForUpdate()}.
     *
     * @return true if an update is available
     */
    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

}
