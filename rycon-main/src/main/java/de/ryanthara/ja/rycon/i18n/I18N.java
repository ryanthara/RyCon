/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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

package de.ryanthara.ja.rycon.i18n;

/**
 * I18N implements multi-language support to RyCON.
 * <p>
 * In the first version of RyCON there are support for the following languages.
 * <ul>
 * <li>english - since version 1 </li>
 * <li>german - since version 1 </li>
 * <li>... </li>
 * </ul>
 * <p>
 * Every translated text in the program is prepared as a <code>String</code> object here.
 * The singular and plural words are controlled with parameters.
 * <p>
 * Because of the fact that there are a lot of user who has java version 7
 * running, RyCON uses no functions of java version 8 in versions lower than 2.
 * <p>
 * Most of the content of this class is self explaining. Due to this there is less javadoc comment.
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
public class I18N {

    /**
     * Return the application title of RyCON.
     *
     * @return application title
     */
    public static String getApplicationTitle() {
        return Messages.getString("applicationTitle");
    }

    /**
     * Return the cancel button text.
     *
     * @return the cancel button text
     */
    public static String getBtnCancelLabel() {
        return Messages.getString("btnCancel");
    }

    /**
     * Return the cancel button tooltip text.
     *
     * @return the cancel button tooltip text
     */
    public static String getBtnCancelLabelToolTip() {
        return Messages.getString("btnCancelToolTip");
    }

    public static String getBtnChkBoxCadworkUseZeroHeights() {
        return Messages.getString("btnChkBoxCadworkUseZeroHeights");
    }

    public static String getBtnChkBoxCreateAdminFolder() {
        return Messages.getString("btnChkBoxCreateAdminFolder");
    }

    public static String getBtnChkBoxCreateBigDataFolder() {
        return Messages.getString("btnChkBoxCreateBigDataFolder");
    }

    public static String getBtnChkBoxCreateProjectFolder() {
        return Messages.getString("btnChkBoxCreateProjectFolder");
    }

    public static String getBtnChkBoxKFormatUseSimpleFormat() {
        return Messages.getString("btnChkBoxKFormatUseSimpleFormat");
    }

    public static String getBtnChkBoxLTOPEliminateDuplicatePoints() {
        return Messages.getString("btnChkBoxLTOPEliminateDuplicatePoints");
    }

    public static String getBtnChkBoxLTOPSortOutputFileByNumber() {
        return Messages.getString("btnChkBoxLTOPSortOutputFileByNumber");
    }

    public static String getBtnChkBoxLTOPUseZenithDistance() {
        return Messages.getString("btnChkBoxLTOPUseZenithDistance");
    }

    public static String getBtnChkBoxSourceContainsCode() {
        return Messages.getString("btnChkBoxSourceContainsCode");
    }

    public static String getBtnChkBoxWriteCodeColumn() {
        return Messages.getString("btnChkBoxWriteCodeColumn");
    }

    public static String getBtnChkConverterCSVSemiColonSeparator() {
        return Messages.getString("btnChkConverterCSVSemiColonSeparator");
    }

    public static String getBtnChkConverterTXTSpaceSeparator() {
        return Messages.getString("btnChkConverterTXTSpaceSeparator");
    }

    public static String getBtnChkConverterWriteCommentLine() {
        return Messages.getString("btnChkConverterWriteCommentLine");
    }

    public static String getBtnChkLevellingIgnoreChangePoints() {
        return Messages.getString("btnChkLevellingIgnoreChangePoints");
    }

    public static String getBtnChkSplitterInsertCodeColumn() {
        return Messages.getString("btnChkSplitterInsertCodeColumn");
    }

    public static String getBtnChkSplitterWriteCodeZero() {
        return Messages.getString("btnChkSplitterWriteCodeZero");
    }

    public static String getBtnChkTidyUpHoldControlPoints() {
        return Messages.getString("btnChkTidyUpHoldControlPoints");
    }

    public static String getBtnChkTidyUpHoldStations() {
        return Messages.getString("btnChkTidyUpHoldStations");
    }

    public static String getBtnChooseFiles() {
        return Messages.getString("btnChooseFiles");
    }

    public static String getBtnChooseFilesToolTip() {
        return Messages.getString("btnChooseFilesToolTip");
    }

    public static String getBtnChoosePath() {
        return Messages.getString("btnChoosePath");
    }

    public static String getBtnChoosePathToolTip() {
        return Messages.getString("btnChoosePathToolTip");
    }

    public static String getBtnCleanLabel() {
        return Messages.getString("btnClean");
    }

    public static String getBtnCleanLabelToolTip() {
        return Messages.getString("btnCleanToolTip");
    }

    public static String getBtnConvertLabel() {
        return Messages.getString("btnConvert");
    }

    public static String getBtnConvertLabelToolTip() {
        return Messages.getString("btnConvertToolTip");
    }

    public static String getBtnDefaultSettings() {
        return Messages.getString("btnDefaultSettings");
    }

    public static String getBtnDefaultSettingsToolTip() {
        return Messages.getString("btnDefaultSettingsToolTip");
    }

    public static String getBtnEliminateZeroCoordinate() {
        return Messages.getString("btnEliminateZeroCoordinate");
    }

    public static String getBtnExitLabel() {
        return Messages.getString("btnExit");
    }

    public static String getBtnExitLabelToolTip() {
        return Messages.getString("btnExitToolTip");
    }

    public static String getBtnGeneratorLabel() {
        return Messages.getString("btnGenerator");
    }

    public static String getBtnGeneratorLabelToolTip() {
        return Messages.getString("btnGeneratorToolTip");
    }

    public static String getBtnLevelingLabel() {
        return Messages.getString("btnLeveling");
    }

    public static String getBtnLevelingLabelToolTip() {
        return Messages.getString("btnLevelingToolTip");
    }

    public static String getBtnOKAndExitLabel() {
        return Messages.getString("btnOKAndExit");
    }

    public static String getBtnOKAndExitLabelToolTip() {
        return Messages.getString("btnOKAndExitToolTip");
    }

    public static String getBtnOKAndOpenBrowserLabel() {
        return Messages.getString("btnOKAndOpenBrowserLabel");
    }

    public static String getBtnOKAndOpenBrowserToolTip() {
        return Messages.getString("btnOKAndOpenBrowserToolTip");
    }

    public static String getBtnOKAndOpenLabel() {
        return Messages.getString("btnOKAndOpen");
    }

    public static String getBtnOKAndOpenLabelToolTip() {
        return Messages.getString("btnOKAndOpenToolTip");
    }

    public static String getBtnOKLabel() {
        return Messages.getString("btnOK");
    }

    public static String getBtnOKToolTip() {
        return Messages.getString("btnOKToolTip");
    }

    public static String getBtnPrintLabel() {
        return Messages.getString("btnPrintLabel");
    }

    public static String getBtnPrintLabelToolTip() {
        return Messages.getString("btnPrintLabelToolTip");
    }

    public static String getBtnSettingsLabel() {
        return Messages.getString("btnSettings");
    }

    public static String getBtnSettingsLabelToolTip() {
        return Messages.getString("btnSettingsToolTip");
    }

    public static String getBtnSettingsMainLabel() {
        return Messages.getString("btnSettingsMainLabel");
    }

    public static String getBtnSettingsMainLabelToolTip() {
        return Messages.getString("btnSettingsMainLabelToolTip");
    }

    public static String getBtnSplitterLabel() {
        return Messages.getString("btnSplitter");
    }

    public static String getBtnSplitterLabelToolTip() {
        return Messages.getString("btnSplitterToolTip");
    }

    public static String getBtnTransformationLabel() {
        return Messages.getString("btnTransformationLabel");
    }

    public static String getBtnTransformationLabelToolTip() {
        return Messages.getString("btnTransformationLabelToolTip");
    }

    public static String getBtnUseSpaceAtLineEnd() {
        return Messages.getString("btnUseSpaceAtLineEnd");
    }

    public static String getCaplanColumnTyp(String columnType) {
        return CaplanKDescription.getDescription(columnType);
    }

    public static String getErrorTextJavaVersion() {
        return Messages.getString("errorTextJavaVersion");
    }

    public static String getErrorTitleJavaVersion() {
        return Messages.getString("errorTitleJavaVersion");
    }

    public static String getFileChooserConverterSourceMessage() {
        return Messages.getString("fileChooserConverterSourceMessage");
    }

    public static String getFileChooserConverterSourceText() {
        return Messages.getString("fileChooserConverterSourceText");
    }

    public static String getFileChooserDirAdminMessage() {
        return Messages.getString("fileChooserDirAdminMessage");
    }

    public static String getFileChooserDirAdminTemplateMessage() {
        return Messages.getString("fileChooserDirAdminDefaultMessage");
    }

    public static String getFileChooserDirAdminTemplateTitle() {
        return Messages.getString("fileChooserDirAdminDefaultTitle");
    }

    public static String getFileChooserDirAdminTitle() {
        return Messages.getString("fileChooserDirAdminTitle");
    }

    public static String getFileChooserDirBaseMessage() {
        return Messages.getString("fileChooserPathDefaultMessage");
    }

    public static String getFileChooserDirBaseTitle() {
        return Messages.getString("fileChooserPathDefaultTitle");
    }

    public static String getFileChooserDirBigDataMessage() {
        return Messages.getString("fileChooserDirBigDataMessage");
    }

    public static String getFileChooserDirBigDataTemplateMessage() {
        return Messages.getString("fileChooserDirBigDataTemplateMessage");
    }

    public static String getFileChooserDirBigDataTemplateTitle() {
        return Messages.getString("fileChooserDirBigDataTemplateTitle");
    }

    public static String getFileChooserDirBigDataTitle() {
        return Messages.getString("fileChooserDirBigDataTitle");
    }

    public static String getFileChooserDirProjectMessage() {
        return Messages.getString("fileChooserDirProjectMessage");
    }

    public static String getFileChooserDirProjectTemplateMessage() {
        return Messages.getString("fileChooserDirProjectDefaultMessage");
    }

    public static String getFileChooserDirProjectTemplateTitle() {
        return Messages.getString("fileChooserDirProjectDefaultTitle");
    }

    public static String getFileChooserDirProjectTitle() {
        return Messages.getString("fileChooserDirProjectTitle");
    }

    public static String getFileChooserFilterNameCSV() {
        return Messages.getString("fileChooserFilterNameCSV");
    }

    public static String getFileChooserFilterNameCadwork() {
        return Messages.getString("fileChooserFilterNameCadwork");
    }

    public static String getFileChooserFilterNameGSI() {
        return Messages.getString("fileChooserFilterNameGSI");
    }

    public static String getFileChooserFilterNameK() {
        return Messages.getString("fileChooserFilterNameK");
    }

    public static String getFileChooserFilterNameLTOP() {
        return Messages.getString("fileChooserFilterNameLTOP");
    }

    public static String getFileChooserFilterNameNIGRA() {
        return Messages.getString("fileChooserFilterNameNIGRA");
    }

    public static String getFileChooserFilterNameTXT() {
        return Messages.getString("fileChooserFilterNameTXT");
    }

    public static String getFileChooserFilterNameZeiss() {
        return Messages.getString("fileChooserFilterNameZeiss");
    }

    public static String getFileChooserLevellingSourceMessage() {
        return Messages.getString("fileChooserLevellingSourceMessage");
    }

    public static String getFileChooserLevellingSourceText() {
        return Messages.getString("fileChooserLevellingSourceText");
    }

    public static String getFileChooserSplitterSourceMessage() {
        return Messages.getString("fileChooserSplitterSourceMessage");
    }

    public static String getFileChooserSplitterSourceText() {
        return Messages.getString("fileChooserSplitterSourceText");
    }

    public static String getFileChooserTidyUpSourceMessage() {
        return Messages.getString("fileChooserTidyUpSourceMessage");
    }

    public static String getFileChooserTidyUpSourceText() {
        return Messages.getString("fileChooserTidyUpSourceText");
    }

    public static String getGroupTitleConverterSettings() {
        return Messages.getString("groupTitleConverterSettings");
    }

    public static String getGroupTitleFormatSettings() {
        return Messages.getString("groupTitleFormatSettings");
    }

    public static String getGroupTitleGeneralSettings() {
        return Messages.getString("groupTitleGeneralSettings");
    }

    public static String getGroupTitleGeneratorNumberInput() {
        return Messages.getString("groupTitleGeneratorNumberInput");
    }

    public static String getGroupTitleGeneratorNumberInputAdvice() {
        return Messages.getString("groupTitleGeneratorNumberInputAdvice");
    }

    public static String getGroupTitleOptions() {
        return Messages.getString("groupTitleOptions");
    }

    public static String getGroupTitlePathSelection() {
        return Messages.getString("groupTitlePathSelection");
    }

    public static String getGroupTitlePathSettings() {
        return Messages.getString("groupTitleSettingsPath");
    }

    public static String getGroupTitleSourceFileFormat() {
        return Messages.getString("groupTitleSourceFileFormat");
    }

    public static String getGroupTitleTargetFileFormat() {
        return Messages.getString("groupTitleTargetFileFormat");
    }

    public static String getGroupTitleTidyUpSettings() {
        return Messages.getString("groupTitleTidyUpSettings");
    }

    public static String getInfoTextRyCONUpdate() {
        return Messages.getString("infoTextRyCONUpdate");
    }

    public static String getInfoTitleRyCONUpdate() {
        return Messages.getString("infoTitleRyCONUpdate");
    }

    public static String getLTOPCleanNoControlPointWarning() {
        return Messages.getString("ltopCleanNoControlPointWarning");
    }

    public static String getLabelTextCodeStringLabel() {
        return Messages.getString("labelTextCodeStringLabel");
    }

    public static String getLabelTextDestination() {
        return Messages.getString("labelTextDestination");
    }

    public static String getLabelTextDirAdmin() {
        return Messages.getString("labelTextAdminPath");
    }

    public static String getLabelTextDirAdminTemplate() {
        return Messages.getString("labelTextAdminPathDefaultFolder");
    }

    public static String getLabelTextDirBase() {
        return Messages.getString("labelTextDirBase");
    }

    public static String getLabelTextDirBigData() {
        return Messages.getString("labelTextDirBigData");
    }

    public static String getLabelTextDirBigDataTemplate() {
        return Messages.getString("labelTextDirBigDataTemplate");
    }

    public static String getLabelTextDirProject() {
        return Messages.getString("labelTextProjectPath");
    }

    public static String getLabelTextDirProjectTemplate() {
        return Messages.getString("labelTextProjectPathDefaultFolder");
    }

    public static String getLabelTextEditStringLabel() {
        return Messages.getString("labelTextEditStringLabel");
    }

    public static String getLabelTextIdentifierFreeStation() {
        return Messages.getString("labelTextIdentifierFreeStation");
    }

    public static String getLabelTextIdentifierLTOP() {
        return Messages.getString("labelTextIdentifierLTOP");
    }

    public static String getLabelTextIdentifierStakeOutPoint() {
        return Messages.getString("labelTextIdentifierStakeOutPoint");
    }

    public static String getLabelTextIdentifierStation() {
        return Messages.getString("labelTextIdentifierStation");
    }

    public static String getLabelTextMinimumPointDistanceLabel() {
        return Messages.getString("labelTextMinimumPointDistanceLabel");
    }

    public static String getLabelTextProjectNumber() {
        return Messages.getString("labelTextProjectNumber");
    }

    public static String getLabelTextSource() {
        return Messages.getString("labelTextSource");
    }

    public static String getLabelTextZeissRecDialect() {
        return Messages.getString("labelTextZeissRecDialect");
    }

    public static String getLabelTipConverterWidget() {
        return Messages.getString("labelTipConverterWidget");
    }

    public static String getLabelTipGeneratorWidget() {
        return Messages.getString("labelTipGeneratorWidget");
    }

    public static String getLabelTipLevellingWidget() {
        return Messages.getString("labelTipLevellingWidget");
    }

    public static String getLabelTipSplitterWidget() {
        return Messages.getString("labelTipSplitterWidget");
    }

    public static String getLabelTipTidyUpWidget() {
        return Messages.getString("labelTipTidyUpWidget");
    }

    public static String getMsgBoxTitleError() {
        return Messages.getString("msgBoxTitleError");
    }

    public static String getMsgBoxTitleSuccess() {
        return Messages.getString("msgBoxTitleSuccess");
    }

    public static String getMsgBoxTitleWarning() {
        return Messages.getString("msgBoxTitleWarning");
    }

    public static String getMsgChooseDirWarning() {
        return Messages.getString("msgWarningChooseDir");
    }

    public static String getMsgConvertFailed() {
        return Messages.getString("msgConvertFailed");
    }

    public static String getMsgConvertReaderBaselLandschaftFailed() {
        return Messages.getString("msgConvertReaderBaselLandschaftFailed");
    }

    public static String getMsgConvertReaderBaselStadtFailed() {
        return Messages.getString("msgConvertReaderBaselStadtFailed");
    }

    public static String getMsgConvertReaderCSVFailed() {
        return Messages.getString("msgConvertReaderCSVFailed");
    }

    public static String getMsgConvertReaderCadworkFailed() {
        return Messages.getString("msgConvertReaderCadworkFailed");
    }

    public static String getMsgConvertReaderCaplanFailed() {
        return Messages.getString("msgConvertReaderCaplanFailed");
    }

    public static String getMsgConvertReaderGSIFailed() {
        return Messages.getString("msgConvertReaderGSIFailed");
    }

    public static String getMsgConvertReaderTXTFailed() {
        return Messages.getString("msgConvertReaderTXTFailed");
    }

    public static String getMsgConvertReaderZeissFailed() {
        return Messages.getString("msgConvertReaderZeissFailed");
    }

    public static String getMsgConvertSuccess(boolean singular) {
        return prepareString("msgConvertSuccess", singular);
    }

    public static String getMsgCreateDirAdminExist() {
        return Messages.getString("msgCreateDirAdminExist");
    }

    public static String getMsgCreateDirAdminWarning() {
        return Messages.getString("msgCreateDirAdminWarning");
    }

    public static String getMsgCreateDirBigDataExist() {
        return Messages.getString("msgCreateDirBigDataExist");
    }

    public static String getMsgCreateDirBigDataWarning() {
        return Messages.getString("msgCreateDirBigDataWarning");
    }

    public static String getMsgCreateDirProjectExist() {
        return Messages.getString("msgCreateDirProjectExist");
    }

    public static String getMsgCreateDirProjectWarning() {
        return Messages.getString("msgCreateDirProjectWarning");
    }

    public static String getMsgDirAdminAndBigDataAndProjectGenerated() {
        return Messages.getString("msgDirAdminAndBigDataAndProjectGenerated");
    }

    public static String getMsgDirAdminAndBigDataGenerated() {
        return Messages.getString("msgDirAdminAndBigDataGenerated");
    }

    public static String getMsgDirAdminAndProjectGenerated() {
        return Messages.getString("msgDirAdminAndProjectGenerated");
    }

    public static String getMsgDirAdminDefaultNotFound() {
        return Messages.getString("msgDirAdminDefaultNotFound");
    }

    public static String getMsgDirAdminGenerated() {
        return Messages.getString("msgDirAdminGenerated");
    }

    public static String getMsgDirAdminNotFound() {
        return Messages.getString("msgDirAdminNotFound");
    }

    public static String getMsgDirBaseNotFound() {
        return Messages.getString("msgDirBaseNotFound");
    }

    public static String getMsgDirBigDataAndProjectGenerated() {
        return Messages.getString("msgDirBigDataAndProjectGenerated");
    }

    public static String getMsgDirBigDataDefaultNotFound() {
        return Messages.getString("msgDirBigDataDefaultNotFound");
    }

    public static String getMsgDirBigDataGenerated() {
        return Messages.getString("msgDirBigDataGenerated");
    }

    public static String getMsgDirBigDataNotFound() {
        return Messages.getString("msgDirBigDataNotFound");
    }

    public static String getMsgDirNotFound() {
        return Messages.getString("msgDirNotFound");
    }

    public static String getMsgDirProjectDefaultNotFound() {
        return Messages.getString("msgDirProjectDefaultNotFound");
    }

    public static String getMsgDirProjectGenerated() {
        return Messages.getString("msgDirProjectGenerated");
    }

    public static String getMsgDirProjectNotFound() {
        return Messages.getString("msgDirProjectNotFound");
    }

    public static String getMsgEmptyTextFieldWarning() {
        return Messages.getString("msgEmptyTextFieldWarning");
    }

    public static String getMsgFileExist() {
        return Messages.getString("msgErrorFileExist");
    }

    public static String getMsgFileNotExist() {
        return Messages.getString("msgErrorFileExistNot");
    }

    public static String getMsgLevellingError() {
        return Messages.getString("msgLevellingError");
    }

    public static String getMsgLevellingSuccess(boolean singular) {
        return prepareString("msgLevellingSuccess", singular);
    }

    public static String getMsgNewConfigFileGenerated() {
        return Messages.getString("msgNewConfigFileGenerated");
    }

    public static String getMsgSettingsDefaultGenerated() {
        return Messages.getString("msgSettingsDefaultGenerated");
    }

    public static String getMsgSettingsError() {
        return Messages.getString("msgSettingsError");
    }

    public static String getMsgSettingsSuccess() {
        return Messages.getString("msgSettingsSuccess");
    }

    public static String getMsgSplittingError() {
        return Messages.getString("msgSplittingError");
    }

    public static String getMsgSplittingSuccess(boolean singular) {
        return prepareString("msgSplittingSuccess", singular);
    }

    public static String getMsgTidyUpError() {
        return Messages.getString("msgTidyUpError");
    }

    public static String getMsgTidyUpSuccess(boolean singular) {
        return prepareString("msgTidyUpSuccess", singular);
    }

    public static String getStatusExitInitialized() {
        return Messages.getString("statusExitInitialized");
    }

    public static String getStatusCleanInitialized() {
        return Messages.getString("statusCleanInitialized");
    }

    public static String getStatusSplitterInitialized() {
        return Messages.getString("statusSplitterInitialized");
    }

    public static String getStatusLevelInitialized() {
        return Messages.getString("statusLevelInitialized");
    }

    public static String getStatusConverterInitialized() {
        return Messages.getString("statusConverterInitialized");
    }

    public static String getStatusGeneratorInitialized() {
        return Messages.getString("statusGeneratorInitialized");
    }

    public static String getStatusSettingsOpened() {
        return Messages.getString("statusSettingsOpened");
    }

    public static String getStatusCleanFileSuccessful(boolean singular) {
        return prepareString("statusCleanFileSuccessful", singular);
    }

    public static String getStatusCodeSplitSuccess(boolean singular) {
        return prepareString("statusCodeSplitSuccessful", singular);
    }

    public static String getStatusConvertSuccess(boolean singular) {
        return prepareString("statusConvertSuccessful", singular);
    }

    public static String getStatusFolderAdminGenerated() {
        return Messages.getString("statusFolderAdminGenerated");
    }

    public static String getStatusFolderBigDataGenerated() {
        return Messages.getString("statusFolderBigDataGenerated");
    }

    public static String getStatusFolderProjectGenerated() {
        return Messages.getString("statusFolderProjectGenerated");
    }

    public static String getStatusFoldersAdminAndBigDataAndProjectGenerated() {
        return Messages.getString("statusFoldersAdminAndBigDataAndProjectGenerated");
    }

    public static String getStatusFoldersAdminAndBigDataGenerated() {
        return Messages.getString("statusFoldersAdminAndBigDataGenerated");
    }

    public static String getStatusFoldersAdminAndProjectGenerated() {
        return Messages.getString("statusFoldersAdminAndProjectGenerated");
    }

    public static String getStatusFoldersBigDataAndProjectGenerated() {
        return Messages.getString("statusFoldersBigDataAndProjectGenerated");
    }

    public static String getStatusPrepareLevelSuccess(boolean singular) {
        return prepareString("statusLevellingFilesGenerated", singular);
    }

    public static String getStatusRyCONInitialized() {
        return Messages.getString("statusRyCONInitialized");
    }

    public static String getStatusSettingsSaved() {
        return Messages.getString("statusRyCONSettingsSaved");
    }

    public static String getStrCaplanCommentLine() {
        return Messages.getString("strCaplanCommentLine");
    }

    public static String getStrLTOPCommentLine() {
        return Messages.getString("strLTOPCommentLine");
    }

    public static String getTextDirAdminAndBigDataAndProjectGenerated() {
        return Messages.getString("textDirAdminAndBigDataAndProjectGenerated");
    }

    public static String getTextDirAdminAndBigDataGenerated() {
        return Messages.getString("textDirAdminAndBigDataGenerated");
    }

    public static String getTextDirAdminAndProjectGenerated() {
        return Messages.getString("textDirAdminAndProjectGenerated");
    }

    public static String getTextDirAdminGenerated() {
        return Messages.getString("textDirAdminGenerated");
    }

    public static String getTextDirBigDataAndProjectGenerated() {
        return Messages.getString("textDirBigDataAndProjectGenerated");
    }

    public static String getTextDirBigDataGenerated() {
        return Messages.getString("textDirBigDataGenerated");
    }

    public static String getTextDirProjectGenerated() {
        return Messages.getString("textDirProjectGenerated");
    }

    public static String getTrayMenuItemExit() {
        return Messages.getString("trayMenuItemExit");
    }

    public static String getTrayMenuItemHelp() {
        return Messages.getString("trayMenuItemHelp");
    }

    public static String getTrayMenuItemInfo() {
        return Messages.getString("trayMenuItemInfo");
    }

    public static String getTrayMenuItemSettings() {
        return Messages.getString("trayMenuItemSettings");
    }

    public static String getTrayMenuItemWebsite() {
        return Messages.getString("trayMenuItemWebsite");
    }

    public static String getWidgetTitleConverter() {
        return Messages.getString("widgetTitleConverter");
    }

    public static String getWidgetTitleGenerator() {
        return Messages.getString("widgetTitleGenerator");
    }

    public static String getWidgetTitleGeneratorSettings() {
        return Messages.getString("widgetTitleGeneratorSettings");
    }

    public static String getWidgetTitleLevelling() {
        return Messages.getString("widgetTitleLevelling");
    }

    public static String getWidgetTitleSettingsWidget() {
        return Messages.getString("widgetTitleSettings");
    }

    public static String getWidgetTitleSplitter() {
        return Messages.getString("widgetTitleSplitter");
    }

    public static String getWidgetTitleTidyUp() {
        return Messages.getString("widgetTitleTidyUp");
    }

    public static String getWordIndexDescription(int wordIndex) {
        return GSIWordIndices.getWordIndexDescription(wordIndex);
    }

    /**
     * Helper for string preparation to differ between singular and plural text.
     * <p>
     * In the properties file the String "§§" is used as separator. The values
     * for singular is set with {@code Main.TEXT_SINGULAR} and for plural is
     * set with {@code Main.TEXT_PLURAL}
     *
     * @param property property to get the text from
     * @param singular set to get a singular or plural text back
     *
     * @return singular or plural string message
     */
    private static String prepareString(String property, boolean singular) {
        String[] s = Messages.getString(property).split("§§");
        if (singular) {
            return s[0];
        } else {
            return s[1];
        }
    }

    public static String getBtnCopyLabel() {
        return Messages.getString("btnCopyLabel");
    }

    public static String getBtnCopyLabelToolTip() {
        return Messages.getString("btnCopyToolTip");
    }
} // end of I18N
