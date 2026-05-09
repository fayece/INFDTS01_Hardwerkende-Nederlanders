package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.Permission;

import java.util.Set;
import java.util.UUID;

public interface PermissionRepository extends DatabaseMutableRepository<Permission> {
    boolean hasPermission(UUID userId, String permissionKey);
    Set<String> findUserPermissions(UUID userId);
}
