-- name: insert
INSERT INTO media_items (id, url, media_type, created_at)
VALUES (:id, :url, :mediaType, :createdAt);

-- name: update
UPDATE media_items
SET url = :url,
    media_type = :mediaType,
    created_at = :createdAt
WHERE id = :id;

-- name: findById
SELECT * FROM media_items WHERE id = :id;

-- name: deleteById
DELETE FROM media_items WHERE id = :id;

-- name: findAllPaged
SELECT * FROM media_items
         ORDER BY created_at DESC
         LIMIT :limit
             OFFSET :offset;