package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.UUID;

public interface PermissionService {
    boolean hasPermission(UUID userId, String permissionKey);
}
