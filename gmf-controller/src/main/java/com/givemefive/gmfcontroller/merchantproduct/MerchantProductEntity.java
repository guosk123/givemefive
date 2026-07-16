package com.givemefive.gmfcontroller.merchantproduct;

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
@Table(name = "merchant_product")
public class MerchantProductEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "user_openid", nullable = false, length = 128)
    private String userOpenid;

    @Column(name = "product_name_encrypted", nullable = false)
    private String productNameEncrypted;

    @Column(name = "purchase_price_encrypted", nullable = false)
    private String purchasePriceEncrypted;

    @Column(name = "sale_price_encrypted", nullable = false)
    private String salePriceEncrypted;

    @Column(name = "stock_quantity_encrypted", nullable = false)
    private String stockQuantityEncrypted;

    @Column(name = "unit_encrypted")
    private String unitEncrypted;

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

    public String getProductNameEncrypted() {
        return productNameEncrypted;
    }

    public void setProductNameEncrypted(String productNameEncrypted) {
        this.productNameEncrypted = productNameEncrypted;
    }

    public String getPurchasePriceEncrypted() {
        return purchasePriceEncrypted;
    }

    public void setPurchasePriceEncrypted(String purchasePriceEncrypted) {
        this.purchasePriceEncrypted = purchasePriceEncrypted;
    }

    public String getSalePriceEncrypted() {
        return salePriceEncrypted;
    }

    public void setSalePriceEncrypted(String salePriceEncrypted) {
        this.salePriceEncrypted = salePriceEncrypted;
    }

    public String getStockQuantityEncrypted() {
        return stockQuantityEncrypted;
    }

    public void setStockQuantityEncrypted(String stockQuantityEncrypted) {
        this.stockQuantityEncrypted = stockQuantityEncrypted;
    }

    public String getUnitEncrypted() {
        return unitEncrypted;
    }

    public void setUnitEncrypted(String unitEncrypted) {
        this.unitEncrypted = unitEncrypted;
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
