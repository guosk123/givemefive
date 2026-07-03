package com.givemefive.gmfcontroller.platformsecret;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformSecretRepository extends JpaRepository<PlatformSecretEntity, UUID> {

    List<PlatformSecretEntity> findByUserOpenidOrderByPlatformNameAscLabelAsc(String userOpenid);

    List<PlatformSecretEntity> findByUserOpenidAndPlatformNameOrderByLabelAsc(
            String userOpenid, String platformName);

    Optional<PlatformSecretEntity> findByIdAndUserOpenid(UUID id, String userOpenid);
}
