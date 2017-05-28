package com.svt.transitions;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.*;

public class Transition {

    public final Point[] path;

    public Transition(Point[] path) {
        this.path = path;
    }

    public static Transition parseXML(Element element) {

        NodeList pointsNodes = element.getElementsByTagName("point");

        Point[] path = new Point[pointsNodes.getLength()];

        for (int i = 0; i < path.length; i++) {

            Element point = (Element) pointsNodes.item(i);

            int x = Integer.parseInt(point.getAttribute("x"));
            int y = Integer.parseInt(point.getAttribute("y"));

            path[i] = new Point(x * 2, y * 2);
        }

        return new Transition(path);
    }
}
