import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.viacheslav.DBController;
import org.viacheslav.Point;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DBControllerTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private TypedQuery<Point> query;

    private DBController dbController;
    private MockedStatic<Persistence> persistenceMock;
    private MockedStatic<DBController> dbControllerMock;

    @BeforeEach
    void setUp() throws Exception {
        persistenceMock = mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory("default"))
                .thenReturn(entityManagerFactory);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);

        dbController = new DBController();
    }

    @AfterEach
    void tearDown() {
        persistenceMock.close();
        if (dbControllerMock != null) {
            dbControllerMock.close();
        }
    }

    @Test
    void getInstance_returnsSingletonInstance() {
        // Тест синглтона
        DBController testInstance = new DBController();

        dbControllerMock = mockStatic(DBController.class);
        dbControllerMock.when(DBController::getInstance).thenReturn(testInstance);

        DBController instance1 = DBController.getInstance();
        DBController instance2 = DBController.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
        assertSame(testInstance, instance1);
    }

    @Test
    void getAll_returnsAllPoints() {
        List<Point> expectedPoints = new ArrayList<>();
        expectedPoints.add(new Point(1.0, 2.0, 3.0));
        expectedPoints.add(new Point(4.0, 5.0, 6.0));

        when(entityManager.createQuery("select point from Point point", Point.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedPoints);

        ArrayList<Point> actualPoints = dbController.getAll();

        verify(transaction).begin();
        verify(transaction).commit();
        assertEquals(expectedPoints.size(), actualPoints.size());
        assertIterableEquals(expectedPoints, actualPoints);
    }

    @Test
    void addPoint_persistsPoint() {
        Point point = new Point(1.0, 2.0, 3.0);

        dbController.addPoint(point);

        verify(transaction).begin();
        verify(entityManager).persist(point);
        verify(transaction).commit();
    }

    @Test
    void clear_deletesAllPoints() {
        Query deleteQuery = mock(Query.class);
        when(entityManager.createQuery("delete from Point")).thenReturn(deleteQuery);
        when(deleteQuery.executeUpdate()).thenReturn(5);

        dbController.clear("test-session-id");

        verify(transaction).begin();
        verify(deleteQuery).executeUpdate();
        verify(transaction).commit();
    }
}