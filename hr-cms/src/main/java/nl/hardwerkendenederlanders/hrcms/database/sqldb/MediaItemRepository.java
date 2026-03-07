package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.MediaItem;
import nl.hardwerkendenederlanders.hrcms.models.MediaType;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class MediaItemRepository extends JdbcMutableRepository<MediaItem> {

    public MediaItemRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, MediaItem.class);
    }

    @Override
    protected RowMapper<MediaItem> rowMapper() {
        return (rs, _) -> new MediaItem(
                UUID.fromString(rs.getString("id")),
                rs.getString("url"),
                MediaType.valueOf(rs.getString("media_type")),
                rs.getObject("created_at", OffsetDateTime.class));
    }
}
