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
import java.util.List;
import java.util.Objects;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.ERROR;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXT;

/**
 * A reader for reading comma separated values (CSV) files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CsvReader extends Reader {

    private static final Logger logger = LoggerFactory.getLogger(CsvReader.class.getName());

    private final boolean useSemicolonAsSeparator;
    private final Shell innerShell;
    private List<String[]> csv;

    /**
     * Constructs a new reader with a reference to the shell of the calling object.
     *
     * @param innerShell    reference to the inner shell
     * @param readParameter grab the use semicolon as separator sign from the read parameter helper
     */
    public CsvReader(Shell innerShell, ReadParameter readParameter) {
        this.innerShell = innerShell;
        this.useSemicolonAsSeparator = readParameter.isUseSemicolonSeparator();
    }

    /**
     * Returns the read string lines as {@link List}.
     *
     * @return read string lines
     */
    @Override
    public List<String[]> getCsv() {
        return List.copyOf(csv);
    }

    /**
     * Reads the comma separated values (CSV) file given as parameter and returns the read file success.
     *
     * @param file2Read read path reference
     * @return read file success
     */
    @Override
    public boolean readFile(Path file2Read) {
        Objects.requireNonNull(file2Read, "path must not be null");

        char separatorCSV = useSemicolonAsSeparator ? ';' : ',';

        try {
            /* This should be the preferred method of creating a Reader as there are so many possible values to be set it is
             * impossible to have constructors for all of them and keep backwards compatibility with previous constructors.
             */
            final CSVParser parser =
                    new CSVParserBuilder()
                            .withSeparator(separatorCSV)
                            .withIgnoreQuotations(true)
                            .build();
            final CSVReader reader =
                    new CSVReaderBuilder(Files.newBufferedReader(file2Read, Charset.forName("ISO-8859-1")))
                            .withSkipLines(0)
                            .withCSVParser(parser)
                            .build();

            csv = reader.readAll();

            return true;
        } catch (IOException e) {
            logger.error("Comma separated values file '{}' could not be read.", file2Read.toString());

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.msgBox_Error),
                    ResourceBundleUtils.getLangString(ERROR, Errors.readerCsvFailed));

            return false;
        }

    }

}
