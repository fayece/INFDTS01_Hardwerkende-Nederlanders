package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.MediaItem;

public interface MediaRepository {

    void insert(MediaItem entity);

    Optional<MediaItem> findById(UUID id);

    List<MediaItem> findAllPaged(int page, int limit);
}
