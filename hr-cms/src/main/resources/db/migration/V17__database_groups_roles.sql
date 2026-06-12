-- Group roles
CREATE ROLE cms_role_unauthenticated NOLOGIN;
CREATE ROLE cms_role_user NOLOGIN;
CREATE ROLE cms_role_content_manager NOLOGIN;
CREATE ROLE cms_role_administrator NOLOGIN;
CREATE ROLE cms_role_seeder NOLOGIN;  -- specifically for database seeding.

-- Additional role for moderators, not part of the main hierarchy, but for database access control
CREATE ROLE cms_role_moderator NOLOGIN;



-- Granting roles to each other to establish a hierarchy
GRANT cms_role_unauthenticated TO cms_role_user;
GRANT cms_role_user TO cms_role_content_manager;
GRANT cms_role_content_manager TO cms_role_administrator;


-- Login roles
    -- Passwords are set in the .env file for development for convenience.
    -- In production, these should be strong and regularly rotated, and should not be stored in the .env file, but in a secure vault or similar.

CREATE ROLE cms_app WITH LOGIN NOINHERIT PASSWORD '${cms-app-password}';
CREATE ROLE cms_flyway WITH LOGIN PASSWORD '${cms-flyway-password}';
CREATE ROLE cms_backup WITH LOGIN PASSWORD '${cms-backup-password}';
CREATE ROLE cms_moderator WITH LOGIN PASSWORD '${cms-moderator-password}';
CREATE ROLE cms_superuser WITH SUPERUSER LOGIN PASSWORD '${cms-superuser-password}';

GRANT cms_role_administrator TO cms_app;
GRANT cms_role_moderator TO cms_moderator;
GRANT cms_role_seeder TO cms_app;

-- Set default role for cms_app to the lowest privilege role, so that it can only access what it needs by default, and will switch per request as needed.
ALTER ROLE cms_app SET ROLE = 'cms_role_unauthenticated';



-- Transfer ownership of existing tables, sequences, and other database objects to the appropriate roles

ALTER SCHEMA public OWNER TO cms_flyway;
ALTER SCHEMA pii OWNER TO cms_flyway;
ALTER SCHEMA pii_strict OWNER TO cms_flyway;

ALTER TABLE public.article_authors OWNER TO cms_flyway;
ALTER TABLE public.article_viewers OWNER TO cms_flyway;
ALTER TABLE public.articles OWNER TO cms_flyway;
ALTER TABLE public.integrity_logs OWNER TO cms_flyway;
ALTER TABLE public.media_items OWNER TO cms_flyway;
ALTER TABLE public.permissions OWNER TO cms_flyway;
ALTER TABLE public.role_permissions OWNER TO cms_flyway;
ALTER TABLE public.roles OWNER TO cms_flyway;
ALTER TABLE public.subjects OWNER TO cms_flyway;
ALTER TABLE public.users OWNER TO cms_flyway;
ALTER TABLE pii.users_pii OWNER TO cms_flyway;
ALTER TABLE pii_strict.users_pii_strict OWNER TO cms_flyway;


GRANT SELECT, INSERT, UPDATE, DELETE ON public.flyway_schema_history TO cms_flyway;

GRANT SELECT (id) ON public.users TO cms_role_unauthenticated;
GRANT SELECT (user_id, password_hash) ON pii_strict.users_pii_strict TO cms_role_unauthenticated;
GRANT SELECT (user_id, role_id, active) ON pii.users_pii TO cms_role_unauthenticated;
GRANT SELECT (id, internal_name) ON public.roles TO cms_role_unauthenticated;

GRANT USAGE ON SCHEMA public TO cms_role_unauthenticated;
GRANT USAGE ON SCHEMA pii TO cms_role_unauthenticated;
GRANT USAGE ON SCHEMA pii_strict TO cms_role_unauthenticated;
GRANT USAGE ON SCHEMA public TO cms_backup;
GRANT USAGE ON SCHEMA pii TO cms_backup;
GRANT USAGE ON SCHEMA pii_strict TO cms_backup;

-- PUBLIC SCHEMA

-- cms_role_unauthenticated
GRANT INSERT ON public.integrity_logs TO cms_role_unauthenticated;

-- cms_role_user
GRANT SELECT ON public.users TO cms_role_user;
GRANT SELECT ON public.roles TO cms_role_user;
GRANT SELECT ON public.permissions TO cms_role_user;
GRANT SELECT ON public.role_permissions TO cms_role_user;
GRANT SELECT ON public.articles TO cms_role_user;
GRANT SELECT ON public.article_authors TO cms_role_user;
GRANT SELECT, INSERT ON public.article_viewers TO cms_role_user;
GRANT SELECT, INSERT ON public.media_items TO cms_role_user;
GRANT SELECT ON public.subjects TO cms_role_user;

-- cms_role_content_manager
GRANT INSERT, UPDATE, DELETE ON public.articles TO cms_role_content_manager;
GRANT INSERT, UPDATE, DELETE ON public.article_authors TO cms_role_content_manager;
GRANT INSERT, UPDATE ON public.subjects TO cms_role_content_manager;

-- cms_role_administrator
GRANT INSERT, DELETE ON public.users TO cms_role_administrator;

-- cms_role_moderator
GRANT SELECT, DELETE ON public.integrity_logs TO cms_role_moderator;

-- cms_role_seeder
GRANT INSERT, DELETE ON public.users TO cms_role_seeder;
GRANT INSERT, DELETE ON public.articles TO cms_role_seeder;
GRANT INSERT, DELETE ON public.article_authors TO cms_role_seeder;
GRANT INSERT, DELETE ON public.article_viewers TO cms_role_seeder;

-- cms_backup
GRANT SELECT ON ALL TABLES IN SCHEMA public TO cms_backup;

-- PII SCHEMA

-- cms_role_user
GRANT SELECT ON pii.users_pii TO cms_role_user;

-- cms_role_content_manager
    -- No additional permissions needed compared to cms_role_user.

-- cms_role_administrator
GRANT INSERT, UPDATE ON pii.users_pii TO cms_role_administrator;

-- cms_role_seeder
GRANT INSERT ON pii.users_pii TO cms_role_seeder;

-- cms_backup
GRANT SELECT ON pii.users_pii TO cms_backup;

-- PII_STRICT SCHEMA

-- cms_role_user
GRANT UPDATE ON pii_strict.users_pii_strict TO cms_role_user;

-- cms_role_content_manager
    -- No additional permissions needed compared to cms_role_user.

-- cms_role_administrator
GRANT INSERT ON pii_strict.users_pii_strict TO cms_role_administrator;

-- cms_role_seeder
GRANT INSERT ON pii_strict.users_pii_strict TO cms_role_seeder;

-- cms_backup
GRANT SELECT ON pii_strict.users_pii_strict TO cms_backup;
