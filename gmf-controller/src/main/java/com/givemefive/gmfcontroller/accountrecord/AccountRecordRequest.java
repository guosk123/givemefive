package com.givemefive.gmfcontroller.accountrecord;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountRecordRequest(
        @NotNull AccountRecordType recordType,
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal amount,
        @NotBlank @Size(max = 64) String category,
        @Size(max = 512) String note,
        @NotNull LocalDate recordDate
) {
}
