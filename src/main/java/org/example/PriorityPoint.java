package org.example;

class PriorityPoint {
    private double priority;
    private MyCoordinate point;

    public boolean equal(MyCoordinate end) {
        return end.equals(this.point);
    }

    public PriorityPoint(double priority,
                         MyCoordinate point) {
        this.priority = priority;
        this.point = point;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public MyCoordinate getPoint() {
        return point;
    }

    public void setPoint(MyCoordinate point) {
        this.point = point;
    }
}
