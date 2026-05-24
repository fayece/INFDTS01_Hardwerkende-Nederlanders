package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.LoggingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcIntegrityLogRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String TABLE = "integrity_logs";

    public void save(LoggingEntity log) {
        jdbcTemplate.update(
                String.format(
                        "INSERT INTO %s (id, message, severity, timestamp, user_id, profile_id) VALUES (?, ?, ?, ?, ?, ?)",
                        TABLE),
                UUID.randomUUID(),
                log.getMessage(),
                log.getSeverity(),
                log.getTimestamp(),
                log.getUserId(),
                log.getProfileId());
    }
}
