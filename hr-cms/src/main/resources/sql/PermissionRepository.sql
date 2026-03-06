-- name: insert
INSERT INTO permissions (id, resource, action_name, permission_key, internal_name)
VALUES (:id, :resource, :actionName, :permissionKey, :internalName);

-- name: update
UPDATE permissions
SET resource = :resource,
    action_name = :actionName,
    permission_key = :permissionKey,
    internal_name = :internalName
WHERE id = :id;

-- name: findById
SELECT * FROM permissions WHERE id = :id;

-- name: deleteById
DELETE FROM permissions WHERE id = :id;

-- name: findAllPaged
SELECT * FROM permissions
         LIMIT :limit
             OFFSET :offset;