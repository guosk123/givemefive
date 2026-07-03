CREATE TABLE user_platform_secret (
    id UUID PRIMARY KEY,
    user_openid VARCHAR(128) NOT NULL,
    platform_name VARCHAR(128) NOT NULL,
    label VARCHAR(128) NOT NULL,
    account_encrypted TEXT,
    password_encrypted TEXT,
    secret_key_encrypted TEXT,
    extra_secrets_encrypted TEXT,
    remark_encrypted TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_user_platform_secret_openid_platform
    ON user_platform_secret (user_openid, platform_name);
