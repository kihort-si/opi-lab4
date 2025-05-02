package org.viacheslav.beans;

public class ShapeArea implements ShapeAreaMBean {
    private double r = 1.0;

    @Override
    public void setRadius(double r) {
        if (r < 0) throw new IllegalArgumentException("Радиус должен быть неотрицательным");
        this.r = r;
    }

    @Override
    public double getRadius() {
        return r;
    }

    @Override
    public double getArea() {
        double triangle = (r * r) / 4;
        double quarterCircle = (Math.PI * r * r) / 4;
        double rectangle = r * r;
        return triangle + quarterCircle + rectangle;
    }
}
