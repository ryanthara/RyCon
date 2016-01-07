/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.notifier.caches
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

package de.ryanthara.ja.rycon.gui.notifier.caches;

import de.ryanthara.ja.rycon.Main;
import de.ryanthara.ja.rycon.tools.ImageConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Instances of this class caches colors for usage in SWT components.
 * <p>
 * This function is used as a test for the notification widget which is implemented in RyCON.
 * <p>
 * The implementation is inspired by an article on <a href="http://hexapixel.com/2009/06/30/creating-a-notification-popup-widget">hexapixel.com</a>
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up </li>
 *     <li>1: basic implementation </li>
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 2
 */
public class ColorCache {

    private static ColorCache colorCache;
    private static HashMap<RGB, Color> colorMap = new HashMap<RGB, Color>();

    private ColorCache() {
        colorCache = this;
    }

    /**
     * Default RGB for black.
     */
    public static final RGB BLACK = new RGB(0, 0, 0);

    /**
     * Default RGB for white.
     */
    public static final RGB WHITE = new RGB(255, 255, 255);

    // static constructor because of getInstance()
    static {
        // TODO check if this is good and necessary
        new ColorCache();
    }

    /**
     * Dispose all colors that have been cached.
     * <p>
     * This is necessary because of memory usage. See the <a href="http://www.eclipse.org/articles/swt-design-2/swt-design-2.html">article</a>
     */
    public static void disposeColors() {

        Iterator<Color> iterator = colorMap.values().iterator();

        while (iterator.hasNext()) {
            iterator.next().dispose();
        }

    }

    /**
     * Returns the black as {@code Color}
     * @return the black as {@code Color}
     */
    public static Color getBlack() {
        return getColorFromRGB(BLACK);
    }

    /**
     * Returns the Color of given value for the red, the green and the blue.
     * <p>
     * On first load the  {@code Color} will be cached. After this it is possible
     * to get it very fast from the memory.
     *
     * @param r the value for red
     * @param g the value for green
     * @param b the value for blue
     * @return the color as {@code Color} object
     */
    public static Color getColor(int r, int g, int b) {

        RGB rgb = new RGB(r, g, b);

        Color color = colorMap.get(rgb);

        // try to get the color from the HashMap
        if (color == null) {
                    color = new Color(Display.getCurrent(), rgb);
            colorMap.put(rgb, color);
        }

        return color;

    }

    /**
     * Returns the Color of a given RGB.
     * <p>
     * On first load the  {@code Color} will be cached. After this it is possible
     * to get it very fast from the memory.
     *
     * @param rgb the rgb object
     * @return the color as {@code Color} object
     */
    public static Color getColorFromRGB(RGB rgb) {

        return getColor(rgb.red, rgb.green, rgb.blue);

    }

    /**
     * Returns an instance to the {@code ColorCache}.
     * @return an instance of this {@code ColorCache}
     */
    public static ColorCache getInstance(){
        return colorCache;
    }

    /**
     * Returns the white as {@code Color}
     * @return the white as {@code Color}
     */
    public static Color getWhite() {
        return getColorFromRGB(WHITE);
    }

    private static Image createImage(String fileName) {

        // TODO the original functionality was implemented with a Classloader and images in a jar file

        return new ImageConverter().convertToImage(Main.shell.getDisplay(), fileName);

    }

} // end of ImageCache
