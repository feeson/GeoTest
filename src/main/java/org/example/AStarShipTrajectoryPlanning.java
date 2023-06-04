package org.example;

import org.locationtech.jts.geom.*;

import java.util.*;

public class AStarShipTrajectoryPlanning {
    MyCoordinate start_point;
    MyCoordinate end_point;
    Coordinate vs;
    double estimate_distance;
    Coordinate indexVct;
    Ship ship;
    public AStarShipTrajectoryPlanning(double sx,double sy,double svx,double svy,double ex,double ey,Ship ship,double diff){
        start_point= new MyCoordinate(Math.floor(sx),Math.floor(sy));
        start_point.setParent_point(start_point);
        start_point.setCost_distance(0);
        end_point= new MyCoordinate(Math.floor(ex),Math.floor(ey));
        estimate_distance=start_point.distance(end_point);
        vs=new Coordinate(svx,svy);
        start_point.setV(vs);
        this.ship=ship;
        this.diff=diff;

        this.indexVct=new Coordinate(ex-sx,ey-sy);
        normalize(indexVct);


    }
    private void normalize(Coordinate coordinate){
        double x=coordinate.getX();
        double y=coordinate.getY();
        double distance=Math.sqrt(x*x+y*y);
        coordinate.setX(x/distance);
        coordinate.setY(y/distance);
    }
    class MyCoordinate extends Coordinate{
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

        public MyCoordinate(double x, double y) {
            super(x,y);
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
            double tolerance = diff-1;  // 允许的误差范围
            return this.distance(that)<tolerance;
        }
    }
    class PriorityPoint {
        private double priority;
        private MyCoordinate point;

        public boolean equal(MyCoordinate end){
            return end.equals(this.point);
        }

        public PriorityPoint(double priority, MyCoordinate point) {
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
    public static class Circle extends Polygon {
        private final double radius;
        private final Coordinate center;

        public Circle(Coordinate center, double radius) {
            super(createCircleGeometry(center, radius), null, new GeometryFactory());
            this.center = center;
            this.radius = radius;
        }

        public double getRadius() {
            return radius;
        }

        public Coordinate getCenter() {
            return center;
        }

        private static LinearRing createCircleGeometry(Coordinate center, double radius) {
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate[] coordinates = createCircleCoordinates(center, radius);
            try {
                return geometryFactory.createLinearRing(coordinates);
            }catch (Exception e){
                return null;
            }
        }

        private static Coordinate[] createCircleCoordinates(Coordinate center, double radius) {
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
    public  class MyCoordinateSet extends HashSet<MyCoordinate>{
        @Override
        public boolean contains(Object obj) {
            if (!(obj instanceof MyCoordinate)) {
                return false;
            }

            MyCoordinate target = (MyCoordinate) obj;

            for (MyCoordinate coordinate : this) {
                double tolerance = diff-1;  // 允许的误差范围
                return coordinate.distance(target)<tolerance;
            }

            return false;
        }
    }
    GeometryFactory geometryFactory = new GeometryFactory();
    PriorityQueue<PriorityPoint> open_set = new PriorityQueue<>(
            Comparator.comparingDouble(PriorityPoint::getPriority));
    MyCoordinateSet close_set=new MyCoordinateSet();
    double diff;
    Window window;
    public List<Coordinate> getPath(){
        PriorityPoint start = new PriorityPoint(0, start_point);
        open_set.add(start);
        window=new Window("Routing");
        while (!open_set.isEmpty()){
            PriorityPoint poll = open_set.poll();

//            window.drawPoint(poll.getPoint().getX(), poll.getPoint().getY());

            if (poll.equal(end_point)){
                List<Coordinate> path=new ArrayList<>();
                MyCoordinate cur=poll.getPoint();
                while (cur.parent_point!=start_point){
                    path.add(cur);
                    cur=cur.parent_point;
                }
                return path;
            }else {
                close_set.add(poll.getPoint());
                MyCoordinate[] coordinates=getNeighbour(poll.getPoint(),poll.getPoint().getParent_point());
                try {
                    for (int i=0;i<coordinates.length;++i){
                        MyCoordinate coordinate = coordinates[i];
                        if (close_set.contains(coordinate))
                            continue;
                        double priority=getPriority(coordinate,poll.getPoint());
                        PriorityPoint priorityPoint = new PriorityPoint(priority,coordinate);
                        open_set.add(priorityPoint);
                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

            }
        }
        return null;
    }
    Coordinate origin=new Coordinate(0,0);
    private double getPriority(MyCoordinate coordinate,MyCoordinate parentCoordinate){
        double to_parent=coordinate.distance(parentCoordinate);
        coordinate.setCost_distance(parentCoordinate.getCost_distance()+to_parent);

        Coordinate v = coordinate.getV();

        double parent_parent_estimate=parentCoordinate.getParent_point().distance(end_point);
        double parent_estimate=parentCoordinate.distance(end_point);
        double me_estimate=coordinate.distance(end_point);

        double value =Math.pow(coordinate.distance(end_point),2) +Math.sqrt(coordinate.getCost_distance()) ;

        //奖励
        Coordinate vct=new Coordinate(v.getX(),v.getY());
        normalize(vct);
        value-=value* (vct.getX()*indexVct.getX()+vct.getY()* indexVct.getY()-1)*0.12;

        if (me_estimate<parent_estimate){
            value-=diff*diff;
            value*=0.8;
        }
        //
        return value;
    }
    public static Coordinate getVCoordinate(double distance,Coordinate start,Coordinate end){
        double deltaX=end.getX()-start.getX();
        double deltaY=end.getY()-start.getY();
        double coff=Math.sqrt(deltaX*deltaX+deltaY*deltaY);
        return new Coordinate(deltaX/coff*distance,deltaY/coff*distance);
    }
    public static Coordinate[] getPerpendicularCoordinates(Coordinate point1, Coordinate point2, double distance) {
        point2.setX(point2.getX()+point1.getX());
        point2.setY(point2.getY()+point1.getY());
        LineSegment lineSegment = new LineSegment(point1, point2);
        Coordinate[] perpendicularCoordinates = new Coordinate[2];

        // 计算垂线方向上的两个点
        double deltaX = point2.x - point1.x;
        double deltaY = point2.y - point1.y;
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double unitX = deltaX / length;
        double unitY = deltaY / length;
        double offsetX = -distance * unitY;
        double offsetY = distance * unitX;

        perpendicularCoordinates[0] = new Coordinate(point1.x + offsetX, point1.y + offsetY);
        perpendicularCoordinates[1] = new Coordinate(point1.x - offsetX, point1.y - offsetY);

        return perpendicularCoordinates;
    }
    public MyCoordinate[] getNeighbour(MyCoordinate coordinate,MyCoordinate parentCoordinate){
        Coordinate v = coordinate.getV();
        double speed=origin.distance(v);
        double gyrationRadius = PhysicUtil.getGyrationRadius(speed,
                                                             ship.powerYaw);
        double next_speed=Math.sqrt(PhysicUtil.getSquareAcceleratedSpeed(speed,diff,ship.getEnginePower(),ship.getDragCoefficient(),ship.getMass()));

        Coordinate[] circleCenters = getPerpendicularCoordinates(
                coordinate, v, gyrationRadius-1);
        Circle circle_l=new Circle(circleCenters[0],gyrationRadius);
        Circle circle_r=new Circle(circleCenters[1],gyrationRadius);

        Coordinate[] coordinates = new Circle(coordinate, diff).getBoundary().difference(circle_l.union(circle_r)).getCoordinates();

        List<MyCoordinate> res=new ArrayList<>();
        for (Coordinate c:coordinates){
            double x = v.getX() * (c.getX() - coordinate.getX()) + v.getY() * (c.getY() - coordinate.getY());
            double distance=c.distance(coordinate);
            if (x>0&&Math.abs(diff-distance)<2){
                MyCoordinate myCoordinate = new MyCoordinate(c.getX(),
                                                             c.getY());
                myCoordinate.setParent_point(coordinate);
                myCoordinate.setV(getVCoordinate(next_speed,coordinate,c));
                res.add(myCoordinate);

            }
        }
        return res.toArray(new MyCoordinate[0]);
    }
}
