package org.entando.entando.aps.system.init;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.entando.entando.aps.system.init.IDatabaseManager.DatabaseType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DatabaseRestorerTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private DatabaseRestorer databaseRestorer;

    @Test
    void testGetDatabaseTypeDerby() throws Exception {
        testGetDatabaseType(DatabaseType.DERBY, "Apache Derby");
    }

    @Test
    void testGetDatabaseTypePostgres() throws Exception {
        testGetDatabaseType(DatabaseType.POSTGRESQL, "PostgreSQL");
    }

    @Test
    void testGetDatabaseTypeMySQL() throws Exception {
        testGetDatabaseType(DatabaseType.MYSQL, "MySQL");
    }

    @Test
    void testGetDatabaseTypeOracle() throws Exception {
        testGetDatabaseType(DatabaseType.ORACLE, "Oracle");
    }

    @Test
    void testGetDatabaseTypeSQLServer() throws Exception {
        testGetDatabaseType(DatabaseType.SQLSERVER, "Microsoft SQL Server");
    }

    @Test
    void testGetDatabaseTypeUnknown() throws Exception {
        testGetDatabaseType(DatabaseType.UNKNOWN, "H2");
    }

    private void testGetDatabaseType(IDatabaseManager.DatabaseType expectedType, String dbProductName) throws Exception {

        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        DatabaseMetaData databaseMetaData = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(connection.getMetaData()).thenReturn(databaseMetaData);
        Mockito.when(databaseMetaData.getDatabaseProductName()).thenReturn(dbProductName);

        Assertions.assertEquals(expectedType, databaseRestorer.getType(dataSource));
    }
}
