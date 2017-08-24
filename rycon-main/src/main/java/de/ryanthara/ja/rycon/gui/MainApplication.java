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
package de.ryanthara.ja.rycon.gui;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.check.PathCheck;
import de.ryanthara.ja.rycon.data.DefaultKeys;
import de.ryanthara.ja.rycon.data.PreferenceKeys;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.gui.custom.StatusBar;
import de.ryanthara.ja.rycon.gui.preferences.PreferencesDialog;
import de.ryanthara.ja.rycon.gui.widgets.*;
import de.ryanthara.ja.rycon.i18n.*;
import de.ryanthara.ja.rycon.tools.ImageConverter;
import de.ryanthara.ja.rycon.tools.ShellPositioner;
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

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static de.ryanthara.ja.rycon.gui.MainApplicationDnDButtons.*;
import static de.ryanthara.ja.rycon.gui.custom.Status.OK;
import static de.ryanthara.ja.rycon.gui.custom.Status.WARNING;
import static de.ryanthara.ja.rycon.i18n.ResourceBundles.*;

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

    private final static Logger logger = Logger.getLogger(MainApplication.class.getName());
    private boolean firstStart;
    private int operations;
    private Display display;
    private FileTransfer fileTransfer;
    private Transfer[] types;

    /**
     * Constructs a new instance of this class without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public MainApplication() {
        fileTransfer = null;
        firstStart = true;
        operations = Integer.MIN_VALUE;
        initUI();
    }

    /*
     * Sets the default logging level of <tt>RyCON</tt> to 'SEVERE'.
     * <p>
     * Different logging levels can be set with the command line interface.
     * @see de.ryanthara.ja.rycon.cli.CmdLineInterfaceParser
     */
    private static void initLogging() {
        // avoid console logging
        LogManager.getLogManager().reset();

        Main.loggingLevel = Level.SEVERE;

        logger.setLevel(Main.loggingLevel);

        try {
            FileHandler fh = new FileHandler("RyCON_logfile%g.xml", 1024 * 1024, 10, true);

            Main.fileHandler = fh;

            logger.addHandler(fh);
            logger.log(Level.INFO, "logging with level '" + Main.loggingLevel.getName() + "' for RyCON enabled successful");
        } catch (IOException e) {
            System.err.println("Can not access file 'RyCON_logfile[n].xml' " + e.getMessage());
        }
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

    private void actionBtn0() {
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.exitInitialized), OK);
        shell.getDisplay().dispose();
    }

    private void actionBtn1() {
        new GeneratorWidget();
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.generatorInitialized), OK);
    }

    private void actionBtn2() {
        new TransferWidget();
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.transferInitialized), OK);
    }

    private void actionBtn3() {
        new TidyUpWidget();
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.tidyUpInitialized), OK);
    }

    private void actionBtn4() {
        new CodeSplitterWidget();
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.splitFilesInitialized), OK);
    }

    private void actionBtn5() {
        new LevellingWidget();
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.levellingInitialized), OK);
    }

    private void actionBtn6() {
        new ConverterWidget();
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.converterInitialized), OK);
    }

    private void actionBtn7() {
        MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING, "Warning", "Not implemented yet.");
        new TransformationWidget();
        statusBar.setStatus("not implemented yet.", WARNING);
    }

    private void actionBtn8() {
        MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING, "Warning", "Not implemented yet.");
        new PrinterWidget();
        statusBar.setStatus("not implemented yet.", WARNING);
    }

    private void actionBtn9() {
        new SettingsWidget();
        statusBar.setStatus(ResourceBundleUtils.getLangString(LABELS, Labels.settingsInitialized), OK);
    }

    private void addKeyBoardInputFilter(final Shell shell) {
        display.addFilter(SWT.KeyDown, event -> {
            if (!getSubShellStatus()) {
                switch (event.keyCode) {
                    case '1':
                        actionBtn1();
                        break;

                    case '2':
                        actionBtn2();
                        break;

                    case '3':
                        actionBtn3();
                        break;

                    case '4':
                        actionBtn4();
                        break;

                    case '5':
                        actionBtn5();
                        break;

                    case '6':
                        actionBtn6();
                        break;

                    case '7':
                        actionBtn7();
                        break;

                    case '8':
                        actionBtn8();
                        break;

                    case '9':
                        actionBtn9();
                        break;

                    case '0':
                        actionBtn0();
                        break;

                    case 'c':
                        shell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(shell));
                        break;

                    case 'p':
                        new PreferencesDialog();
                        break;

                    case 't':
                        test();

                    default:
                        System.err.println("MainApplication.addKeyBoardInputFilter() : unsupported key pressed " + event.keyCode);
                }
            }
        });
    }

    private void createButtonCleanTool(Composite composite) {
        Button btnToolboxClean = new Button(composite, SWT.PUSH);
        btnToolboxClean.setAlignment(SWT.LEFT);
        btnToolboxClean.setImage(new ImageConverter().convertToImage(display, Images.btnClean.getPath()));
        btnToolboxClean.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.cleanText));
        btnToolboxClean.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.cleanToolTip));

        btnToolboxClean.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn3();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxClean.setLayoutData(gridData);

        handleDropTarget(btnToolboxClean, CLEAN);
    }

    private void createButtonConvertTool(Composite composite) {
        Button btnToolboxConvert = new Button(composite, SWT.PUSH);
        btnToolboxConvert.setAlignment(SWT.LEFT);
        btnToolboxConvert.setImage(new ImageConverter().convertToImage(display, Images.btnConvert.getPath()));
        btnToolboxConvert.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.convertText));
        btnToolboxConvert.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.convertToolTip));

        btnToolboxConvert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn6();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxConvert.setLayoutData(gridData);
    }

    private void createButtonCopyTool(Composite composite) {
        Button btnToolboxCopyTool = new Button(composite, SWT.PUSH);
        btnToolboxCopyTool.setAlignment(SWT.LEFT);
        btnToolboxCopyTool.setImage(new ImageConverter().convertToImage(display, Images.btnCopy.getPath()));
        btnToolboxCopyTool.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.importText));
        btnToolboxCopyTool.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.importToolTip));

        btnToolboxCopyTool.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn2();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxCopyTool.setLayoutData(gridData);
    }

    private void createButtonExit(Composite composite) {
        Button btnExit = new Button(composite, SWT.PUSH);
        btnExit.setAlignment(SWT.LEFT);
        btnExit.setImage(new ImageConverter().convertToImage(display, Images.btnExit.getPath()));
        btnExit.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.exitText));
        btnExit.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.exitToolTip));

        btnExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn0();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnExit.setLayoutData(gridData);
    }

    private void createButtonLevelTool(Composite composite) {
        Button btnToolboxLeveling = new Button(composite, SWT.PUSH);
        btnToolboxLeveling.setAlignment(SWT.LEFT);
        btnToolboxLeveling.setImage(new ImageConverter().convertToImage(display, Images.btnLevel.getPath()));
        btnToolboxLeveling.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.levellingText));
        btnToolboxLeveling.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.levellingToolTip));

        btnToolboxLeveling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn5();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxLeveling.setLayoutData(gridData);

        handleDropTarget(btnToolboxLeveling, LEVELLING);
    }

    private void createButtonPrintTool(Composite composite) {
        Button btnPrint = new Button(composite, SWT.PUSH);
        btnPrint.setAlignment(SWT.LEFT);
        btnPrint.setImage(new ImageConverter().convertToImage(display, Images.btnPrint.getPath()));
        btnPrint.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.printText));
        btnPrint.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.printToolTip));

        btnPrint.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn8();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnPrint.setLayoutData(gridData);

        btnPrint.setEnabled(false);
    }

    private void createButtonProject(Composite composite) {
        Button btnToolboxProject = new Button(composite, SWT.PUSH);
        btnToolboxProject.setAlignment(SWT.LEFT);
        btnToolboxProject.setImage(new ImageConverter().convertToImage(display, Images.btnProject.getPath()));
        btnToolboxProject.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.generatorText));
        btnToolboxProject.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.generatorToolTip));

        btnToolboxProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn1();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxProject.setLayoutData(gridData);
    }

    private void createButtonSettingsTool(Composite composite) {
        Button btnSettings = new Button(composite, SWT.PUSH);
        btnSettings.setAlignment(SWT.LEFT);
        btnSettings.setImage(new ImageConverter().convertToImage(display, Images.btnSettings.getPath()));
        btnSettings.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.settingsText));
        btnSettings.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.settingsToolTip));

        btnSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn9();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnSettings.setLayoutData(gridData);
    }

    private void createButtonSplitTool(Composite composite) {
        Button btnToolboxSplitter = new Button(composite, SWT.PUSH);
        btnToolboxSplitter.setAlignment(SWT.LEFT);
        btnToolboxSplitter.setImage(new ImageConverter().convertToImage(display, Images.btnSplit.getPath()));
        btnToolboxSplitter.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.splitterText));
        btnToolboxSplitter.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.splitterToolTip));

        btnToolboxSplitter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn4();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnToolboxSplitter.setLayoutData(gridData);

        handleDropTarget(btnToolboxSplitter, SPLIT);
    }

    private void createButtonTransformationTool(Composite composite) {
        Button btnTransformation = new Button(composite, SWT.PUSH);
        btnTransformation.setAlignment(SWT.LEFT);
        btnTransformation.setImage(new ImageConverter().convertToImage(display, Images.btnTransformation.getPath()));
        btnTransformation.setText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.transformationText));
        btnTransformation.setToolTipText(ResourceBundleUtils.getLangString(BUTTONS, Buttons.transformationToolTip));

        btnTransformation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn7();
            }
        });

        GridData gridData = new GridData(Sizes.RyCON_GRID_WIDTH.getValue(), Sizes.RyCON_GRID_HEIGHT.getValue());
        btnTransformation.setLayoutData(gridData);

        btnTransformation.setEnabled(false);
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
            System.err.println("System tray functionality is not available on your system.");
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
                    new SettingsWidget();
                }
            });

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem infoItem = new MenuItem(menu, SWT.PUSH);
            infoItem.setText(ResourceBundleUtils.getLangString(LABELS, Labels.infoItemTrayMenu) + Version.getBuildNumber() + " (" + Version.getBuildDate() + ")");

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem exitItem = new MenuItem(menu, SWT.PUSH);
            exitItem.setText(ResourceBundleUtils.getLangString(LABELS, Labels.exitItemTrayMenu));
            exitItem.addListener(SWT.Selection, event -> actionBtn0());

            item.addListener(SWT.MenuDetect, event -> menu.setVisible(true));
        }
    }

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
        DropTarget targetLevelling = new DropTarget(button, operations);
        targetLevelling.setTransfer(types);

        targetLevelling.addDropListener(new DropTargetAdapter() {

            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] droppedFiles = (String[]) event.data;
                    Path[] paths = new Path[droppedFiles.length];

                    for (int i = 0; i < droppedFiles.length; i++) {
                        paths[i] = Paths.get(droppedFiles[i]);
                    }

                    switch (dnDButtons) {
                        case CLEAN:
                            new TidyUpWidget(paths).executeDropInjection();
                            break;
                        case LEVELLING:
                            new LevellingWidget(paths).executeDropInjection();
                            break;
                        case SPLIT:
                            new CodeSplitterWidget(paths).executeDropInjection();
                            break;
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
        Display.setAppName(ResourceBundleUtils.getLangString(ResourceBundles.LABELS, Labels.applicationName));
        display = new Display();

        // initialize a shell and make it global
        final Shell shell = new Shell(display, SWT.DIALOG_TRIM);
        Main.shell = shell;

        createTrayIcon();

        // Dock icon for OS X and Windows task bar
        shell.setImage(new ImageConverter().convertToImage(display, Images.taskIcon.getPath()));
        shell.setText(ResourceBundleUtils.getLangString(LABELS, Labels.applicationTitle));
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
        createButtonTransformationTool(compositeGrid);
        createButtonPrintTool(compositeGrid);
        createButtonSettingsTool(compositeGrid);
        createWithoutFunction(compositeGrid);
        createButtonExit(compositeGrid);
        createWithoutFunction(compositeGrid);

        StatusBar statusBar = createStatusBar(shell);

        if (pref.isDefaultSettingsGenerated()) {
            statusBar.setStatus(ResourceBundleUtils.getLangString(MESSAGES, Messages.newConfigFileGenerated), WARNING);

            logger.log(Level.INFO, "default settings generated");
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
            new SettingsWidget();
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
            logger.log(Level.SEVERE, "can not open the RyCON website " + uri.get().getPath()
                    + " with the default browser.", e);
        }
    }

    private void openRyCONWebsiteHelp() {
        Optional<URI> uri = DefaultKeys.JAVA_WEBSITE.getURI();

        try {
            if (uri.isPresent()) {
                Desktop.getDesktop().browse(uri.get());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "can not open the RyCON help website " + uri.get().getPath()
                    + " with the default browser.", e);
        }
    }

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

        //Main.pref.setUserPreference(PreferenceKeys.LAST_USED_PROJECTS, "[]");
        //Main.pref.setUserPreference(PreferenceKeys.OVERWRITE_EXISTING, "true");




        PathCheck.isFile("");

    }

} // end of MainApplication
