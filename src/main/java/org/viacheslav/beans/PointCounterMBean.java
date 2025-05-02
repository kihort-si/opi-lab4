package org.viacheslav.beans;

public interface PointCounterMBean {
    void addPoint(double x, double y, double r);
    int getTotalPoints();
    int getPointsInArea();
}
