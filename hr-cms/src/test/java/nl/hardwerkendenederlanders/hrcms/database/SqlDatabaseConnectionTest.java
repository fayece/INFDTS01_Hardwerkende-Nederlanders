package nl.hardwerkendenederlanders.hrcms.database;

import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.SqlDatabaseConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SqlDatabaseConnectionTest {

    @Autowired
    private SqlDatabaseConnection sqlDatabaseConnection;

    @Test
    void testDatabaseLogic() throws Exception {}
}
