package com.givemefive.gmfcontroller.merchantproduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantProductService {

    private final MerchantProductRepository repository;
    private final MerchantProductCrypto crypto;

    public MerchantProductService(
            MerchantProductRepository repository,
            MerchantProductCrypto crypto) {
        this.repository = repository;
        this.crypto = crypto;
    }

    @Transactional
    public MerchantProductResponse create(String openid, MerchantProductRequest request) {
        requireOpenid(openid);

        MerchantProductEntity entity = new MerchantProductEntity();
        entity.setUserOpenid(openid);
        fillEntity(openid, entity, request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public MerchantProductResponse update(String openid, UUID id, MerchantProductRequest request) {
        requireOpenid(openid);
        MerchantProductEntity entity = repository.findByIdAndUserOpenid(id, openid)
                .orElseThrow(() -> new IllegalArgumentException("product is not found"));
        fillEntity(openid, entity, request);
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public MerchantProductListResponse list(String openid) {
        requireOpenid(openid);
        List<MerchantProductResponse> products = repository
                .findTop200ByUserOpenidOrderByUpdatedAtDesc(openid)
                .stream()
                .map(this::toResponse)
                .toList();

        BigDecimal stockCostTotal = products.stream()
                .map(MerchantProductResponse::stockCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal stockRevenueTotal = products.stream()
                .map(MerchantProductResponse::stockRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int stockTotal = products.stream()
                .mapToInt(MerchantProductResponse::stockQuantity)
                .sum();
        return new MerchantProductListResponse(
                products,
                products.size(),
                stockTotal,
                stockCostTotal,
                stockRevenueTotal,
                stockRevenueTotal.subtract(stockCostTotal)
        );
    }

    @Transactional
    public void delete(String openid, UUID id) {
        requireOpenid(openid);
        repository.findByIdAndUserOpenid(id, openid).ifPresent(repository::delete);
    }

    private void fillEntity(
            String openid,
            MerchantProductEntity entity,
            MerchantProductRequest request) {
        entity.setProductNameEncrypted(
                crypto.encrypt(openid, StringUtils.trim(request.productName())));
        entity.setPurchasePriceEncrypted(crypto.encryptAmount(openid, request.purchasePrice()));
        entity.setSalePriceEncrypted(crypto.encryptAmount(openid, request.salePrice()));
        entity.setStockQuantityEncrypted(crypto.encryptInteger(openid, request.stockQuantity()));
        entity.setUnitEncrypted(crypto.encrypt(openid, StringUtils.trim(request.unit())));
        entity.setRemarkEncrypted(crypto.encrypt(openid, StringUtils.trim(request.remark())));
    }

    private MerchantProductResponse toResponse(MerchantProductEntity entity) {
        String openid = entity.getUserOpenid();
        BigDecimal purchasePrice = crypto.decryptAmount(openid, entity.getPurchasePriceEncrypted());
        BigDecimal salePrice = crypto.decryptAmount(openid, entity.getSalePriceEncrypted());
        Integer stockQuantity = crypto.decryptInteger(openid, entity.getStockQuantityEncrypted());
        BigDecimal stock = BigDecimal.valueOf(stockQuantity);
        return new MerchantProductResponse(
                entity.getId(),
                crypto.decrypt(openid, entity.getProductNameEncrypted()),
                purchasePrice,
                salePrice,
                stockQuantity,
                crypto.decrypt(openid, entity.getUnitEncrypted()),
                crypto.decrypt(openid, entity.getRemarkEncrypted()),
                salePrice.subtract(purchasePrice),
                purchasePrice.multiply(stock),
                salePrice.multiply(stock),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void requireOpenid(String openid) {
        if (StringUtils.isBlank(openid)) {
            throw new IllegalArgumentException("openid is required");
        }
    }
}
