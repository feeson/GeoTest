package org.example;

public class Ship {
    private String shipName;
    private String id;
    double enginePower=158000;
    double dragCoefficient=1.8;

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(double enginePower) {
        this.enginePower = enginePower;
    }

    public double getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDragCoefficient(double dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getPowerYaw() {
        return powerYaw;
    }

    public void setPowerYaw(double powerYaw) {
        this.powerYaw = powerYaw;
    }

    double mass=45000;
    double powerYaw;
}
