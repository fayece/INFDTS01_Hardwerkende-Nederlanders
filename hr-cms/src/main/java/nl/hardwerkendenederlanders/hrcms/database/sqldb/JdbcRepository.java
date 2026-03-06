package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.DatabaseRepository;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.FileCopyUtils;

public abstract class JdbcRepository<T> implements DatabaseRepository<T> {

    protected final NamedParameterJdbcTemplate jdbc;
    private final Map<String, String> queries = new HashMap<>();
    private final Class<T> type;

    public JdbcRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader, Class<T> type) {

        this.jdbc = jdbc;
        this.type = type;
        loadSqlFile(resourceLoader);
    }

    // SuppressWarning annotations are used here to indicate that the SQL queries are safe for all intents and purposes
    // within the context of this application, as they are loaded from trusted resources and use parameterized queries
    // to prevent SQL injection.

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void insert(T entity) {

        jdbc.update(getQuery("insert"), new EnumAwareSqlParameterSource(entity));
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public T findById(UUID id) {

        return jdbc.queryForObject(getQuery("findById"), Map.of("id", id), rowMapper());
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void delete(UUID id) {

        jdbc.update(getQuery("deleteById"), Map.of("id", id));
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public List<T> findAllPaged(int page, int limit) {

        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0.");
        if (page <= 0) throw new IllegalArgumentException("Page must be greater than 0.");
        int offset = (page - 1) * limit;
        return jdbc.query(getQuery("findAllPaged"), Map.of("offset", offset, "limit", limit), rowMapper());
    }

    private void loadSqlFile(ResourceLoader resourceLoader) {
        String fileName = this.getClass().getSimpleName() + ".sql";
        try {
            var resource = resourceLoader.getResource("classpath:sql/" + fileName);
            if (!resource.exists()) return;

            String content = FileCopyUtils.copyToString(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String[] blocks = content.split("-- name: ");
            for (String block : blocks) {
                if (block.isBlank()) continue;
                String[] lines = block.split("\\R", 2);
                if (lines.length == 2) queries.put(lines[0].trim(), lines[1].trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SQL for " + fileName, e);
        }
    }

    protected String getQuery(String name) {
        String query = queries.get(name);
        if (query == null) throw new IllegalArgumentException("Query '" + name + "' not found.");
        return query;
    }

    protected RowMapper<T> rowMapper() {
        return new BeanPropertyRowMapper<>(type);
    }
}
