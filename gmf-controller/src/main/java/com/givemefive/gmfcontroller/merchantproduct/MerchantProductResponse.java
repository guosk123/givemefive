package com.givemefive.gmfcontroller.merchantproduct;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MerchantProductResponse(
        UUID id,
        String productName,
        BigDecimal purchasePrice,
        BigDecimal salePrice,
        Integer stockQuantity,
        String unit,
        String remark,
        BigDecimal grossProfit,
        BigDecimal stockCost,
        BigDecimal stockRevenue,
        Instant createdAt,
        Instant updatedAt
) {
}
