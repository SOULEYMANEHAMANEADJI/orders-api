-- Flyway baseline schema for rest-api-orders
-- Products
CREATE TABLE products_tbl (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(500),
  weight DECIMAL(19,3) NOT NULL,
  price DECIMAL(19,2) NOT NULL,
  stock INT NOT NULL,
  version BIGINT,
  CONSTRAINT chk_products_stock CHECK (stock >= 0)
) ENGINE=InnoDB;

-- Customers
CREATE TABLE customers_tbl (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  address VARCHAR(100) NOT NULL,
  vip BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

-- Orders
CREATE TABLE orders_tbl (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  status ENUM('DRAFT','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'DRAFT',
  total_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
  version BIGINT,
  CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers_tbl(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Order lines
CREATE TABLE order_lines_tbl (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  order_id BIGINT NOT NULL,
  unit_price DECIMAL(19,2) NOT NULL,
  CONSTRAINT fk_orderline_product FOREIGN KEY (product_id) REFERENCES products_tbl(id) ON DELETE RESTRICT,
  CONSTRAINT fk_orderline_order FOREIGN KEY (order_id) REFERENCES orders_tbl(id) ON DELETE CASCADE,
  CONSTRAINT chk_orderline_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;

-- Users
CREATE TABLE users_tbl (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  CONSTRAINT chk_users_role CHECK (role IN ('USER','ADMIN'))
) ENGINE=InnoDB;

