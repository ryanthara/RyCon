/*
 * License: MIT. Copyright (c) 2017 by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.zfex.i18n
 *
 * Copyright (c) 2017 ryanthara
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.ryanthara.ja.rycon.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The <tt>XMLResourceBundleControl</tt> extends the {@link ResourceBundle.Control} for using a XML document
 * instead of a properties file.
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
public class XMLResourceBundleControl extends ResourceBundle.Control {

    private static final Logger logger = LoggerFactory.getLogger(XMLResourceBundleControl.class.getName());

    private static final String XML = "xml";
    private static final List<String> SINGLETON_LIST = Collections.singletonList(XML);

    /**
     * Returns the list of strings.
     *
     * @param baseName base name string
     *
     * @return list of strings
     */
    @Override
    public List<String> getFormats(String baseName) {
        return SINGLETON_LIST;
    }

    /**
     * Creates a new {@link ResourceBundle} with a set of parameters.
     *
     * @param baseName base name string
     * @param locale   used locale
     * @param format   format string
     * @param loader   reference to the {@link ClassLoader}
     * @param reload   force reloading
     *
     * @return loaded {@link ResourceBundle}
     *
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @throws IOException            IOException
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {

        if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
            logger.error("baseName, locale, format and loader cannot be null");
            throw new IllegalArgumentException("baseName, locale, format and loader cannot be null");
        }

        if (!format.equals(XML)) {
            logger.error("format must be xml");
            throw new IllegalArgumentException("format must be xml");
        }

        final String bundleName = toBundleName(baseName, locale);
        final String resourceName = toResourceName(bundleName, format);
        final URL url = loader.getResource(resourceName);
        if (url == null) {
            logger.warn("URL is null.");
            return null;
        }

        final URLConnection urlconnection = url.openConnection();
        if (urlconnection == null) {
            logger.warn("URLConnection is null");
            return null;
        }

        if (reload) {
            urlconnection.setUseCaches(false);
        }

        try (final InputStream stream = urlconnection.getInputStream();
             final BufferedInputStream bis = new BufferedInputStream(stream)
        ) {
            return new XMLResourceBundle(bis);
        }
    }

} // end of XMLResourceBundleControl
