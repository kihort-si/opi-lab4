import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.viacheslav.Point;

import java.io.Serializable;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void testDefaultConstructor() {
        Point point = new Point();

        assertNotNull(point);
        assertEquals(0, point.getId());
        assertEquals(0.0, point.getX());
        assertEquals(0.0, point.getY());
        assertEquals(0.0, point.getR());
        assertNull(point.getDate());
        assertFalse(point.isResult());
        assertNull(point.getSession());
    }

    @Test
    void testParameterizedConstructor() {
        Point point = new Point(1.5, 2.5, 3.0);

        assertEquals(0, point.getId()); // id не установлен
        assertEquals(1.5, point.getX());
        assertEquals(2.5, point.getY());
        assertEquals(3.0, point.getR());
        assertNull(point.getDate());
        assertFalse(point.isResult());
        assertNull(point.getSession());
    }

    @Test
    void testFullParameterizedConstructor() {
        Point point = new Point(1.5, 2.5, 3.0, true);

        assertEquals(1.5, point.getX());
        assertEquals(2.5, point.getY());
        assertEquals(3.0, point.getR());
        assertTrue(point.isResult());
    }

    @Test
    void testSettersAndGetters() {
        Point point = new Point();
        Date now = new Date();

        point.setId(1);
        point.setX(0.5);
        point.setY(-0.5);
        point.setR(2.0);
        point.setDate(now);
        point.setResult(true);
        point.setSession("test-session");

        assertEquals(1, point.getId());
        assertEquals(0.5, point.getX());
        assertEquals(-0.5, point.getY());
        assertEquals(2.0, point.getR());
        assertEquals(now, point.getDate());
        assertTrue(point.isResult());
        assertEquals("test-session", point.getSession());
    }

    @ParameterizedTest
    @CsvSource({
            "1.5, 2.5, 3.0, true, 'x = 1.5, y = 2.5, r = 3.0, date = null, isHit = true'",
            "0.0, 0.0, 1.0, false, 'x = 0.0, y = 0.0, r = 1.0, date = null, isHit = false'"
    })
    void testToString(double x, double y, double r, boolean result, String expected) {
        Point point = new Point(x, y, r, result);
        assertEquals(expected, point.toString());
    }

    @Test
    void testDateAutoSet() {
        Point point = new Point();
        point.setDate(new Date());

        assertNotNull(point.getDate());
        assertTrue(Math.abs(System.currentTimeMillis() - point.getDate().getTime()) < 1000);
    }

    @Test
    void testSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Point.class));
    }
}