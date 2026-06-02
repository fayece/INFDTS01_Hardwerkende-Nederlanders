ALTER TABLE subjects
    ADD CONSTRAINT unique_subject_name UNIQUE (subject_name);

ALTER TABLE permissions
    DROP COLUMN internal_name;

-- drop the old permission_key column and recreate it as generated columns.
ALTER TABLE permissions
    DROP COLUMN permission_key;

ALTER TABLE roles
    DROP COLUMN internal_name;

ALTER TABLE permissions
    ADD COLUMN permission_key VARCHAR(255) GENERATED ALWAYS AS (
        LOWER(resource) || ':' || LOWER(action_name)
    ) STORED UNIQUE;

ALTER TABLE roles
    ADD COLUMN internal_name VARCHAR(255) GENERATED ALWAYS AS (
        UPPER(REPLACE(role_name, ' ', '_'))
    ) STORED UNIQUE;


WITH roles_insert AS (
    INSERT INTO roles (id, role_name) VALUES
        (gen_random_uuid(), 'Administrator'),
        (gen_random_uuid(), 'Content Manager'),
        (gen_random_uuid(), 'User')
    ON CONFLICT DO NOTHING
    RETURNING id, internal_name
)
SELECT * FROM roles_insert;


WITH perms_insert AS (
    INSERT INTO permissions(id, resource, action_name) VALUES
        (gen_random_uuid(), 'article', 'create'),
        (gen_random_uuid(), 'article', 'read'),
        (gen_random_uuid(), 'article', 'update'),
        (gen_random_uuid(), 'article', 'delete'),
        (gen_random_uuid(), 'article', 'publish'),
        (gen_random_uuid(), 'comment', 'create'),
        (gen_random_uuid(), 'comment', 'read'),
        (gen_random_uuid(), 'comment', 'update'),
        (gen_random_uuid(), 'comment', 'delete'),
        (gen_random_uuid(), 'media', 'upload'),
        -- user profiles are linked to users. No need to create 'create' or 'delete' permissions
        (gen_random_uuid(), 'profile', 'read'),
        (gen_random_uuid(), 'profile', 'update'),
        (gen_random_uuid(), 'admin', 'manage_articles'),            -- article moderation
        (gen_random_uuid(), 'admin', 'manage_comments'),            -- comment moderation
        (gen_random_uuid(), 'admin', 'manage_users'),               -- create/delete users, assign roles to users
        (gen_random_uuid(), 'admin', 'manage_profiles'),            -- in case someone's profile needs admin intervention
        (gen_random_uuid(), 'admin', 'manage_roles'),               -- create/delete roles and assign permissions to roles
        (gen_random_uuid(), 'admin', 'manage_permissions')          -- create/delete permissions
    ON CONFLICT DO NOTHING
    RETURNING id
)
SELECT * FROM perms_insert;


WITH admin_role AS (
    SELECT id
    FROM roles
    WHERE internal_name = 'ADMINISTRATOR'
),
content_manager_role AS (
    SELECT id
    FROM roles
    WHERE internal_name = 'CONTENT_MANAGER'
),
user_role AS (
    SELECT id FROM roles WHERE internal_name = 'USER'
),
article_create AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'article:create'
),
article_read AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'article:read'
),
article_update AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'article:update'
),
article_delete AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'article:delete'
),
article_publish AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'article:publish'
),
comment_create AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'comment:create'
),
comment_read AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'comment:read'
),
comment_update AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'comment:update'
),
comment_delete AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'comment:delete'
),
media_upload AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'media:upload'
),
profile_read AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'profile:read'
),
profile_update AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'profile:update'
),
admin_manage_articles AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'admin:manage_articles'
),
admin_manage_comments AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'admin:manage_comments'
),
admin_manage_users AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'admin:manage_users'
),
admin_manage_profiles AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'admin:manage_profiles'
),
admin_manage_roles AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'admin:manage_roles'
),
admin_manage_permissions AS (
    SELECT id
    FROM permissions
    WHERE permission_key = 'admin:manage_permissions'
)


INSERT INTO role_permissions (id, role_id, permission_id) VALUES

    -- Admin - all permissions
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM article_create)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM article_read)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM article_update)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM article_delete)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM article_publish)),

    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM comment_create)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM comment_read)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM comment_update)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM comment_delete)),

    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM media_upload)),

    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM profile_read)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM profile_update)),

    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM admin_manage_articles)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM admin_manage_comments)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM admin_manage_users)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM admin_manage_profiles)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM admin_manage_roles)),
    (gen_random_uuid(), (SELECT id FROM admin_role), (SELECT id FROM admin_manage_permissions)),


    -- Content Manager - all non-admin specific permissions
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM article_create)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM article_read)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM article_update)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM article_delete)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM article_publish)),

    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM comment_create)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM comment_read)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM comment_update)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM comment_delete)),

    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM media_upload)),

    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM profile_read)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM profile_update)),

    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM admin_manage_articles)),
    (gen_random_uuid(), (SELECT id FROM content_manager_role), (SELECT id FROM admin_manage_comments)),


    -- User - basic engagement permissions
    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM article_read)),

    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM comment_create)),
    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM comment_read)),
    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM comment_update)),
    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM comment_delete)),

    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM media_upload)),

    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM profile_read)),
    (gen_random_uuid(), (SELECT id FROM user_role), (SELECT id FROM profile_update))

ON CONFLICT DO NOTHING;

INSERT INTO subjects (id, subject_name) VALUES
    (gen_random_uuid(), 'Announcements'),
    (gen_random_uuid(), 'General Discussion'),
    (gen_random_uuid(), 'Artist Discussion'),
    (gen_random_uuid(), 'Gig Updates'),
    (gen_random_uuid(), 'Suggestions')
ON CONFLICT (subject_name) DO NOTHING;
