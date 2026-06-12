package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbRoleContext;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionRole;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class DbRoleAspectTest {

    @Autowired
    private JdbcUserRepository repository;

    @AfterEach
    void tearDown() {
        DbRoleContext.clear();
    }

    @Test
    void findAllUsers_withUnauthenticatedRole_isDenied() {
        DbRoleContext.set(DbSessionRole.UNAUTHENTICATED);

        assertThrows(DataAccessException.class, repository::findAllUsers);
    }

    @Test
    void findAllUsers_withNoRoleSet_defaultsToUnauthenticatedAndIsDenied() {
        assertThrows(DataAccessException.class, repository::findAllUsers);
    }

    @Test
    void findAllUsers_withUserRole_isAllowed() {
        DbRoleContext.set(DbSessionRole.USER);

        assertDoesNotThrow(repository::findAllUsers);
    }

    @Test
    void roleSwitch_doesNotLeakIntoSubsequentCallWithoutContext() {
        DbRoleContext.set(DbSessionRole.ADMINISTRATOR);
        assertDoesNotThrow(repository::findAllUsers);
        DbRoleContext.clear();

        assertThrows(DataAccessException.class, repository::findAllUsers);
    }
}