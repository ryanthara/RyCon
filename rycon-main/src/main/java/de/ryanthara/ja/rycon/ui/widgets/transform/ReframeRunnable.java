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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.ryanthara.ja.rycon.i18n.ResourceBundles.TEXT;

/**
 * Computes the reframe within a thread to update the progress bar on the gui
 * of the {@link de.ryanthara.ja.rycon.ui.widgets.TransformationWidget}.
 *
 * @author sebastian
 * @version 1
 * @since 26
 */
public class ReframeRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReframeRunnable.class.getName());

    private final ProgressBar progressBar;
    private final Label progressLabel;
    private final BottomButtonBar bottomButtonBar;
    private final double[][] coordinates;
    private final double[][] outputCoordinates;
    private final IReframe.PlanimetricFrame planimetricFrameSource;
    private final IReframe.PlanimetricFrame planimetricFrameTarget;
    private final IReframe.AltimetricFrame altimetricFrameSource;
    private final IReframe.AltimetricFrame altimetricFrameTarget;
    private final Reframe reframeObj = new Reframe();
    private boolean cancel;
    private boolean finished;

    /**
     * @param progressBar            the progress bar reference
     * @param progressLabel          the progress label
     * @param bottomButtonBar        the bottom button bar with ok, ok and exit and cancel button
     * @param coordinates            the input coordinates
     * @param planimetricFrameSource planimetric frame of the source / input
     * @param planimetricFrameTarget planimetric frame of the target / output
     * @param altimetricFrameSource  altimetric frame of the source / input
     * @param altimetricFrameTarget  altimetric frame of the source / input
     */
    public ReframeRunnable(ProgressBar progressBar, Label progressLabel, BottomButtonBar bottomButtonBar, double[][] coordinates, IReframe.PlanimetricFrame planimetricFrameSource, IReframe.PlanimetricFrame planimetricFrameTarget, IReframe.AltimetricFrame altimetricFrameSource, IReframe.AltimetricFrame altimetricFrameTarget) {
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;
        this.bottomButtonBar = bottomButtonBar;
        this.coordinates = coordinates;
        this.planimetricFrameSource = planimetricFrameSource;
        this.planimetricFrameTarget = planimetricFrameTarget;
        this.altimetricFrameSource = altimetricFrameSource;
        this.altimetricFrameTarget = altimetricFrameTarget;

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

    /**
     * Returns true if the thread work is done.
     *
     * @return true if thread work is done
     */
    public boolean isFinished() {
        return finished;
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
     * Runs the thread and perform the operations.
     */
    @Override
    public void run() {
        if (getDisplay().isDisposed()) {
            return;
        }

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
    }

    private void computeReframe(int i) {
        try {
            outputCoordinates[i] = reframeObj.ComputeReframe(
                    coordinates[i],
                    planimetricFrameSource,
                    planimetricFrameTarget,
                    altimetricFrameSource,
                    altimetricFrameTarget);
        } catch (IllegalArgumentException e) {
            logger.error("Input coordinates outside Swiss TLM parameter. Height transformation impossible.", e.getMessage());
        } catch (NullPointerException e) {
            logger.error("Binary data set not loaded correctly.", e.getCause());
        } catch (Exception e) {
            logger.error("Unknown exception {} thrown.", e.getMessage(), e);
        }
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

    /**
     * Cancels this thread.
     */
    public void cancel() {
        this.cancel = true;
    }

}
