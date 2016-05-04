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

import de.ryanthara.ja.rycon.tools.ImageConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * SplashScreen displays a splash screen while RyCON is starting.
 * <p>
 * This is necessary because of a bug in Cocoa from Apple when 
 * starting a SWT-jar which needs -XstartOnFirstThread option!
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 1
 */
public class SplashScreen {
    
    private int splashPos = 0;
    private final int SPLASH_MAX = 100;

    /**
     * Constructor with most of the functionality.
     * <p>
     * This will be changed later.
     *
     * @param display display object
     */
    public SplashScreen(Display display) {

        final Image image = new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/RyCON_SplashScreen.png");
        final Shell shell = new Shell(SWT.ON_TOP);
        final ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
        progressBar.setMaximum(SPLASH_MAX);

        Label label = new Label(shell, SWT.NONE);
        label.setImage(image);

        Label infoText = new Label(shell, SWT.NONE);
        infoText.setText("APP started");

        FormLayout formLayout = new FormLayout();
        shell.setLayout(formLayout);

        FormData labelData = new FormData();
        labelData.right = new FormAttachment(100, 0);
        labelData.bottom = new FormAttachment(100, 0);
        label.setLayoutData(labelData);

        FormData progressBarData = new FormData();
        progressBarData.left = new FormAttachment(0, -5);
        progressBarData.right = new FormAttachment(100, 0);
        progressBarData.top = new FormAttachment(76, 0);
        progressBar.setLayoutData(progressBarData);

        FormData infoTextData = new FormData();
        infoTextData.left = new FormAttachment(0, -5);
        infoTextData.right = new FormAttachment(100, 5);
        infoTextData.bottom = new FormAttachment(progressBar, -50);
        infoText.setLayoutData(infoTextData);

        shell.pack();

        shell.setLocation(ShellPositioner.centerShellOnPrimaryMonitor(shell));
        shell.open();

        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                for (splashPos = 0; splashPos < SPLASH_MAX; splashPos++) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.setSelection(splashPos);
                }

                // RyCONApplication.mainApplication.initUI();

                shell.close();
                image.dispose();
            }
        });
        
        while (splashPos != SPLASH_MAX) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
    
} // end of SplashScreen
