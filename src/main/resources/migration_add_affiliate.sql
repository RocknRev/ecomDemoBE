CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE t_roles
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE t_users
(
    id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),

    email VARCHAR(255) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    phone VARCHAR(20),

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE t_user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY(user_id, role_id),

    CONSTRAINT fk_user_role_user
        FOREIGN KEY(user_id)
            REFERENCES users(id),

    CONSTRAINT fk_user_role_role
        FOREIGN KEY(role_id)
            REFERENCES roles(id)
);

INSERT INTO roles(name, created_at)
VALUES
    ('ROLE_ADMIN', NOW()),
    ('ROLE_CUSTOMER', NOW());

CREATE TABLE t_categories
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    description TEXT,

    image_url TEXT,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    parent_id BIGINT,

    sort_order INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_category_parent
        FOREIGN KEY(parent_id)
            REFERENCES categories(id)
);

CREATE TABLE t_products
(
    id BIGSERIAL PRIMARY KEY,

    category_id BIGINT NOT NULL,

    name VARCHAR(255) NOT NULL,

    slug VARCHAR(255) NOT NULL UNIQUE,

    short_description TEXT,

    description TEXT,

    sku VARCHAR(100) NOT NULL UNIQUE,

    brand VARCHAR(150),

    price NUMERIC(12,2) NOT NULL,

    discount_price NUMERIC(12,2),

    stock INTEGER NOT NULL DEFAULT 0,

    thumbnail_url TEXT,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    featured BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_product_category
        FOREIGN KEY(category_id)
            REFERENCES categories(id)
);

CREATE TABLE t_product_images
(
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL,

    image_url TEXT NOT NULL,

    display_order INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_product_image
        FOREIGN KEY(product_id)
            REFERENCES products(id)
            ON DELETE CASCADE
);

CREATE TABLE t_addresses
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    type VARCHAR(20) NOT NULL,

    full_name VARCHAR(150) NOT NULL,

    phone VARCHAR(20) NOT NULL,

    email VARCHAR(255),

    address_line1 VARCHAR(255) NOT NULL,

    address_line2 VARCHAR(255),

    city VARCHAR(100) NOT NULL,

    state VARCHAR(100) NOT NULL,

    postal_code VARCHAR(20) NOT NULL,

    country VARCHAR(100) NOT NULL,

    label VARCHAR(50),

    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_address_user
        FOREIGN KEY(user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_address_user
    ON addresses(user_id);

CREATE TABLE t_carts
(
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL UNIQUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_cart_user
        FOREIGN KEY(user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE TABLE t_cart_items
(
    id BIGSERIAL PRIMARY KEY,

    cart_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    quantity INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY(cart_id)
            REFERENCES carts(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_cart_item_product
        FOREIGN KEY(product_id)
            REFERENCES products(id),

    CONSTRAINT uk_cart_product
        UNIQUE(cart_id, product_id)
);

CREATE TABLE t_orders
(
    id BIGSERIAL PRIMARY KEY,

    order_number VARCHAR(50) NOT NULL UNIQUE,

    user_id BIGINT NOT NULL,

    address_id BIGINT,

    full_name VARCHAR(150) NOT NULL,

    phone VARCHAR(20) NOT NULL,

    email VARCHAR(255),

    address_line1 VARCHAR(255) NOT NULL,

    address_line2 VARCHAR(255),

    city VARCHAR(100) NOT NULL,

    state VARCHAR(100) NOT NULL,

    postal_code VARCHAR(20) NOT NULL,

    country VARCHAR(100) NOT NULL,

    subtotal NUMERIC(12,2) NOT NULL,

    shipping_charge NUMERIC(12,2) NOT NULL DEFAULT 0,

    tax_amount NUMERIC(12,2) NOT NULL DEFAULT 0,

    discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,

    total_amount NUMERIC(12,2) NOT NULL,

    order_status VARCHAR(30) NOT NULL,

    payment_status VARCHAR(30) NOT NULL,

    payment_method VARCHAR(30),

    notes TEXT,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_order_user
        FOREIGN KEY(user_id)
            REFERENCES users(id),

    CONSTRAINT fk_order_address
        FOREIGN KEY(address_id)
            REFERENCES addresses(id)
);

CREATE INDEX idx_orders_user
    ON orders(user_id);

CREATE INDEX idx_orders_number
    ON orders(order_number);

CREATE TABLE t_order_items
(
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    product_name VARCHAR(255) NOT NULL,

    sku VARCHAR(100),

    price NUMERIC(12,2) NOT NULL,

    quantity INTEGER NOT NULL,

    subtotal NUMERIC(12,2) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_order_item_order
        FOREIGN KEY(order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_order_item_product
        FOREIGN KEY(product_id)
            REFERENCES products(id)
);

CREATE TABLE t_payments
(
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL UNIQUE,

    payment_id VARCHAR(255),

    gateway_order_id VARCHAR(255),

    gateway_payment_id VARCHAR(255),

    gateway_signature VARCHAR(500),

    gateway VARCHAR(50) NOT NULL,

    amount NUMERIC(12,2) NOT NULL,

    currency VARCHAR(10) NOT NULL DEFAULT 'INR',

    payment_status VARCHAR(30) NOT NULL,

    transaction_time TIMESTAMP,

    failure_reason TEXT,

    raw_response TEXT,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    CONSTRAINT fk_payment_order
        FOREIGN KEY(order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_payment_gateway_order
    ON payments(gateway_order_id);

CREATE INDEX idx_payment_gateway_payment
    ON payments(gateway_payment_id);
