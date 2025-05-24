
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.viacheslav.beans.ShapeArea;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

class ShapeAreaTest {

    private ShapeArea shapeArea;

    @BeforeEach
    void setUp() {
        shapeArea = new ShapeArea();
    }

    @Test
    void testDefaultRadius() {
        assertEquals(1.0, shapeArea.getRadius());
    }

    @Test
    void testSetValidRadius() {
        shapeArea.setRadius(2.5);
        assertEquals(2.5, shapeArea.getRadius());
    }

    @Test
    void testSetNegativeRadius_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            shapeArea.setRadius(-1.0);
        });
    }

    @Test
    void testGetArea_WithDefaultRadius() {
        double expectedArea = (1.0 * 1.0 / 4) + (Math.PI * 1.0 * 1.0 / 4) + (1.0 * 1.0);
        expectedArea = Math.round(expectedArea * 100) / 100.0;
        assertEquals(expectedArea, shapeArea.getArea());
    }

    @Test
    void testGetArea_WithCustomRadius() {
        shapeArea.setRadius(2.0);
        double expectedArea = (2.0 * 2.0 / 4) + (Math.PI * 2.0 * 2.0 / 4) + (2.0 * 2.0);
        expectedArea = Math.round(expectedArea * 100) / 100.0;
        assertEquals(expectedArea, shapeArea.getArea());
    }

    @Test
    void testSerializable() {
        assertTrue(shapeArea instanceof Serializable);
    }
}