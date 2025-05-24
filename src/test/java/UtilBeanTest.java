import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.viacheslav.DBController;
import org.viacheslav.Point;
import org.viacheslav.UtilBean;
import org.viacheslav.beans.PointCounter;
import org.viacheslav.beans.ShapeArea;
import org.viacheslav.services.PointServiceImplementation;
import org.viacheslav.utils.MBeanRegistry;

import java.util.ArrayList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilBeanTest {

    @Mock
    private DBController dbController;

    @Mock
    private ShapeArea shapeArea;

    @Mock
    private PointCounter pointCounter;

    @Mock
    private PointServiceImplementation pointService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private Logger logger;

    @InjectMocks
    private UtilBean utilBean;

    @BeforeEach
    void setUp() {
        try (MockedStatic<FacesContext> mockedFaces = mockStatic(FacesContext.class);
             MockedStatic<MBeanRegistry> mockedMBean = mockStatic(MBeanRegistry.class);
             MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {

            mockedFaces.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);
            when(externalContext.getRequest()).thenReturn(request);
            when(request.getSession(false)).thenReturn(session);
            when(session.getId()).thenReturn("test-session-id");

            mockedLogger.when(() -> Logger.getLogger("UtilBean")).thenReturn(logger);

            // Инициализация списка точек
            when(dbController.getAll()).thenReturn(new ArrayList<>());

            // Инициализация бина
            utilBean.init();
        }
    }

    @Test
    void testInitialState() {
        assertEquals(0, utilBean.getX());
        assertEquals(0, utilBean.getY());
        assertEquals(1, utilBean.getR());
        assertNotNull(utilBean.getPointsList());
    }

    @Test
    void testSetRadius() {
        utilBean.setR(2.5);
        assertEquals(2.5, utilBean.getR());
        assertTrue(utilBean.getArea() > 0); // Проверяем, что площадь пересчиталась
    }

    @Test
    void testCheckAndAdd() {
        utilBean.setX(1.5);
        utilBean.setY(2.5);
        utilBean.setR(3.0);

        Point testPoint = new Point(1.5, 2.5, 3.0, true);
        when(pointService.createAndCheckPoint(1.5, 2.5, 3.0, "test-session-id"))
                .thenReturn(testPoint);

        String result = utilBean.checkAndAdd();

        assertEquals("goToMain?faces-redirect=true", result);
        verify(dbController).addPoint(testPoint);
        assertTrue(utilBean.getPointsList().contains(testPoint));
        verify(logger).info("Пришёл запрос на добавление точки: x = 1.5, y = 2.5, r = 3.0");
    }

    @Test
    void testClear() {
        // Добавляем тестовые точки
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(1, 2, 3));
        utilBean.setPointsList(points);

        String result = utilBean.clear();

        assertEquals("goToMain?faces-redirect=true", result);
        verify(dbController).clear("test-session-id");
        assertTrue(utilBean.getPointsList().isEmpty());
    }

    @Test
    void testPointsToString() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(1, 2, 3, true));
        points.add(new Point(4, 5, 6, false));
        utilBean.setPointsList(points);

        String result = utilBean.pointsToString();
        assertEquals("1.0,2.0,3.0,true;4.0,5.0,6.0,false", result);
    }

    @Test
    void testCheckSession() {
        assertTrue(utilBean.checkSession("test-session-id"));
        assertFalse(utilBean.checkSession("wrong-session-id"));
    }

//    @Test
//    void testLifecycleMethods() {
//        try (MockedStatic<MBeanRegistry> mockedMBean = mockStatic(MBeanRegistry.class)) {
//            // Тестируем инициализацию
//            utilBean.init(mock(SessionScoped.class), new Object());
//
//            // Проверяем регистрацию бинов
//            mockedMBean.verify(() -> MBeanRegistry.registerBean(any(PointCounter.class), eq("pointCounter")));
//            mockedMBean.verify(() -> MBeanRegistry.registerBean(any(ShapeArea.class), eq("shapeArea")));
//
//            // Тестируем уничтожение
//            utilBean.destroy(mock(SessionScoped.class), new Object());
//
//            // Проверяем удаление бинов
//            mockedMBean.verify(() -> MBeanRegistry.unregisterBean(any(PointCounter.class)));
//            mockedMBean.verify(() -> MBeanRegistry.unregisterBean(any(ShapeArea.class)));
//        }
//    }
}