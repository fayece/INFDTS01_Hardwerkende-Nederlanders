package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.Set;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Permission;

public interface PermissionRepository extends DatabaseMutableRepository<Permission> {
    boolean hasPermission(UUID userId, String permissionKey);

    Set<String> findUserPermissions(UUID userId);
}
