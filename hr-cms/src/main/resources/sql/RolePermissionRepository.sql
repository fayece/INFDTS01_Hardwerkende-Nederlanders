-- name: insert
INSERT INTO role_permissions (id, role_id, permission_id)
VALUES (:id, :roleId, :permissionId);

-- name: update
UPDATE role_permissions
SET role_id = :roleId,
    permission_id = :permissionId
WHERE id = :id;

-- name: findById
SELECT * FROM role_permissions WHERE id = :id;

-- name: deleteById
DELETE FROM role_permissions WHERE id = :id;

-- name: findAllPaged
SELECT * FROM role_permissions
         LIMIT :limit
             OFFSET :offset;