package com.givemefive.gmfcontroller.accountrecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRecordRepository extends JpaRepository<AccountRecordEntity, UUID> {

    List<AccountRecordEntity> findTop100ByUserOpenidOrderByRecordDateDescCreatedAtDesc(
            String userOpenid);

    Optional<AccountRecordEntity> findByIdAndUserOpenid(UUID id, String userOpenid);
}
