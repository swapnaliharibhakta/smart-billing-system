-- V7: Convert CHAR(2) columns to VARCHAR(2) to match Hibernate entity mappings.
-- PostgreSQL stores CHAR(n) as bpchar which Hibernate validates as a type mismatch
-- against Java String fields mapped to varchar.

ALTER TABLE company_settings
    ALTER COLUMN state_code TYPE VARCHAR(2);

ALTER TABLE customers
    ALTER COLUMN billing_state_code  TYPE VARCHAR(2),
    ALTER COLUMN shipping_state_code TYPE VARCHAR(2);

ALTER TABLE invoices
    ALTER COLUMN bill_to_state_code TYPE VARCHAR(2),
    ALTER COLUMN ship_to_state_code TYPE VARCHAR(2);
