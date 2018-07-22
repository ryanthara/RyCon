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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.ERRORS;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.LABELS;

/**
 * Instances of this class are used for reading 'comma separated values' (CSV) files from
 * the {@link ConverterWidget} of <tt>RyCON</tt>.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CsvReader implements Reader {

    private final static Logger logger = Logger.getLogger(CsvReader.class.getName());

    private boolean useSemicolonAsSeparator;
    private List<String[]> readCSVFile;
    private Shell innerShell;

    /**
     * Constructs a new instance of this class given a reference to the inner shell of the calling object.
     *
     * @param innerShell              reference to the inner shell
     * @param useSemicolonAsSeparator use semicolon as separator sign
     */
    public CsvReader(Shell innerShell, boolean useSemicolonAsSeparator) {
        this.innerShell = innerShell;
        this.useSemicolonAsSeparator = useSemicolonAsSeparator;
    }

    /**
     * Returns the reader CSV lines as {@link List}.
     * * <p>
     * This method is used vise versa with method {@link #getReadStringLines()}. The one which is not used,
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
     * This method is used vise versa with method {@link #getReadCSVFile()}. The one which is not used,
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
     * Reads the comma separated values (CSV) file given as parameter and returns the reader file success.
     *
     * @param file2Read reader path reference
     *
     * @return reader file success
     */
    @Override
    public boolean readFile(Path file2Read) {
        boolean success = false;
        char separatorCSV = useSemicolonAsSeparator ? ';' : ',';

        // use opencsv project for reading -> could this be done better?
        try {
            // CSVReader reader = new CSVReader(new FileReader(file2Read.toFile()), separatorCSV);
            CSVReader reader = new CSVReader(Files.newBufferedReader(file2Read, Charset.forName("ISO-8859-1")), separatorCSV);
            readCSVFile = reader.readAll();

            success = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "File " + file2Read.getFileName() + " could not be read.");

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox),
                    ResourceBundleUtils.getLangString(ERRORS, Errors.readerCSVFailed));
        }

        return success;
    }

} // end of CsvReader
