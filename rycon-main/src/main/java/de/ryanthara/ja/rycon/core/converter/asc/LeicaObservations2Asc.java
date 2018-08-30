/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.core.converter.asc
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
package de.ryanthara.ja.rycon.core.converter.asc;

import de.ryanthara.ja.rycon.core.converter.Converter;
import de.ryanthara.ja.rycon.util.DummyCoordinates;
import org.eclipse.swt.graphics.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Instances of this class provides functions to convert different XML based Leica Geosystems
 * level observations files into an ascii file.
 * <p>
 * The line based ascii file contains one point (no x y z) in every line which coordinates
 * are separated by a single white space character.
 * <p>
 * The point coordinates are taken from the Leica Geosystems level protocol file if present.
 * Otherwise they will be set to local values starting at 0,0 and raise in both axis by a constant value.
 *
 * @author sebastian
 * @version 1
 * @since 27
 */
public class LeicaObservations2Asc extends Converter {

    private static final Logger logger = LoggerFactory.getLogger(LeicaObservations2Asc.class.getName());

    private final boolean ignoreChangePoints;
    private final Path path;

    /**
     * Constructs a new instance of this class with a parameter
     * for the Leica Geosystems observations protocol (*.LEV).
     *
     * @param path               path to xml file
     * @param ignoreChangePoints change points with number '0' has to be ignored
     */
    public LeicaObservations2Asc(Path path, boolean ignoreChangePoints) {
        this.path = path;
        this.ignoreChangePoints = ignoreChangePoints;

    }

    /**
     * Converts a read Leica Geosystems protocol file (*.ASC) into an ascii file
     * with pseudo coordinates for x and y.
     *
     * @return converted Nigra altitude register format file
     */
    @Override
    public ArrayList<String> convert() {
        ArrayList<String> result = new ArrayList<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(path.toFile());

            // optional, but recommend
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            // catch all points
            NodeList nList = doc.getElementsByTagName("Point");

            ArrayList<Point> dummyCoordinates = DummyCoordinates.getList(nList.getLength());

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String number = eElement.getAttribute("PointId");

                    Point p = dummyCoordinates.get(i);
                    String x = Integer.toString(p.x) + ".000";
                    String y = Integer.toString(p.y) + ".000";

                    String height = eElement.getAttribute("HeightOrtho");

                    if (ignoreChangePoints) {
                        if (!number.equals("0")) {
                            result.add(number.trim() + Converter.SEPARATOR + x + Converter.SEPARATOR + y + Converter.SEPARATOR + height.trim());
                        }
                    } else {
                        result.add(number.trim() + Converter.SEPARATOR + x + Converter.SEPARATOR + y + Converter.SEPARATOR + height.trim());
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            logger.error("Can not establish a document builder for '{}'.", path.toString(), e.getCause());
        } catch (SAXException e) {
            logger.error("Can not parse the document '{}'.", path.toString(), e.getCause());
        } catch (IOException e) {
            logger.error("Can not read the file '{}'.", path.toString(), e.getCause());
        }

        return new ArrayList<>(result);
    }

} // end of LeicaObservations2Asc
