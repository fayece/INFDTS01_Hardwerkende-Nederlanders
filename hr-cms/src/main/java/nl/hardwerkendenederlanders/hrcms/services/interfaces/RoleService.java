package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.List;
import nl.hardwerkendenederlanders.hrcms.models.Role;

public interface RoleService {
    List<Role> findAll();
}
