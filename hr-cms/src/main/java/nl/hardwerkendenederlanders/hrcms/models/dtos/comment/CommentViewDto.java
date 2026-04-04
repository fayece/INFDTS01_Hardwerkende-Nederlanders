package nl.hardwerkendenederlanders.hrcms.models.dtos.comment;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import nl.hardwerkendenederlanders.hrcms.models.Comment;

@Builder
public record CommentViewDto(
        UUID id,
        UUID articleId,
        UUID parentCommentId,
        UUID mediaId,
        String commentBody,
        String creatorName,
        OffsetDateTime createdAt,
        boolean isDeleted) {
    public static CommentViewDto from(Comment comment, String authorName) {
        boolean isDeleted = comment.getDeletedAt() != null;

        return CommentViewDto.builder()
                .id(comment.getId())
                .articleId(comment.getArticleId())
                .parentCommentId(comment.getParentCommentId())
                .mediaId(comment.getMediaId())
                .commentBody(comment.getCommentBody())
                .creatorName(authorName)
                .createdAt(comment.getCreatedAt())
                .isDeleted(isDeleted)
                .build();
    }
}
