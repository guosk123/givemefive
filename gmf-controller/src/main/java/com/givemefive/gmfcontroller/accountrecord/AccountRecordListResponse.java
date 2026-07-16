package com.givemefive.gmfcontroller.accountrecord;

import java.math.BigDecimal;
import java.util.List;

public record AccountRecordListResponse(
        List<AccountRecordResponse> records,
        BigDecimal incomeTotal,
        BigDecimal expenseTotal,
        BigDecimal balance
) {
}
