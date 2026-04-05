package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.MediaRepository;
import nl.hardwerkendenederlanders.hrcms.models.MediaItem;
import nl.hardwerkendenederlanders.hrcms.models.MediaType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcMediaRepository implements MediaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcMediaRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    protected RowMapper<MediaItem> rowMapper() {
        return (rs, _) -> MediaItem.builder()
                .id(UUID.fromString(rs.getString("id")))
                .url(rs.getString("url"))
                .mediaType(MediaType.valueOf(rs.getString("media_type")))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .build();
    }

    private MapSqlParameterSource paramsFromMediaItem(MediaItem mediaItem) {
        return new MapSqlParameterSource()
                .addValue("id", mediaItem.getId())
                .addValue("url", mediaItem.getUrl())
                .addValue("mediaType", mediaItem.getMediaType().name())
                .addValue("createdAt", mediaItem.getCreatedAt());
    }

    @Override
    public void insert(MediaItem entity) {

        String sql = """
            INSERT INTO media_items
                (id, url, media_type, created_at)
            VALUES
                (:id, :url, :mediaType, :createdAt);
            """;
        jdbc.update(sql, paramsFromMediaItem(entity));
    }

    @Override
    public void update(MediaItem entity) {

        String sql = """
            UPDATE media_items
            SET url = :url,
                media_type = :mediaType,
                created_at = :createdAt
            WHERE id = :id;
            """;
        jdbc.update(sql, paramsFromMediaItem(entity));
    }

    @Override
    public Optional<MediaItem> findById(UUID id) {

        String sql = """
            SELECT *
            FROM media_items
            WHERE id = :id;
            """;

        List<MediaItem> results = jdbc.query(sql, Map.of("id", id), rowMapper());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public void delete(UUID id) {

        String sql = """
            DELETE FROM media_items
            WHERE id = :id;
            """;

        jdbc.update(sql, Map.of("id", id));
    }

    @Override
    public List<MediaItem> findAllPaged(int page, int limit) {

        String sql = """
            SELECT * FROM media_items
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset;
            """;

        return jdbc.query(sql, Map.of("limit", limit, "offset", (page - 1) * limit), rowMapper());
    }
}
