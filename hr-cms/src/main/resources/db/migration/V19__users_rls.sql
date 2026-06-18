-- gets current user id from session variable (or empty string)
CREATE FUNCTION pii.current_user_id() RETURNS uuid
    LANGUAGE sql STABLE AS
$$
    SELECT NULLIF(current_setting('app.current_user_id', true), '')::uuid
$$;

-- pii.users_pii
ALTER TABLE pii.users_pii ENABLE ROW LEVEL SECURITY;

CREATE POLICY users_pii_select ON pii.users_pii
    FOR SELECT
    USING (
        current_user IN ('cms_role_administrator', 'cms_role_unauthenticated', 'cms_backup')
        OR user_id = pii.current_user_id()
    );

CREATE POLICY users_pii_insert ON pii.users_pii
    FOR INSERT
    WITH CHECK (current_user IN ('cms_role_administrator', 'cms_role_seeder'));

CREATE POLICY users_pii_update ON pii.users_pii
    FOR UPDATE
    USING (current_user = 'cms_role_administrator')
    WITH CHECK (current_user = 'cms_role_administrator');


-- pii_strict.users_pii_strict
ALTER TABLE pii_strict.users_pii_strict ENABLE ROW LEVEL SECURITY;

CREATE POLICY users_pii_strict_select ON pii_strict.users_pii_strict
    FOR SELECT
    USING (current_user IN ('cms_role_unauthenticated', 'cms_backup'));

CREATE POLICY users_pii_strict_insert ON pii_strict.users_pii_strict
    FOR INSERT
    WITH CHECK (current_user IN ('cms_role_administrator', 'cms_role_seeder'));

CREATE POLICY users_pii_strict_update ON pii_strict.users_pii_strict
    FOR UPDATE
    USING (
        current_user = 'cms_role_administrator'
        OR user_id = pii.current_user_id()
    )
    WITH CHECK (
        current_user = 'cms_role_administrator'
        OR user_id = pii.current_user_id()
    );
