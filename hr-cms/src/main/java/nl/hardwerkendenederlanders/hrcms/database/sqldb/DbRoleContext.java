package nl.hardwerkendenederlanders.hrcms.database.sqldb;

public final class DbRoleContext {

    private static final ThreadLocal<DbSessionRole> CURRENT_ROLE = new ThreadLocal<>();

    private DbRoleContext() {}

    public static void set(DbSessionRole role) {
        CURRENT_ROLE.set(role);
    }

    public static DbSessionRole get() {
        DbSessionRole role = CURRENT_ROLE.get();
        return role != null ? role : DbSessionRole.UNAUTHENTICATED;
    }

    public static void clear() {
        CURRENT_ROLE.remove();
    }
}
