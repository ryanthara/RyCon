/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * This class displays a splash screen while RyCON is starting.
 * <p>
 * This is necessary because of a bug in Cocoa from Apple when 
 * starting a SWT-jar which needs -XstartOnFirstThread option!
 *  
 * Created by sebastian on 08.02.15.
 */
public class SplashScreen {
    
    private int splashPos = 0;
    
    private final int SPLASH_MAX = 100;
    
    public SplashScreen(Display display) {
        
        final Image image = new ImageConverter().convertToImage(display, "/de/ryanthara/ja/rycon/gui/RyCON_SplashScreen.png");
        
        final Shell splash = new Shell(SWT.ON_TOP);
        final ProgressBar progressBar = new ProgressBar(splash, SWT.NONE);
        progressBar.setMaximum(SPLASH_MAX);

        Label label = new Label(splash, SWT.NONE);
        label.setImage(image);

        FormLayout formLayout = new FormLayout();
        splash.setLayout(formLayout);

        FormData labelData = new FormData();
        labelData.right = new FormAttachment(100, 0);
        labelData.bottom = new FormAttachment(100, 0);
        label.setLayoutData(labelData);
        
        FormData progressBarData = new FormData();
        progressBarData.left = new FormAttachment(0, -5);
        progressBarData.right = new FormAttachment(100, 0);
        progressBarData.bottom = new FormAttachment(100, 0);
        progressBar.setLayoutData(progressBarData);
        
        splash.pack();

        Rectangle splashRect = splash.getBounds();
        Rectangle displayRect = display.getBounds();
        int x = (displayRect.width - splashRect.width) / 2;
        int y = (displayRect.height - splashRect.width) / 2;
        splash.setLocation(x, y);
        splash.open();

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

//                RyCONApplication.mainApplication.initUI();

                splash.close();
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
