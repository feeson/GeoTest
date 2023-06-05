package org.example;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;

import java.util.*;

public class ShipRoutePlan {
    MyCoordinate start_point;
    MyCoordinate end_point;
    Coordinate vs;
    double estimate_distance;
    Coordinate indexVct;
    Ship ship;

    public ShipRoutePlan(double sx, double sy, double svx, double svy, double ex, double ey, Ship ship){
        start_point= new MyCoordinate(this, Math.floor(sx), Math.floor(sy));
        start_point.setParent_point(start_point);
        start_point.setCost_distance(0);
        end_point= new MyCoordinate(this, Math.floor(ex), Math.floor(ey));
        estimate_distance=start_point.distance(end_point);
        vs=new Coordinate(svx,svy);
        start_point.setV(vs);
        this.ship=ship;

        this.indexVct=new Coordinate(ex-sx,ey-sy);
        normalize(indexVct);
    }
    private Coordinate normalize(Coordinate coordinate){
        double x=coordinate.getX();
        double y=coordinate.getY();
        double distance=Math.sqrt(x*x+y*y);
        coordinate.setX(x/distance);
        coordinate.setY(y/distance);
        return coordinate;
    }

    public  class MyCoordinateSet extends HashSet<MyCoordinate>{
        @Override
        public boolean contains(Object obj) {
            if (!(obj instanceof MyCoordinate)) {
                return false;
            }

            MyCoordinate target = (MyCoordinate) obj;

            for (MyCoordinate coordinate : this) {
                double tolerance = 1;  // 允许的误差范围
                return coordinate.distance(target)<tolerance;
            }

            return false;
        }
    }
    GeometryFactory geometryFactory = new GeometryFactory();
    PriorityQueue<PriorityPoint> open_set = new PriorityQueue<>(
            Comparator.comparingDouble(PriorityPoint::getPriority));
    MyCoordinateSet close_set=new MyCoordinateSet();

    public List<MyCoordinate> getPath(){
        PriorityPoint start = new PriorityPoint(0, start_point);
        open_set.add(start);

        while (!open_set.isEmpty()){

            PriorityPoint poll = open_set.poll();
            if (open_set.size()>1000){
                System.out.println("Out of stack");
                List<MyCoordinate> path=new ArrayList<>();
                MyCoordinate cur=poll.getPoint();
                while (cur.getParent_point()!=start_point){
                    path.add(cur);
                    cur=cur.getParent_point();
                }
                return path;
            }
            if (poll.equal(end_point)){
                List<MyCoordinate> path=new ArrayList<>();
                MyCoordinate cur=poll.getPoint();
                while (cur.getParent_point()!=start_point){
                    path.add(cur);
                    cur=cur.getParent_point();
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
        System.out.println("error");
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

        double value =coordinate.distance(end_point) +coordinate.getCost_distance()+coordinate.distance(end_point);
        value+=value*(1-dotProduct(normalize(new Coordinate(v)),normalize(getVector(coordinate,end_point))));

        //奖励
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
        Coordinate point=new Coordinate(point2.getX()+point1.getX(),point2.getY()+point1.getY());
        LineSegment lineSegment = new LineSegment(point1, point);
        Coordinate[] perpendicularCoordinates = new Coordinate[2];

        // 计算垂线方向上的两个点
        double deltaX = point.x - point1.x;
        double deltaY = point.y - point1.y;
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double unitX = deltaX / length;
        double unitY = deltaY / length;
        double offsetX = -distance * unitY;
        double offsetY = distance * unitX;

        perpendicularCoordinates[0] = new Coordinate(point1.x + offsetX, point1.y + offsetY);
        perpendicularCoordinates[1] = new Coordinate(point1.x - offsetX, point1.y - offsetY);

        return perpendicularCoordinates;
    }
    private MyCoordinate[] getNeighbour(MyCoordinate coordinate,MyCoordinate parentCoordinate){
        int diff=32;

        Coordinate v = coordinate.getV();
        double speed=origin.distance(v);
        double gyrationRadius = PhysicUtil.getGyrationRadius(speed,
                                                             ship.getPowerYaw());
        double to_next_distance=gyrationRadius/diff;
        while (to_next_distance<1){
            diff*=0.65;
            to_next_distance=gyrationRadius/diff;
        }
        double next_speed=Math.sqrt(PhysicUtil.getSquareAcceleratedSpeed(speed,to_next_distance,ship.getEnginePower(),ship.getDragCoefficient(),ship.getMass()));
        Coordinate[] circleCenters = getPerpendicularCoordinates(
                coordinate, v, gyrationRadius);
        Circle circle_r=new Circle(circleCenters[0],gyrationRadius);
        Circle circle_l=new Circle(circleCenters[1],gyrationRadius);

        double distance_y=(gyrationRadius*Math.sqrt(4* diff * diff -1)/(2* diff * diff));
        double vec_x_x=(circle_r.getCenter().getX()-coordinate.getX())/(2* diff * diff);
        double vec_x_y=(circle_r.getCenter().getY()-coordinate.getY())/(2* diff * diff);
        double vec_y_x=v.getX()/speed*distance_y;
        double vec_y_y=v.getY()/speed*distance_y;

        List<MyCoordinate> coordinates=new ArrayList<>();

        {
            MyCoordinate myCoordinate = new MyCoordinate(this,
                                                         coordinate.getX() + vec_x_x + vec_y_x,
                                                         coordinate.getY() + vec_x_y + vec_y_y);
            myCoordinate.setCost_distance(coordinate.getCost_distance()+to_next_distance);
            myCoordinate.setParent_point(coordinate);
            Coordinate normaled = normalAxB(myCoordinate, circle_r.getCenter(),
                                               false);
            normaled.setX(normaled.getX()*next_speed);
            normaled.setY(normaled.getY()*next_speed);
            myCoordinate.setV(normaled);
            coordinates.add(myCoordinate);
        }
        {
            MyCoordinate myCoordinate = new MyCoordinate(this,coordinate.getX()-vec_x_x+vec_y_x,coordinate.getY()-vec_x_y+vec_y_y);
            myCoordinate.setCost_distance(coordinate.getCost_distance()+to_next_distance);
            myCoordinate.setParent_point(coordinate);
            Coordinate normaled = normalAxB(myCoordinate, circle_l.getCenter(),
                                            true);
            normaled.setX(normaled.getX()*next_speed);
            normaled.setY(normaled.getY()*next_speed);
            myCoordinate.setV(normaled);
            coordinates.add(myCoordinate);
        }

        Coordinate normal_v=normalize(new Coordinate(v));
        Coordinate vct_to_end= getVector(coordinate,
                                         getCoordinate(coordinate, end_point, coordinate.distance(end_point)/4));
        if (dotProduct(normal_v,normalize(getVector(coordinate,coordinates.get(0))))<dotProduct(normal_v,normalize(new Coordinate(vct_to_end)))){
            MyCoordinate myCoordinate = new MyCoordinate(this,
                                                         vct_to_end.getX() + coordinate.getX(),
                                                         vct_to_end.getY() + coordinate.getY());
            myCoordinate.setParent_point(coordinate);
            myCoordinate.setCost_distance(coordinate.getCost_distance()+to_next_distance);
            Coordinate normaled =normalize(getVector(coordinate, end_point));
            next_speed=Math.sqrt(PhysicUtil.getSquareAcceleratedSpeed(speed,myCoordinate.distance(end_point),ship.getEnginePower(),ship.getDragCoefficient(),ship.getMass()));
            normaled.setX(normaled.getX()*next_speed);
            normaled.setY(normaled.getY()*next_speed);
            myCoordinate.setV(normaled);
            coordinates.add(myCoordinate);
        }

        return coordinates.toArray(new MyCoordinate[0]);
    }
    Coordinate getVector(Coordinate v0, Coordinate v1){
        return new Coordinate(v1.getX()-v0.getX(),v1.getY()-v0.getY());
    }
    Coordinate getCoordinate(Coordinate v0, Coordinate v1, double distance){
        Coordinate v = new Coordinate(v1.getX() - v0.getX(),
                                               v1.getY() - v0.getY());
        double d=origin.distance(v);
        v.setX(v0.getX()+v.getX()/d*distance);
        v.setY(v0.getY()+v.getY()/d*distance);
        return v;
    }
    double dotProduct(Coordinate v1,Coordinate v2){
        return v1.getX()*v2.getX()+v1.getY()*v2.getY();
    }
    Coordinate normalAxB(Coordinate v0,Coordinate v1,boolean rightSide){
        Coordinate vector = getVector(v0, v1);
        if (rightSide)
            return normalize(new Coordinate(-vector.getY(), vector.getX()));
        else
            return normalize(new Coordinate(vector.getY(), -vector.getX()));

    }
}