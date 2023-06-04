package org.example;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

public class Circle extends Polygon {
    private final double radius;
    private final Coordinate center;

    public Circle(Coordinate center, double radius) {
        super(createCircleGeometry(center, radius), null,
              new GeometryFactory());
        this.center = center;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public Coordinate getCenter() {
        return center;
    }

    private static LinearRing createCircleGeometry(Coordinate center,
                                                   double radius) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = createCircleCoordinates(center, radius);
        try {
            return geometryFactory.createLinearRing(coordinates);
        } catch (Exception e) {
            return null;
        }
    }

    private static Coordinate[] createCircleCoordinates(Coordinate center,
                                                        double radius) {
        int sides = 20; // 圆形的边数
        double angleIncrement = (2 * Math.PI) / sides;
        Coordinate[] coordinates = new Coordinate[sides + 1];

        for (int i = 0; i < sides; i++) {
            double angle = i * angleIncrement;
            double x = center.x + radius * Math.cos(angle);
            double y = center.y + radius * Math.sin(angle);
            coordinates[i] = new Coordinate(x, y);
        }

        // 将第一个坐标点重复一次作为最后一个坐标点，以确保线性环闭合
        coordinates[sides] = coordinates[0];

        return coordinates;
    }

}
