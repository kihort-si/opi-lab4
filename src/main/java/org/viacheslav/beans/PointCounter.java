package org.viacheslav.beans;

import org.viacheslav.services.AreaChecker;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class PointCounter extends NotificationBroadcasterSupport implements PointCounterMBean {
    private int totalPoints = 0;
    private int pointsInArea = 0;
    private long sequenceNumber = 1;

    private AreaChecker areaChecker;

    @Override
    public synchronized void addPoint(double x, double y, double r) {
        totalPoints++;
        if (areaChecker.check(x, y, r)) {
            pointsInArea++;
        }

        if (totalPoints % 10 == 0) {
            Notification notification = new Notification(
                    "point.count.multiple10",
                    this,
                    sequenceNumber++,
                    System.currentTimeMillis(),
                    "Количество точек кратно 10: " + totalPoints
            );
            sendNotification(notification);
        }
    }

    @Override
    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public int getPointsInArea() {
        return pointsInArea;
    }
}
