package nl.hardwerkendenederlanders.hrcms.services;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.PermissionRepository;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.PermissionService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public boolean hasPermission(UUID userId, String permissionKey) {
        return permissionRepository.hasPermission(userId, permissionKey);
    }
}
