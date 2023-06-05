package org.example;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.index.sweepline.SweepLineIndex;

import java.util.List;
import java.util.Random;

public class Main {
    /**
     * 一种基于A*算法的舰船航迹规划
     * @author fee
     * @param args
     * setEnginePower: 引擎功率
     * mass：排水量
     * power_of_yaw：偏航力矩
     * start_point：起始点
     * start_velocity： 起始速度
     * end_point： 结束点
     * diff: 寻路步长
     */
    public static void main(String[] args) throws InterruptedException {
        double engine_power=212000;
        double mass=45000;
        double power_of_yaw=0.021;
        Random random=new Random();

        for (int j=0;j<20;++j){

            Ship ship=new Ship();
            ship.setEnginePower(engine_power);
            ship.setMass(mass);
            ship.setPowerYaw(power_of_yaw);
            ship.setDragCoefficient(0.8);
            Coordinate start_point=new Coordinate(random.nextInt(10000),random.nextInt(10000));
            Coordinate start_velocity=new Coordinate(random.nextInt(15),random.nextInt(15));
            Coordinate end_point=new Coordinate(random.nextInt(10000),random.nextInt(10000));

            System.out.printf("radius: %f %n distance: %f %n sx: %f sy: %f %n vx: %f vy: %f %n ex: %f ey: %f %n",
                              PhysicUtil.getGyrationRadius(start_velocity.distance(new Coordinate(0,0)),ship.getPowerYaw()),
                              start_point.distance(end_point),
                              start_point.getX(),start_point.getY(),
                              start_velocity.getX(),start_velocity.getY(),
                              end_point.getX(),end_point.getY());
            long start=System.currentTimeMillis();
            ShipRoutePlan aStarShipTrajectoryPlanning = new ShipRoutePlan(
                    start_point.getX(), start_point.getY(),
                    start_velocity.getX(),start_velocity.getY(),
                    end_point.getX(),end_point.getY(),
                    ship);
            List<MyCoordinate> path = aStarShipTrajectoryPlanning.getPath();
            System.out.printf("path node: %d Spend time: %d ms",path.size(),(System.currentTimeMillis()-start));
            for (int i=570;i<750;i+=500){
                Circle circle=new Circle(new Coordinate(0,0),i);
                Coordinate[] coordinates = circle.getBoundary().getCoordinates();
                Window window=new Window("Final path");
                for (Coordinate c:path){
                    window.drawPoint(c.getX(),c.getY());
                }
            }
            Thread.sleep(500);
        }
    }

    public static void mainDebug(String[] args) throws InterruptedException {
        double engine_power=212000;
        double mass=45000;
        double power_of_yaw=0.021;
        Ship ship=new Ship();
        ship.setEnginePower(engine_power);
        ship.setMass(mass);
        ship.setPowerYaw(power_of_yaw);
        ship.setDragCoefficient(0.8);
        Coordinate start_point=new Coordinate(4638,3231);
        Coordinate start_velocity=new Coordinate(0,2);
        Coordinate end_point=new Coordinate(6734,116);

        System.out.printf("radius: %f %n distance: %f %n sx: %f sy: %f %n vx: %f vy: %f %n ex: %f ey: %f %n",
                          PhysicUtil.getGyrationRadius(start_velocity.distance(new Coordinate(0,0)),ship.getPowerYaw()),
                          start_point.distance(end_point),
                          start_point.getX(),start_point.getY(),
                          start_velocity.getX(),start_velocity.getY(),
                          end_point.getX(),end_point.getY());
        long start=System.currentTimeMillis();
        ShipRoutePlan aStarShipTrajectoryPlanning = new ShipRoutePlan(
                start_point.getX(), start_point.getY(),
                start_velocity.getX(),start_velocity.getY(),
                end_point.getX(),end_point.getY(),
                ship);
        List<MyCoordinate> path = aStarShipTrajectoryPlanning.getPath();
        System.out.printf("path node: %d Spend time: %d ms",path.size(),(System.currentTimeMillis()-start));
        for (int i=570;i<750;i+=500){
            Circle circle=new Circle(new Coordinate(0,0),i);
            Coordinate[] coordinates = circle.getBoundary().getCoordinates();
            Window window=new Window("Final path");
            for (Coordinate c:path){
                window.drawPoint(c.getX(),c.getY());
            }
        }
        Thread.sleep(100);
    }
}
