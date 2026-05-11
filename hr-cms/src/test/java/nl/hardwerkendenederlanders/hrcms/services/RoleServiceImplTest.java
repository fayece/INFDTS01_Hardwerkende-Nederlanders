package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.RoleService;
import org.junit.jupiter.api.Test;

class RoleServiceImplTest {

    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final RoleService roleService = new RoleServiceImpl(roleRepository);

    @Test
    void findAll_withExistingRoles_returnsListOfRoles() {
        List<Role> roles = List.of(
            Role.of("Admin").build(),
            Role.of("Editor").build()
        );
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.findAll();

        assertEquals(roles, result);
        verify(roleRepository).findAll();
    }

    @Test
    void findAll_withNoRoles_returnsEmptyList() {
        when(roleRepository.findAll()).thenReturn(List.of());

        List<Role> result = roleService.findAll();

        assertTrue(result.isEmpty());
        verify(roleRepository).findAll();
    }
}
