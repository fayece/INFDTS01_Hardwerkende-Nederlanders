package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.UUID;

public interface PermissionRepository {
    boolean hasPermission(UUID userId, String permissionKey);
}
