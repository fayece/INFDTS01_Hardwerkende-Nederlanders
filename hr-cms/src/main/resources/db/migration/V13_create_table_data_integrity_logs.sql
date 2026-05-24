CREATE TABLE integrity_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message VARCHAR(255) NOT NULL,
    severity VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    user_id UUID,
    profile_id VARCHAR(255)
);
