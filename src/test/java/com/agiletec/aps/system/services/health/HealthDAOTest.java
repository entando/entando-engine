package com.agiletec.aps.system.services.health;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HealthDAOTest {

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
    public void isServDBConnectionHealthyWithWorkingDataSourceShouldReturnTrue() throws Exception {
        when(servDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);

        assertTrue(healthDAO.isServDBConnectionHealthy());
    }

    @Test
    public void isServDBConnectionHealthyWithNotValidConnectionShouldReturnFalse() throws Exception {
        when(servDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(false);
        assertFalse(healthDAO.isServDBConnectionHealthy());
    }

    @Test
    public void isServDBConnectionHealthyWithNotWorkingDataSourceShouldReturnFalse() throws Exception {
        when(servDataSource.getConnection()).thenThrow(new SQLException());
        assertFalse(healthDAO.isServDBConnectionHealthy());
    }

    @Test
    public void isPortDBConnectionHealthyWithWorkingDataSourceShouldReturnTrue() throws Exception {
        when(portDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);
        assertTrue(healthDAO.isPortDBConnectionHealthy());
    }

    @Test
    public void isPortDBConnectionHealthyWithNotWorkingDataSourceShouldReturnFalse() throws Exception {
        when(portDataSource.getConnection()).thenThrow(new SQLException());
        assertFalse(healthDAO.isPortDBConnectionHealthy());
    }

    @Test
    public void isPortDBConnectionHealthyWithNotValidConnectionShouldReturnFalse() throws Exception {
        when(portDataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(false);
        assertFalse(healthDAO.isPortDBConnectionHealthy());
    }
    
}
