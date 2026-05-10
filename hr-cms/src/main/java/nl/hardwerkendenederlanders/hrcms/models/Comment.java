package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Builder
@Getter
public class Comment {

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Setter
    @Nullable
    private UUID mediaId;

    @Setter
    private String commentBody;

    @Setter
    private UUID creatorId;

    @Setter
    private UUID articleId;

    @Setter
    @Nullable
    private UUID parentCommentId;

    @Builder.Default
    private final OffsetDateTime createdAt = OffsetDateTime.now();

    @Setter
    @Nullable
    private OffsetDateTime deletedAt;
}
