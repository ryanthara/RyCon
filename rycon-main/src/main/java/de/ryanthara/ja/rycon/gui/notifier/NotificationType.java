/*
 * License: GPL. Copyright 2014- (C) by Sebastian Aust (http://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.notifier
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

package de.ryanthara.ja.rycon.gui.notifier;

import de.ryanthara.ja.rycon.gui.notifier.caches.ImageCache;
import org.eclipse.swt.graphics.Image;

/**
 * This enumeration holds different images for the {@code NotificationPopupWidget}.
 * <p>
 * In this basic version there are less images used. They are all taken from the
 * <a href="https://code.google.com/p/gnome-colors/">gnome-colors project</a>.
 * The following icons can be used:
 * <ul>
 *     <li>LOCKED - shows a closed lock
 *     <li>UNLOCKED - shows an open lock
 * </ul>
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
public enum NotificationType {

    LOCKED(ImageCache.getImage("locked.png")),
    UNLOCKED(ImageCache.getImage("unlocked.png"));

    private final Image image;

    private NotificationType(Image image) {
        this.image = image;
    }

    /**
     * Returns the {@code Image} object.
     *
     * @return the image as {@code Image}
     */
    public Image getImage() {
        return image;
    }

} // end of NotificationType
