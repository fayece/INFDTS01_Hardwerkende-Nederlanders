package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Organization;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationRepository extends JdbcMutableRepository<Organization> {

    public OrganizationRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, Organization.class);
    }

    @Override
    protected RowMapper<Organization> rowMapper() {
        return (rs, _) -> new Organization(UUID.fromString(rs.getString("id")), rs.getString("org_name"));
    }
}
