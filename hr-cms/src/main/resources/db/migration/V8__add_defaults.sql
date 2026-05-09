ALTER TABLE subjects
ADD CONSTRAINT unique_subject_name UNIQUE (subject_name);


WITH roles_insert AS (
    INSERT INTO roles (id, role_name, internal_name) VALUES
        (gen_random_uuid(), 'Administrator', 'ADMIN'),
        (gen_random_uuid(), 'Content Manager', 'CONTENT_MANAGER'),
        (gen_random_uuid(), 'User', 'USER')
    ON CONFLICT DO NOTHING
    RETURNING id, internal_name
)
SELECT * FROM roles_insert;


WITH perms_insert AS (
    INSERT INTO permissions(id, resource, action_name, permission_key, internal_name) VALUES
        (gen_random_uuid(), 'article', 'create', 'article:create', 'CREATE_ARTICLE'),
        (gen_random_uuid(), 'article', 'read', 'article:read', 'READ_ARTICLE'),
        (gen_random_uuid(), 'article', 'update', 'article:update', 'UPDATE_ARTICLE'),
        (gen_random_uuid(), 'article', 'delete', 'article:delete', 'DELETE_ARTICLE'),
        (gen_random_uuid(), 'article', 'publish', 'article:publish', 'PUBLISH_ARTICLE'),
        (gen_random_uuid(), 'comment', 'create', 'comment:create', 'CREATE_COMMENT'),
        (gen_random_uuid(), 'comment', 'read', 'comment:read', 'READ_COMMENT'),
        (gen_random_uuid(), 'comment', 'update', 'comment:update', 'UPDATE_COMMENT'),
        (gen_random_uuid(), 'comment', 'delete', 'comment:delete', 'DELETE_COMMENT'),
        -- user profiles are linked to users. No need to create profile:create or profile:delete permissions
        (gen_random_uuid(), 'profile', 'read', 'profile:read', 'READ_PROFILE'),
        (gen_random_uuid(), 'profile', 'update', 'profile:update', 'UPDATE_PROFILE'),
        (gen_random_uuid(), 'admin', 'manage_articles', 'admin:manage_articles', 'MANAGE_ARTICLES'),            -- article moderation
        (gen_random_uuid(), 'admin', 'manage_comments', 'admin:manage_comments', 'MANAGE_COMMENTS'),            -- comment moderation
        (gen_random_uuid(), 'admin', 'manage_users', 'admin:manage_users', 'MANAGE_USERS'),                     -- create/delete users, assign roles to users
        (gen_random_uuid(), 'admin', 'manage_profiles', 'admin:manage_profiles', 'MANAGE_PROFILES'),            -- in case someone's profile needs admin intervention
        (gen_random_uuid(), 'admin', 'manage_roles', 'admin:manage_roles', 'MANAGE_ROLES'),                     -- create/delete roles and assign permissions to roles
        (gen_random_uuid(), 'admin', 'manage_permissions', 'admin:manage_permissions', 'MANAGE_PERMISSIONS')    -- create/delete permissions
    ON CONFLICT DO NOTHING
    RETURNING id, internal_name
)
SELECT * FROM perms_insert;


WITH admin_role AS (
    SELECT id
    FROM roles
    WHERE internal_name = 'ADMIN'
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
