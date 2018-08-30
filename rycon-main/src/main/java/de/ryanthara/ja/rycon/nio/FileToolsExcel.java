/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.tools
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
package de.ryanthara.ja.rycon.nio;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.nio.file.Path;

/**
 * Instances of this class provides basic file operations to writer Microsoft xls, xlsx, ... files.
 *
 * @author sebastian
 * @version 2
 * @since 9
 */
class FileToolsExcel {

    private static final Logger logger = LoggerFactory.getLogger(FileToolsExcel.class.getName());

    private final Workbook workbook;

    /**
     * Constructs a new instance of this class given a {@link Workbook} object for writing the filled table
     * to the file system.
     *
     * @param workbook {@link Workbook} object
     */
    FileToolsExcel(Workbook workbook) {
        this.workbook = workbook;
    }

    /**
     * Writes the converted XLS file to the file system.
     *
     * @param writeFile file to be written
     *
     * @return success writer success
     */
    boolean writeXls(Path writeFile) {
        try (FileOutputStream fileOut = new FileOutputStream(writeFile.toFile())) {
            workbook.write(fileOut);

            fileOut.close();
            return true;
        } catch (Exception e) {
            logger.error("Unable to save Microsoft Excel XLS Spreadsheet file '{}' to disk.", writeFile.toString(), e.getCause());
        }

        return false;
    }

    /**
     * Writes the converted XLSX file to the file system.
     *
     * @param writeFile path to be written
     *
     * @return success writer success
     */
    boolean writeXlsx(Path writeFile) {
        boolean writeSuccess = false;

        try (FileOutputStream fileOut = new FileOutputStream(writeFile.toFile())) {
            workbook.write(fileOut);

            fileOut.close();
            writeSuccess = true;
        } catch (Exception e) {
            logger.error("Unable to save Microsoft Excel XLSX Spreadsheet file '{}' to disk.", writeFile.toString(), e.getCause());
        }

        return writeSuccess;
    }

} // end of FileToolsExcel
