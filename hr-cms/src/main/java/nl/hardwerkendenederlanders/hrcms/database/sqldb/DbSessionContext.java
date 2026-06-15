package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;

public final class DbSessionContext {

    private static final ThreadLocal<DbSessionRole> CURRENT_ROLE = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_USER_ID = new ThreadLocal<>();

    private DbSessionContext() {}

    public static void set(DbSessionRole role, UUID userId) {
        CURRENT_ROLE.set(role);
        CURRENT_USER_ID.set(userId);
    }

    public static DbSessionRole getRole() {
        DbSessionRole role = CURRENT_ROLE.get();
        return role != null ? role : DbSessionRole.UNAUTHENTICATED;
    }

    public static UUID getUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void clear() {
        CURRENT_ROLE.remove();
        CURRENT_USER_ID.remove();
    }
}