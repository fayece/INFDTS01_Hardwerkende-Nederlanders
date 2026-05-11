package nl.hardwerkendenederlanders.hrcms.services;

import java.util.List;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.RoleService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
