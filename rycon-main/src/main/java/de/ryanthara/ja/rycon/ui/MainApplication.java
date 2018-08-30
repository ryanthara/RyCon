/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
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
package de.ryanthara.ja.rycon.ui;

import com.swisstopo.geodesy.reframe_lib.IReframe;
import com.swisstopo.geodesy.reframe_lib.Reframe;
import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.DefaultKeys;
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.ui.cocoa.CocoaUIEnhancer;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.custom.StatusBar;
import de.ryanthara.ja.rycon.ui.image.ImageConverter;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.widgets.*;
import de.ryanthara.ja.rycon.util.check.PathCheck;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.prefs.BackingStoreException;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.MainApplicationDnDButtons.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;
import static de.ryanthara.ja.rycon.ui.custom.Status.WARNING;

/**
 * Instances of this class are the main application of RyCON.
 * <p>
 * This class initializes the main window of <tt>RyCON</tt> and setup the
 * background functionality which is done by the extension of the
 * {@code Main} class.
 *
 * @author sebastian
 * @version 8
 * @see Main
 * @since 1
 */
public class MainApplication extends Main {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class.getName());
    private boolean firstStart;
    private int operations;
    private Display display;
    private Shell shell;
    private FileTransfer fileTransfer;
    private Transfer[] types;

    /**
     * Constructs a new instance of this class without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public MainApplication() {
        this.fileTransfer = null;
        this.firstStart = true;
        this.operations = Integer.MIN_VALUE;

        initUI();
    }

    /*
     * Sets the default logging level of <tt>RyCON</tt> to 'SEVERE'.
     * <p>
     * Different logging levels can be set with the command line interface.
     * @see de.ryanthara.ja.rycon.cli.CmdLineInterfaceParser
     */
    private static void initLogging() {
        // TODO implement init logging
    }

    /**
     * Main application startup.
     *
     * @param args command line arguments
     */
    public static void main(String... args) {
        initLogging();
        checkCommandLineInterfaceArguments(args);
        checkJavaVersion();
        checkRyCONVersion();
        initApplicationPreferences();

        // to provide illegal thread access -> https://github.com/udoprog/c10t-swt/issues/1
        // add -XstartOnFirstThread as a java option on VM parameter on OS X
        new MainApplication();
    }

    private void about() {
        new AboutWidget(shell);
    }

    private void actionBtn00() {
        quit();
    }

    private void actionBtn01() {
        new GeneratorWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.generatorInitialized), OK);
    }

    private void actionBtn02() {
        Path cardReaderPath = Paths.get(Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER));

        if (PathCheck.directoryExists(cardReaderPath)) {
            try {
                if (PathCheck.directoryContainsSubfolder(cardReaderPath, 1)) {
                    new TransferWidget(shell);
                    statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.transferInitialized), OK);
                } else {
                    MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING,
                            ResourceBundleUtils.getLangString(WARNINGS, Warnings.noCardReaderExistsText),
                            ResourceBundleUtils.getLangString(WARNINGS, Warnings.noCardReaderExistsMessage));
                }
            } catch (IOException e) {
                logger.warn("Card reader path '{}' doesn't contains subfolder for level 1", cardReaderPath.toString());
            }
        }
    }

    private void actionBtn03() {
        new ClearUpWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.clearUpInitialized), OK);
    }

    private void actionBtn04() {
        new CodeSplitterWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.splitFilesInitialized), OK);
    }

    private void actionBtn05() {
        new LevellingWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.levellingInitialized), OK);
    }

    private void actionBtn06() {
        new ConverterWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.converterInitialized), OK);
    }

    private void actionBtn07() {
        new AnalyzerWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.reportInitialized), OK);
    }

    private void actionBtn08() {
        new TransformationWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.transformationInitialized), OK);
    }

    private void actionBtn09() {
        MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING, "Warning", "Not implemented yet.");
        new PrinterWidget(shell);
        statusBar.setStatus("not implemented yet.", WARNING);
    }

    private void actionBtn11() {
        new AboutWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.aboutInitialized), OK);
    }

    private void actionBtnP() {
        new SettingsWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.settingsInitialized), OK);
    }

    private void addKeyBoardInputFilter(final Shell shell) {
        display.addFilter(SWT.KeyDown, event -> {
            if (!getSubShellStatus()) {
                switch (event.keyCode) {
                    case '1':
                        actionBtn01();
                        break;

                    case '2':
                        actionBtn02();
                        break;

                    case '3':
                        actionBtn03();
                        break;

                    case '4':
                        actionBtn04();
                        break;

                    case '5':
                        actionBtn05();
                        break;

                    case '6':
                        actionBtn06();
                        break;

                    case '7':
                        actionBtn07();
                        break;

                    case '8':
                        actionBtn08();
                        break;

                    case '9':
                        actionBtn09();
                        break;

                    case '0':
                        actionBtn00();
                        break;

                    case 'a':   // about, english version
                        actionBtn11();
                        break;

                    case 'c':
                        shell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(shell));
                        break;

                    case 'p':
                        actionBtnP();
                        break;

                    case 'q':
                        PreferenceHandler.removeOldKeys1();
                        break;

                    case 'w':
                        PreferenceHandler.removeOldKeys2();
                        break;

                    case 't':
                        // test();
                        testReframJar();
                        break;

                    case 'ü':   // about, german version
                        actionBtn11();
                        break;

                }
            }
        });
    }

    private void createButtonAbout(Composite composite) {
        Button btnAbout = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnAbout.setImage(new ImageConverter().convertToImage(display, Images.btnAbout.getPath()));
        btnAbout.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.aboutText));
        btnAbout.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.aboutToolTip));

        btnAbout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn11();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnAbout.setLayoutData(gridData);
    }

    private void createButtonCleanTool(Composite composite) {
        Button btnToolboxClean = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxClean.setImage(new ImageConverter().convertToImage(display, Images.btnClean.getPath()));
        btnToolboxClean.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.cleanText));
        btnToolboxClean.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.cleanToolTip));

        btnToolboxClean.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn03();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxClean.setLayoutData(gridData);

        handleDropTarget(btnToolboxClean, CLEAN);
    }

    private void createButtonConvertTool(Composite composite) {
        Button btnToolboxConvert = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxConvert.setImage(new ImageConverter().convertToImage(display, Images.btnConvert.getPath()));
        btnToolboxConvert.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.convertText));
        btnToolboxConvert.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.convertToolTip));

        btnToolboxConvert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn06();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxConvert.setLayoutData(gridData);
    }

    private void createButtonCopyTool(Composite composite) {
        Button btnToolboxCopyTool = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxCopyTool.setImage(new ImageConverter().convertToImage(display, Images.btnCopy.getPath()));
        btnToolboxCopyTool.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.importText));
        btnToolboxCopyTool.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.importToolTip));

        btnToolboxCopyTool.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn02();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxCopyTool.setLayoutData(gridData);
    }

    private void createButtonExit(Composite composite) {
        Button btnExit = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnExit.setImage(new ImageConverter().convertToImage(display, Images.btnExit.getPath()));
        btnExit.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.exitText));
        btnExit.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.exitToolTip));

        btnExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn00();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnExit.setLayoutData(gridData);
    }

    private void createButtonLevelTool(Composite composite) {
        Button btnToolboxLeveling = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxLeveling.setImage(new ImageConverter().convertToImage(display, Images.btnLevel.getPath()));
        btnToolboxLeveling.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.levellingText));
        btnToolboxLeveling.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.levellingToolTip));

        btnToolboxLeveling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn05();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxLeveling.setLayoutData(gridData);

        handleDropTarget(btnToolboxLeveling, LEVELLING);
    }

    private void createButtonPrintTool(Composite composite) {
        Button btnPrint = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnPrint.setImage(new ImageConverter().convertToImage(display, Images.btnPrint.getPath()));
        btnPrint.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.printText));
        btnPrint.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.printToolTip));

        btnPrint.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn08();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnPrint.setLayoutData(gridData);

        btnPrint.setEnabled(false);
    }

    private void createButtonProject(Composite composite) {
        Button btnToolboxProject = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxProject.setImage(new ImageConverter().convertToImage(display, Images.btnProject.getPath()));
        btnToolboxProject.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.generatorText));
        btnToolboxProject.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.generatorToolTip));

        btnToolboxProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn01();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxProject.setLayoutData(gridData);
    }

    private void createButtonReport(Composite composite) {
        Button btnToolboxReport = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxReport.setImage(new ImageConverter().convertToImage(display, Images.btnReport.getPath()));
        btnToolboxReport.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.reportText));
        btnToolboxReport.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.reportToolTip));

        btnToolboxReport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn07();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxReport.setLayoutData(gridData);

        handleDropTarget(btnToolboxReport, ANALYZE);
    }

    private void createButtonSettingsTool(Composite composite) {
        Button btnSettings = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnSettings.setImage(new ImageConverter().convertToImage(display, Images.btnSettings.getPath()));
        btnSettings.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.settingsText));
        btnSettings.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.settingsToolTip));

        btnSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnP();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnSettings.setLayoutData(gridData);
    }

    private void createButtonSplitTool(Composite composite) {
        Button btnToolboxSplitter = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxSplitter.setImage(new ImageConverter().convertToImage(display, Images.btnSplit.getPath()));
        btnToolboxSplitter.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.splitterText));
        btnToolboxSplitter.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.splitterToolTip));

        btnToolboxSplitter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn04();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxSplitter.setLayoutData(gridData);

        handleDropTarget(btnToolboxSplitter, SPLIT);
    }

    private void createButtonTransformationTool(Composite composite) {
        Button btnTransformation = new Button(composite, SWT.PUSH | SWT.LEFT);
        btnTransformation.setImage(new ImageConverter().convertToImage(display, Images.btnTransformation.getPath()));
        btnTransformation.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.transformationText));
        btnTransformation.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.transformationToolTip));

        btnTransformation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn08();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnTransformation.setLayoutData(gridData);
    }

    private StatusBar createStatusBar(Shell shell) {
        StatusBar statusBar = new StatusBar(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.ryCONInitialized), OK);

        Main.statusBar = statusBar;

        FormData formDataStatus = new FormData();
        formDataStatus.width = 3 * Sizes.RyCON_GRID_WIDTH.getValue() + 2; // width of the status bar!
        formDataStatus.bottom = new FormAttachment(100, -8);
        formDataStatus.left = new FormAttachment(0, 8);

        statusBar.setLayoutData(formDataStatus);

        return statusBar;
    }

    private void createTrayIcon() {
        final Tray tray = display.getSystemTray();

        if (tray == null) {
            logger.warn("System tray functionality is not available on your system {}.", System.getProperty("os.name"));
        } else {
            final TrayItem item = new TrayItem(tray, SWT.NONE);
            item.setImage(new ImageConverter().convertToImage(display, Images.trayIcon64.getPath()));
            item.setToolTipText("RyCON: " + Version.getBuildNumber() + " <--> " + Version.getBuildDate());

            final Menu menu = new Menu(shell, SWT.POP_UP);

            MenuItem webItem = new MenuItem(menu, SWT.PUSH);
            webItem.setText(ResourceBundleUtils.getLangString(LABELS, Labels.websiteItemTrayMenu));
            webItem.addListener(SWT.Selection, event -> openRyCONWebsite());

            MenuItem helpItem = new MenuItem(menu, SWT.PUSH);
            helpItem.setText(ResourceBundleUtils.getLangString(LABELS, Labels.helpItemTrayMenu));
            helpItem.addListener(SWT.Selection, event -> openRyCONWebsiteHelp());

            MenuItem settingsItem = new MenuItem(menu, SWT.PUSH);
            settingsItem.setText(ResourceBundleUtils.getLangString(LABELS, Labels.settingsItemTrayMenu));
            settingsItem.addListener(SWT.Selection, event -> {
                if (!Main.isSettingsWidgetOpen()) {
                    new SettingsWidget(shell);
                }
            });

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem infoItem = new MenuItem(menu, SWT.PUSH);
            infoItem.setText(ResourceBundleUtils.getLangString(LABELS, Labels.infoItemTrayMenu) + Version.getBuildNumber() + " (" + Version.getBuildDate() + ")");

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem exitItem = new MenuItem(menu, SWT.PUSH);
            exitItem.setText(ResourceBundleUtils.getLangString(LABELS, Labels.exitItemTrayMenu));
            exitItem.addListener(SWT.Selection, event -> actionBtn00());

            item.addListener(SWT.MenuDetect, event -> menu.setVisible(true));
        }
    }

    /*
     * Simple place holder button for the grid. This is not used if all buttons are functional.
     */
    private void createWithoutFunction(Composite composite) {
        Button btnWithoutFunction = new Button(composite, SWT.PUSH);
        btnWithoutFunction.setAlignment(SWT.LEFT);
        btnWithoutFunction.setEnabled(false);

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnWithoutFunction.setLayoutData(gridData);
    }

    private void enableDNDSupport() {
        operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        fileTransfer = FileTransfer.getInstance();
        types = new Transfer[]{
                fileTransfer
        };
    }

    private void handleDropTarget(Button button, final MainApplicationDnDButtons dnDButtons) {
        DropTarget targets = new DropTarget(button, operations);
        targets.setTransfer(types);

        targets.addDropListener(new DropTargetAdapter() {

            public void dragEnter(DropTargetEvent event) {
                shell.setCursor(display.getSystemCursor(SWT.CURSOR_HAND));
            }

            public void dragLeave(DropTargetEvent event) {
                shell.setCursor(null);
            }

            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] droppedFiles = (String[]) event.data;
                    Path[] paths = new Path[droppedFiles.length];

                    for (int i = 0; i < droppedFiles.length; i++) {
                        paths[i] = Paths.get(droppedFiles[i]);
                    }

                    switch (dnDButtons) {
                        case CLEAN:
                            new ClearUpWidget(paths).executeDropInjection();
                            break;
                        case LEVELLING:
                            new LevellingWidget(paths).executeDropInjection();
                            break;
                        case SPLIT:
                            new CodeSplitterWidget(paths).executeDropInjection();
                            break;
                        default:
                            logger.warn("Dropped to an unsupported button {}.", dnDButtons);
                            throw new UnsupportedOperationException("Dropped to an unsupported button");
                    }
                }
            }

        });
    }

    /**
     * Implements the user interface (UI) and all its components.
     * <p>
     * Drag and drop is implemented on the buttons of the following modules.
     * <ul>
     * <li>Clean files...</li>
     * <li>Split files by code...</li>
     * <li>Levelling to cad-import...</li>
     * </ul>
     */
    private void initUI() {
        final String appName = ResourceBundleUtils.getLangString(ResourceBundles.LABELS, Labels.applicationName);

        // TODO check this as a better solution to CocoaUIEnhancer class

        /*
        Desktop desktop = Desktop.getDesktop();

        desktop.setAboutHandler(e ->
                JOptionPane.showMessageDialog(null, "About dialog")
        );
        desktop.setPreferencesHandler(e ->
                JOptionPane.showMessageDialog(null, "Preferences dialog")
        );
        desktop.setQuitHandler((e,r) -> {
                    JOptionPane.showMessageDialog(null, "Quit dialog");
                    System.exit(0);
                }
        );
        */

        Display.setAppName(appName);

        display = Display.getDefault();

        if (SWT.getPlatform().equals("cocoa")) {
            Listener aboutListener = event -> about();
            Listener preferencesListener = event -> preferences();
            Listener quitListener = event -> quit();

            CocoaUIEnhancer enhancer = new CocoaUIEnhancer(appName);
            enhancer.hookApplicationMenu(display, quitListener, aboutListener, preferencesListener);
        }

        shell = new Shell(display, SWT.DIALOG_TRIM);

        createTrayIcon();

        // Dock icon for OS X and Windows task bar
        shell.setImage(new ImageConverter().convertToImage(display, Images.taskIcon.getPath()));
        shell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.applicationTitle));

        FormLayout formLayout = new FormLayout();
        shell.setLayout(formLayout);

        // 3 x 3 grid for the buttons
        Composite compositeGrid = new Composite(shell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = true;

        compositeGrid.setLayout(gridLayout);

        // listen to keyboard inputs. There is no modifier key used!
        addKeyBoardInputFilter(shell);

        enableDNDSupport();

        createButtonProject(compositeGrid);
        createButtonCopyTool(compositeGrid);
        createButtonCleanTool(compositeGrid);
        createButtonSplitTool(compositeGrid);
        createButtonLevelTool(compositeGrid);
        createButtonConvertTool(compositeGrid);
        //createWithoutFunction(compositeGrid);
        createButtonReport(compositeGrid);
        createButtonTransformationTool(compositeGrid);
        createButtonPrintTool(compositeGrid);
        createButtonSettingsTool(compositeGrid);
        createButtonAbout(compositeGrid);
        createButtonExit(compositeGrid);

        StatusBar statusBar = createStatusBar(shell);

        if (pref.isDefaultSettingsGenerated()) {
            statusBar.setStatus(ResourceBundleUtils.getLangString(MESSAGES, Messages.newConfigFileGenerated), WARNING);
        }

        shell.pack();

        // size depends on the grid size
        shell.setSize(3 * Sizes.RyCON_GRID_WIDTH.getValue() + 20, 4 * Sizes.RyCON_GRID_HEIGHT.getValue() + 80);

        // set the window position (last position or centered)
        shell.setLocation(ShellPositioner.positShell(shell));

        shell.addShellListener(new ShellAdapter() {
            /**
             * Sent when a shell becomes the active window.
             * The default behavior is to do nothing.
             *
             * @param e an event containing information about the activation
             */
            @Override
            public void shellActivated(ShellEvent e) {
                super.shellActivated(e);

                // do a couple of things only when RyCON is started
                if (firstStart) {
                    firstStart = false;
                }
            }
        });

        shell.addListener(SWT.Move, e -> saveWindowPosition());

        shell.open();

        if (pref.isDefaultSettingsGenerated()) {
            new SettingsWidget(shell);
        }

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        display.dispose();
    }

    private void openRyCONWebsite() {
        Optional<URI> uri = DefaultKeys.JAVA_WEBSITE.getURI();

        try {
            if (uri.isPresent()) {
                Desktop.getDesktop().browse(uri.get());
            }
        } catch (IOException e) {
            logger.warn("Can not open the RyCON website '{}' with the default browser.", uri.get().getPath(), e.getCause());
        }
    }

    private void openRyCONWebsiteHelp() {
        Optional<URI> uri = DefaultKeys.JAVA_WEBSITE.getURI();

        try {
            if (uri.isPresent()) {
                Desktop.getDesktop().browse(uri.get());
            }
        } catch (IOException e) {
            logger.warn("Can not open the RyCON help website '{}' with the default browser.", uri.get().getPath(), e.getCause());
        }
    }

    private void preferences() {
        new SettingsWidget(shell);
    }

    private void quit() {
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.exitInitialized), OK);
        shell.getDisplay().dispose();
    }

    /*
     * Called after window movement.
     */
    private void saveWindowPosition() {
        // find out on which display RyCON is visible
        Monitor[] monitors = shell.getDisplay().getMonitors();

        if (monitors.length == 1) {         // only one display
            Main.pref.setUserPreference(PreferenceKeys.LAST_USED_DISPLAY, "0");
            Main.pref.setUserPreference(PreferenceKeys.LAST_POS_PRIMARY_MONITOR,
                    Integer.toString(shell.getLocation().x).concat(",").concat(Integer.toString(shell.getLocation().y)));
        } else if (monitors.length > 1) {   // multi display solutions
            // Monitor activeMonitor = null;
            Rectangle r = shell.getBounds();
            for (int i = 0; i < monitors.length; i++) {
                if (monitors[i].getBounds().intersects(r)) {
                    // activeMonitor = monitors[i];
                    Main.pref.setUserPreference(PreferenceKeys.LAST_USED_DISPLAY, Integer.toString(i));
                    Main.pref.setUserPreference(PreferenceKeys.LAST_POS_SECONDARY_MONITOR,
                            Integer.toString(shell.getLocation().x).concat(",").concat(Integer.toString(shell.getLocation().y)));
                }
            }

        }
    }

    private void test() {
        System.out.println("### TEST ###");
        System.out.println();

        /*
        System.out.println(Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER));
        System.out.println(Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_JOB_FILES));
        System.out.println(Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_DATA_FILES));
        System.out.println(Main.pref.getUserPreference(PreferenceKeys.DIR_CARD_READER_EXPORT_FILES));
        */


        System.out.println("Default keys: " + DefaultKeys.values().length);

        try {
            System.out.println("Stored keys: " + Main.pref.getKeys().length);
        } catch (BackingStoreException e) {
            logger.error("Can not the length of the stored user preferences keys.", e.getCause());
        }

        try {
            int i = 0;

            for (String s : pref.getKeys()) {
                System.out.println(i + ": " + s);
                i++;
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("### END OF TEST ###");
    }

    private void testReframJar() {
        // REFRAME object
        Reframe reframeObj = new Reframe();

        double[] inputCoordinates = new double[]{540000.0, 260000.0, 600.0};
        System.out.println(String.valueOf(inputCoordinates[0]) + " / " + String.valueOf(inputCoordinates[1]) + " / " + String.valueOf(inputCoordinates[2]));
        try {
            double[] outputCoordinates = reframeObj.ComputeReframe(inputCoordinates, IReframe.PlanimetricFrame.LV03_Military, IReframe.PlanimetricFrame.LV95, IReframe.AltimetricFrame.LN02, IReframe.AltimetricFrame.Ellipsoid);
            System.out.println(String.valueOf(outputCoordinates[0]) + " / " + String.valueOf(outputCoordinates[1]) + " / " + String.valueOf(outputCoordinates[2]));

            outputCoordinates = reframeObj.ComputeGpsref(outputCoordinates, IReframe.ProjectionChange.LV95ToETRF93Geographic);
            System.out.println(String.valueOf(outputCoordinates[0]) + " / " + String.valueOf(outputCoordinates[1]) + " / " + String.valueOf(outputCoordinates[2]));
        } catch (IllegalArgumentException e) {
            System.out.println("Outside grid");
        } catch (NullPointerException e) {
            System.out.println("Dataset file missing");
        } catch (Exception e) {
            System.out.println("Error 2");
        }
    }

} // end of MainApplication
