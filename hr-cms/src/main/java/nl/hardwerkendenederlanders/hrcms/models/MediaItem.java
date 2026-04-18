package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class MediaItem {

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Setter
    private String url;

    @Setter
    @Builder.Default
    private MediaType mediaType = MediaType.IMAGE;

    @Builder.Default
    private final OffsetDateTime createdAt = OffsetDateTime.now();
}
