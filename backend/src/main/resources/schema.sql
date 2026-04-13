CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    avatar VARCHAR(255),
    balance DECIMAL(10, 2) DEFAULT 0.00,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort INT DEFAULT 0,
    icon VARCHAR(255),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    main_image VARCHAR(255),
    sub_images TEXT,
    description TEXT,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    selected TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    zip_code VARCHAR(10),
    is_default TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    pay_amount DECIMAL(10, 2) NOT NULL,
    freight_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    status TINYINT DEFAULT 0,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    payment_time TIMESTAMP NULL,
    delivery_time TIMESTAMP NULL,
    receive_time TIMESTAMP NULL,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(255),
    product_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO category (name, parent_id, sort, status) VALUES 
('电子产品', 0, 1, 1),
('服装', 0, 2, 1),
('食品', 0, 3, 1),
('手机', 1, 1, 1),
('电脑', 1, 2, 1),
('男装', 2, 1, 1),
('女装', 2, 2, 1);

INSERT INTO product (name, category_id, price, original_price, stock, sales, description, status) VALUES 
('iPhone 15 Pro', 4, 7999.00, 8999.00, 100, 50, '最新款iPhone，A17芯片，钛金属设计', 1),
('MacBook Pro 14', 5, 14999.00, 16999.00, 50, 30, 'M3芯片，14英寸视网膜显示屏', 1),
('小米14 Ultra', 4, 5999.00, 6499.00, 200, 100, '徕卡影像，骁龙8 Gen3', 1),
('男士休闲外套', 6, 299.00, 399.00, 500, 200, '舒适面料，时尚设计', 1),
('女士连衣裙', 7, 199.00, 299.00, 300, 150, '优雅气质，多色可选', 1);
