package org.example;

import org.locationtech.jts.geom.Coordinate;

class MyCoordinate extends Coordinate {
    private final ShipRoutePlan aStarShipTrajectoryPlanning;

    public Coordinate getV() {
        return v;
    }

    public void setV(Coordinate v) {
        this.v = v;
    }

    private MyCoordinate parent_point;
    private double cost_distance;
    private Coordinate v;

    public double getCost_distance() {
        return cost_distance;
    }

    public void setCost_distance(double cost_distance) {
        this.cost_distance = cost_distance;
    }

    public MyCoordinate(
            ShipRoutePlan aStarShipTrajectoryPlanning, double x,
            double y) {
        super(x, y);
        this.aStarShipTrajectoryPlanning = aStarShipTrajectoryPlanning;
    }

    public MyCoordinate getParent_point() {
        return parent_point;
    }

    public void setParent_point(
            MyCoordinate parent_point) {
        this.parent_point = parent_point;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        MyCoordinate that = (MyCoordinate) other;
        // 自定义的坐标比较逻辑
        double tolerance = 1;  // 允许的误差范围
        return this.distance(that) < tolerance;
    }
}
