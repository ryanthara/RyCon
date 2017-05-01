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

import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.io.LineReader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class are used for reading Caplan K files from the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CaplanReadFile implements ReadFile {

    private ArrayList<String> readStringFile;
    private Shell innerShell;

    /**
     * Constructs a new instance of this class given a reference to the inner shell of the calling object.
     *
     * @param innerShell reference to the inner shell
     */
    public CaplanReadFile(Shell innerShell) {
        this.innerShell = innerShell;
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
        return null;
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
        return readStringFile;
    }

    /**
     * Reads the Caplan K file given as parameter and returns the read file success.
     *
     * @param file2Read read file reference
     *
     * @return read file success
     */
    @Override
    public boolean readFile(Path file2Read) {
        boolean success = false;

        LineReader lineReader = new LineReader(file2Read);

        if (lineReader.readFile()) {
            if ((readStringFile = lineReader.getLines()) != null) {
                success = true;
            }
        } else {
            System.err.println("File " + file2Read.getFileName() + " could not be read.");
            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR, Labels.getString("errorTextMsgBox"),
                    Errors.getString("caplanReadingFailed"));

        }

        return success;
    }

} // end of CaplanReadFile
