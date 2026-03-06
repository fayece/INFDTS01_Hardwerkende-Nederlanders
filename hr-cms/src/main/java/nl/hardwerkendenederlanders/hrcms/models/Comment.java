package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

@SuperBuilder
@Getter
public class Comment extends BaseTimedEntity {

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

    @Setter
    @Nullable
    private OffsetDateTime deletedAt;
    /*
     Note: deletedAt will be set when a comment is "deleted". This allows us to keep the comment in the database
     for historical and relational integrity, while marking it as deleted.
     The comment's body is also preserved, for potential moderation review, but should be replaced in the application
     with placeholder text (e.g., "[deleted]") when deletedAt is not null.
    */

    public Comment(
            UUID id,
            String commentBody,
            UUID creatorId,
            UUID articleId,
            @Nullable UUID mediaId,
            @Nullable UUID parentCommentId,
            OffsetDateTime createdAt,
            @Nullable OffsetDateTime deletedAt) {
        super(id, createdAt);
        this.commentBody = commentBody;
        this.creatorId = creatorId;
        this.articleId = articleId;
        this.mediaId = mediaId;
        this.parentCommentId = parentCommentId;
        this.deletedAt = deletedAt;
    }
}
