CREATE TABLE match_history (
    id BIGSERIAL PRIMARY KEY,
    offer_id VARCHAR(64) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
