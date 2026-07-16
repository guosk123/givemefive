package com.givemefive.gmfcontroller.accountrecord;

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
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "user_account_record")
public class AccountRecordEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "user_openid", nullable = false, length = 128)
    private String userOpenid;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 32)
    private AccountRecordType recordType;

    @Column(name = "amount_encrypted", nullable = false)
    private String amountEncrypted;

    @Column(name = "category_encrypted", nullable = false)
    private String categoryEncrypted;

    @Column(name = "note_encrypted")
    private String noteEncrypted;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

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

    public AccountRecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(AccountRecordType recordType) {
        this.recordType = recordType;
    }

    public String getAmountEncrypted() {
        return amountEncrypted;
    }

    public void setAmountEncrypted(String amountEncrypted) {
        this.amountEncrypted = amountEncrypted;
    }

    public String getCategoryEncrypted() {
        return categoryEncrypted;
    }

    public void setCategoryEncrypted(String categoryEncrypted) {
        this.categoryEncrypted = categoryEncrypted;
    }

    public String getNoteEncrypted() {
        return noteEncrypted;
    }

    public void setNoteEncrypted(String noteEncrypted) {
        this.noteEncrypted = noteEncrypted;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
