-- PostgreSQL Schema for Divaksha E-commerce System

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    referral_code VARCHAR(50) UNIQUE NOT NULL,
    affiliate_code VARCHAR(50) UNIQUE NOT NULL,
    parent_id BIGINT,
    effective_parent_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    last_sale_at TIMESTAMPTZ,
    inactive_since TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    sku VARCHAR(100),
    name VARCHAR(255),
    description TEXT,
    price NUMERIC(18,2),
    image_url VARCHAR(500),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    buyer_user_id BIGINT,
    seller_user_id BIGINT,
    transaction_id VARCHAR(255),
    amount NUMERIC(18,2),
    status VARCHAR(50),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Sales table
CREATE TABLE IF NOT EXISTS sales (
    id SERIAL PRIMARY KEY,
    seller_user_id BIGINT NOT NULL,
    buyer_id BIGINT,
    affiliate_user_id BIGINT,
    total_amount NUMERIC(18,2),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_sales_affiliate_user FOREIGN KEY (affiliate_user_id) REFERENCES users(id)
);

-- Commission ledger table
CREATE TABLE IF NOT EXISTS commission_ledger (
    id SERIAL PRIMARY KEY,
    sale_id BIGINT,
    beneficiary_user_id BIGINT,
    level INT,
    percentage NUMERIC(5,2),
    amount NUMERIC(18,2),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Referral shift history table
CREATE TABLE IF NOT EXISTS referral_shift_history (
    id SERIAL PRIMARY KEY,
    affected_child_id BIGINT,
    inactive_user_id BIGINT,
    previous_effective_parent_id BIGINT,
    new_effective_parent_id BIGINT,
    changed_at TIMESTAMPTZ DEFAULT NOW(),
    reverted BOOLEAN DEFAULT FALSE,
    revert_at TIMESTAMPTZ
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_parent_id ON users(parent_id);
CREATE INDEX IF NOT EXISTS idx_users_effective_parent_id ON users(effective_parent_id);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_referral_code ON users(referral_code);
CREATE INDEX IF NOT EXISTS idx_users_affiliate_code ON users(affiliate_code);
CREATE INDEX IF NOT EXISTS idx_sales_affiliate_user_id ON sales(affiliate_user_id);
CREATE INDEX IF NOT EXISTS idx_users_last_sale_at ON users(last_sale_at);
CREATE INDEX IF NOT EXISTS idx_orders_buyer_user_id ON orders(buyer_user_id);
CREATE INDEX IF NOT EXISTS idx_orders_seller_user_id ON orders(seller_user_id);
CREATE INDEX IF NOT EXISTS idx_sales_seller_user_id ON sales(seller_user_id);
CREATE INDEX IF NOT EXISTS idx_commission_ledger_sale_id ON commission_ledger(sale_id);
CREATE INDEX IF NOT EXISTS idx_commission_ledger_beneficiary_user_id ON commission_ledger(beneficiary_user_id);
CREATE INDEX IF NOT EXISTS idx_referral_shift_history_inactive_user_id ON referral_shift_history(inactive_user_id);
CREATE INDEX IF NOT EXISTS idx_referral_shift_history_affected_child_id ON referral_shift_history(affected_child_id);

