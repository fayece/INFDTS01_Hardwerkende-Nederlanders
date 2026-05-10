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
        String authorName,
        UUID creatorId,
        OffsetDateTime createdAt,
        boolean isDeleted,
        boolean hasReplies) {
    public static CommentViewDto from(Comment comment, String authorName, int replyCount) {
        boolean isDeleted = comment.getDeletedAt() != null;

        return CommentViewDto.builder()
                .id(comment.getId())
                .articleId(comment.getArticleId())
                .parentCommentId(comment.getParentCommentId())
                .mediaId(comment.getMediaId())
                .commentBody(comment.getCommentBody())
                .authorName(authorName)
                .creatorId(comment.getCreatorId())
                .createdAt(comment.getCreatedAt())
                .isDeleted(isDeleted)
                .hasReplies(replyCount > 0)
                .build();
    }
}
