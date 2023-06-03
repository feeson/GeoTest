package org.example;

public class PhysicUtil {
    public static double getSquareAcceleratedSpeed(double v0,double distance,double engine_power,double drag_coefficient,double mass){
        double squareV0=Math.pow(v0,2);
        double quarticV0=Math.pow(squareV0,2);
        return squareV0+2*distance*(engine_power-drag_coefficient*quarticV0)/v0/mass/10;
    }
    public static double getGyrationRadius(double v0,double power_yaw){
        return 0.06*v0*v0/power_yaw;
    }
}
