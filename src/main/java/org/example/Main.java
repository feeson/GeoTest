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

        Coordinate start_point=new Coordinate(0, 0);
        Coordinate start_velocity=new Coordinate(0,0.5);
        Coordinate end_point=new Coordinate(10,-1800);

        AStarShipTrajectoryPlanning aStarShipTrajectoryPlanning = new AStarShipTrajectoryPlanning(
                start_point.getX(), start_point.getY(),
                start_velocity.getX(),start_velocity.getY(),
                end_point.getX(),end_point.getY(),
                ship,50);
        List<Coordinate> path = aStarShipTrajectoryPlanning.getPath();
        Window window=new Window("最终路线");
        for (Coordinate coordinate:path){
            window.drawPoint(coordinate.getX(), coordinate.getY());
            System.out.println("X: "+coordinate.getX()+", Y: "+coordinate.getY());
        }
    }
}
