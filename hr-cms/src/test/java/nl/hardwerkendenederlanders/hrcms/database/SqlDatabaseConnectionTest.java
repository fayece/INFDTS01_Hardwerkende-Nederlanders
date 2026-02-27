package nl.hardwerkendenederlanders.hrcms.database;

import nl.hardwerkendenederlanders.hrcms.database.sqldb.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SqlDatabaseConnectionTest {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Test
    void testDatabaseLogic() throws Exception {
        databaseConnection.makeDatabase();
        databaseConnection.AddArticle();
    }
}
