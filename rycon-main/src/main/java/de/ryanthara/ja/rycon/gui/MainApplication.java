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
import de.ryanthara.ja.rycon.data.PreferenceHandler;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.gui.custom.MessageBoxes;
import de.ryanthara.ja.rycon.gui.custom.StatusBar;
import de.ryanthara.ja.rycon.gui.widget.*;
import de.ryanthara.ja.rycon.i18n.I18N;
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
import org.eclipse.swt.widgets.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.ryanthara.ja.rycon.gui.MainApplicationDnDButtons.*;
import static de.ryanthara.ja.rycon.gui.custom.Status.OK;
import static de.ryanthara.ja.rycon.gui.custom.Status.WARNING;

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

    private void actionBtn0() {
        statusBar.setStatus(I18N.getStatusExitInitialized(), OK);
        shell.getDisplay().dispose();
    }

    private void actionBtn1() {
        new GeneratorWidget();
        statusBar.setStatus(I18N.getStatusGeneratorInitialized(), OK);
    }

    private void actionBtn2() {
        MessageBoxes.showMessageBox(shell, SWT.ICON_WARNING, "Warning", "Not implemented yet.");
        new TransferWidget();
        statusBar.setStatus("not implemented yet.", WARNING);
    }

    private void actionBtn3() {
        new TidyUpWidget();
        statusBar.setStatus(I18N.getStatusCleanInitialized(), OK);
    }

    private void actionBtn4() {
        new CodeSplitterWidget();
        statusBar.setStatus(I18N.getStatusSplitterInitialized(), OK);
    }

    private void actionBtn5() {
        new LevellingWidget();
        statusBar.setStatus(I18N.getStatusLevelInitialized(), OK);
    }

    private void actionBtn6() {
        new ConverterWidget();
        statusBar.setStatus(I18N.getStatusConverterInitialized(), OK);
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
        statusBar.setStatus(I18N.getStatusSettingsOpened(), OK);
    }

    private void addKeyBoardInputFilter(final Shell shell) {
        display.addFilter(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
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
                            new SettingsWidget();
                            break;

                        default:
                            System.err.println("MainApplication.addKeyBoardInputFilter() : unsupported key pressed " + event.keyCode);
                    }
                }
            }
        });
    }

    private void createButtonExit(Composite composite) {
        Button btnExit = new Button(composite, SWT.PUSH);
        btnExit.setAlignment(SWT.LEFT);
        btnExit.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_exit.png"));
        btnExit.setText(I18N.getBtnExitLabel());
        btnExit.setToolTipText(I18N.getBtnExitLabelToolTip());

        btnExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn0();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnExit.setLayoutData(gridData);
    }

    private void createButtonProject(Composite composite) {
        Button btnToolboxProject = new Button(composite, SWT.PUSH);
        btnToolboxProject.setAlignment(SWT.LEFT);
        btnToolboxProject.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_project.png"));
        btnToolboxProject.setText(I18N.getBtnGeneratorLabel());
        btnToolboxProject.setToolTipText(I18N.getBtnGeneratorLabelToolTip());

        btnToolboxProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn1();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxProject.setLayoutData(gridData);
    }

    private void createButtonCopyTool(Composite composite) {
        Button btnToolboxCopyTool = new Button(composite, SWT.PUSH);
        btnToolboxCopyTool.setAlignment(SWT.LEFT);
        btnToolboxCopyTool.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_copy.png"));
        btnToolboxCopyTool.setText(I18N.getBtnCopyLabel());
        btnToolboxCopyTool.setToolTipText(I18N.getBtnCopyLabelToolTip());

        btnToolboxCopyTool.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn2();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxCopyTool.setLayoutData(gridData);
    }

    private void createButtonCleanTool(Composite composite) {
        Button btnToolboxClean = new Button(composite, SWT.PUSH);
        btnToolboxClean.setAlignment(SWT.LEFT);
        btnToolboxClean.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_clean.png"));
        btnToolboxClean.setText(I18N.getBtnCleanLabel());
        btnToolboxClean.setToolTipText(I18N.getBtnCleanLabelToolTip());

        btnToolboxClean.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn3();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxClean.setLayoutData(gridData);

        handleDropTarget(btnToolboxClean, CLEAN);
    }

    private void createButtonSplitTool(Composite composite) {
        Button btnToolboxSplitter = new Button(composite, SWT.PUSH);
        btnToolboxSplitter.setAlignment(SWT.LEFT);
        btnToolboxSplitter.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_code.png"));
        btnToolboxSplitter.setText(I18N.getBtnSplitterLabel());
        btnToolboxSplitter.setToolTipText(I18N.getBtnSplitterLabelToolTip());

        btnToolboxSplitter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn4();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxSplitter.setLayoutData(gridData);

        handleDropTarget(btnToolboxSplitter, SPLIT);
    }

    private void createButtonLevelTool(Composite composite) {
        Button btnToolboxLeveling = new Button(composite, SWT.PUSH);
        btnToolboxLeveling.setAlignment(SWT.LEFT);
        btnToolboxLeveling.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_level.png"));
        btnToolboxLeveling.setText(I18N.getBtnLevelingLabel());
        btnToolboxLeveling.setToolTipText(I18N.getBtnLevelingLabelToolTip());

        btnToolboxLeveling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn5();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxLeveling.setLayoutData(gridData);

        handleDropTarget(btnToolboxLeveling, LEVELLING);
    }

    private void createButtonConvertTool(Composite composite) {
        Button btnToolboxConvert = new Button(composite, SWT.PUSH);
        btnToolboxConvert.setAlignment(SWT.LEFT);
        btnToolboxConvert.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_convert.png"));
        btnToolboxConvert.setText(I18N.getBtnConvertLabel());
        btnToolboxConvert.setToolTipText(I18N.getBtnConvertLabelToolTip());

        btnToolboxConvert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn6();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxConvert.setLayoutData(gridData);
    }

    private void createButtonTransformationTool(Composite composite) {
        Button btnTransformation = new Button(composite, SWT.PUSH);
        btnTransformation.setAlignment(SWT.LEFT);
        btnTransformation.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_transformation.png"));
        btnTransformation.setText(I18N.getBtnTransformationLabel());
        btnTransformation.setToolTipText(I18N.getBtnTransformationLabelToolTip());

        btnTransformation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn7();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnTransformation.setLayoutData(gridData);
    }

    private void createButtonPrintTool(Composite composite) {
        Button btnPrint = new Button(composite, SWT.PUSH);
        btnPrint.setAlignment(SWT.LEFT);
        btnPrint.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_printer.png"));
        btnPrint.setText(I18N.getBtnPrintLabel());
        btnPrint.setToolTipText(I18N.getBtnPrintLabelToolTip());

        btnPrint.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn8();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnPrint.setLayoutData(gridData);
    }

    private void createButtonSettingsTool(Composite composite) {
        Button btnSettings = new Button(composite, SWT.PUSH);
        btnSettings.setAlignment(SWT.LEFT);
        btnSettings.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_settings.png"));
        btnSettings.setText(I18N.getBtnSettingsMainLabel());
        btnSettings.setToolTipText(I18N.getBtnSettingsMainLabelToolTip());

        btnSettings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn9();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnSettings.setLayoutData(gridData);
    }

    private void createWithoutFunction(Composite composite) {
        Button btnWithoutFunction = new Button(composite, SWT.PUSH);
        btnWithoutFunction.setAlignment(SWT.LEFT);
        //btnWithoutFunction.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/btn_exit.png"));
        //btnWithoutFunction.setText(I18N.getBtnExitLabel());
        //btnWithoutFunction.setToolTipText(I18N.getBtnExitLabelToolTip());
        btnWithoutFunction.setEnabled(false);

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnWithoutFunction.setLayoutData(gridData);
    }

    private StatusBar createStatusBar(Shell shell) {
        StatusBar statusBar = new StatusBar(shell);
        statusBar.setStatus(I18N.getStatusRyCONInitialized(), OK);
        Main.statusBar = statusBar;

        FormData formDataStatus = new FormData();
        formDataStatus.width = 3 * getRyCON_GRID_WIDTH() + 2; // width of the status bar!
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
            item.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/RyCON_TrayIcon64x64.png"));
            item.setToolTipText("RyCON: " + Version.getBuildNumber() + " <--> " + Version.getBuildDate());

            final Menu menu = new Menu(shell, SWT.POP_UP);

            MenuItem webItem = new MenuItem(menu, SWT.PUSH);
            webItem.setText(I18N.getTrayMenuItemWebsite());
            webItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    Main.openURI(Main.RyCON_WEBSITE);
                }
            });

            MenuItem helpItem = new MenuItem(menu, SWT.PUSH);
            helpItem.setText(I18N.getTrayMenuItemHelp());
            helpItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    Main.openURI(Main.RyCON_WEBSITE_HELP);
                }
            });

            MenuItem settingsItem = new MenuItem(menu, SWT.PUSH);
            settingsItem.setText(I18N.getTrayMenuItemSettings());
            settingsItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (!Main.isSettingsWidgetOpen()) {
                        new SettingsWidget();
                    }
                }
            });

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem infoItem = new MenuItem(menu, SWT.PUSH);
            infoItem.setText(I18N.getTrayMenuItemInfo() + Version.getBuildNumber() + " (" + Version.getBuildDate() + ")");

            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem exitItem = new MenuItem(menu, SWT.PUSH);
            exitItem.setText(I18N.getTrayMenuItemExit());
            exitItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    actionBtn0();
                }
            });

            item.addListener(SWT.MenuDetect, new Listener() {
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            });
        }
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
        Display.setAppName(Main.getRyCONAppName());
        display = new Display();

        // initialize a shell and make it global
        final Shell shell = new Shell(display, SWT.DIALOG_TRIM);
        Main.shell = shell;

        createTrayIcon();

        // Dock icon for OS X and Windows task bar
        shell.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/RyCON_blank256x256.png"));
        shell.setText(I18N.getApplicationTitle());

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
            statusBar.setStatus(I18N.getMsgNewConfigFileGenerated(), WARNING);
        }

        shell.pack();

        // size depends on the grid size
        shell.setSize(3 * getRyCON_GRID_WIDTH() + 20, 4 * getRyCON_GRID_HEIGHT() + 80);

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

        shell.addListener(SWT.Move, new Listener() {
            public void handleEvent(Event e) {
                saveWindowPosition();
            }
        });

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

    private void saveWindowPosition() {
        // find out on which display RyCON is visible
        Monitor[] monitors = shell.getDisplay().getMonitors();

        if (monitors.length == 1) {         // only one display
            Main.pref.setUserPref(PreferenceHandler.LAST_USED_DISPLAY, "0");
            Main.pref.setUserPref(PreferenceHandler.LAST_POS_PRIMARY_MONITOR,
                    Integer.toString(shell.getLocation().x).concat(",").concat(Integer.toString(shell.getLocation().y)));
        } else if (monitors.length > 1) {   // multi display solutions
            // Monitor activeMonitor = null;
            Rectangle r = shell.getBounds();
            for (int i = 0; i < monitors.length; i++) {
                if (monitors[i].getBounds().intersects(r)) {
                    // activeMonitor = monitors[i];
                    Main.pref.setUserPref(PreferenceHandler.LAST_USED_DISPLAY, Integer.toString(i));
                    Main.pref.setUserPref(PreferenceHandler.LAST_POS_SECONDARY_MONITOR,
                            Integer.toString(shell.getLocation().x).concat(",").concat(Integer.toString(shell.getLocation().y)));
                }
            }

        }
    }

} // end of MainApplication
