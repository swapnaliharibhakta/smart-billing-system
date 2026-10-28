-- V6: Add GST line-item breakdown fields to invoice_items
ALTER TABLE invoice_items
    ADD COLUMN IF NOT EXISTS hsn_sac        VARCHAR(8),
    ADD COLUMN IF NOT EXISTS unit           VARCHAR(20),
    ADD COLUMN IF NOT EXISTS discount_pct   NUMERIC(5,2)  NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS taxable_value  NUMERIC(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cgst_rate      NUMERIC(5,2)  NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cgst_amount    NUMERIC(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS sgst_rate      NUMERIC(5,2)  NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS sgst_amount    NUMERIC(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS igst_rate      NUMERIC(5,2)  NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS igst_amount    NUMERIC(15,2) NOT NULL DEFAULT 0;

-- Back-fill taxable_value for existing rows (no discount existed, so taxable = qty * unit_price)
UPDATE invoice_items
SET taxable_value = quantity * unit_price
WHERE taxable_value = 0;
