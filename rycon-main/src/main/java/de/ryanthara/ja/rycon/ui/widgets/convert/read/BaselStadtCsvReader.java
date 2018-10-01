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

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Texts;
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
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXTS;

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
    private final Shell innerShell;
    private List<String[]> readCsvFile;

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
    public List<String[]> getReadCsvFile() {
        return readCsvFile;
    }

    /**
     * Returns the reader string lines as {@link ArrayList}.
     * <p>
     * This method is used vice versa with the method {@link #getReadCsvFile()}. The one which is not used,
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
     * @return read file success
     */
    @Override
    public boolean readFile(Path file2Read) {
        boolean success = false;

        try {
            /* This should be the preferred method of creating a Reader as there are so many possible values to be set it is
             * impossible to have constructors for all of them and keep backwards compatibility with previous constructors.
             */
            final CSVParser parser =
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .withIgnoreQuotations(true)
                            .build();
            final CSVReader reader =
                    new CSVReaderBuilder(Files.newBufferedReader(file2Read, Charset.forName("ISO-8859-1")))
                            .withSkipLines(0)
                            .withCSVParser(parser)
                            .build();

            readCsvFile = reader.readAll();

            success = true;
        } catch (IOException e) {
            logger.error("Basel Stadt CSV file '{}' could not be read.", file2Read.toString());

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangStringFromXml(TEXTS, Texts.msgBox_Error),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.csvBSReadingFailed));
        }

        return success;
    }

} // end of BaselStadtCsvReader
