-- +goose Up
CREATE SCHEMA IF NOT EXISTS inventory;

CREATE TABLE inventory.stock (
    sku TEXT PRIMARY KEY,
    available INT NOT NULL CHECK (available >= 0)
);

INSERT INTO inventory.stock (sku, available) VALUES
    ('SKU-1', 100),
    ('SKU-2', 50)
ON CONFLICT (sku) DO NOTHING;

-- +goose Down
DROP TABLE IF EXISTS inventory.stock;
DROP SCHEMA IF EXISTS inventory;
