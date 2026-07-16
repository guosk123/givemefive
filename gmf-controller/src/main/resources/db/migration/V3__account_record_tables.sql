CREATE TABLE user_account_record (
    id UUID PRIMARY KEY,
    user_openid VARCHAR(128) NOT NULL,
    record_type VARCHAR(32) NOT NULL,
    amount_encrypted TEXT NOT NULL,
    category_encrypted TEXT NOT NULL,
    note_encrypted TEXT,
    record_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_user_account_record_type CHECK (record_type IN ('INCOME', 'EXPENSE'))
);

CREATE INDEX idx_user_account_record_openid_date
    ON user_account_record (user_openid, record_date, created_at);
