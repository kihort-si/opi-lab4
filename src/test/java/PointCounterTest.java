
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.viacheslav.Point;
import org.viacheslav.beans.PointCounter;

import javax.management.*;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointCounterTest {

    private PointCounter pointCounter;
    private Point pointInArea;
    private Point pointNotInArea;

    @BeforeEach
    void setUp() {
        pointCounter = new PointCounter();
        pointInArea = new Point(-0.5, 0.5, 1, true);
        pointNotInArea = new Point(3, 3, 1, false);
    }

    @Test
    void testInitialState() {
        assertEquals(0, pointCounter.getTotalPoints());
        assertEquals(0, pointCounter.getPointsInArea());
    }

    @Test
    void testAddPoint_InArea() {
        pointCounter.addPoint(pointInArea);
        assertEquals(1, pointCounter.getTotalPoints());
        assertEquals(1, pointCounter.getPointsInArea());
    }

    @Test
    void testAddPoint_NotInArea() {
        pointCounter.addPoint(pointNotInArea);
        assertEquals(1, pointCounter.getTotalPoints());
        assertEquals(0, pointCounter.getPointsInArea());
    }

    @Test
    void testAddMultiplePoints() {
        pointCounter.addPoint(pointInArea);
        pointCounter.addPoint(pointInArea);
        pointCounter.addPoint(pointNotInArea);
        assertEquals(3, pointCounter.getTotalPoints());
        assertEquals(2, pointCounter.getPointsInArea());
    }

    @Test
    void testSetTotalPoints() {
        pointCounter.setTotalPoints(5);
        assertEquals(5, pointCounter.getTotalPoints());
    }

    @Test
    void testNotificationOnMultipleOf10() throws Exception {
        NotificationListener mockListener = mock(NotificationListener.class);
        pointCounter.addNotificationListener(mockListener, null, null);

        // Добавляем 10 точек (кратно 10)
        for (int i = 0; i < 10; i++) {
            pointCounter.addPoint(pointInArea);
        }

        verify(mockListener, times(1)).handleNotification(
                any(Notification.class),
                any()
        );
    }

    @Test
    void testNoNotificationOnNonMultipleOf10() throws Exception {
        NotificationListener mockListener = mock(NotificationListener.class);
        pointCounter.addNotificationListener(mockListener, null, null);

        // Добавляем 9 точек (не кратно 10)
        for (int i = 0; i < 9; i++) {
            pointCounter.addPoint(pointInArea);
        }

        verify(mockListener, never()).handleNotification(
                any(Notification.class),
                any()
        );
    }

    @Test
    void testRemoveNotificationListener() throws Exception {
        NotificationListener mockListener = mock(NotificationListener.class);
        pointCounter.addNotificationListener(mockListener, null, null);
        pointCounter.removeNotificationListener(mockListener);

        // Добавляем 10 точек (кратно 10)
        for (int i = 0; i < 10; i++) {
            pointCounter.addPoint(pointInArea);
        }

        verify(mockListener, never()).handleNotification(
                any(Notification.class),
                any()
        );
    }

    @Test
    void testGetNotificationInfo() {
        MBeanNotificationInfo[] infos = pointCounter.getNotificationInfo();
        assertNotNull(infos);
        assertEquals(1, infos.length);
        assertEquals("javax.management.Notification", infos[0].getName());
        assertArrayEquals(new String[]{"point.count.multiple10"}, infos[0].getNotifTypes());
    }

    @Test
    void testSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(PointCounter.class));
    }
}