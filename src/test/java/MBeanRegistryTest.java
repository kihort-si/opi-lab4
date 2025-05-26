import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.viacheslav.utils.MBeanRegistry;

import javax.management.*;
import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MBeanRegistryTest {

    @Mock
    MBeanServer mbs;
    MockedStatic<ManagementFactory> mf;

    Object bean = new Object();

    @BeforeEach
    void open() {
        mf = mockStatic(ManagementFactory.class);
        mf.when(ManagementFactory::getPlatformMBeanServer).thenReturn(mbs);
    }

    @AfterEach
    void close() {
        mf.close();
        MBeanRegistry.unregisterBean(bean);
    }

    @Test
    void register_success() throws Exception {
        ArgumentCaptor<ObjectName> captor = ArgumentCaptor.forClass(ObjectName.class);

        MBeanRegistry.registerBean(bean, "testBean");

        verify(mbs).registerMBean(eq(bean), captor.capture());
        assertTrue(captor.getValue().getCanonicalName()
                .contains("name=testBean"));
    }

    @Test
    void register_alreadyExists_isIgnored() throws Exception {
        doThrow(new InstanceAlreadyExistsException())
                .when(mbs).registerMBean(any(), any());

        assertDoesNotThrow(() -> MBeanRegistry.registerBean(bean, "testBean"));
    }

    @Test
    void unregister_success() throws Exception {
        MBeanRegistry.registerBean(bean, "testBean");

        ArgumentCaptor<ObjectName> captor = ArgumentCaptor.forClass(ObjectName.class);
        MBeanRegistry.unregisterBean(bean);

        verify(mbs).unregisterMBean(captor.capture());
        assertTrue(captor.getValue().getCanonicalName()
                .contains("name=testBean"));
    }

    @Test
    void unregister_notRegistered_doesNothing() {
        assertDoesNotThrow(() -> MBeanRegistry.unregisterBean(new Object()));
        verifyNoInteractions(mbs);
    }
}
