package com.givemefive.gmfcontroller.wxappuser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "wx_app_user")
public class WxAppUserEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 128)
    private String openid;

    @Column(length = 128)
    private String unionid;

    @Column(name = "nickname_encrypted")
    private String nicknameEncrypted;

    @Column(name = "avatar_url_encrypted")
    private String avatarUrlEncrypted;

    @Column(name = "phone_encrypted")
    private String phoneEncrypted;

    @Column(name = "profile_encrypted")
    private String profileEncrypted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WxAppUserStatus status;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = WxAppUserStatus.ACTIVE;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNicknameEncrypted() {
        return nicknameEncrypted;
    }

    public void setNicknameEncrypted(String nicknameEncrypted) {
        this.nicknameEncrypted = nicknameEncrypted;
    }

    public String getAvatarUrlEncrypted() {
        return avatarUrlEncrypted;
    }

    public void setAvatarUrlEncrypted(String avatarUrlEncrypted) {
        this.avatarUrlEncrypted = avatarUrlEncrypted;
    }

    public String getPhoneEncrypted() {
        return phoneEncrypted;
    }

    public void setPhoneEncrypted(String phoneEncrypted) {
        this.phoneEncrypted = phoneEncrypted;
    }

    public String getProfileEncrypted() {
        return profileEncrypted;
    }

    public void setProfileEncrypted(String profileEncrypted) {
        this.profileEncrypted = profileEncrypted;
    }

    public WxAppUserStatus getStatus() {
        return status;
    }

    public void setStatus(WxAppUserStatus status) {
        this.status = status;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
