package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionAspect;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionContext;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionRole;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcUserRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.SkipDbSessionContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@SkipDbSessionContext
class DbSessionAspectTest {

    @Autowired
    private JdbcUserRepository repository;

    @Autowired
    private DbSessionAspect dbSessionAspect;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        DbSessionContext.clear();
    }

    @Test
    void findAllUsers_withUnauthenticatedRole_isDenied() {
        DbSessionContext.set(DbSessionRole.UNAUTHENTICATED, null);

        assertThrows(DataAccessException.class, repository::findAllUsers);
    }

    @Test
    void findAllUsers_withNoRoleSet_defaultsToUnauthenticatedAndIsDenied() {
        assertThrows(DataAccessException.class, repository::findAllUsers);
    }

    @Test
    void findAllUsers_withUserRole_isAllowed() {
        DbSessionContext.set(DbSessionRole.USER, null);

        assertDoesNotThrow(repository::findAllUsers);
    }

    @Test
    void roleSwitch_doesNotLeakIntoSubsequentCallWithoutContext() {
        DbSessionContext.set(DbSessionRole.ADMINISTRATOR, null);
        assertDoesNotThrow(repository::findAllUsers);
        DbSessionContext.clear();

        assertThrows(DataAccessException.class, repository::findAllUsers);
    }

    @Test
    void applyDbSession_withUserId_setsCurrentUserIdSessionVariable() throws Throwable {
        UUID userId = UUID.randomUUID();
        DbSessionContext.set(DbSessionRole.USER, userId);
        ProceedingJoinPoint joinPoint = currentUserIdQueryJoinPoint();

        Object result = dbSessionAspect.applyDbSession(joinPoint);

        assertEquals(userId.toString(), result);
    }

    @Test
    void applyDbSession_withoutUserId_clearsCurrentUserIdSessionVariable() throws Throwable {
        DbSessionContext.set(DbSessionRole.ADMINISTRATOR, null);
        ProceedingJoinPoint joinPoint = currentUserIdQueryJoinPoint();

        Object result = dbSessionAspect.applyDbSession(joinPoint);

        assertEquals("", result);
    }

    private ProceedingJoinPoint currentUserIdQueryJoinPoint() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed())
                .thenAnswer(_ -> jdbcTemplate.queryForObject(
                        "SELECT current_setting('app.current_user_id', true)", String.class));
        return joinPoint;
    }
}
