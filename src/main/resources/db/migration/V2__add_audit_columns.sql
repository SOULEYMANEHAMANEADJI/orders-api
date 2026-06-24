-- Add audit trail columns to main tables
ALTER TABLE products_tbl
  ADD COLUMN created_at DATETIME(6),
  ADD COLUMN updated_at DATETIME(6);

ALTER TABLE customers_tbl
  ADD COLUMN created_at DATETIME(6),
  ADD COLUMN updated_at DATETIME(6);

ALTER TABLE orders_tbl
  ADD COLUMN created_at DATETIME(6),
  ADD COLUMN updated_at DATETIME(6);
