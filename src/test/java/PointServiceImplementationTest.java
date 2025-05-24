
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.viacheslav.Point;
import org.viacheslav.services.AreaChecker;
import org.viacheslav.services.PointServiceImplementation;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceImplementationTest {

    @Mock
    private AreaChecker areaChecker; // Мок AreaChecker

    @InjectMocks
    private PointServiceImplementation pointService; // Тестируемый сервис с внедрённым моком

    private final String testSession = "test-session-id";

    @Test
    void testCreateAndCheckPoint_InsideArea() {
        // Устанавливаем поведение мока
        when(areaChecker.check(1.0, 1.0, 2.0)).thenReturn(true);

        Point point = pointService.createAndCheckPoint(1.0, 1.0, 2.0, testSession);

        // Проверяем, что точка создана корректно
        assertNotNull(point);
        assertEquals(1.0, point.getX());
        assertEquals(1.0, point.getY());
        assertEquals(2.0, point.getR());
        assertTrue(point.isResult());
        assertEquals(testSession, point.getSession());
        assertNotNull(point.getDate());

        // Проверяем, что метод areaChecker.check() был вызван 1 раз
        verify(areaChecker, times(1)).check(1.0, 1.0, 2.0);
    }

    @Test
    void testCreateAndCheckPoint_OutsideArea() {
        when(areaChecker.check(3.0, 3.0, 2.0)).thenReturn(false);

        Point point = pointService.createAndCheckPoint(3.0, 3.0, 2.0, testSession);

        assertNotNull(point);
        assertEquals(3.0, point.getX());
        assertEquals(3.0, point.getY());
        assertEquals(2.0, point.getR());
        assertFalse(point.isResult());
        assertEquals(testSession, point.getSession());
        assertNotNull(point.getDate());

        verify(areaChecker, times(1)).check(3.0, 3.0, 2.0);
    }

    @Test
    void testCreateAndCheckPoint_ZeroRadius() {
        when(areaChecker.check(0.5, 0.5, 0.0)).thenReturn(false);

        Point point = pointService.createAndCheckPoint(0.5, 0.5, 0.0, testSession);

        assertNotNull(point);
        assertEquals(0.5, point.getX());
        assertEquals(0.5, point.getY());
        assertEquals(0.0, point.getR());
        assertFalse(point.isResult());

        verify(areaChecker, times(1)).check(0.5, 0.5, 0.0);
    }

    @Test
    void testCreateAndCheckPoint_DateIsSet() {
        when(areaChecker.check(1.0, 1.0, 1.0)).thenReturn(true);

        Point point = pointService.createAndCheckPoint(1.0, 1.0, 1.0, testSession);

        assertNotNull(point.getDate());
        // Проверяем, что дата установлена примерно сейчас (допуск ±1 секунда)
        assertTrue(Math.abs(new Date().getTime() - point.getDate().getTime()) < 1000);
    }

    @Test
    void testCreateAndCheckPoint_SessionIsSet() {
        when(areaChecker.check(1.0, 1.0, 1.0)).thenReturn(true);

        String customSession = "custom-session-123";
        Point point = pointService.createAndCheckPoint(1.0, 1.0, 1.0, customSession);

        assertEquals(customSession, point.getSession());
    }
}