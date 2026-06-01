CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_users_fullname_trgm
    ON users USING gin ((first_name || ' ' || last_name) gin_trgm_ops);