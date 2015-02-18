/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.data
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

package de.ryanthara.ja.rycon.data;

import de.ryanthara.ja.rycon.Main;

import java.io.*;
import java.util.Properties;


/**
 * This class brought the functionality for handling config files to RyCON.
 * <p>
 * The less needed configuration settings of RyCON are stored with simple XML file in
 * the file system of your computer. The java {@link Properties} functionality is used
 * for this case.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: add a couple of parameters
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class Preferences {

    /**
     * Member that is used for indicating the generation of default settings.
     */
    private boolean isDefaultSettingsGenerated = false;

    /**
     * Member that is used for indicating the read file status.
     */
    private boolean isReadConfigFileFromDisk = false;

    /**
     * Member that holds the file object. This {@code File} object is used to store the
     * path and filename for the config file of RyCON. As default the file name
     * 'RyCON.config' is used and it is stored in the path of the RyCON application.
     */
    private File file = new File("." + File.separator + "RyCON.config");

    /**
     * Member for the description string of the written properties list.
     */
    private String description = "Config file created by RyCON - see: http://code.ryanthara.de/RyCON";

    /**
     * Member of the properties object.
     */
    private Properties properties = null;

    /**
     * Class constructor.
     * <p>
     * During the initialisation the program tries to read the config file from the file
     * system. If no file exist, RyCON will generate a file with default settings.
     *
     * @see #readConfigFileFromDisk()
     */
    public Preferences() {
        properties = new Properties();

        isReadConfigFileFromDisk = readConfigFileFromDisk();
    }

    /**
     * Returns the properties object in which all the settings of RyCON are stored.
     * @return properties object
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns the value of a single property by known key.
     * @param key known key as string
     * @return value of the property
     */
    public String getSingleProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns true if the config file has been read from the disk.
     * @return success
     */
    public boolean isConfigFileRead() {
        return isReadConfigFileFromDisk;
    }

    /**
     * Returns true if a file with default settings was generated.
     * @return success
     */
    public boolean isDefaultSettingsGenerated() {
        return isDefaultSettingsGenerated;
    }

    /**
     * Sets the value for defaultSettingsGenerated from outside this class.
     * @param defaultSettingsGenerated value to be set
     */
    public void setDefaultSettingsGenerated(boolean defaultSettingsGenerated) {
        isDefaultSettingsGenerated = defaultSettingsGenerated;
    }

    /**
     * Sets the complete properties back to the {@code Preferences} object.
     * @param properties properties to be set
     * @return success
     */
    public boolean setProperties(Properties properties) {
        this.properties = properties;
        return writeConfigFileToDisk(description);
    }

    /**
     * Sets a single property by known key and given value.
     * @param key key to set
     * @param value value to set
     */
    public void setSingleProperty(String key, String value) {
        properties.setProperty(key, value);
        writeConfigFileToDisk(description);
    }

    /**
     * Fills in the default settings for RyCON into an empty config file.
     * <p>
     * Default settings are generated for the following parameters (parameter name - value).
     * <ul>
     *     <li>'Generator' - 'RyCON'
     *     <li>'Build' - 'version - build date'
     *     <li>'Information' - 'information string'
     *     <li>'DirBase' - '.'
     *     <li>'DirJobs' - './jobs'
     *     <li>'DirJobsTemplate' - './jobs/template-folder'
     *     <li>'DirProjects' - './projects'
     *     <li>'DirProjectsTemplate' - './projects/template-folder'
     *     <li>'ParamControlPointString' - 'STKE'
     *     <li>'ParamFreeStationString' - 'FS'
     *     <li>'ParamStationString' - 'ST'
     * </ul>
     * <p>
     * It is <b>highly recommend</b> that the user will overwrite this settings to his preferred values
     * after the first start of RyCON.
     */
    private void createDefaultSettings() {
        // general settings
        properties.setProperty("Generator", "RyCON");
        properties.setProperty("Build", Main.getRyCONBuild());
        properties.setProperty("Information", Main.getRyCONWebsite());

        // parameters for #1
        properties.setProperty("ParamControlPointString", "STKE");
        properties.setProperty("ParamFreeStationString", "FS");
        properties.setProperty("ParamStationString", "ST");

        // paths for #5 - project generation
        properties.setProperty("DirBase", ".");
        properties.setProperty("DirJobs", "./jobs");
        properties.setProperty("DirJobsTemplate", "./jobs/template-folder");
        properties.setProperty("DirProjects", "./projects");
        properties.setProperty("DirProjectsTemplate", "./projects/template-folder");

        writeConfigFileToDisk(description);
    }

    /**
     * Tries to read a config file name with 'RyCON.config' from the file system.
     * <p>
     * If this file wouldn't exist, RyCON generates a default one with given parameters.
     *
     * @return success after reading the config file, otherwise false
     * @see #createDefaultSettings()
     */
    private boolean readConfigFileFromDisk() {

        boolean success = false;

        if (file.exists()) {
            try {
                properties.loadFromXML(new BufferedInputStream(
                        new FileInputStream(file)));
                isDefaultSettingsGenerated = false;
                success = true;
            } catch (IOException e1) {
                System.err.println("Error 1 : " + e1.toString());
            }
        } else {
            try {
                if (file.createNewFile()) {
                    createDefaultSettings();
                    isDefaultSettingsGenerated = true;

                    success = false;
                }
            } catch (IOException e2) {
                System.err.println("Error 2 : " + e2.toString());
            }
        }

        return success;
    }

    /**
     * Writes the config file to the file system.
     *
     * @param description description string for the config file
     * @return success after writing, otherwise false
     */
    private boolean writeConfigFileToDisk(String description) {

        boolean success = false;

        try {
            properties.storeToXML(new BufferedOutputStream(new FileOutputStream(file)), description, "UTF-8");
            success = true;
        } catch (IOException e) {
            System.err.println("Error 3 : " + e.toString());
        }

        return success;
    }

} // end of Preferences
