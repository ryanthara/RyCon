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

import de.ryanthara.ja.rycon.i18n.Error;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Text;
import de.ryanthara.ja.rycon.nio.FileFormat;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.ERROR;
import static de.ryanthara.ja.rycon.i18n.ResourceBundle.TEXT;

/**
 * A reader for reading Toporail MEP and PTS files in the {@link ConverterWidget} of RyCON.
 *
 * <p>
 * Du to some issues of the Toporail file formats a differentiation is made with
 * the fileFormat enumeration.
 *
 * @author sebastian
 * @version 1
 * @since 25
 */
public class ToporailReader extends Reader {

    private static final Logger logger = LoggerFactory.getLogger(ToporailReader.class.getName());

    private final Shell innerShell;
    private final FileFormat fileFormat;
    private List<String> lines;

    /**
     * Constructs a new reader with a reference to the shell of
     * the calling object and the file type of the Toporail file.
     *
     * @param innerShell reference to the inner shell
     * @param fileFormat file type of the Toporail file (MEP or PTS)
     */
    public ToporailReader(Shell innerShell, FileFormat fileFormat) {
        this.innerShell = innerShell;
        this.fileFormat = fileFormat;
    }

    /**
     * Returns the read string lines as {@link List}.
     *
     * @return read string lines
     */
    @Override
    public List<String> getLines() {
        return List.copyOf(lines);
    }

    /**
     * Reads the cadwork node.dat file given as parameter and returns the read file success.
     *
     * @param file2Read read file reference
     * @return read file success
     */
    @Override
    public boolean readFile(Path file2Read) {
        LineReader lineReader = new LineReader(file2Read);

        if (lineReader.readFile(true, ":")) {
            lines = lineReader.getLines();

            return true;
        } else {
            String errorMessage;

            if (fileFormat == FileFormat.MEP) {
                errorMessage = ResourceBundleUtils.getLangString(ERROR, Error.toporailMepReadingFailed);
            } else {
                errorMessage = ResourceBundleUtils.getLangString(ERROR, Error.toporailPtsReadingFailed);
            }

            logger.warn("File '{}' could not be read.", file2Read.getFileName());

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Text.msgBox_Error), errorMessage);

            return false;
        }
    }

}
