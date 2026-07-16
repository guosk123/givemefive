package com.givemefive.gmfcontroller.merchantproduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantProductRepository extends JpaRepository<MerchantProductEntity, UUID> {

    List<MerchantProductEntity> findTop200ByUserOpenidOrderByUpdatedAtDesc(String userOpenid);

    Optional<MerchantProductEntity> findByIdAndUserOpenid(UUID id, String userOpenid);
}
