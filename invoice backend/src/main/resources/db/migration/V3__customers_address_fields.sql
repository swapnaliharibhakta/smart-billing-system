-- V3: Add structured billing and shipping address to customers
ALTER TABLE customers
    ADD COLUMN IF NOT EXISTS billing_street     TEXT,
    ADD COLUMN IF NOT EXISTS billing_city       VARCHAR(100),
    ADD COLUMN IF NOT EXISTS billing_state      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS billing_state_code CHAR(2),
    ADD COLUMN IF NOT EXISTS billing_pincode    VARCHAR(6),
    ADD COLUMN IF NOT EXISTS shipping_name      VARCHAR(200),
    ADD COLUMN IF NOT EXISTS shipping_street    TEXT,
    ADD COLUMN IF NOT EXISTS shipping_city      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS shipping_state     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS shipping_state_code CHAR(2),
    ADD COLUMN IF NOT EXISTS shipping_pincode   VARCHAR(6);

-- Migrate existing flat address text into billing_street
UPDATE customers SET billing_street = address WHERE address IS NOT NULL AND billing_street IS NULL;
