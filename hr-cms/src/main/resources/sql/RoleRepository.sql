-- name: insert
INSERT INTO roles (id, role_name, internal_name)
VALUES (:id, :roleName, :internalName);

-- name: update
UPDATE roles
SET role_name = :roleName,
    internal_name = :internalName
WHERE id = :id;

-- name: findById
SELECT * FROM roles WHERE id = :id;

-- name: deleteById
DELETE FROM roles WHERE id = :id;

-- name: findAllPaged
SELECT * FROM roles
         LIMIT :limit
             OFFSET :offset;