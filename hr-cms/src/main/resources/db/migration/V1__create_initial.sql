CREATE TABLE organizations (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    org_name            VARCHAR(255)            NOT NULL
);

CREATE TABLE roles (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    role_name           VARCHAR(255)            NOT NULL,
    internal_name       VARCHAR(255)            NOT NULL                            UNIQUE
);

CREATE TABLE permissions (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    resource            VARCHAR(255)            NOT NULL,
    action_name         VARCHAR(255)            NOT NULL,
    permission_key      VARCHAR(255)            NOT NULL                            UNIQUE,
    internal_name       VARCHAR(255)            NOT NULL                            UNIQUE
);

CREATE TABLE role_permissions (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    role_id             UUID                    NOT NULL REFERENCES roles(id)       ON DELETE CASCADE,
    permission_id       UUID                    NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE (role_id, permission_id)
);

CREATE TABLE users (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    first_name          VARCHAR(255)            NOT NULL,
    prefix              VARCHAR(50),
    last_name           VARCHAR(255)            NOT NULL,
    email               VARCHAR(255)            NOT NULL                            UNIQUE,
    password_hash       VARCHAR(255)            NOT NULL,
    role_id             UUID                    REFERENCES roles(id)                ON DELETE SET NULL,
    -- role_id should be not null, but to avoid issues during initial migrations, we set it to nullable for now.
    -- Once we have added a default role,
    -- we can update role_id to have that role as default and set it to NOT NULL when ON DELETE is activated.
    organization_id     UUID                    REFERENCES organizations(id)        ON DELETE SET NULL,
    created_at          TIMESTAMPTZ             NOT NULL                            DEFAULT NOW(),
    active              BOOLEAN                 NOT NULL                            DEFAULT TRUE
);

CREATE TABLE media_items (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    url                 TEXT                    NOT NULL,
    media_type          VARCHAR(255)            NOT NULL                            DEFAULT 'IMAGE',
    created_at          TIMESTAMPTZ             NOT NULL                            DEFAULT NOW()
);

CREATE TABLE articles (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    title               VARCHAR(255)            NOT NULL,
    text_content        TEXT                    NOT NULL,
    created_at          TIMESTAMPTZ             NOT NULL                            DEFAULT NOW(),
    updated_at          TIMESTAMPTZ,
    publication_status  VARCHAR(255)            NOT NULL                            DEFAULT 'DRAFT'
);

CREATE TABLE article_authors (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    article_id          UUID                    NOT NULL REFERENCES articles(id)    ON DELETE CASCADE,
    author_id           UUID                    NOT NULL REFERENCES users(id)       ON DELETE CASCADE,
    UNIQUE (article_id, author_id)
);

CREATE TABLE comments (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    media_id            UUID                    REFERENCES media_items(id)          ON DELETE SET NULL,
    comment_body        TEXT                    NOT NULL,
    creator_id          UUID                    NOT NULL REFERENCES users(id)       ON DELETE CASCADE,
    article_id          UUID                    NOT NULL REFERENCES articles(id)    ON DELETE CASCADE,
    parent_comment_id   UUID                    REFERENCES comments(id),
    created_at          TIMESTAMPTZ             NOT NULL                            DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);
