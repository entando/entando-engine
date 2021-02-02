package com.agiletec.aps.system.services.health;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HealthDAOTest {

    @Mock
    private static DataSource portDataSource;
    @Mock
    private static DataSource servDataSource;
    @Mock
    private static Connection connection;

    private static HealthDAO healthDAO;

    @BeforeAll
    public static void setup() {
        MockitoAnnotations.initMocks(HealthDAOTest.class);
    }
    
    @BeforeEach
    private void init() {
        healthDAO = new HealthDAO()
                .setPortDataSource(portDataSource)
                .setServDataSource(servDataSource);
    }
    
    @Test
    void isServDBConnectionHealthyWithWorkingDataSourceShouldReturnTrue() throws Exception {
        when(servDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);

        assertTrue(healthDAO.isServDBConnectionHealthy());
    }

    @Test
    void isServDBConnectionHealthyWithNotValidConnectionShouldReturnFalse() throws Exception {
        when(servDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(false);
        assertFalse(healthDAO.isServDBConnectionHealthy());
    }

    @Test
    void isServDBConnectionHealthyWithNotWorkingDataSourceShouldReturnFalse() throws Exception {
        when(servDataSource.getConnection()).thenThrow(new SQLException());
        assertFalse(healthDAO.isServDBConnectionHealthy());
    }

    @Test
    void isPortDBConnectionHealthyWithWorkingDataSourceShouldReturnTrue() throws Exception {
        when(portDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);
        assertTrue(healthDAO.isPortDBConnectionHealthy());
    }

    @Test
    void isPortDBConnectionHealthyWithNotWorkingDataSourceShouldReturnFalse() throws Exception {
        when(portDataSource.getConnection()).thenThrow(new SQLException());
        assertFalse(healthDAO.isPortDBConnectionHealthy());
    }

    @Test
    void isPortDBConnectionHealthyWithNotValidConnectionShouldReturnFalse() throws Exception {
        when(portDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(false);
        assertFalse(healthDAO.isPortDBConnectionHealthy());
    }
    
}
