package org.example;

import org.locationtech.jts.geom.Coordinate;

import java.util.List;

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
    public static void main(String[] args) {
        double engine_power=212000;
        double mass=45000;
        double power_of_yaw=0.021675;

        Ship ship=new Ship();
        ship.setEnginePower(engine_power);
        ship.setMass(mass);
        ship.setPowerYaw(power_of_yaw);
        ship.setDragCoefficient(0.8);
        Coordinate start_point=new Coordinate(0, 0);
        Coordinate start_velocity=new Coordinate(0,15);
        Coordinate end_point=new Coordinate(0,0);

        for (int i=0;i<10000;i+=500){
            Circle circle = new Circle(
                    new Coordinate(0, 0), 500+i);
            for (Coordinate c:circle.getCoordinates()){
                end_point=c;
                System.out.println(
                        "radius: "+PhysicUtil.getGyrationRadius(start_velocity.distance(new Coordinate(0,0)),ship.getPowerYaw())
                                +" distance: "+start_point.distance(end_point)
                                +" X: "+end_point.getX()+" Y: "+end_point.getY());
                long start=System.currentTimeMillis();
                AStarShipTrajectoryPlanning aStarShipTrajectoryPlanning = new AStarShipTrajectoryPlanning(
                        start_point.getX(), start_point.getY(),
                        start_velocity.getX(),start_velocity.getY(),
                        end_point.getX(),end_point.getY(),
                        ship,Math.min(start_point.distance(end_point)/30,150));
                List<Coordinate> path = aStarShipTrajectoryPlanning.getPath();
                System.out.println("Spend time: "+(System.currentTimeMillis()-start)+" ms");
                Window window=new Window("Final path");
                for (Coordinate coordinate:path){
                    window.drawPoint(coordinate.getX(), coordinate.getY());
                }
            }
        }


    }
}
