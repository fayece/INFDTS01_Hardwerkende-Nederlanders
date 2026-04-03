package nl.hardwerkendenederlanders.hrcms.database;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcUserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

class JdbcUserRepositoryTest {

    @Test
    void insert_shouldCallJdbcUpdate() {
        NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
        JdbcUserRepository repository = new JdbcUserRepository(jdbc);

        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(jdbc.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        repository.insert(user);

        verify(jdbc).update(anyString(), any(SqlParameterSource.class));
    }

    @Test
    void insert_shouldReturnOneRowAffected() {
        NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
        JdbcUserRepository repository = new JdbcUserRepository(jdbc);

        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(jdbc.update(anyString(), any(SqlParameterSource.class))).thenReturn(1);

        repository.insert(user);

        verify(jdbc).update(anyString(), any(SqlParameterSource.class));
    }
}
