CREATE TABLE slots (
    id VARCHAR(64) PRIMARY KEY,
    total_capacity INT NOT NULL,
    reserved INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
