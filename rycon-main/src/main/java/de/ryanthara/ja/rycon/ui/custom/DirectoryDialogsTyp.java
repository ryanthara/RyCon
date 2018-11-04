/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.custom
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
package de.ryanthara.ja.rycon.ui.custom;

import de.ryanthara.ja.rycon.i18n.FileChooser;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;

import static de.ryanthara.ja.rycon.i18n.ResourceBundle.FILECHOOSER;

/**
 * The {@code DirectoryDialogsTyp} enumeration holds the directory dialog text
 * and message strings for RyCON.
 * <p>
 * This enumeration is used for encapsulating the data.
 *
 * @author sebastian
 * @version 1
 * @since 2.0
 */
public enum DirectoryDialogsTyp {

    DIR_ADMIN(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirAdminMessage),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirAdminText)),
    DIR_ADMIN_TEMPLATE(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirAdminTemplateMessage),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirAdminTemplateText)),
    DIR_BASE(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirBaseMessage),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirBaseText)),
    DIR_BIG_DATA(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirBigDataMessage),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirBigDataText)),
    DIR_BIG_DATA_TEMPLATE(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirBigDataTemplateMessage),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirBigDataTemplateText)),
    DIR_CARD_READER(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.cardReaderText),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.cardReaderMessage)),
    DIR_GENERAL(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.generalText),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.generalMessage)),
    DIR_PROJECT(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirProjectMessage),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirProjectText)),
    DIR_PROJECT_TEMPLATE(
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirProjectTemplateMessage),
            ResourceBundleUtils.getLangString(FILECHOOSER, FileChooser.dirProjectTemplateText));

    private final String message;
    private final String text;

    DirectoryDialogsTyp(String message, String text) {
        this.message = message;
        this.text = text;
    }

    /**
     * Returns the message string of the directory dialog.
     *
     * @return message string
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the text string of the directory dialog.
     *
     * @return text string
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the {@link DirectoryDialogsTyp} as string.
     *
     * @return DirectoryDialogsTyp
     */
    @Override
    public String toString() {
        return super.toString();
    }

}