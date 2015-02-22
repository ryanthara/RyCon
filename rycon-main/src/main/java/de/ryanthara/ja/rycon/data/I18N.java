/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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

package de.ryanthara.ja.rycon.data;

import de.ryanthara.ja.rycon.tools.Messages;

/**
 * This class implements multi-language support to RyCON.
 * <p>
 * In the first version of RyCON there are support for the following languages.
 * <ul>
 *     <li>english - since version 1
 *     <li>german - since version 1
 *     <li>...
 * </ul>
 * <p>
 * Every translated text in the program is prepared as <code>String</code> object here.
 * The singular and plural words are controlled with parameters.
 * <p>
 * Because of the fact that there are a lot of user who has java version 6 or 7
 * running, RyCON uses no functions of java version 8 in versions lower than 2.
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
public class I18N {

    public static String getApplicationTitle() {
        return Messages.getString("applicationTitle");
    }

    public static String getBtnCancelLabel() {
        return Messages.getString("btnCancel");
    }

    public static String getBtnCancelLabelToolTip() {
        return Messages.getString("btnCancelToolTip");
    }

    public static String getBtnChkConverterCSVSemiColonDelimiter() {
        return Messages.getString("btnChkConverterCSVSemiColonDelimiter");
    }

    public static String getBtnChkConverterTXTSpaceDelimiter() {
        return Messages.getString("btnChkConverterTXTSpaceDelimiter");
    }

    public static String getBtnChkConverterWriteCommentLine() {
        return Messages.getString("btnChkConverterWriteCommentLine");
    }

    public static String getBtnChkLevellingIgnoreChangePoints() {
        return Messages.getString("btnChkLevellingIgnoreChangePoints");
    }

    public static String getBtnChkSplitterIgnoreCodeColumn() {
        return Messages.getString("btnChkSplitterIgnoreCodeColumn");
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

    public static String getBtnChooseFile() {
        return Messages.getString("btnChooseFile");
    }

    public static String getBtnChooseFileToolTip() {
        return Messages.getString("btnChooseFileToolTip");
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

    public static String getBtnChoosePaths() {
        return Messages.getString("btnChoosePaths");
    }

    public static String getBtnChoosePathsToolTip() {
        return Messages.getString("btnChoosePathsToolTip");
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

    public static String getBtnOKAndOpenLabel() {
        return Messages.getString("btnOKAndOpen");
    }

    public static String getBtnOKAndOpenLabelToolTip() {
        return Messages.getString("btnOKAndOpenToolTip");
    }

    public static String getBtnOKLabel() {
        return Messages.getString("btnOK");
    }

    public static String getBtnOKLabelToolTip() {
        return Messages.getString("btnOKToolTip");
    }

    public static String getBtnSettingsLabel() {
        return Messages.getString("btnSettings");
    }

    public static String getBtnSettingsLabelToolTip() {
        return Messages.getString("btnSettingsToolTip");
    }

    public static String getBtnSplitterLabel() {
        return Messages.getString("btnSplitter");
    }

    public static String getBtnSplitterLabelToolTip() {
        return Messages.getString("btnSplitterToolTip");
    }

    public static String getErrorTextJavaVersion() {
        return Messages.getString("errorTextJavaVersion");
    }

    public static String getErrorTitleJavaVersion() {
        return Messages.getString("errorTitleJavaVersion");
    }

    public static String getFileChooserDirBaseMessage() {
        return Messages.getString("fileChooserPathDefaultMessage");
    }

    public static String getFileChooserDirBaseTitle() {
        return Messages.getString("fileChooserPathDefaultTitle");
    }

    public static String getFileChooserDirJobsMessage() {
        return Messages.getString("fileChooserFolderJobMessage");
    }

    public static String getFileChooserDirJobsTemplateMessage() {
        return Messages.getString("fileChooserFolderJobDefaultMessage");
    }

    public static String getFileChooserDirJobsTemplateTitle() {
        return Messages.getString("fileChooserFolderJobDefaultTitle");
    }

    public static String getFileChooserDirJobsTitle() {
        return Messages.getString("fileChooserFolderJobTitle");
    }

    public static String getFileChooserDirProjectTemplateMessage() {
        return Messages.getString("fileChooserFolderProjectDefaultMessage");
    }

    public static String getFileChooserDirProjectTemplateTitle() {
        return Messages.getString("fileChooserFolderProjectDefaultTitle");
    }

    public static String getFileChooserDirProjectsMessage() {
        return Messages.getString("fileChooserFolderProjectMessage");
    }

    public static String getFileChooserDirProjectsTitle() {
        return Messages.getString("fileChooserFolderProjectTitle");
    }

    public static String getFileChooserFilterNameCSV() {
        return Messages.getString("fileChooserFilterNameCSV");
    }

    public static String getFileChooserFilterNameGSI() {
        return Messages.getString("fileChooserFilterNameGSI");
    }

    public static String getFileChooserFilterNameGSIAndTXT() {
        return Messages.getString("fileChooserFilterNameGSIAndTXT");
    }

    public static String getFileChooserFilterNameTXT() {
        return Messages.getString("fileChooserFilterNameTXT");
    }

    public static String getFileChooserLevellingSourceText() {
        return Messages.getString("fileChooserLevellingSourceText");
    }

    public static String getFileChooserSplitterSourceText() {
        return Messages.getString("fileChooserSplitterSourceText");
    }

    public static String getFileChooserTidyUpSourceText() {
        return Messages.getString("fileChooserTidyUpSourceText");
    }

    public static String getGroupTitleNumberInput() {
        return Messages.getString("groupTitleNumberInput");
    }

    public static String getGroupTitleNumberInputAdvice() {
        return Messages.getString("groupTitleNumberInputAdvice");
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

    public static String getLabelDestination() {
        return Messages.getString("labelDestination");
    }

    public static String getLabelDirBase() {
        return Messages.getString("labelDirBase");
    }

    public static String getLabelDirJobs() {
        return Messages.getString("labelJobPath");
    }

    public static String getLabelDirJobsTemplate() {
        return Messages.getString("labelJobPathDefaultFolder");
    }

    public static String getLabelDirProjects() {
        return Messages.getString("labelProjectPath");
    }

    public static String getLabelDirProjectsTemplate() {
        return Messages.getString("labelProjectPathDefaultFolder");
    }

    public static String getLabelIdentifierFreeStation() {
        return Messages.getString("labelIdentifierFreeStation");
    }

    public static String getLabelIdentifierStakeOutPoint() {
        return Messages.getString("labelIdentifierStakeOutPoint");
    }

    public static String getLabelIdentifierStation() {
        return Messages.getString("labelIdentifierStation");
    }

    public static String getLabelJobAndProjectNumber() {
        return Messages.getString("labelJobAndProjectNumber");
    }

    public static String getLabelSource() {
        return Messages.getString("labelSource");
    }

    public static String getLabelTipConverterWidget() {
        return Messages.getString("labelTipConverterWidget");
    }

    public static String getLabelTipGeneratorWidget() {
        return Messages.getString("labelTipGeneratorWidgetNumber");
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

    public static String getLicenseMsgDemo() {
        return Messages.getString("licenseMsgDemo");
    }

    public static String getLicenseMsgFull() {
        return Messages.getString("licenseMsgFull");
    }

    public static String getLicenseTitleDemo() {
        return Messages.getString("licenseTitleDemo");
    }

    public static String getLicenseTitleFull() {
        return Messages.getString("licenseTitleFull");
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

    public static String getMsgConvertReaderCSVFailed() {
        return Messages.getString("msgConvertReaderCSVFailed");
    }

    public static String getMsgConvertReaderFailed() {
        return Messages.getString("msgConvertReaderFailed");
    }

    public static String getMsgConvertSuccess(boolean singular) {
        return prepareString("msgConvertSuccess", singular);
    }

    public static String getMsgCreateDirJobAndProjectExist() {
        return Messages.getString("msgCreateDirJobAndProjectExist");
    }

    public static String getMsgCreateDirJobAndProjectGenerated() {
        return Messages.getString("msgCreateDirJobAndProjectGenerated");
    }

    public static String getMsgCreateDirJobAndProjectWarning() {
        return Messages.getString("msgCreateDirJobAndProjectWarning");
    }

    public static String getMsgCreateDirJobExist() {
        return Messages.getString("msgCreateDirJobExist");
    }

    public static String getMsgCreateDirProjectExist() {
        return Messages.getString("msgCreateDirProjectExist");
    }

    public static String getMsgDirBaseNotFound() {
        return Messages.getString("msgDirBaseNotFound");
    }

    public static String getMsgDirDestinationNotExistWarning() {
        return Messages.getString("msgDirDestinationNotExistWarning");
    }

    public static String getMsgDirJobDefaultNotFound() {
        return Messages.getString("msgDirJobDefaultNotFound");
    }

    public static String getMsgDirJobNotFound() {
        return Messages.getString("msgDirJobNotFound");
    }

    public static String getMsgDirProjectDefaultNotFound() {
        return Messages.getString("msgDirProjectDefaultNotFound");
    }

    public static String getMsgDirProjectNotFound() {
        return Messages.getString("msgDirProjectNotFound");
    }

    public static String getMsgEmptyTextFieldWarning() {
        return Messages.getString("msgEmptyTextFieldWarning");
    }

    public static String getMsgEmptyTextFieldWarningJobNumber() {
        return Messages.getString("msgEmptyTextFieldWarningJobNumber");
    }

    public static String getMsgFileExist() {
        return Messages.getString("msgFileExist");
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

    public static String getMsgTidyUpSuccess(boolean singular) {
        return prepareString("msgTidyUpSuccess", singular);
    }

    public static String getStatus1CleanInitialized() {
        return Messages.getString("status1CleanInitialized");
    }

    public static String getStatus2SplitterInitialized() {
        return Messages.getString("status2SplitterInitialized");
    }

    public static String getStatus3LevelInitialized() {
        return Messages.getString("status3LevelInitialized");
    }

    public static String getStatus4ConverterInitialized() {
        return Messages.getString("status4ConverterInitialized");
    }

    public static String getStatus5GeneratorInitialized() {
        return Messages.getString("status5GeneratorInitialized");
    }

    public static String getStatus6ExitInitialized() {
        return Messages.getString("status6ExitInitialized");
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

    public static String getStatusJobAndProjectGenerated() {
        return Messages.getString("statusJobAndProjectGenerated");
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

    public static String getTrayMenuItemExit() {
        return Messages.getString("trayMenuItemExit");
    }

    public static String getTrayMenuItemHelp() {
        return Messages.getString("trayMenuItemHelp");
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

    /**
     * Helper for string preparation to differ between singular and plural text.
     * <p>
     * In the properties file the String "§§" is used as delimiter. The values
     * for singular is set with {@code Main.TEXT_SINGULAR} and for plural is
     * set with {@code Main.TEXT_PLURAL}
     *
     * @param property property to get the text from
     * @param singular set to get a singular or plurar text back
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

    public static String getBtnDefaultSettings() {
        return Messages.getString("btnDefaultSettings");
    }

    public static String getBtnDefaultSettingsToolTip() {
        return Messages.getString("btnDefaultSettingsToolTip");
    }
} // end of I18N
