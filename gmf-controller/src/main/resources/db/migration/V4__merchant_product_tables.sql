CREATE TABLE merchant_product (
    id UUID PRIMARY KEY,
    user_openid VARCHAR(128) NOT NULL,
    product_name_encrypted TEXT NOT NULL,
    purchase_price_encrypted TEXT NOT NULL,
    sale_price_encrypted TEXT NOT NULL,
    stock_quantity_encrypted TEXT NOT NULL,
    unit_encrypted TEXT,
    remark_encrypted TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_merchant_product_openid_updated_at
    ON merchant_product (user_openid, updated_at);
