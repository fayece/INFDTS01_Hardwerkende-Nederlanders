package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import nl.hardwerkendenederlanders.hrcms.models.Role;

public interface RoleRepository extends DatabaseMutableRepository<Role> {
    List<Role> findAll();
}
