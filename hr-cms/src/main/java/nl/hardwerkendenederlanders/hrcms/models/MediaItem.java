package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class MediaItem extends BaseTimedEntity {

    @Setter
    private String url;

    @Setter
    @Builder.Default
    private MediaType mediaType = MediaType.IMAGE;

    public MediaItem(UUID id, String url, MediaType mediaType, OffsetDateTime createdAt) {
        super(id, createdAt);
        this.url = url;
        this.mediaType = mediaType;
    }

    public MediaItem(String url, MediaType mediaType) {
        super();
        this.url = url;
        this.mediaType = mediaType;
    }
}
