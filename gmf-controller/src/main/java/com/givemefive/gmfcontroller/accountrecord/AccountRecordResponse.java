package com.givemefive.gmfcontroller.accountrecord;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AccountRecordResponse(
        UUID id,
        AccountRecordType recordType,
        BigDecimal amount,
        String category,
        String note,
        LocalDate recordDate,
        Instant createdAt,
        Instant updatedAt
) {
}
