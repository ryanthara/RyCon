/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.widget.convert
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
package de.ryanthara.ja.rycon.ui.widgets.convert.read;

import com.opencsv.CSVReader;
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.ERRORS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;

/**
 * Instances of this class are used for reading coordinate files (CSV format) from the geodata server
 * Kanton Basel Stadt (Switzerland) from the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class BaselStadtCsvReader implements Reader {

    private static final Logger logger = LoggerFactory.getLogger(BaselStadtCsvReader.class.getName());

    private List<String[]> readCSVFile;
    private final Shell innerShell;

    /**
     * Constructs a new instance of this class given a reference to the inner shell of the calling object.
     *
     * @param innerShell reference to the inner shell
     */
    public BaselStadtCsvReader(Shell innerShell) {
        this.innerShell = innerShell;
    }

    /**
     * Returns the reader CSV lines as {@link List}.
     * * <p>
     * This method is used vice versa with the method {@link #getReadStringLines()}. The one which is not used,
     * returns null for indication.
     *
     * @return reader CSV lines
     */
    @Override
    public List<String[]> getReadCSVFile() {
        return readCSVFile;
    }

    /**
     * Returns the reader string lines as {@link ArrayList}.
     * <p>
     * This method is used vice versa with the method {@link #getReadCSVFile()}. The one which is not used,
     * returns null for indication.
     *
     * @return reader string lines
     */
    @Override
    // TODO correct return null
    public ArrayList<String> getReadStringLines() {
        return null;
    }

    /**
     * Reads the coordinate file in CSV format from the geodata server Basel Stadt (Switzerland) given as parameter
     * and returns the read file success.
     *
     * @param file2Read read file reference
     *
     * @return read file success
     */
    @Override
    public boolean readFile(Path file2Read) {
        boolean success = false;

        try {
            CSVReader reader = new CSVReader(Files.newBufferedReader(file2Read, Charset.forName("ISO-8859-1")), ';', '"', 0); // do not skip first line!
            readCSVFile = reader.readAll();

            success = true;
        } catch (IOException e) {
            logger.error("Basel Stadt csv file '{}' could not be read.", file2Read.toString());

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.csvBSReadingFailed));
        }

        return success;
    }

} // end of BaselStadtCsvReader
