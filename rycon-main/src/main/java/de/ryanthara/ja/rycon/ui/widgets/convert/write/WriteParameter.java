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
package de.ryanthara.ja.rycon.ui.widgets.convert.write;

import de.ryanthara.ja.rycon.core.converter.zeiss.ZeissDialect;

/**
 * Instances of this class holds parameter for the writing classes in
 * the package {@link de.ryanthara.ja.rycon.ui.widgets.convert.write}
 * and returns them with getter methods.
 */
public class WriteParameter {

    private final boolean isGSI16;
    private final boolean cadworkUseZeroHeights;
    private final boolean kFormatUseSimpleFormat;
    private final boolean ltopEliminateDuplicatePoints;
    private final boolean sortOutputFileByNumber;
    private final boolean sourceContainsCode;
    private final boolean writeCodeColumn;
    private final boolean writeCommentLine;
    private final boolean writeZeroHeights;
    private final int sourceNumber;
    private final String separatorCSV;
    private final String separatorTXT;
    private final ZeissDialect dialect;

    /**
     * Constructs a new instance of this class with a couple of parameters.
     *
     * @param sourceNumber                 number of the source button
     * @param isGSI16                      true if is GSI16 format
     * @param cadworkUseZeroHeights        use zero heights from cadwork
     * @param kFormatUseSimpleFormat       use simple K format
     * @param ltopEliminateDuplicatePoints eliminate duplicate points
     * @param sortOutputFileByNumber       sort output file by number option
     * @param sourceContainsCode           true if source file contains code column
     * @param writeCodeColumn              writer code column to the output file
     * @param writeCommentLine             writer comment line to the output file
     * @param writeZeroHeights             write zero heights to the output file
     * @param separatorCSV                 CSV separator sign
     * @param separatorTXT                 text files separator sign
     * @param dialect                      Zeiss REC format dialect
     */
    public WriteParameter(int sourceNumber,
                          boolean isGSI16,
                          boolean cadworkUseZeroHeights,
                          boolean kFormatUseSimpleFormat,
                          boolean ltopEliminateDuplicatePoints,
                          boolean sortOutputFileByNumber,
                          boolean sourceContainsCode,
                          boolean writeCodeColumn,
                          boolean writeCommentLine,
                          boolean writeZeroHeights,
                          String separatorCSV,
                          String separatorTXT,
                          ZeissDialect dialect) {
        this.sourceNumber = sourceNumber;
        this.isGSI16 = isGSI16;
        this.cadworkUseZeroHeights = cadworkUseZeroHeights;
        this.kFormatUseSimpleFormat = kFormatUseSimpleFormat;
        this.ltopEliminateDuplicatePoints = ltopEliminateDuplicatePoints;
        this.sortOutputFileByNumber = sortOutputFileByNumber;
        this.sourceContainsCode = sourceContainsCode;
        this.writeCodeColumn = writeCodeColumn;
        this.writeCommentLine = writeCommentLine;
        this.writeZeroHeights = writeZeroHeights;
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
     * Returns the source button number.
     *
     * @return source button number
     */
    public int getSourceNumber() {
        return sourceNumber;
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
     * Returns write zero heights (0.000 metre) for missing height coordinates.
     *
     * @return write zero heights
     */
    boolean isWriteZeroHeights() {
        return writeZeroHeights;
    }

    /**
     * Returns the separator sign for comma separated value (CSV) files.
     *
     * @return the csv separator sign
     */
    String getSeparatorCSV() {
        return separatorCSV;
    }

    /**
     * Returns the separator sign for text files.
     *
     * @return the txt separator sign
     */
    String getSeparatorTXT() {
        return separatorTXT;
    }

    /**
     * Returns the cadwork use zero heights option.
     *
     * @return cadwork use zero heights
     */
    boolean isCadworkUseZeroHeights() {
        return cadworkUseZeroHeights;
    }

    /**
     * Returns use simple format.
     *
     * @return use simple format.
     */
    boolean isKFormatUseSimpleFormat() {
        return kFormatUseSimpleFormat;
    }

    /**
     * Returns remove duplicate points in LTOP KOO files.
     *
     * @return LTOP remove duplicate points
     */
    boolean isLtopEliminateDuplicatePoints() {
        return ltopEliminateDuplicatePoints;
    }

    /**
     * Returns sort output files ascending by point number.
     *
     * @return ascending sort output file by number
     */
    boolean isSortOutputFileByNumber() {
        return sortOutputFileByNumber;
    }

    /**
     * Returns the source file contains code column parameter.
     *
     * @return true if source file contains code
     */
    boolean sourceContainsCode() {
        return sourceContainsCode;
    }

} // end of WriteParameter
