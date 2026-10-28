-- V2: Add GST-required fields to company_settings
ALTER TABLE company_settings
    ADD COLUMN IF NOT EXISTS pan              VARCHAR(10),
    ADD COLUMN IF NOT EXISTS state_code       CHAR(2),
    ADD COLUMN IF NOT EXISTS bank_name        VARCHAR(100),
    ADD COLUMN IF NOT EXISTS bank_account     VARCHAR(30),
    ADD COLUMN IF NOT EXISTS bank_ifsc        VARCHAR(11),
    ADD COLUMN IF NOT EXISTS bank_branch      VARCHAR(100),
    ADD COLUMN IF NOT EXISTS upi_id           VARCHAR(100),
    ADD COLUMN IF NOT EXISTS logo_path        TEXT,
    ADD COLUMN IF NOT EXISTS signature_path   TEXT,
    ADD COLUMN IF NOT EXISTS default_terms    TEXT,
    ADD COLUMN IF NOT EXISTS default_notes    TEXT;
