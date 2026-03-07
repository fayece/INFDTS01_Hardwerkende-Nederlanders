-- name: insert
INSERT INTO users (
                   id,
                   first_name,
                   prefix,
                   last_name,
                   email,
                   password_hash,
                   role_id,
                   organization_id,
                   created_at,
                   active)
VALUES (:id,
        :firstName,
        :prefix,
        :lastName,
        :email,
        :passwordHash,
        :roleId,
        :organizationId,
        :createdAt,
        :active
       );

-- name: update
UPDATE users
SET first_name = :firstName,
    prefix = :prefix,
    last_name = :lastName,
    email = :email,
    password_hash = :passwordHash,
    role_id = :roleId,
    organization_id = :organizationId,
    created_at = :createdAt,
    active = :active
WHERE id = :id;

-- name: findById
SELECT * FROM users WHERE id = :id;

-- name: deleteById
DELETE FROM users WHERE id = :id;

-- name: findAllPaged
SELECT * FROM users
         ORDER BY created_at DESC
         LIMIT :limit
             OFFSET :offset;
