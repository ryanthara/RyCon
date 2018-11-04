/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.transformer
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
package de.ryanthara.ja.rycon.core.transformer;

import com.swisstopo.geodesy.reframe_lib.IReframe;
import com.swisstopo.geodesy.reframe_lib.Reframe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Numeric examples are taken from REFRAME library Developer’s manual 09-07 A.
 */
@DisplayName("Testing the transformer class...")
class TransformerTest {
    /*
     * Consider a tests consist of three core parts: given, when and then.
     */

    private Reframe reframeObj;

    @BeforeEach
    void setUp() {
        reframeObj = new Reframe();
    }

    @Test
    void isReframePresent() {
        assertEquals("Reframe", reframeObj.getClass().getSimpleName());
        assertNotNull(reframeObj);
    }

    @Test
    void testReframeJar() {
        // Given easting and northing in LV03, usual height (LN02)
        double[] inputCoordinates = new double[]{601000.0, 197500.0, 555.0};

        try {
            // When
            double[] outputCoordinates = reframeObj.ComputeReframe(inputCoordinates, IReframe.PlanimetricFrame.LV03_Military, IReframe.PlanimetricFrame.LV95, IReframe.AltimetricFrame.LN02, IReframe.AltimetricFrame.Ellipsoid);

            /*
             * After this code:
             *  - outputCoordinates[0] (east in LV95)  = 2601000.030 m
             *  - outputCoordinates[1] (north in LV95) = 1197500.037 m
             *  - outputCoordinates[2] (ellipsoidal height on Bessel) = 554.335 m
             */

            // Then
            assertTrue(2601000.030 - outputCoordinates[0] <= 0.001);
            assertTrue(1197500.037 - outputCoordinates[1] <= 0.001);
            assertTrue(554.335 - outputCoordinates[2] <= 0.001);

            /*
             * After this code:
             *  - outputCoordinates[0] (longitude) = 7.451764 °
             *  - outputCoordinates[1] (latitude) = 46.928595 °
             *  - outputCoordinates[2] (ellipsoidal height on GRS80) = 604.004 m
             */

            outputCoordinates = reframeObj.ComputeGpsref(outputCoordinates, IReframe.ProjectionChange.LV95ToETRF93Geographic);

            assertTrue(7.451764 - outputCoordinates[0] <= 0.001);
            assertTrue(46.928595 - outputCoordinates[1] <= 0.001);
            assertTrue(604.004 - outputCoordinates[2] <= 0.001);
        } catch (IllegalArgumentException e) {
            // Coordinates outside Swiss TLM perimeter:
            // => height transformer impossible
            System.err.println("Outside grid");
        } catch (NullPointerException e) {
            // A binary data set file was not correctly loaded
            System.err.println("Data set file missing or not loaded correctly");
        } catch (Exception e) {
            System.err.println("Error 2");
        }
    }

}
