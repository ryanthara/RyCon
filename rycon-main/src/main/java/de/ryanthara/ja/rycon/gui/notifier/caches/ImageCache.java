/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
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
import org.eclipse.swt.graphics.Image;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Instances of this class caches images for usage in SWT components.
 * <p>
 * This function is used as a test for the notification widget which is implemented in RyCON.
 * <p>
 * The implementation is inspired by an article on <a href="http://hexapixel.com/2009/06/30/creating-a-notification-popup-widget">hexapixel.com</a>
 *
 * <h3>Changes:</h3>
 * <ul>
 *     <li>2: code improvements and clean up</li>
 *     <li>1: basic implementation
 * </ul>
 *
 * @author sebastian
 * @version 2
 * @since 2
 */
public class ImageCache {

    private static HashMap<String, Image> imageMap = new HashMap<String, Image>();
    private static final String IMAGE_ROOT_PATH = "/de/ryanthara/ja/rycon/gui/icons/";

    private static Image createImage(String fileName) {
        // TODO the original functionality was implemented with a Classloader and images in a jar file
        return new ImageConverter().convertToImage(Main.shell.getDisplay(), fileName);
    }

    /**
     * Returns the image.
     * <p>
     * On first load the  {@code Image} will be cached. After this it is possible to get it
     * very fast from the memory.
     *
     * @param fileName filename of the image
     * @return the image as {@code Image} object
     */
    public static Image getImage(String fileName) {
        fileName = IMAGE_ROOT_PATH.concat(fileName);

        // try to get the image from the HashMap
        Image image = imageMap.get(fileName);

        if (image == null) {
            image = createImage(fileName);
            imageMap.put(fileName, image);
        }

        return image;
    }

    /**
     * Dispose all images that have been cached.
     * <p>
     * This is necessary because of memory usage. See this <a href="http://www.eclipse.org/articles/swt-design-2/swt-design-2.html">article</a>.
     */
    public static void dispose() {
        Iterator<Image> iterator = imageMap.values().iterator();

        while (iterator.hasNext()) {
            iterator.next().dispose();
        }
    }

} // end of ImageCache
