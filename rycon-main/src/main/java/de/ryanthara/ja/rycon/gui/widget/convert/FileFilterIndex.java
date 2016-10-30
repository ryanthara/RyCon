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
import de.ryanthara.ja.rycon.i18n.I18N;

import java.util.ArrayList;

/**
 * This enumeration is used for file filter indices in the {@link ConverterWidget}.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum FileFilterIndex {

    GSI(0, "*.gsi", I18N.getFileChooserFilterNameGSI()),
    TXT(1, "*.txt", I18N.getFileChooserFilterNameTXT()),
    CSV(2, "*.csv", I18N.getFileChooserFilterNameCSV()),
    K(3, "*.K", I18N.getFileChooserFilterNameK()),
    DAT(4, "*.dat", I18N.getFileChooserFilterNameCadwork()),
    REC(5, "*.REC", I18N.getFileChooserFilterNameZeiss());

    private final int index;
    private final String extension;
    private final String filterName;

    FileFilterIndex(int index, String extension, String filterName) {
        this.index = index;
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
        for (FileFilterIndex fileFilterIndex : values()) {
            if (fileFilterIndex.index == index) {
                return fileFilterIndex;
            }
        }
        return null;
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
     * Returns the file filter name as string.
     *
     * @return file filter name
     */
    String getFilterName() {
        return filterName;
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
     * Returns the index.
     *
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

} // end of FileFilterIndex
