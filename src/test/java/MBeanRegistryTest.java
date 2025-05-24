import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.viacheslav.utils.MBeanRegistry;

import javax.management.*;
import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MBeanRegistryTest {

    private MBeanServer mockMBeanServer;
    private Object testBean;
    private ObjectName testObjectName;

    @BeforeEach
    void setUp() throws MalformedObjectNameException {
        mockMBeanServer = mock(MBeanServer.class);
        testBean = new Object();
        testObjectName = new ObjectName(this.getClass().getPackage().getName() + ":type=Object,name=testBean");
    }

    @AfterEach
    void tearDown() {
        // Очистка статического состояния после каждого теста
        MBeanRegistry.unregisterBean(testBean);
    }

    @Test
    void testRegisterBean_Success() throws Exception {
        try (MockedStatic<ManagementFactory> mockedManagementFactory = Mockito.mockStatic(ManagementFactory.class)) {
            mockedManagementFactory.when(ManagementFactory::getPlatformMBeanServer).thenReturn(mockMBeanServer);

            MBeanRegistry.registerBean(testBean, "testBean");

            System.out.println(eq(testBean));
            verify(mockMBeanServer).registerMBean(eq(testBean), eq(testObjectName));
        }
    }

    @Test
    void testRegisterBean_AlreadyExists() throws Exception {
        try (MockedStatic<ManagementFactory> mockedManagementFactory = Mockito.mockStatic(ManagementFactory.class)) {
            mockedManagementFactory.when(ManagementFactory::getPlatformMBeanServer).thenReturn(mockMBeanServer);
            doThrow(new InstanceAlreadyExistsException("MBean already exists"))
                    .when(mockMBeanServer).registerMBean(any(), any());

            MBeanRegistry.registerBean(testBean, "testBean");

            System.out.println(eq(testBean));
            verify(mockMBeanServer).registerMBean(eq(testBean), eq(testObjectName));
        }
    }

    @Test
    void testRegisterBean_MalformedName() throws Exception {
        try (MockedStatic<ManagementFactory> mockedManagementFactory = Mockito.mockStatic(ManagementFactory.class)) {
            mockedManagementFactory.when(ManagementFactory::getPlatformMBeanServer).thenReturn(mockMBeanServer);
            doThrow(new MalformedObjectNameException("Invalid name"))
                    .when(mockMBeanServer).registerMBean(any(), any());

            assertDoesNotThrow(() -> MBeanRegistry.registerBean(testBean, "invalid/name"));
        }
    }

    @Test
    void testUnregisterBean_Success() throws Exception {
        try (MockedStatic<ManagementFactory> mockedManagementFactory = Mockito.mockStatic(ManagementFactory.class)) {
            mockedManagementFactory.when(ManagementFactory::getPlatformMBeanServer).thenReturn(mockMBeanServer);

            MBeanRegistry.registerBean(testBean, "testBean");
            MBeanRegistry.unregisterBean(testBean);

            verify(mockMBeanServer).unregisterMBean(testObjectName);
        }
    }

    @Test
    void testUnregisterBean_NotFound() throws Exception {
        try (MockedStatic<ManagementFactory> mockedManagementFactory = Mockito.mockStatic(ManagementFactory.class)) {
            mockedManagementFactory.when(ManagementFactory::getPlatformMBeanServer).thenReturn(mockMBeanServer);
            doThrow(new InstanceNotFoundException("MBean not found"))
                    .when(mockMBeanServer).unregisterMBean(any());

            MBeanRegistry.registerBean(testBean, "testBean");
            MBeanRegistry.unregisterBean(testBean);

            verify(mockMBeanServer).unregisterMBean(testObjectName);
        }
    }

    @Test
    void testUnregisterBean_NotRegistered() {
        try (MockedStatic<ManagementFactory> ignored = Mockito.mockStatic(ManagementFactory.class)) {
            assertDoesNotThrow(() -> MBeanRegistry.unregisterBean(new Object()));
        }
    }
}