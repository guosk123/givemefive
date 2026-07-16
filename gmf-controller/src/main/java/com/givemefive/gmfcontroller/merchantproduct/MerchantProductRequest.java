package com.givemefive.gmfcontroller.merchantproduct;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record MerchantProductRequest(
        @NotBlank @Size(max = 128) String productName,
        @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2) BigDecimal purchasePrice,
        @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2) BigDecimal salePrice,
        @NotNull @Min(0) @Max(999999999) Integer stockQuantity,
        @Size(max = 32) String unit,
        @Size(max = 512) String remark
) {
}
