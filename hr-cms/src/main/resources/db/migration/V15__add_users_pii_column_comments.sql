CREATE SCHEMA IF NOT EXISTS pii;
CREATE SCHEMA IF NOT EXISTS pii_strict;

CREATE TABLE pii.users_pii (
    user_id         UUID            PRIMARY KEY     REFERENCES users(id) ON DELETE CASCADE,
    first_name      VARCHAR(255)    NOT NULL,
    prefix          VARCHAR(50),
    last_name       VARCHAR(255)    NOT NULL,
    role_id         UUID                            REFERENCES roles(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ     NOT NULL        DEFAULT NOW(),
    active          BOOLEAN         NOT NULL        DEFAULT TRUE
);

CREATE TABLE pii_strict.users_pii_strict (
    user_id         UUID            PRIMARY KEY     REFERENCES users (id) ON DELETE CASCADE,
    password_hash   VARCHAR(255)    NOT NULL
);

INSERT INTO pii.users_pii (user_id, first_name, prefix, last_name, role_id, created_at, active)
SELECT id, first_name, prefix, last_name, role_id, created_at, active
FROM users;

INSERT INTO pii_strict.users_pii_strict (user_id, password_hash)
SELECT id, password_hash
FROM users;


-- Recreate views to support the pii.users_pii table.
DROP VIEW IF EXISTS full_articles;
DROP VIEW IF EXISTS article_authors_named;

CREATE VIEW article_authors_named AS
    SELECT aa.id, aa.article_id, aa.author_id, aa.created_at, upii.role_id, r.role_name
    FROM article_authors AS aa
        JOIN pii.users_pii AS upii ON aa.author_id = upii.user_id
        LEFT JOIN roles AS r ON upii.role_id = r.id
    ORDER BY aa.created_at;

CREATE VIEW full_articles AS
    WITH most_recent_authors AS (
        SELECT *
        FROM article_authors_named AS aan
        WHERE aan.created_at IN
        (SELECT min(created_at) FROM article_authors GROUP BY article_id)
    )
    SELECT
        a.id as article_id,
        a.title,
        a.text_content,
        a.updated_at,
        a.created_at,
        a.publication_status,
        s.subject_name,
        count(distinct av.id) as view_count,
        0 as comment_count,
        mrs.author_id as first_author_id

    FROM articles a
        LEFT JOIN subjects s ON s.id = a.subject_id
        LEFT JOIN article_viewers av ON a.id = av.article_id
        LEFT JOIN most_recent_authors mrs on a.id = mrs.article_id

    GROUP BY a.id, s.id, mrs.author_id;

ALTER TABLE users
    DROP COLUMN first_name,
    DROP COLUMN prefix,
    DROP COLUMN last_name,
    DROP COLUMN password_hash,
    DROP COLUMN role_id,
    DROP COLUMN created_at,
    DROP COLUMN active;

REVOKE ALL ON SCHEMA pii FROM PUBLIC;
REVOKE ALL ON SCHEMA pii_strict FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA pii FROM PUBLIC;
REVOKE ALL ON ALL TABLES IN SCHEMA pii_strict FROM PUBLIC;
