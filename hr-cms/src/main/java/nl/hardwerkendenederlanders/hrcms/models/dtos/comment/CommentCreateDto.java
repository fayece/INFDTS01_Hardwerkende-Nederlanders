package nl.hardwerkendenederlanders.hrcms.models.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import nl.hardwerkendenederlanders.hrcms.models.Comment;

@Getter
@Setter
public class CommentCreateDto {
    @NotBlank(message = "Comment body cannot be empty.")
    @Size(min = 2, message = "Comment must be at least 2 characters long.")
    @Size(max = 1000, message = "Comment cannot be longer than 1000 characters.")
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
