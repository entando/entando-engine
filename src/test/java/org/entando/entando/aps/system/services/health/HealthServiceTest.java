package org.entando.entando.aps.system.services.health;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.system.services.health.IHealthDAO;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HealthServiceTest {

    @Mock
    private IHealthDAO healthDAO;

    private HealthService healthService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        healthService = new HealthService(healthDAO);
    }

    @Test
    void withPortSchemaNotReachableShouldReturnFalse() {

        when(healthDAO.isPortDBConnectionHealthy()).thenReturn(false);

        assertFalse(healthService.isHealthy());
    }

    @Test
    void withServSchemaNotReachableShouldReturnFalse() {

        when(healthDAO.isPortDBConnectionHealthy()).thenReturn(true);
        when(healthDAO.isServDBConnectionHealthy()).thenReturn(false);

        assertFalse(healthService.isHealthy());
    }

    @Test
    void withAllSchemaaReachableShouldReturnTrue() {

        when(healthDAO.isPortDBConnectionHealthy()).thenReturn(true);
        when(healthDAO.isServDBConnectionHealthy()).thenReturn(true);

        assertTrue(healthService.isHealthy());
    }
}
