/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.tools
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

package de.ryanthara.ja.rycon.tools;

/*
 * The basic design of this class based on the ideas from Beanfabrics Framework
 * which is Copyright (C) 2011 by Michael Karneim, beanfabrics.org
 * Use is subject to license terms. See license.txt.
 *
 * Imported from https://code.google.com/p/beanfabrics/source/browse/trunk/beanfabrics/
 * beanfabrics-swt-samples/src/main/java/org/beanfabrics/swt/samples/filebrowser/
 * ImageConverter.java?r=498
 *
 * on November, 13th. 20014 by sebastian
 */

import com.bulenkov.iconloader.IconLoader;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

/**
 * This class converts images between AWT, Swing and SWT with support for Apple retina
 * displays. These high resolution displays need special image preparation.
 * <p>
 * This converter depends on the ImageConverter.class by Michael Karneim from
 * beanfabrics.org, who implements the basic functionality for converting the
 * AWT/Swing Images and Icons to SWT ImageDate objects.
 * <p>
 * The functions for retina support are added from IconLoader by Konstantin Bulenkov
 * from http://bulenkov.com/2013/06/23/retina-support-in-oracle-jdk-1-7/ with jar.
 * <p>
 * From me there are a couple of additional methods implemented.
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
public class ImageConverter {

    /**
     * Converts an image (GIF, JPG, PGN, ...) to {@code Image} object from swt
     * {@link org.eclipse.swt.graphics.Image}.
     *
     * @param display Display object on which the image is loaded (swt dependency)
     * @param path    String with the URL of the image as String
     * @return {@link org.eclipse.swt.graphics.Image} object
     */
    public Image convertToImage(Display display, String path) {
        return new Image(display, convertToImageData(convertToBufferedImage(IconLoader.getIcon(path))));
    }

    /**
     * Converts a {@code BufferedImage} object to an {@code ImageData} object.
     * @param bufferedImage the BufferedImage to convert
     * @return converted BufferedImage as ImageData
     */
    public ImageData convertToImageData(BufferedImage bufferedImage) {
        if (bufferedImage.getColorModel() instanceof DirectColorModel) {
            DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());

            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
            data.alphaData = new byte[bufferedImage.getWidth() * bufferedImage.getHeight()];

            WritableRaster alphaRaster = bufferedImage.getAlphaRaster();
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    int rgb = bufferedImage.getRGB(x, y);
                    int alpha = alphaRaster.getSample(x, y, 0);
                    data.alphaData[y * data.width + x] = (byte) alpha;
                    int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
                    data.setPixel(x, y, pixel);
                }
            }

            return data;
        } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
            IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];
            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);
            RGB[] rgbs = new RGB[size];
            for (int i = 0; i < rgbs.length; i++) {
                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
            }
            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    data.setPixel(x, y, pixelArray[0]);
                }
            }
            return data;
        }
        return null;
    }

    private BufferedImage convertToBufferedImage(Icon icon) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g2D = image.createGraphics();
        icon.paintIcon(null, g2D, 0, 0);
        g2D.dispose();

        return image;
    }

} // end of ImageConverter
