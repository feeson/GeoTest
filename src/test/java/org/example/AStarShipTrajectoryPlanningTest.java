package org.example;

import junit.framework.TestCase;
import org.locationtech.jts.geom.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AStarShipTrajectoryPlanningTest extends TestCase {
    public class Circle extends GeometryCollection {
        public Circle(Point center, double radius) {
            super(new Geometry[] { center.buffer(radius) }, new GeometryFactory());
        }
    }
    public static void main(String[] args) {

    }
}