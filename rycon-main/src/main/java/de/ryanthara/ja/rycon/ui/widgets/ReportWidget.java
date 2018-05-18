/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.gui
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
package de.ryanthara.ja.rycon.ui.widgets;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.i18n.Errors;
import de.ryanthara.ja.rycon.i18n.Labels;
import de.ryanthara.ja.rycon.i18n.Messages;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.util.StringUtils;
import de.ryanthara.ja.rycon.util.check.PathCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import java.nio.file.Path;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;

/**
 * Instances of this class provides functions to analyze log files to show an overview sheet
 * with the results of a stake out or reference line measurement.
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public class ReportWidget extends AbstractWidget {

    private final static Logger logger = Logger.getLogger(ReportWidget.class.getName());

    private final String[] acceptableFileSuffixes = {"*.txt"};
    private Shell parent;
    private Path[] files2read;
    private Shell innerShell;

    /**
     * Constructs a new instance of this class without parameters.
     *
     * @param parent parent shell
     */
    public ReportWidget(final Shell parent) {
        this.parent = parent;

        files2read = new Path[0];
        innerShell = null;

        initUI();
        handleFileInjection();
    }

    /**
     * Class constructor with a file array as parameter. This constructor type
     * is used for the drag and drop injection.
     * <p>
     * The file array of the dropped files will be checked for being valid and not being a directory.
     *
     * @param droppedFiles {@link Path} array from drop source
     */
    public ReportWidget(Path... droppedFiles) {
        files2read = PathCheck.getValidFiles(droppedFiles, acceptableFileSuffixes);
        innerShell = null;
    }

    /**
     * Execute the drop action as injection.
     * <p>
     * The file processing will be done without a graphical user interface
     * and the result is only shown on the status bar.
     */
    public void executeDropInjection() {
        if ((files2read != null) && (files2read.length > 0)) {
            if (processFileOperationsDND()) {
                updateStatus();
            }
        }
    }

    private boolean processFileOperationsDND() {
        int counter = fileOperations();

        if (counter > 0) {
            // set the counter for status bar information
            Main.countFileOps = counter;
            return true;
        } else {
            return false;
        }
    }

    @Override
    void actionBtnCancel() {

    }

    @Override
    boolean actionBtnOk() {
        return false;
    }

    @Override
    void actionBtnOkAndExit() {

    }

    @Override
    void initUI() {

    }

    private int fileOperations() {


        return 0;
    }

    private void handleFileInjection() {
        String files = Main.getCLIInputFiles();

        if (files != null) {
            System.out.println("you may analyze this");
//            inputFieldsComposite.setSourceTextFieldText(files);
        }
    }

    private boolean processFileOperations() {
        boolean success;

        final int counter = fileOperations();

        if (counter > 0) {
            String message;

            final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.reportMessage);

            if (counter == 1) {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), counter);
            } else {
                message = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), counter);
            }

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_INFORMATION,
                    ResourceBundleUtils.getLangString(LABELS, Labels.successTextMsgBox), message);

            // set the counter for status bar information
            Main.countFileOps = counter;
            success = true;
        } else {
            final String message = ResourceBundleUtils.getLangString(ERRORS, Errors.reportFailed);

            MessageBoxes.showMessageBox(innerShell, SWT.ICON_WARNING,
                    ResourceBundleUtils.getLangString(LABELS, Labels.errorTextMsgBox), message);

            success = false;
        }

        return success;
    }

    private void updateStatus() {
        String status;

        final String helper = ResourceBundleUtils.getLangString(MESSAGES, Messages.reportStatus);

        // use counter to display different text on the status bar
        if (Main.countFileOps == 1) {
            status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_SINGULAR), Main.countFileOps);
        } else {
            status = String.format(StringUtils.singularPluralMessage(helper, Main.TEXT_PLURAL), Main.countFileOps);
        }

        Main.statusBar.setStatus(status, OK);
    }

} // end of ReportWidget
