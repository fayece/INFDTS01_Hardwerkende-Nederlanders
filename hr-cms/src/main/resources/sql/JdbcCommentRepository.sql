-- name: insert
INSERT INTO comments (id, media_id, comment_body, creator_id, article_id, parent_comment_id, created_at, deleted_at)
VALUES (:id, :mediaId, :commentBody, :creatorId, :articleId, :parentCommentId, :createdAt, :deletedAt);

-- name: update
UPDATE comments
SET media_id = :mediaId,
    comment_body = :commentBody,
    creator_id = :creatorId,
    article_id = :articleId,
    created_at = :createdAt
WHERE id = :id;

-- name: findById
SELECT * FROM comments WHERE id = :id;

-- name: deleteById
UPDATE comments
SET deleted_at = NOW()
WHERE id = :id;

-- name: findAllPaged
SELECT * FROM comments
         ORDER BY created_at DESC
         LIMIT :limit
             OFFSET :offset;