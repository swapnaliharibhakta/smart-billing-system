-- V4: Add GST-required fields to products
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS hsn_sac     VARCHAR(8),
    ADD COLUMN IF NOT EXISTS unit        VARCHAR(20);
