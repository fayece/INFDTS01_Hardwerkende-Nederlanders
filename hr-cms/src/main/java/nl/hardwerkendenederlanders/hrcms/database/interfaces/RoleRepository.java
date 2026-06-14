package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Role;

public interface RoleRepository {

    Optional<Role> findById(UUID id);

    List<Role> findAll();

    List<Role> findAllPaged(int page, int limit);

    Optional<String> findInternalNameById(UUID id);
}
