package com.givemefive.gmfcontroller.platformsecret;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "user_platform_secret")
public class PlatformSecretEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "user_openid", nullable = false, length = 128)
    private String userOpenid;

    @Column(name = "platform_name", nullable = false, length = 128)
    private String platformName;

    @Column(nullable = false, length = 128)
    private String label;

    @Column(name = "account_encrypted")
    private String accountEncrypted;

    @Column(name = "password_encrypted")
    private String passwordEncrypted;

    @Column(name = "secret_key_encrypted")
    private String secretKeyEncrypted;

    @Column(name = "extra_secrets_encrypted")
    private String extraSecretsEncrypted;

    @Column(name = "remark_encrypted")
    private String remarkEncrypted;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUserOpenid() {
        return userOpenid;
    }

    public void setUserOpenid(String userOpenid) {
        this.userOpenid = userOpenid;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAccountEncrypted() {
        return accountEncrypted;
    }

    public void setAccountEncrypted(String accountEncrypted) {
        this.accountEncrypted = accountEncrypted;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getSecretKeyEncrypted() {
        return secretKeyEncrypted;
    }

    public void setSecretKeyEncrypted(String secretKeyEncrypted) {
        this.secretKeyEncrypted = secretKeyEncrypted;
    }

    public String getExtraSecretsEncrypted() {
        return extraSecretsEncrypted;
    }

    public void setExtraSecretsEncrypted(String extraSecretsEncrypted) {
        this.extraSecretsEncrypted = extraSecretsEncrypted;
    }

    public String getRemarkEncrypted() {
        return remarkEncrypted;
    }

    public void setRemarkEncrypted(String remarkEncrypted) {
        this.remarkEncrypted = remarkEncrypted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
