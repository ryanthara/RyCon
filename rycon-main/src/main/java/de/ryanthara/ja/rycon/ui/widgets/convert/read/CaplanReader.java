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

import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Texts;
import de.ryanthara.ja.rycon.nio.LineReader;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.widgets.ConverterWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.ERROR;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXT;

/**
 * A reader for reading Caplan K files in the {@link ConverterWidget} of RyCON.
 *
 * @author sebastian
 * @version 2
 * @since 12
 */
public class CaplanReader extends Reader {

    private static final Logger logger = LoggerFactory.getLogger(CaplanReader.class.getName());

    private final Shell innerShell;
    private List<String> lines;

    /**
     * Constructs a new reader with a reference to the shell of the calling object.
     *
     * @param innerShell reference to the inner shell
     */
    public CaplanReader(Shell innerShell) {
        this.innerShell = innerShell;
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
     * Reads the Caplan K file given as parameter and returns the read file success.
     *
     * @param file2Read read file reference
     * @return read file success
     */
    @Override
    public boolean readFile(Path file2Read) {
        LineReader lineReader = new LineReader(file2Read);

        if (lineReader.readFile(false)) {
            lines = lineReader.getLines();

            return true;
        } else {
            logger.warn("Caplan K file {} could not be read.", file2Read.toString());

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_ERROR,
                    ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.msgBox_Error),
                    ResourceBundleUtils.getLangString(ERROR, Errors.caplanReadingFailed));

            return false;
        }
    }

}
