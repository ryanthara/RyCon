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
import de.ryanthara.ja.rycon.data.I18N;
import de.ryanthara.ja.rycon.data.Version;
import de.ryanthara.ja.rycon.gui.notifier.NotificationPopupWidget;
import de.ryanthara.ja.rycon.gui.notifier.NotificationType;
import de.ryanthara.ja.rycon.tools.ImageConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.io.File;

/**
 * This class is the main application of RyCON.
 * <p>
 * This class initializes the main window of RyCON and setup the
 * background functionality which is done by the extension of the
 * {@code Main} class.
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>4: enable drag and drop handling </li>
 *     <li>3: code improvements and clean up </li>
 *     <li>2: basic improvements </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 4
 * @since 1
 * @see de.ryanthara.ja.rycon.Main
 */
public class MainApplication extends Main {

    private boolean firstStart = true;
    private int operations = Integer.MIN_VALUE;
    private Display display = null;
    private FileTransfer fileTransfer = null;
    private Transfer[] types;

    /**
     * Class constructor without parameters.
     * <p>
     * The user interface is initialized in a separate method, which is called from here.
     */
    public MainApplication() {
        initUI();
    }

    /**
     * Implements the user interface (UI) and all its components.
     * <p>
     * Drag and drop is implemented on the buttons of the following modules.
     * <ul>
     *     <li>Clean files...</li>
     *     <li>Split files by code...</li>
     *     <li>Levelling to cad-import...</li>
     * </ul>*
     */
    private void initUI() {
        Display.setAppName(Main.getRyCONAppName());
        display = new Display();

        // initialize a shell and make it global
        Shell shell = new Shell(display, SWT.DIALOG_TRIM);
        Main.shell = shell;

        createTrayIcon();

        // Dock icon for OS X and Windows task bar
        shell.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/RyCON_blank256x256.png"));
        shell.setText(I18N.getApplicationTitle());

        FormLayout formLayout = new FormLayout();
        shell.setLayout(formLayout);

        // 3 x 2 grid for the buttons
        Composite compositeGrid = new Composite(shell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = true;

        compositeGrid.setLayout(gridLayout);

        // listen to keyboard inputs. There is no modifier key used!
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
                        case 'p':
                            new SettingsWidget();
                            break;
                    }
                }
            }
        });

        enableDNDSupport();

        createButton1CleanTool(compositeGrid);
        createButton2SplitTool(compositeGrid);
        createButton3LevelTool(compositeGrid);
        createButton4ConvertTool(compositeGrid);
        createButton5ProjectTool(compositeGrid);
        createButton6Exit(compositeGrid);

        StatusBar statusBar = new StatusBar(shell, SWT.NONE);
        statusBar.setStatus(I18N.getStatusRyCONInitialized(), StatusBar.OK);
        Main.statusBar = statusBar;

        FormData formDataStatus = new FormData();
        formDataStatus.width = 3 * getRyCON_GRID_WIDTH() + 2; // width of the status bar!
        formDataStatus.bottom = new FormAttachment(100, -8);
        formDataStatus.left = new FormAttachment(0, 8);

        statusBar.setLayoutData(formDataStatus);

        if (pref.isDefaultSettingsGenerated()) {
            statusBar.setStatus(I18N.getMsgNewConfigFileGenerated(), StatusBar.WARNING);
        }

        shell.pack();

        // size depends on the grid size
        shell.setSize(3 * getRyCON_GRID_WIDTH() + 20, 2 * getRyCON_GRID_HEIGHT() + 100);

        shell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(shell));

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
                    applicationStarted();
                    firstStart = false;
                }
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
                    actionBtn6();
                }
            });

            item.addListener (SWT.MenuDetect, new Listener () {
                public void handleEvent (Event event) {
                    menu.setVisible (true);
                }
            });
        }
    }

    private void enableDNDSupport() {
        operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        fileTransfer = FileTransfer.getInstance();
        types = new Transfer[] {
                fileTransfer
        };
    }

    private void createButton1CleanTool(Composite composite) {
        Button btnToolboxClean = new Button(composite, SWT.PUSH);
        btnToolboxClean.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/1-clean.png"));
        btnToolboxClean.setText(I18N.getBtnCleanLabel());
        btnToolboxClean.setToolTipText(I18N.getBtnCleanLabelToolTip());

        btnToolboxClean.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn1();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxClean.setLayoutData(gridData);

        // Drag and drop for clean files tool
        DropTarget targetClean = new DropTarget(btnToolboxClean, operations);
        targetClean.setTransfer(types);

        targetClean.addDropListener(new DropTargetAdapter() {
            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] droppedFiles = (String[]) event.data;
                    File files[] = new File[droppedFiles.length];

                    for (int i = 0; i < droppedFiles.length; i++) {
                        files[i] = new File(droppedFiles[i]);
                    }

                    new TidyUpWidget(files).executeDropInjection();

                }
            }
        });
    }

    private void createButton2SplitTool(Composite composite) {
        Button btnToolboxSplitter = new Button(composite, SWT.PUSH);
        btnToolboxSplitter.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/2-code.png"));
        btnToolboxSplitter.setText(I18N.getBtnSplitterLabel());
        btnToolboxSplitter.setToolTipText(I18N.getBtnSplitterLabelToolTip());

        //register listener for the selection event
        btnToolboxSplitter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn2();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxSplitter.setLayoutData(gridData);

        // Drag and drop for splitter tool
        DropTarget targetSplitter = new DropTarget(btnToolboxSplitter, operations);
        targetSplitter.setTransfer(types);

        targetSplitter.addDropListener(new DropTargetAdapter() {

            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] droppedFiles = (String[]) event.data;
                    File files[] = new File[droppedFiles.length];

                    for (int i = 0; i < droppedFiles.length; i++) {
                        files[i] = new File(droppedFiles[i]);
                    }

                    new CodeSplitterWidget(files).executeDropInjection();

                }
            }

        });
    }

    private void createButton3LevelTool(Composite composite) {
        Button btnToolboxLeveling = new Button(composite, SWT.PUSH);
        btnToolboxLeveling.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/3-level.png"));
        btnToolboxLeveling.setText(I18N.getBtnLevelingLabel());
        btnToolboxLeveling.setToolTipText(I18N.getBtnLevelingLabelToolTip());

        //register listener for the selection event
        btnToolboxLeveling.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn3();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxLeveling.setLayoutData(gridData);

        DropTarget targetLevelling = new DropTarget(btnToolboxLeveling, operations);
        targetLevelling.setTransfer(types);

        targetLevelling.addDropListener(new DropTargetAdapter() {

            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] droppedFiles = (String[]) event.data;
                    File files[] = new File[droppedFiles.length];

                    for (int i = 0; i < droppedFiles.length; i++) {
                        files[i] = new File(droppedFiles[i]);
                    }

                    new LevellingWidget(files).executeDropInjection();

                }
            }

        });
    }

    private void createButton4ConvertTool(Composite composite) {
        Button btnToolboxConvert = new Button(composite, SWT.PUSH);
        btnToolboxConvert.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/4-convert.png"));
        btnToolboxConvert.setText(I18N.getBtnConvertLabel());
        btnToolboxConvert.setToolTipText(I18N.getBtnConvertLabelToolTip());

        //register listener for the selection event
        btnToolboxConvert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn4();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxConvert.setLayoutData(gridData);
    }

    private void createButton5ProjectTool(Composite composite) {
        Button btnToolboxGenerator = new Button(composite, SWT.PUSH);
        btnToolboxGenerator.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/5-project.png"));
        btnToolboxGenerator.setText(I18N.getBtnGeneratorLabel());
        btnToolboxGenerator.setToolTipText(I18N.getBtnGeneratorLabelToolTip());

        //register listener for the selection event
        btnToolboxGenerator.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn5();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnToolboxGenerator.setLayoutData(gridData);
    }

    private void createButton6Exit(Composite composite) {
        Button btnExit = new Button(composite, SWT.PUSH);
        btnExit.setImage(new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/icons/6-exit.png"));
        btnExit.setText(I18N.getBtnExitLabel());
        btnExit.setToolTipText(I18N.getBtnExitLabelToolTip());

        //register listener for the selection event
        btnExit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                actionBtn6();
            }
        });

        GridData gridData = new GridData(getRyCON_GRID_WIDTH(), getRyCON_GRID_HEIGHT());
        btnExit.setLayoutData(gridData);
    }

    /**
     * Main application startup
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        checkCommandLineInterfaceArguments(args);
        checkJavaVersion();
        checkRyCONVersion();
        checkLicense();
        initApplicationPreferences();

        // to provide illegal thread access -> https://github.com/udoprog/c10t-swt/issues/1
        // add -XstartOnFirstThread as an java option on VM parameter on OS X
        new MainApplication();
    }

    private void applicationStarted() {
        if (LICENSE) {
            NotificationPopupWidget.notify(I18N.getLicenseTitleFull(), I18N.getLicenseMsgFull(), NotificationType.values()[1], 4500);
        } else {
            NotificationPopupWidget.notify(I18N.getLicenseTitleDemo(), I18N.getLicenseMsgDemo(), NotificationType.values()[0], Integer.MAX_VALUE);
        }
    }

    private void actionBtn1() {
        new TidyUpWidget();
        statusBar.setStatus(I18N.getStatus1CleanInitialized(), StatusBar.OK);
    }

    private void actionBtn2() {
        new CodeSplitterWidget();
        statusBar.setStatus(I18N.getStatus2SplitterInitialized(), StatusBar.OK);
    }

    private void actionBtn3() {
        new LevellingWidget();
        statusBar.setStatus(I18N.getStatus3LevelInitialized(), StatusBar.OK);
    }

    private void actionBtn4() {
        new ConverterWidget();
        statusBar.setStatus(I18N.getStatus4ConverterInitialized(), StatusBar.OK);
    }

    private void actionBtn5() {
        new GeneratorWidget();
        statusBar.setStatus(I18N.getStatus5GeneratorInitialized(), StatusBar.OK);
    }

    private void actionBtn6() {
        statusBar.setStatus(I18N.getStatus6ExitInitialized(), StatusBar.OK);
        shell.getDisplay().dispose();
    }

} // end of MainApplication
