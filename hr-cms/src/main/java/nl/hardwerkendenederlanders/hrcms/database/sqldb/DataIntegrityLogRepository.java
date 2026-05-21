package nl.hardwerkendenederlanders.hrcms.database.sqldb;
import nl.hardwerkendenederlanders.hrcms.models.LoggingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class DataIntegrityLogRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final String TABLE = "integrity_logs";

    public void save(LoggingEntity log) {
        jdbcTemplate.update(
                String.format("INSERT INTO %s (id, message, severity, timestamp) VALUES (?, ?, ?, ?)", TABLE),
                UUID.randomUUID(),
                log.getMessage(),
                log.getSeverity(),
                log.getTimestamp()
        );
    }
}
