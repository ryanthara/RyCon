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

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.data.*;
import de.ryanthara.ja.rycon.i18n.Buttons;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.i18n.Texts;
import de.ryanthara.ja.rycon.nio.util.check.PathCheck;
import de.ryanthara.ja.rycon.ui.cocoa.CocoaUIEnhancer;
import de.ryanthara.ja.rycon.ui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.ui.custom.StatusBar;
import de.ryanthara.ja.rycon.ui.image.ImageConverter;
import de.ryanthara.ja.rycon.ui.util.ShellPositioner;
import de.ryanthara.ja.rycon.ui.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.stream.IntStream;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;
import static de.ryanthara.ja.rycon.ui.MainApplicationDnDButtons.*;
import static de.ryanthara.ja.rycon.ui.custom.Status.OK;
import static de.ryanthara.ja.rycon.ui.custom.Status.WARNING;

/**
 * Instances of this class are the main application of RyCON.
 * <p>
 * This class initializes the main window of RyCON and setup the
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

    /**
     * Main application startup.
     *
     * @param args command line arguments
     */
    public static void main(String... args) {
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
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitGenerator), OK);
    }

    private void actionBtn02() {
        Path cardReaderPath = Paths.get(Main.pref.getUserPreference(PreferenceKey.DIR_CARD_READER));

        if (PathCheck.directoryExists(cardReaderPath)) {
            try {
                if (PathCheck.directoryContainsSubfolder(cardReaderPath, 1)) {
                    new TransferWidget(shell);
                    statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitTransfer), OK);
                } else {
                    MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING,
                            ResourceBundleUtils.getLangString(ResourceBundles.WARNING, Warnings.noCardReaderExistsText),
                            ResourceBundleUtils.getLangString(ResourceBundles.WARNING, Warnings.noCardReaderExistsMessage));
                }
            } catch (IOException e) {
                logger.warn("Card reader path '{}' doesn't contains subfolder for level 1", cardReaderPath.toString());
            }
        }
    }

    private void actionBtn03() {
        new ClearUpWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitClearUp), OK);
    }

    private void actionBtn04() {
        new CodeSplitterWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitSplitter), OK);
    }

    private void actionBtn05() {
        new LevellingWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitLevelling), OK);
    }

    private void actionBtn06() {
        new ConverterWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitConverter), OK);
    }

    private void actionBtn07() {
        new ReportWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitReport), OK);
    }

    private void actionBtn08() {
        new TransformationWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitTransformation), OK);
    }

    private void actionBtn09() {
        MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING, "Warnings", "Not implemented yet.");
        new PrinterWidget(shell);
        statusBar.setStatus("not implemented yet.", WARNING);
    }

    private void actionBtn11() {
        new AboutWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitAbout), OK);
    }

    private void actionBtnP() {
        new SettingsWidget(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitSettings), OK);
    }

    private void addKeyBoardInputFilter(Shell shell) {
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
                        //test();
                        set();
                        break;

                    case 'ü':   // about, german version
                        actionBtn11();
                        break;

                }
            }
        });
    }

    private void createButtonAbout(Composite composite) {
        org.eclipse.swt.widgets.Button btnAbout = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnAbout.setImage(new ImageConverter().convertToImage(display, Image.btnAbout.getPath()));
        btnAbout.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.aboutText));
        btnAbout.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.aboutToolTip));

        btnAbout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn11();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnAbout.setLayoutData(gridData);
    }

    private void createButtonCleanTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnToolboxClean = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxClean.setImage(new ImageConverter().convertToImage(display, Image.btnClean.getPath()));
        btnToolboxClean.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.cleanText));
        btnToolboxClean.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.cleanToolTip));

        btnToolboxClean.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn03();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnToolboxClean.setLayoutData(gridData);

        handleDropTarget(btnToolboxClean, CLEAN);
    }

    private void createButtonConvertTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnToolboxConvert = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxConvert.setImage(new ImageConverter().convertToImage(display, Image.btnConvert.getPath()));
        btnToolboxConvert.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.convertText));
        btnToolboxConvert.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.convertToolTip));

        btnToolboxConvert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn06();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnToolboxConvert.setLayoutData(gridData);
    }

    private void createButtonCopyTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnToolboxCopyTool = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxCopyTool.setImage(new ImageConverter().convertToImage(display, Image.btnCopy.getPath()));
        btnToolboxCopyTool.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.importText));
        btnToolboxCopyTool.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.importToolTip));

        btnToolboxCopyTool.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn02();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnToolboxCopyTool.setLayoutData(gridData);
    }

    private void createButtonExit(Composite composite) {
        org.eclipse.swt.widgets.Button btnExit = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnExit.setImage(new ImageConverter().convertToImage(display, Image.btnExit.getPath()));
        btnExit.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.exitText));
        btnExit.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.exitToolTip));

        btnExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn00();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnExit.setLayoutData(gridData);
    }

    private void createButtonLevelTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnToolboxLeveling = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxLeveling.setImage(new ImageConverter().convertToImage(display, Image.btnLevel.getPath()));
        btnToolboxLeveling.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.levellingText));
        btnToolboxLeveling.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.levellingToolTip));

        btnToolboxLeveling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn05();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnToolboxLeveling.setLayoutData(gridData);

        handleDropTarget(btnToolboxLeveling, LEVELLING);
    }

    private void createButtonPrintTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnPrint = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnPrint.setImage(new ImageConverter().convertToImage(display, Image.btnPrint.getPath()));
        btnPrint.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.printText));
        btnPrint.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.printToolTip));

        btnPrint.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn08();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnPrint.setLayoutData(gridData);

        btnPrint.setEnabled(false);
    }

    private void createButtonProject(Composite composite) {
        org.eclipse.swt.widgets.Button btnToolboxProject = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxProject.setImage(new ImageConverter().convertToImage(display, Image.btnProject.getPath()));
        btnToolboxProject.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.generatorText));
        btnToolboxProject.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.generatorToolTip));

        btnToolboxProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn01();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnToolboxProject.setLayoutData(gridData);
    }

    private void createButtonReport(Composite composite) {
        org.eclipse.swt.widgets.Button btnToolboxReport = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxReport.setImage(new ImageConverter().convertToImage(display, Image.btnReport.getPath()));
        btnToolboxReport.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.reportText));
        btnToolboxReport.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.reportToolTip));

        btnToolboxReport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn07();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnToolboxReport.setLayoutData(gridData);

        handleDropTarget(btnToolboxReport, ANALYZE);
    }

    private void createButtonSettingsTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnSettings = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnSettings.setImage(new ImageConverter().convertToImage(display, Image.btnSettings.getPath()));
        btnSettings.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.settingsText));
        btnSettings.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.settingsToolTip));

        btnSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtnP();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnSettings.setLayoutData(gridData);
    }

    private void createButtonSplitTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnToolboxSplitter = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnToolboxSplitter.setImage(new ImageConverter().convertToImage(display, Image.btnSplit.getPath()));
        btnToolboxSplitter.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.splitterText));
        btnToolboxSplitter.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.splitterToolTip));

        btnToolboxSplitter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn04();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnToolboxSplitter.setLayoutData(gridData);

        handleDropTarget(btnToolboxSplitter, SPLIT);
    }

    private void createButtonTransformationTool(Composite composite) {
        org.eclipse.swt.widgets.Button btnTransformation = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH | SWT.LEFT);
        btnTransformation.setImage(new ImageConverter().convertToImage(display, Image.btnTransformation.getPath()));
        btnTransformation.setText(ResourceBundleUtils.getLangString(BUTTON, Buttons.transformationText));
        btnTransformation.setToolTipText(ResourceBundleUtils.getLangString(BUTTON, Buttons.transformationToolTip));

        btnTransformation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn08();
            }
        });

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnTransformation.setLayoutData(gridData);
    }

    private StatusBar createStatusBar(Shell shell) {
        StatusBar statusBar = new StatusBar(shell);
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(LANG_STRING, LangStrings.application_Initialized), OK);

        Main.statusBar = statusBar;

        FormData formDataStatus = new FormData();
        formDataStatus.width = 3 * Size.RyCON_GRID_WIDTH.getValue() + 2; // width of the status bar!
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
            item.setImage(new ImageConverter().convertToImage(display, Image.trayIcon64.getPath()));
            item.setToolTipText("RyCON: " + Version.getBuildNumber() + " <--> " + Version.getBuildDate());

            final Menu menu = new Menu(shell, SWT.POP_UP);

            MenuItem webItem = new MenuItem(menu, SWT.PUSH);
            webItem.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.trayMenu_WebsiteItem));
            webItem.addListener(SWT.Selection, event -> openRyCONWebsite());

            MenuItem helpItem = new MenuItem(menu, SWT.PUSH);
            helpItem.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.trayMenu_HelpItem));
            helpItem.addListener(SWT.Selection, event -> openRyCONWebsiteHelp());

            MenuItem settingsItem = new MenuItem(menu, SWT.PUSH);
            settingsItem.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.trayMenu_SettingsItem));
            settingsItem.addListener(SWT.Selection, event -> {
                if (!Main.isSettingsWidgetOpen()) {
                    new SettingsWidget(shell);
                }
            });

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem infoItem = new MenuItem(menu, SWT.PUSH);
            infoItem.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.trayMenu_InfoItem) + Version.getBuildNumber() + " (" + Version.getBuildDate() + ")");

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem exitItem = new MenuItem(menu, SWT.PUSH);
            exitItem.setText(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.trayMenu_ExitItem));
            exitItem.addListener(SWT.Selection, event -> actionBtn00());

            item.addListener(SWT.MenuDetect, event -> menu.setVisible(true));
        }
    }

    /*
     * Simple place holder button for the grid. This is not used if all buttons are functional.
     */
    private void createWithoutFunction(Composite composite) {
        org.eclipse.swt.widgets.Button btnWithoutFunction = new org.eclipse.swt.widgets.Button(composite, SWT.PUSH);
        btnWithoutFunction.setAlignment(SWT.LEFT);
        btnWithoutFunction.setEnabled(false);

        GridData gridData = new GridData(Size.RyCON_GRID_WIDTH.getValue(), Size.RyCON_GRID_HEIGHT.getValue());
        btnWithoutFunction.setLayoutData(gridData);
    }

    private void enableDNDSupport() {
        operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        fileTransfer = FileTransfer.getInstance();
        types = new Transfer[]{
                fileTransfer
        };
    }

    private void handleDropTarget(org.eclipse.swt.widgets.Button button, final MainApplicationDnDButtons dnDButtons) {
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
                    Path[] paths = Arrays.stream(droppedFiles).map(droppedFile -> Paths.get(droppedFile)).toArray(Path[]::new);

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
        final String appName = ResourceBundleUtils.getLangStringFromXml(LANG_STRING, LangStrings.application_Name);

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
        shell.setImage(new ImageConverter().convertToImage(display, Image.taskIcon.getPath()));
        shell.setText(ResourceBundleUtils.getLangStringFromXml(LANG_STRING, LangStrings.application_Title));

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
            statusBar.setStatus(ResourceBundleUtils.getLangString(MESSAGE, Messages.newConfigFileGenerated), WARNING);
        }

        shell.pack();

        // size depends on the grid size
        shell.setSize(3 * Size.RyCON_GRID_WIDTH.getValue() + 20, 4 * Size.RyCON_GRID_HEIGHT.getValue() + 80);

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
        Optional<URI> uri = ApplicationKey.JAVA_WEBSITE.getURI();

        try {
            if (uri.isPresent()) {
                Desktop.getDesktop().browse(uri.get());
            }
        } catch (IOException e) {
            logger.warn("Can not open the RyCON website '{}' with the default browser.", uri.get().getPath(), e.getCause());
        }
    }

    private void openRyCONWebsiteHelp() {
        Optional<URI> uri = ApplicationKey.JAVA_WEBSITE.getURI();

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
        statusBar.setStatus(ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.status_InitExit), OK);
        shell.getDisplay().dispose();
    }

    /*
     * Called after window movement.
     */
    private void saveWindowPosition() {
        // find out on which display RyCON is visible
        Monitor[] monitors = shell.getDisplay().getMonitors();

        if (monitors.length == 1) {         // only one display
            Main.pref.setUserPreference(PreferenceKey.LAST_USED_DISPLAY, "0");
            Main.pref.setUserPreference(PreferenceKey.LAST_POS_PRIMARY_MONITOR,
                    Integer.toString(shell.getLocation().x).concat(",").concat(Integer.toString(shell.getLocation().y)));
        } else if (monitors.length > 1) {   // multi display solutions
            // Monitor activeMonitor = null;
            Rectangle r = shell.getBounds();
            // activeMonitor = monitors[i];
            IntStream.range(0, monitors.length).filter(i -> monitors[i].getBounds().intersects(r)).forEach(i -> {
                Main.pref.setUserPreference(PreferenceKey.LAST_USED_DISPLAY, Integer.toString(i));
                Main.pref.setUserPreference(PreferenceKey.LAST_POS_SECONDARY_MONITOR,
                        Integer.toString(shell.getLocation().x).concat(",").concat(Integer.toString(shell.getLocation().y)));
            });

        }
    }

    private void set() {
        // Main.pref.setUserPreference(PreferenceKey.PARAM_LTOP_STRING, "_LTOP");
        // System.out.println("set");
        // System.out.println(Main.pref.getUserPreference(PreferenceKey.PARAM_LTOP_STRING));
    }

    private void test() {
        System.out.println("### TEST ###");
        System.out.println();

        /*
        System.out.println(Main.pref.getUserPreference(PreferenceKey.DIR_CARD_READER));
        System.out.println(Main.pref.getUserPreference(PreferenceKey.DIR_CARD_READER_JOB_FILES));
        System.out.println(Main.pref.getUserPreference(PreferenceKey.DIR_CARD_READER_DATA_FILES));
        System.out.println(Main.pref.getUserPreference(PreferenceKey.DIR_CARD_READER_EXPORT_FILES));
        */


        System.out.println("Default keys: " + DefaultKey.values().length);

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

}
