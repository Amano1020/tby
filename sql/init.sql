-- Use the database specified in docker-compose.yml
USE `order_db`;

-- Create User Table
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` BIGINT NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `created_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create Product Category Table
CREATE TABLE IF NOT EXISTS `product_category` (
  `category_id` BIGINT NOT NULL,
  `category_name` VARCHAR(255) NOT NULL,
  `tax_rate` DECIMAL(10,4) NOT NULL,
  `created_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create Product Table (No physical FK)
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` BIGINT NOT NULL,
  `category_id` BIGINT NOT NULL,
  `product_name` VARCHAR(255) NOT NULL,
  `unit_price` DECIMAL(19,4) NOT NULL,
  `created_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `idx_product_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create Order Table (No physical FKs)
CREATE TABLE IF NOT EXISTS `orders` (
  `order_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `order_amount` INT NOT NULL,
  `created_at` DATETIME(6) DEFAULT NULL,
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `idx_order_user` (`user_id`),
  KEY `idx_order_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- INJECT MOCK DATA

-- Insert mock users
INSERT IGNORE INTO `user` (`user_id`, `username`, `email`, `created_at`, `updated_at`) VALUES 
(1, 'alice', 'alice@test.com', NOW(), NOW()),
(2, 'bob', 'bob@test.com', NOW(), NOW()),
(3, 'charlie', 'charlie@test.com', NOW(), NOW());

-- Insert mock product categories (tax rates: Electronics=5%, Books=0%, Food=2%)
INSERT IGNORE INTO `product_category` (`category_id`, `category_name`, `tax_rate`, `created_at`, `updated_at`) VALUES 
(100, 'Electronics', 0.0500, NOW(), NOW()),
(101, 'Books', 0.0000, NOW(), NOW()),
(102, 'Food', 0.0200, NOW(), NOW());

-- Insert mock products
INSERT IGNORE INTO `product` (`product_id`, `category_id`, `product_name`, `unit_price`, `created_at`, `updated_at`) VALUES 
(200, 100, 'Laptop M3', 1500.0000, NOW(), NOW()),
(201, 100, 'Wireless Mouse', 45.0000, NOW(), NOW()),
(202, 101, 'Spring Boot in Action', 35.5000, NOW(), NOW()),
(203, 102, 'Organic Apple', 1.5000, NOW(), NOW());

-- Note: We do not prepopulate orders so that the user can test the create endpoint cleanly.
