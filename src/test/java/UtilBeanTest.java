import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.viacheslav.DBController;
import org.viacheslav.FacesContextSetter;
import org.viacheslav.Point;
import org.viacheslav.UtilBean;
import org.viacheslav.services.PointServiceImplementation;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UtilBeanTest {
    @Mock
    DBController dbController;
    @Mock
    PointServiceImplementation pointService;
    @Mock
    FacesContext facesContext;
    @Mock
    ExternalContext externalContext;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;

    private MockedStatic<DBController> dbCtrlStatic;

    private UtilBean utilBean;

    @BeforeEach
    void setUp() throws Exception {
        dbCtrlStatic = mockStatic(DBController.class);
        dbCtrlStatic.when(DBController::getInstance).thenReturn(dbController);
        when(dbController.getAll()).thenReturn(new ArrayList<>());

        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequest()).thenReturn(request);
        when(request.getSession(false)).thenReturn(session);
        when(session.getId()).thenReturn("test-session-id");
        FacesContextSetter.setCurrentInstance(facesContext);

        utilBean = new UtilBean();
        Field f = UtilBean.class.getDeclaredField("pointService");
        f.setAccessible(true);
        f.set(utilBean, pointService);

        utilBean.init();
    }

    @AfterEach
    void tearDown() {
        FacesContextSetter.setCurrentInstance(null);
        dbCtrlStatic.close();
    }

    @Test
    void initial_state_after_init() {
        assertEquals(0, utilBean.getX());
        assertEquals(0, utilBean.getY());
        assertEquals(1, utilBean.getR());
        assertNotNull(utilBean.getPointsList());
        assertTrue(utilBean.getPointsList().isEmpty());
    }

    @Test
    void checkAndAdd_adds_point_and_persists() {
        utilBean.setX(1.5);
        utilBean.setY(2.5);
        utilBean.setR(3);
        Point p = new Point(1.5, 2.5, 3, true);
        when(pointService.createAndCheckPoint(1.5, 2.5, 3, "test-session-id"))
                .thenReturn(p);

        String nav = utilBean.checkAndAdd();

        assertEquals("goToMain?faces-redirect=true", nav);
        verify(dbController).addPoint(p);
        assertEquals(1, utilBean.getPointsList().size());
        assertTrue(utilBean.getPointsList().contains(p));
    }

    @Test
    void clear_removes_points_and_calls_db() {
        utilBean.getPointsList().add(new Point(1, 1, 1, true));
        String nav = utilBean.clear();
        verify(dbController).clear("test-session-id");
        assertTrue(utilBean.getPointsList().isEmpty());
        assertEquals("goToMain?faces-redirect=true", nav);
    }

    @Test
    void pointsToString_formats_correctly() {
        utilBean.getPointsList().add(new Point(1, 2, 3, true));
        utilBean.getPointsList().add(new Point(4, 5, 6, false));
        assertEquals("1.0,2.0,3.0,true;4.0,5.0,6.0,false", utilBean.pointsToString());
    }

    @Test
    void checkSession_compares_ids() {
        assertTrue(utilBean.checkSession("test-session-id"));
        assertFalse(utilBean.checkSession("other"));
    }
}
