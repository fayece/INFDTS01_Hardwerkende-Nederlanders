-- name: insert
INSERT INTO articles (id, title, text_content, created_at, updated_at, publication_status)
VALUES (:id, :title, :textContent, :createdAt, :updatedAt, :publicationStatus);

-- name: update
UPDATE articles
SET title = :title,
    text_content = :textContent,
    created_at = :createdAt,
    updated_at = :updatedAt,
    publication_status = :publicationStatus
WHERE id = :id;

-- name: findById
SELECT * FROM articles WHERE id = :id;

-- name: deleteById
DELETE FROM articles WHERE id = :id;

-- name: findAllPaged
SELECT * FROM articles
         ORDER BY created_at DESC
         LIMIT :limit
             OFFSET :offset;
