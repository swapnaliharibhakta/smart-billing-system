CREATE TABLE users (
                       id            BIGSERIAL PRIMARY KEY,
                       name          VARCHAR(255) NOT NULL,
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       phone         VARCHAR(20),
                       role          VARCHAR(20)  NOT NULL DEFAULT 'STAFF'
                           CHECK (role IN ('ADMIN', 'STAFF')),
                       enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
                       updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE customers (
                           id         BIGSERIAL PRIMARY KEY,
                           name       VARCHAR(200) NOT NULL,
                           email      VARCHAR(255),
                           phone      VARCHAR(30),
                           address    TEXT,
                           gst_number VARCHAR(20),
                           active     BOOLEAN   NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE products (
                          id         BIGSERIAL PRIMARY KEY,
                          name       VARCHAR(200) NOT NULL,
                          unit_price NUMERIC(15,2) NOT NULL CHECK (unit_price >= 0),
                          tax_rate   NUMERIC(5,2)  NOT NULL DEFAULT 0,
                          active     BOOLEAN   NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE company_settings (
                                  id               BIGSERIAL PRIMARY KEY,
                                  company_name     VARCHAR(255) NOT NULL,
                                  address          TEXT,
                                  gst_number       VARCHAR(20),
                                  email            VARCHAR(255),
                                  phone            VARCHAR(20),
                                  invoice_prefix   VARCHAR(10) NOT NULL DEFAULT 'INV',
                                  next_invoice_no  BIGINT      NOT NULL DEFAULT 1,
                                  default_due_days INT         NOT NULL DEFAULT 30,
                                  created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
                                  updated_at       TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE invoices (
                          id             BIGSERIAL PRIMARY KEY,
                          invoice_number VARCHAR(30) NOT NULL UNIQUE,
                          customer_id    BIGINT NOT NULL REFERENCES customers(id),
                          created_by     BIGINT NOT NULL REFERENCES users(id),
                          issue_date     DATE NOT NULL DEFAULT CURRENT_DATE,
                          due_date       DATE NOT NULL,
                          status         VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                              CHECK (status IN ('DRAFT', 'SENT', 'PARTIALLY_PAID',
                                                'PAID', 'OVERDUE', 'CANCELLED')),
                          subtotal       NUMERIC(15,2) NOT NULL DEFAULT 0,
                          tax_total      NUMERIC(15,2) NOT NULL DEFAULT 0,
                          total          NUMERIC(15,2) NOT NULL DEFAULT 0,
                          amount_paid    NUMERIC(15,2) NOT NULL DEFAULT 0,
                          notes          TEXT,
                          pdf_path       TEXT,
                          sent_at        TIMESTAMP,
                          created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoices_customer ON invoices(customer_id);
CREATE INDEX idx_invoices_status   ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);

CREATE TABLE invoice_items (
                               id          BIGSERIAL PRIMARY KEY,
                               invoice_id  BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
                               product_id  BIGINT REFERENCES products(id),
                               description VARCHAR(255) NOT NULL,
                               quantity    NUMERIC(10,2) NOT NULL CHECK (quantity > 0),
                               unit_price  NUMERIC(15,2) NOT NULL,
                               tax_rate    NUMERIC(5,2)  NOT NULL DEFAULT 0,
                               line_total  NUMERIC(15,2) NOT NULL,
                               created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                               updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_items_invoice ON invoice_items(invoice_id);

CREATE TABLE payments (
                          id           BIGSERIAL PRIMARY KEY,
                          invoice_id   BIGINT NOT NULL REFERENCES invoices(id),
                          amount       NUMERIC(15,2) NOT NULL CHECK (amount > 0),
                          method       VARCHAR(30) NOT NULL
                              CHECK (method IN ('CASH', 'UPI', 'CARD', 'BANK_TRANSFER')),
                          paid_on      DATE NOT NULL DEFAULT CURRENT_DATE,
                          reference_no VARCHAR(100),
                          notes        TEXT,
                          created_by   BIGINT REFERENCES users(id),
                          created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_invoice ON payments(invoice_id);

CREATE TABLE invoice_reminders (
                                   id            BIGSERIAL PRIMARY KEY,
                                   invoice_id    BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
                                   sent_to       VARCHAR(255) NOT NULL,
                                   reminder_type VARCHAR(20) NOT NULL
                                       CHECK (reminder_type IN ('BEFORE_DUE', 'OVERDUE')),
                                   sent_at       TIMESTAMP NOT NULL DEFAULT NOW(),
                                   status        VARCHAR(20) NOT NULL
                                       CHECK (status IN ('SENT', 'FAILED')),
                                   created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
                                   updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reminders_invoice ON invoice_reminders(invoice_id);