package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.RolePermission;

public interface RolePermissionRepository {
    Optional<RolePermission> findById(UUID id);

    List<RolePermission> findAllPaged(int page, int limit);
}
