DROP INDEX IF EXISTS idx_users_fullname_trgm;

CREATE INDEX idx_users_firstname_trgm ON pii.users_pii USING gin (first_name gin_trgm_ops);
CREATE INDEX idx_users_lastname_trgm ON pii.users_pii USING gin (last_name gin_trgm_ops);
CREATE INDEX idx_users_fullname_trgm ON pii.users_pii USING gin ((first_name || ' ' || last_name) gin_trgm_ops);