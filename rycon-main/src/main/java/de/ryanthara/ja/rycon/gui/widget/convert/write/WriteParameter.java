/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui.widget.convert.write
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
package de.ryanthara.ja.rycon.gui.widget.convert.write;

import de.ryanthara.ja.rycon.converter.zeiss.ZeissDialect;

/**
 * Instances of this class holds parameter for the writing classes in the package {@link de.ryanthara.ja.rycon.gui.widget.convert.write}
 * and
 */
public class WriteParameter {

    private boolean isGSI16, cadworkUseZeroHeights, kFormatUseSimpleFormat, ltopEliminateDuplicatePoints,
            ltopSortOutputFileByNumber, sourceContainsCode, writeCodeColumn, writeCommentLine;
    private int sourceNumber;
    private String separatorCSV, separatorTXT;
    private ZeissDialect dialect;

    /**
     * Constructs a new instance of this class with a couple of parameters.
     */
    public WriteParameter(int sourceNumber, boolean isGSI16,
                          boolean cadworkUseZeroHeights,
                          boolean kFormatUseSimpleFormat,
                          boolean ltopEliminateDuplicatePoints,
                          boolean ltopSortOutputFileByNumber,
                          boolean sourceContainsCode,
                          boolean writeCodeColumn,
                          boolean writeCommentLine,
                          String separatorCSV, String separatorTXT, ZeissDialect dialect) {
        this.sourceNumber = sourceNumber;
        this.isGSI16 = isGSI16;
        this.cadworkUseZeroHeights = cadworkUseZeroHeights;
        this.kFormatUseSimpleFormat = kFormatUseSimpleFormat;
        this.ltopEliminateDuplicatePoints = ltopEliminateDuplicatePoints;
        this.ltopSortOutputFileByNumber = ltopSortOutputFileByNumber;
        this.sourceContainsCode = sourceContainsCode;
        this.writeCodeColumn = writeCodeColumn;
        this.writeCommentLine = writeCommentLine;
        this.separatorCSV = separatorCSV;
        this.separatorTXT = separatorTXT;
        this.dialect = dialect;
    }

    /**
     * Returns the used Zeiss dialect (R4, R5, REC500 or M5).
     *
     * @return Zeiss dialect
     */
    public ZeissDialect getDialect() {
        return dialect;
    }

    /**
     * Returns the separator sign for comma separated value (CSV) files.
     *
     * @return the csv separator sign
     */
    public String getSeparatorCSV() {
        return separatorCSV;
    }

    /**
     * Returns the separator sign for text files.
     *
     * @return the txt separator sign
     */
    public String getSeparatorTXT() {
        return separatorTXT;
    }

    /**
     * Returns the source button number.
     *
     * @return source button number
     */
    public int getSourceNumber() {
        return sourceNumber;
    }

    /**
     * Returns the cadwork use zero heights option.
     *
     * @return cadwork use zero heights
     */
    public boolean isCadworkUseZeroHeights() {
        return cadworkUseZeroHeights;
    }

    /**
     * Returns the GSI indication parameter.
     *
     * @return true if is GSI16
     */
    public boolean isGSI16() {
        return isGSI16;
    }

    /**
     * Returns use simple format.
     *
     * @return use simple format.
     */
    public boolean isKFormatUseSimpleFormat() {
        return kFormatUseSimpleFormat;
    }

    /**
     * Returns remove duplicate points in LTOP KOO files.
     *
     * @return LTOP remove duplicate points
     */
    public boolean isLtopEliminateDuplicatePoints() {
        return ltopEliminateDuplicatePoints;
    }

    /**
     * Returns sort LTOP KOO files by point number.
     *
     * @return LTOP sort KOO file by number
     */
    public boolean isLtopSortOutputFileByNumber() {
        return ltopSortOutputFileByNumber;
    }

    /**
     * Returns use the code column.
     *
     * @return use code column
     */
    public boolean isWriteCodeColumn() {
        return writeCodeColumn;
    }

    /**
     * Returns write a comment line.
     *
     * @return write a comment line
     */
    public boolean isWriteCommentLine() {
        return writeCommentLine;
    }

    /**
     * Returns the source file contains code column parameter.
     *
     * @return true if source file contains code
     */
    public boolean sourceContainsCode() {
        return sourceContainsCode;
    }

} // end of WriteParameter
