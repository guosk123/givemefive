package com.givemefive.gmfcontroller.accountrecord;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({AccountRecordService.class, AccountRecordCrypto.class})
class AccountRecordServiceTest {

    @Autowired
    private AccountRecordService service;

    @Autowired
    private AccountRecordRepository repository;

    @Test
    void createEncryptsFieldsAndListOnlyReturnsCurrentUserRecords() {
        AccountRecordResponse expense = service.create("openid-001", new AccountRecordRequest(
                AccountRecordType.EXPENSE,
                new BigDecimal("12.50"),
                "餐饮",
                "午餐",
                LocalDate.of(2026, 7, 10)
        ));
        service.create("openid-001", new AccountRecordRequest(
                AccountRecordType.INCOME,
                new BigDecimal("100.00"),
                "工资",
                "兼职",
                LocalDate.of(2026, 7, 9)
        ));
        service.create("openid-002", new AccountRecordRequest(
                AccountRecordType.EXPENSE,
                new BigDecimal("88.00"),
                "购物",
                "other user",
                LocalDate.of(2026, 7, 10)
        ));

        AccountRecordEntity entity = repository.findById(expense.id()).orElseThrow();
        assertThat(entity.getAmountEncrypted()).isNotEqualTo("12.50");
        assertThat(entity.getCategoryEncrypted()).isNotEqualTo("餐饮");
        assertThat(entity.getNoteEncrypted()).isNotEqualTo("午餐");

        AccountRecordListResponse list = service.list("openid-001");

        assertThat(list.records()).hasSize(2);
        assertThat(list.incomeTotal()).isEqualByComparingTo("100.00");
        assertThat(list.expenseTotal()).isEqualByComparingTo("12.50");
        assertThat(list.balance()).isEqualByComparingTo("87.50");
        assertThat(list.records()).extracting(AccountRecordResponse::category)
                .containsExactly("餐饮", "工资");
    }

    @Test
    void deleteOnlyRemovesCurrentUserRecord() {
        AccountRecordResponse ownRecord = service.create("openid-001", new AccountRecordRequest(
                AccountRecordType.EXPENSE,
                new BigDecimal("12.50"),
                "餐饮",
                "",
                LocalDate.of(2026, 7, 10)
        ));
        AccountRecordResponse otherRecord = service.create("openid-002", new AccountRecordRequest(
                AccountRecordType.EXPENSE,
                new BigDecimal("30.00"),
                "交通",
                "",
                LocalDate.of(2026, 7, 10)
        ));

        service.delete("openid-001", otherRecord.id());
        assertThat(repository.findById(otherRecord.id())).isPresent();

        service.delete("openid-001", ownRecord.id());
        assertThat(repository.findById(ownRecord.id())).isEmpty();
    }
}
