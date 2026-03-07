-- name: insert
INSERT INTO organizations (id, org_name)
VALUES (:id, :orgName);

-- name: update
UPDATE organizations
SET org_name = :orgName
WHERE id = :id;

-- name: findById
SELECT * FROM organizations WHERE id = :id;

-- name: deleteById
DELETE FROM organizations WHERE id = :id;

-- name: findAllPaged
SELECT * FROM organizations
         ORDER BY org_name
         LIMIT :limit
             OFFSET :offset;