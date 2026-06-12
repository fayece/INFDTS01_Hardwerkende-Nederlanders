package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Role;

public interface RoleRepository extends DatabaseMutableRepository<Role> {
    List<Role> findAll();

    Optional<String> findInternalNameById(UUID id);
}
