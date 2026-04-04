package nl.hardwerkendenederlanders.hrcms.models.dtos.comment;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import nl.hardwerkendenederlanders.hrcms.models.Comment;

@Getter
@Setter
public class CommentCreateDto {
    private String commentBody;
    private String authorId;
    private String mediaId;
    private String parentCommentId;

    public Comment toComment(UUID articleId) {
        return Comment.builder()
                .commentBody(this.commentBody)
                .creatorId(parseUuid(this.authorId))
                .articleId(articleId)
                .mediaId(parseUuid(this.mediaId))
                .parentCommentId(parseUuid(this.parentCommentId))
                .build();
    }

    private UUID parseUuid(String idString) {
        if (idString == null || idString.trim().isEmpty()) {
            return null;
        }
        return UUID.fromString(idString.trim());
    }
}
