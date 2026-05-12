package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import nl.hardwerkendenederlanders.hrcms.database.interfaces.DatabaseMutableRepository;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class JdbcMutableRepository<T> extends JdbcRepository<T> implements DatabaseMutableRepository<T> {

    public JdbcMutableRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader, Class<T> type) {
        super(jdbc, resourceLoader, type);
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void update(T entity) {

        jdbc.update(getQuery("update"), new EnumAwareSqlParameterSource(entity));
    }
}
