package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.OrganizationRepository;
import nl.hardwerkendenederlanders.hrcms.models.Organization;
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
public class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void insertOrganization_withValidOrganization_shouldPersistAndRetrieve() {

        Organization organization = new Organization("Test Organization");
        organizationRepository.insert(organization);

        Organization retrieved = organizationRepository.findById(organization.getId());

        assertNotNull(retrieved);
        assertEquals(organization.getId(), retrieved.getId());
        assertEquals(organization.getOrgName(), retrieved.getOrgName());
    }

    @Test
    void updateOrganization_withModifiedFields_shouldReflectChanges() {
        Organization organization = new Organization("Test Organization");
        organizationRepository.insert(organization);

        String newOrgName = "Updated Organization Name";
        organization.setOrgName(newOrgName);
        organizationRepository.update(organization);

        Organization updated = organizationRepository.findById(organization.getId());

        assertNotNull(updated);
        assertEquals(organization.getId(), updated.getId());
        assertEquals(newOrgName, updated.getOrgName());
    }

    @Test
    void findOrganizationById_withExistingId_shouldReturnOrganization() {
        Organization organization = new Organization("Test Organization");
        organizationRepository.insert(organization);

        Organization found = organizationRepository.findById(organization.getId());

        assertNotNull(found);
        assertEquals(organization.getId(), found.getId());
        assertEquals(organization.getOrgName(), found.getOrgName());
    }

    @Test
    void findOrganizationById_withNonExistingId_shouldReturnNull() {
        assertThrows(Exception.class, () -> organizationRepository.findById(UUID.randomUUID()));
    }

    @Test
    void deleteOrganization_withExistingOrganization_shouldRemoveFromDatabase() {
        Organization organization = new Organization("Test Organization");
        organizationRepository.insert(organization);

        organizationRepository.delete(organization.getId());

        assertThrows(Exception.class, () -> organizationRepository.findById(organization.getId()));
    }

    @Test
    void findAllOrganizationsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            Organization organization = new Organization("Organization " + i);
            organizationRepository.insert(organization);
        }

        var page1 = organizationRepository.findAllPaged(1, 10);
        var page2 = organizationRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllMediaItemsPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> organizationRepository.findAllPaged(offset, limit));
    }
}
