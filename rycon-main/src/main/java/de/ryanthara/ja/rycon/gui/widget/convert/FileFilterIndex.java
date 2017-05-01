/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.widget
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
package de.ryanthara.ja.rycon.gui.widget.convert;

import de.ryanthara.ja.rycon.gui.widget.ConverterWidget;
import de.ryanthara.ja.rycon.i18n.FileChoosers;

import java.util.ArrayList;

/**
 * This enumeration is used for file filter indices in the {@link ConverterWidget}.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum FileFilterIndex {

    GSI("*.gsi", FileChoosers.getString("filterNameGSI")),
    TXT("*.txt", FileChoosers.getString("filterNameTXT")),
    CSV("*.csv", FileChoosers.getString("filterNameCSV")),
    K("*.K", FileChoosers.getString("filterNameK")),
    DAT("*.dat", FileChoosers.getString("filterNameCadwork")),
    REC("*.REC", FileChoosers.getString("filterNameZeiss"));

    private final String extension;
    private final String filterName;

    FileFilterIndex(String extension, String filterName) {
        this.extension = extension;
        this.filterName = filterName;
    }

    /**
     * Returns the {@link FileFilterIndex} from index parameter as static access from switch cases.
     *
     * @param index index to return
     *
     * @return FileFilterIndex by index
     */
    public static FileFilterIndex fromIndex(int index) {
        FileFilterIndex selectedFileFilterIndex = null;

        for (FileFilterIndex fileFilterIndex : values()) {
            if (fileFilterIndex.ordinal() == index) {
                selectedFileFilterIndex = fileFilterIndex;
            }
        }

        return selectedFileFilterIndex;
    }

    /**
     * Returns all stored file filter extensions as one string array.
     *
     * @return file filter extensions
     */
    public String[] getExtensionsArray() {
        ArrayList<String> list = new ArrayList<>();

        for (FileFilterIndex index : FileFilterIndex.values()) {
            list.add(index.getExtension());
        }

        return list.toArray(new String[0]);
    }

    /**
     * Return all stored file filter names as one string array.
     *
     * @return file filter names
     */
    public String[] getFilterNamesArray() {
        ArrayList<String> list = new ArrayList<>();

        for (FileFilterIndex index : FileFilterIndex.values()) {
            list.add(index.getFilterName());
        }

        return list.toArray(new String[0]);
    }

    /**
     * Returns the file extension string.
     *
     * @return the file extension
     */
    String getExtension() {
        return extension;
    }

    /**
     * Returns the file filter name as string.
     *
     * @return file filter name
     */
    String getFilterName() {
        return filterName;
    }

} // end of FileFilterIndex
