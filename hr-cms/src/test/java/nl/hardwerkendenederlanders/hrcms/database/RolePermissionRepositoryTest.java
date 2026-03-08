package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.PermissionRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.RolePermissionRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Permission;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.RolePermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class RolePermissionRepositoryTest {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        role = new Role("Editor");
        roleRepository.insert(role);

        permission = new Permission("ARTICLE", "READ", "article_read");
        permissionRepository.insert(permission);
    }

    @Test
    void insertRolePermission_withValidRolePermission_shouldPersistAndRetrieve() {
        RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());

        rolePermissionRepository.insert(rolePermission);

        RolePermission retrieved = rolePermissionRepository.findById(rolePermission.getId());

        assertNotNull(retrieved);
        assertEquals(rolePermission.getId(), retrieved.getId());
        assertEquals(role.getId(), retrieved.getRoleId());
        assertEquals(permission.getId(), retrieved.getPermissionId());
    }

    @Test
    void findRolePermissionById_withExistingId_shouldReturnRolePermission() {
        RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());

        rolePermissionRepository.insert(rolePermission);

        RolePermission retrieved = rolePermissionRepository.findById(rolePermission.getId());

        assertNotNull(retrieved);
        assertEquals(rolePermission.getId(), retrieved.getId());
        assertEquals(rolePermission.getRoleId(), retrieved.getRoleId());
        assertEquals(rolePermission.getPermissionId(), retrieved.getPermissionId());
    }

    @Test
    void findRolePermissionById_withNonExistingId_shouldThrowException() {
        assertThrows(Exception.class, () -> rolePermissionRepository.findById(UUID.randomUUID()));
    }

    @Test
    void deleteRolePermission_withExistingRolePermission_shouldRemoveFromDatabase() {
        RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());

        rolePermissionRepository.insert(rolePermission);
        rolePermissionRepository.delete(rolePermission.getId());

        assertThrows(Exception.class, () -> rolePermissionRepository.findById(rolePermission.getId()));
    }

    @Test
    void findAllRolePermissionsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            Permission newPermission = new Permission("RESOURCE_" + i, "READ", "resource_" + i + "_read");
            permissionRepository.insert(newPermission);

            rolePermissionRepository.insert(new RolePermission(role.getId(), newPermission.getId()));
        }

        var page1 = rolePermissionRepository.findAllPaged(1, 10);
        var page2 = rolePermissionRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllRolePermissionsPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> rolePermissionRepository.findAllPaged(offset, limit));
    }
}
