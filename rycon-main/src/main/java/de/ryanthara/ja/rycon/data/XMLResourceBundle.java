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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The XMLResourceBundle extends the {@link ResourceBundle} for using a XML document instead of a properties file.
 *
 * @author sebastian
 * @version 1
 * @since 1
 */
class XMLResourceBundle extends ResourceBundle {

    private final Properties properties;

    /**
     * Constructor which accepts a reference to the {@link InputStream} of the XML file.
     *
     * @param stream InputStream for the XML file
     * @throws IOException InputStream exception
     */
    XMLResourceBundle(InputStream stream) throws IOException {
        properties = new Properties();
        properties.loadFromXML(stream);
    }

    /**
     * Returns an enumeration of the keys.
     *
     * @return an <code>Enumeration</code> of the keys contained in
     * this <code>ResourceBundle</code> and its parent bundles.
     */
    @Override
    public Enumeration<String> getKeys() {
        Set<String> handleKeys = properties.stringPropertyNames();
        return Collections.enumeration(handleKeys);
    }

    /**
     * Gets an object for the given key from this resource bundle.
     * <p>
     * Returns null if this resource bundle does not contain an
     * object for the given key.
     *
     * @param key the key for the desired object
     * @return the object for the given key, or null
     * @throws NullPointerException if <code>key</code> is <code>null</code>
     */
    @Override
    protected Object handleGetObject(String key) {
        return properties.getProperty(key);
    }

}
