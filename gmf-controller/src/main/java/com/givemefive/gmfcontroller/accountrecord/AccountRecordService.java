package com.givemefive.gmfcontroller.accountrecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountRecordService {

    private final AccountRecordRepository repository;
    private final AccountRecordCrypto crypto;

    public AccountRecordService(AccountRecordRepository repository, AccountRecordCrypto crypto) {
        this.repository = repository;
        this.crypto = crypto;
    }

    @Transactional
    public AccountRecordResponse create(String openid, AccountRecordRequest request) {
        requireOpenid(openid);

        AccountRecordEntity entity = new AccountRecordEntity();
        entity.setUserOpenid(openid);
        entity.setRecordType(request.recordType());
        entity.setAmountEncrypted(crypto.encryptAmount(openid, request.amount()));
        entity.setCategoryEncrypted(crypto.encrypt(openid, StringUtils.trim(request.category())));
        entity.setNoteEncrypted(crypto.encrypt(openid, StringUtils.trim(request.note())));
        entity.setRecordDate(request.recordDate());

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public AccountRecordListResponse list(String openid) {
        requireOpenid(openid);
        List<AccountRecordResponse> records = repository
                .findTop100ByUserOpenidOrderByRecordDateDescCreatedAtDesc(openid)
                .stream()
                .map(this::toResponse)
                .toList();

        BigDecimal incomeTotal = total(records, AccountRecordType.INCOME);
        BigDecimal expenseTotal = total(records, AccountRecordType.EXPENSE);
        return new AccountRecordListResponse(
                records,
                incomeTotal,
                expenseTotal,
                incomeTotal.subtract(expenseTotal)
        );
    }

    @Transactional
    public void delete(String openid, UUID id) {
        requireOpenid(openid);
        repository.findByIdAndUserOpenid(id, openid).ifPresent(repository::delete);
    }

    private AccountRecordResponse toResponse(AccountRecordEntity entity) {
        String openid = entity.getUserOpenid();
        return new AccountRecordResponse(
                entity.getId(),
                entity.getRecordType(),
                crypto.decryptAmount(openid, entity.getAmountEncrypted()),
                crypto.decrypt(openid, entity.getCategoryEncrypted()),
                crypto.decrypt(openid, entity.getNoteEncrypted()),
                entity.getRecordDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private BigDecimal total(List<AccountRecordResponse> records, AccountRecordType type) {
        return records.stream()
                .filter(record -> record.recordType() == type)
                .map(AccountRecordResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void requireOpenid(String openid) {
        if (StringUtils.isBlank(openid)) {
            throw new IllegalArgumentException("openid is required");
        }
    }
}
