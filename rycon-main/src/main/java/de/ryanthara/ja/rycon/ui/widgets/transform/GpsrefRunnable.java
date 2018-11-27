/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.widgets.transform
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
package de.ryanthara.ja.rycon.ui.widgets.transform;

import com.swisstopo.geodesy.reframe_lib.IReframe;
import com.swisstopo.geodesy.reframe_lib.Reframe;
import de.ryanthara.ja.rycon.i18n.ResourceBundleUtils;
import de.ryanthara.ja.rycon.i18n.Texts;
import de.ryanthara.ja.rycon.ui.custom.BottomButtonBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import java.util.Arrays;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXT;

/**
 * Computes the gpsref for an array of points in a runnable object to update
 * the progress bar in the {@link de.ryanthara.ja.rycon.ui.widgets.TransformationWidget}.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public class GpsrefRunnable implements Runnable {

    private final ProgressBar progressBar;
    private final Label progressLabel;
    private final BottomButtonBar bottomButtonBar;
    private final IReframe.ProjectionChange projectionChange;
    private final Reframe reframeObj = new Reframe();
    private final double[][] coordinates;
    private final double[][] outputCoordinates;
    private volatile boolean isStopped = false;
    private volatile Thread runThread;
    private boolean finished;
    private boolean cancel;

    public GpsrefRunnable(ProgressBar progressBar, Label progressLabel, BottomButtonBar bottomButtonBar, double[][] coordinates, IReframe.ProjectionChange projectionChange) {
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;
        this.bottomButtonBar = bottomButtonBar;
        this.coordinates = coordinates;
        this.projectionChange = projectionChange;

        outputCoordinates = new double[coordinates.length][3];
    }

    private static Display getDisplay() {
        Display display = Display.getCurrent();

        //may be null if outside the UI thread
        if (display == null) {
            return Display.getDefault();
        }

        return display;
    }

    private void updatedGUIWhenStart() {
        Display display = getDisplay();

        display.asyncExec(() -> {
            bottomButtonBar.disableOkButton();
            bottomButtonBar.disableOkAndExitButton();
        });
    }

    private void updateGUIWhenFinish() {
        Display display = getDisplay();

        display.asyncExec(() -> {
            bottomButtonBar.enableOkButton();
            bottomButtonBar.enableOkAndExitButton();

            progressBar.setSelection(0);
            progressBar.setMaximum(1);

            if (cancel) {
                String cancelled = ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.cancelled);
                progressLabel.setText(cancelled);
            } else {
                String finished = ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.finished);
                progressLabel.setText(finished);
            }

            finished = true;
        });
    }

    private void updateGUIInProgress(int value, int count) {
        Display display = getDisplay();

        /*
         * For a smooth progressbar use different delays in milliseconds.
         */
        int delay = 0;

        if (count < 10000) {
            delay = 1;
        }

        if (count < 1000) {
            delay = 2;
        }

        if (count < 100) {
            delay = 5;
        }

        display.asyncExec(() -> {
            if (progressBar.isDisposed() || progressLabel.isDisposed()) {
                return;
            }

            progressBar.setMaximum(count);
            progressBar.setSelection(value + 1);

            String info = ResourceBundleUtils.getLangStringFromXml(TEXT, Texts.transformation_PointOfPoints);

            progressLabel.setText(String.format(info, (value + 1), count));
        });

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public boolean isStopped() {
        return isStopped;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (getDisplay().isDisposed()) {
            return;
        }

        runThread = Thread.currentThread();

        int count = coordinates.length;

        updatedGUIWhenStart();

        for (int i = 0; i < count; i++) {
            if (cancel) {
                break;
            }

            computeReframe(i);

            updateGUIInProgress(i, count);
        }

        updateGUIWhenFinish();

        /*
        while (isStopped == false) {
            System.out.println(
                    "not stopped..."
            );
        }
        */

    }

    private void computeReframe(int i) {
        // Transform LV95 coordinates to ETRS89 longitude/latitude
        // and ellipsoidal height on Bessel to GRS80
        try {
            outputCoordinates[i] = reframeObj.ComputeGpsref(coordinates[i], projectionChange);
            // outputCoordinates now contains transformed coordinates...

            System.out.println(Arrays.toString(outputCoordinates[i]));
        } catch (Exception e) {
            // exception
        }
    }

    public void stopRequest() {
        isStopped = true;

        if (runThread != null) {
            runThread.interrupt();
        }
    }

    /**
     * Returns the computed reframe results.
     *
     * @return computed coordinates
     */
    public double[][] getResult() {
        return outputCoordinates;
    }

    /**
     * Cancels this thread.
     */
    public void cancel() {
        this.cancel = true;
    }

}
