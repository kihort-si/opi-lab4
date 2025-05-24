import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.viacheslav.services.AreaCheckerImplementation;

import static org.junit.jupiter.api.Assertions.*;

class AreaCheckerImplementationTest {

    private AreaCheckerImplementation areaChecker;

    @BeforeEach
    void setUp() {
        areaChecker = new AreaCheckerImplementation();
    }

    // Проверка треугольника (x ≥ 0, y ≥ 0, y ≤ r/2 - 0.5x)
    @ParameterizedTest
    @CsvSource({
            "0, 0, 2, true",   // Граница треугольника (начало координат)
            "1, 0, 2, true",   // На оси X внутри
            "0, 1, 2, true",   // На оси Y внутри
            "1, 0.5, 2, true", // Внутри треугольника
            "2, 0, 2, true",   // На границе (y = 0)
            "0, 1, 2, true",   // На границе (x = 0)
            "1, 1, 2, false",  // Вне треугольника
            "2, 1, 2, false"   // Вне треугольника
    })
    void testTriangleArea(double x, double y, double r, boolean expected) {
        assertEquals(expected, areaChecker.check(x, y, r));
    }

    // Проверка круга (x ≤ 0, y ≤ 0, √(x² + y²) ≤ r)
    @ParameterizedTest
    @CsvSource({
            "-1, -1, 2, true",    // Внутри круга
            "-1.414, -1.414, 2, true",  // На границе (√2 ≈ 1.414)
            "-0.5, -0.5, 2, true", // Внутри круга
            "-2, 0, 2, true",      // На границе (x = -r, y = 0)
            "0, -2, 2, true",      // На границе (x = 0, y = -r)
            "-2, -2, 2, false",    // Вне круга (√8 ≈ 2.828 > 2)
            "-3, 0, 2, false"      // Вне круга (x = -3 > r)
    })
    void testCircleArea(double x, double y, double r, boolean expected) {
        assertEquals(expected, areaChecker.check(x, y, r));
    }

    // Проверка прямоугольника (x ≤ 0, y ≥ 0, x ≥ -r, y ≤ r)
    @ParameterizedTest
    @CsvSource({
            "-1, 1, 2, true",     // Внутри прямоугольника
            "-2, 0, 2, true",     // На границе (x = -r)
            "0, 2, 2, true",       // На границе (y = r)
            "-1, 2, 2, true",      // На границе (y = r)
            "-2, 2, 2, true",      // Угол прямоугольника
            "-3, 1, 2, false",     // Вне (x < -r)
            "-1, 3, 2, false",     // Вне (y > r)
            "-3, 3, 2, false"      // Вне (x < -r и y > r)
    })
    void testRectangleArea(double x, double y, double r, boolean expected) {
        assertEquals(expected, areaChecker.check(x, y, r));
    }

    // Проверка вне всех областей
    @ParameterizedTest
    @CsvSource({
            "1, -1, 2, false",    // IV квадрант (нет проверки)
            "1, 1, 0, false",     // r = 0 (всё должно быть false)
            "-1, -1, 0.5, false"  // Маленький радиус (точка вне)
    })
    void testOutsideAllAreas(double x, double y, double r, boolean expected) {
        assertEquals(expected, areaChecker.check(x, y, r));
    }

    // Проверка граничных значений (r = 0)
    @Test
    void testZeroRadius() {
        assertTrue(areaChecker.check(0, 0, 0));
        assertFalse(areaChecker.check(1, 1, 0));
        assertFalse(areaChecker.check(-1, -1, 0));
    }
}