CREATE TABLE wx_app_user (
    id UUID PRIMARY KEY,
    openid VARCHAR(128) NOT NULL,
    unionid VARCHAR(128),
    nickname_encrypted TEXT,
    avatar_url_encrypted TEXT,
    phone_encrypted TEXT,
    profile_encrypted TEXT,
    status VARCHAR(32) NOT NULL,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_wx_app_user_openid UNIQUE (openid),
    CONSTRAINT uk_wx_app_user_unionid UNIQUE (unionid),
    CONSTRAINT ck_wx_app_user_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE INDEX idx_wx_app_user_status
    ON wx_app_user (status);

CREATE INDEX idx_wx_app_user_last_login_at
    ON wx_app_user (last_login_at);

CREATE TABLE sys_admin_account (
    id UUID PRIMARY KEY,
    wechat_openid VARCHAR(128) NOT NULL,
    username VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_sys_admin_account_openid UNIQUE (wechat_openid),
    CONSTRAINT uk_sys_admin_account_username UNIQUE (username),
    CONSTRAINT ck_sys_admin_account_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE INDEX idx_sys_admin_account_status
    ON sys_admin_account (status);

CREATE TABLE sys_role (
    id UUID PRIMARY KEY,
    role_code VARCHAR(64) NOT NULL,
    role_name VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    built_in BOOLEAN NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_sys_role_code UNIQUE (role_code)
);

CREATE TABLE sys_permission (
    id UUID PRIMARY KEY,
    permission_code VARCHAR(128) NOT NULL,
    permission_name VARCHAR(128) NOT NULL,
    module VARCHAR(64) NOT NULL,
    resource VARCHAR(128) NOT NULL,
    action VARCHAR(64) NOT NULL,
    description VARCHAR(512),
    built_in BOOLEAN NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_sys_permission_code UNIQUE (permission_code)
);

CREATE INDEX idx_sys_permission_module
    ON sys_permission (module);

CREATE TABLE sys_admin_role (
    admin_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (admin_id, role_id),
    CONSTRAINT fk_sys_admin_role_admin
        FOREIGN KEY (admin_id) REFERENCES sys_admin_account (id),
    CONSTRAINT fk_sys_admin_role_role
        FOREIGN KEY (role_id) REFERENCES sys_role (id)
);

CREATE INDEX idx_sys_admin_role_role_id
    ON sys_admin_role (role_id);

CREATE TABLE sys_role_permission (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_sys_role_permission_role
        FOREIGN KEY (role_id) REFERENCES sys_role (id),
    CONSTRAINT fk_sys_role_permission_permission
        FOREIGN KEY (permission_id) REFERENCES sys_permission (id)
);

CREATE INDEX idx_sys_role_permission_permission_id
    ON sys_role_permission (permission_id);

CREATE TABLE sys_audit_log (
    id UUID PRIMARY KEY,
    actor_type VARCHAR(32) NOT NULL,
    actor_admin_id UUID,
    actor_openid VARCHAR(128),
    action VARCHAR(128) NOT NULL,
    resource_type VARCHAR(128) NOT NULL,
    resource_id VARCHAR(128),
    request_id VARCHAR(128),
    client_ip VARCHAR(64),
    user_agent VARCHAR(512),
    success BOOLEAN NOT NULL,
    detail_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_sys_audit_log_actor_type CHECK (actor_type IN ('ADMIN', 'WX_USER', 'SYSTEM')),
    CONSTRAINT fk_sys_audit_log_admin
        FOREIGN KEY (actor_admin_id) REFERENCES sys_admin_account (id)
);

CREATE INDEX idx_sys_audit_log_actor
    ON sys_audit_log (actor_type, actor_admin_id, actor_openid);

CREATE INDEX idx_sys_audit_log_resource
    ON sys_audit_log (resource_type, resource_id);

CREATE INDEX idx_sys_audit_log_created_at
    ON sys_audit_log (created_at);
