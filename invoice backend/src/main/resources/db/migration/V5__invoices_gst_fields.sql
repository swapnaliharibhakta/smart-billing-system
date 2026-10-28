-- V5: Add GST header fields and customer snapshot to invoices
ALTER TABLE invoices
    ADD COLUMN IF NOT EXISTS place_of_supply   VARCHAR(100),
    ADD COLUMN IF NOT EXISTS terms             TEXT,
    ADD COLUMN IF NOT EXISTS round_off         NUMERIC(10,2) NOT NULL DEFAULT 0,
    -- bill-to snapshot (copied from customer at invoice creation time)
    ADD COLUMN IF NOT EXISTS bill_to_name      VARCHAR(200),
    ADD COLUMN IF NOT EXISTS bill_to_gstin     VARCHAR(20),
    ADD COLUMN IF NOT EXISTS bill_to_street    TEXT,
    ADD COLUMN IF NOT EXISTS bill_to_city      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS bill_to_state     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS bill_to_state_code CHAR(2),
    -- ship-to snapshot
    ADD COLUMN IF NOT EXISTS ship_to_name      VARCHAR(200),
    ADD COLUMN IF NOT EXISTS ship_to_street    TEXT,
    ADD COLUMN IF NOT EXISTS ship_to_city      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS ship_to_state     VARCHAR(100),
    ADD COLUMN IF NOT EXISTS ship_to_state_code CHAR(2);
