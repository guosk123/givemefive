package com.givemefive.gmfcontroller.wxappuser;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WxAppUserRepository extends JpaRepository<WxAppUserEntity, UUID> {

    Optional<WxAppUserEntity> findByOpenid(String openid);
}
