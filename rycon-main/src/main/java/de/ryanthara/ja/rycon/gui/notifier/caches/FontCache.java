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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import java.util.ArrayList;

/**
 * Class that holds font objects.
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
 *
 */
class AFont {

    private int height;
    private int style;
    private Font font;
    private String name;

    /**
     * Class constructor which accepts a {@code FontData} object as parameter.
     * @param fontData use {@code FontData} object for definition
     */
    public AFont(FontData fontData) {
        name = fontData.getName();
        font = new Font(Display.getDefault(), fontData);
        height = fontData.getHeight();
        style = fontData.getStyle();
    }

    /**
     * Class constructor which accepts different values as parameter.
     * @param name font name
     * @param height height as integer value
     * @param style SWT value for the style
     */
    public AFont(String name, int height, int style) {
        this.name = name;
        font = new Font(Display.getDefault(), name, height, style);
        this.height = height;
        this.style = style;
    }

    /**
     * Returns the font.
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the font to a given font.
     * @param font font to be set to
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Returns the height
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height to a given height.
     * @param height height to be set to
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the name of the font.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name to a given name.
     * @param name name to be set to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the style object.
     * @return the style
     */
    public int getStyle() {
        return style;
    }

    /**
     * Sets the style to a given style.
     * @param style style to be set to
     */
    public void setStyle(int style) {
        this.style = style;
    }

    /**
     * Compares the given {@code FontData} object to this.
     * @param fontData object to compare to
     * @return true if is identical
     */
    public boolean matches(FontData fontData) {
        return (fontData.getName().equals(name) && fontData.getHeight() == height && fontData.getStyle() == style);
    }

} // end of AFont

/**
 * Instances of this class caches fonts for usage in SWT components.
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
public class FontCache {

    private static ArrayList<AFont> fontMap = new ArrayList<>();

    /**
     * Disposes all fonts and clear the cache. 
     */
    public static void disposeFonts(){
        for (AFont aFont : fontMap) {
            if ((aFont.getFont() == null) && (!aFont.getFont().isDisposed())) {
                aFont.getFont().dispose();
            }
        }

        fontMap.clear();
    }

    /**
     * Returns a font for an existing {@code FontData} object.
     * @param fontData fontData object
     * @return the font
     */
    public static Font getFont(FontData fontData) {
        try {
            boolean disposed = false;
            AFont toRemove = null;

            for (AFont aFont : fontMap) {
                if (aFont.matches(fontData)) {
                    if (aFont.getFont().isDisposed()) {
                        disposed = true;
                        toRemove = aFont;
                        break;
                    }

                    return aFont.getFont();
                }
            }

            if (disposed) {
                fontMap.remove(toRemove);
            }

            AFont aFont = new AFont(fontData);
            fontMap.add(aFont);

            return aFont.getFont();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a font for an existing {@code Font} object.
     * @param font font object
     * @return the font
     */
    public static Font getFont(Font font) {
        try {
            if ((font == null) || (font.isDisposed())) {
                return null;
            }
            FontData fontData = font.getFontData()[0];
            return getFont(fontData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a font for a given name, height and style.
     * @param name font name
     * @param height height as integer value
     * @param style SWT value for the style
     * @return the font
     */
    public static Font getFont(String name, int height, int style) {
        try {
            boolean disposed = false;
            AFont toRemove = null;

            for (AFont aFont : fontMap) {
                if (aFont.getName().equals(name) && aFont.getHeight() == height && aFont.getStyle() == style) {
                    if (aFont.getFont().isDisposed()) {
                        disposed = true;
                        toRemove = aFont;
                        break;
                    }

                    return aFont.getFont();
                }
            }

            if (disposed) {
                fontMap.remove(toRemove);
            }

            AFont aFont = new AFont(name, height, style);
            fontMap.add(aFont);

            return aFont.getFont();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

} // end of ImageCache
