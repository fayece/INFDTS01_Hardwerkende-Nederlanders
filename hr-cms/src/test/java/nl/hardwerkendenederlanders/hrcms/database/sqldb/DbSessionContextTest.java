package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class DbSessionContextTest {

    @AfterEach
    void tearDown() {
        DbSessionContext.clear();
    }

    @Test
    void getUserId_withNoneSet_isNull() {
        assertNull(DbSessionContext.getUserId());
    }

    @Test
    void set_storesRoleAndUserId() {
        UUID userId = UUID.randomUUID();

        DbSessionContext.set(DbSessionRole.USER, userId);

        assertEquals(DbSessionRole.USER, DbSessionContext.getRole());
        assertEquals(userId, DbSessionContext.getUserId());
    }

    @Test
    void set_withNullUserId_isAllowed() {
        DbSessionContext.set(DbSessionRole.ADMINISTRATOR, null);

        assertEquals(DbSessionRole.ADMINISTRATOR, DbSessionContext.getRole());
        assertNull(DbSessionContext.getUserId());
    }

    @Test
    void clear_resetsRoleAndUserId() {
        DbSessionContext.set(DbSessionRole.USER, UUID.randomUUID());

        DbSessionContext.clear();

        assertEquals(DbSessionRole.UNAUTHENTICATED, DbSessionContext.getRole());
        assertNull(DbSessionContext.getUserId());
    }
}