-- Drop table if exists and recreate fresh
DROP TABLE IF EXISTS feedback CASCADE;

CREATE TABLE feedback (
    id UUID PRIMARY KEY,
    member_id VARCHAR(36) NOT NULL,
    provider_name VARCHAR(80) NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(200),
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);