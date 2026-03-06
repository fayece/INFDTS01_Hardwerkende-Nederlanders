-- name: insert
INSERT INTO article_authors (id, article_id, author_id)
VALUES (:id, :articleId, :authorId);

-- name: update
UPDATE article_authors
SET article_id = :articleId,
    author_id = :authorId
WHERE id = :id;

-- name: findById
SELECT * FROM article_authors WHERE id = :id;

-- name: deleteById
DELETE FROM article_authors WHERE id = :id;

-- name: findAllPaged
SELECT * FROM article_authors
         LIMIT :limit
             OFFSET :offset;
