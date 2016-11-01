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
package de.ryanthara.ja.rycon.gui.widget.convert.read;

import com.opencsv.CSVReader;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import de.ryanthara.ja.rycon.i18n.I18N;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for reading comma separated values (CSV) files from
 * the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class CSVReadFile implements ReadFile {

    private boolean useSemicolonAsSeparator;
    private List<String[]> readCSVFile;
    private Shell innerShell;

    /**
     * Constructs a new instance of this class given a reference to the inner shell of the calling object.
     *
     * @param innerShell reference to the inner shell
     */
    public CSVReadFile(Shell innerShell, boolean useSemicolonAsSeparator) {
        this.innerShell = innerShell;
        this.useSemicolonAsSeparator = useSemicolonAsSeparator;
    }

    /**
     * Returns the read CSV lines as {@link List}.
     * * <p>
     * This method is used vise versa with method {@link #getReadStringLines()}. The one which is not used,
     * returns null for indication.
     *
     * @return read CSV lines
     */
    @Override
    public List<String[]> getReadCSVFile() {
        return readCSVFile;
    }

    /**
     * Returns the read string lines as {@link ArrayList}.
     * <p>
     * This method is used vise versa with method {@link #getReadCSVFile()}. The one which is not used,
     * returns null for indication.
     *
     * @return read string lines
     */
    @Override
    public ArrayList<String> getReadStringLines() {
        return null;
    }

    /**
     * Reads the comma separeted values (CSV) file given as parameter and returns the read file success.
     *
     * @param file2Read read file reference
     *
     * @return read file success
     */
    @Override
    public boolean readFile(File file2Read) {
        boolean success = false;
        char separatorCSV = useSemicolonAsSeparator ? ';' : ',';

        // use opencsv project for reading -> could this be done better?
        try {
            CSVReader reader = new CSVReader(new FileReader(file2Read), separatorCSV);
            readCSVFile = reader.readAll();
            success = true;
        } catch (IOException e) {
            System.err.println("File " + file2Read.getName() + " could not be read.");
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR, I18N.getMsgBoxTitleError(),
                    I18N.getMsgConvertReaderCSVFailed());
        }

        return success;
    }

} // end of CSVReadFile
