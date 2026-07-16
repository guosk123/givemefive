package com.givemefive.gmfcontroller.merchantproduct;

import java.math.BigDecimal;
import java.util.List;

public record MerchantProductListResponse(
        List<MerchantProductResponse> products,
        int productCount,
        int stockTotal,
        BigDecimal stockCostTotal,
        BigDecimal stockRevenueTotal,
        BigDecimal grossProfitTotal
) {
}
