package com.givemefive.gmfcontroller.wxappuser;

import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WxAppUserConfigService {

    private final WxAppUserRepository repository;
    private final WxAppUserConfigCrypto crypto;

    public WxAppUserConfigService(WxAppUserRepository repository, WxAppUserConfigCrypto crypto) {
        this.repository = repository;
        this.crypto = crypto;
    }

    @Transactional
    public WxAppUserConfigResponse getOrCreate(String openid) {
        requireOpenid(openid);
        WxAppUserEntity entity = repository.findByOpenid(openid)
                .orElseGet(() -> repository.save(newUser(openid)));
        return toResponse(entity);
    }

    @Transactional
    public WxAppUserConfigResponse save(String openid, WxAppUserConfigRequest request) {
        requireOpenid(openid);
        WxAppUserEntity entity = repository.findByOpenid(openid).orElseGet(() -> newUser(openid));
        entity.setUnionid(blankToNull(request.unionid()));
        entity.setNicknameEncrypted(crypto.encrypt(openid, StringUtils.trim(request.nickname())));
        entity.setAvatarUrlEncrypted(crypto.encrypt(openid, StringUtils.trim(request.avatarUrl())));
        entity.setPhoneEncrypted(crypto.encrypt(openid, StringUtils.trim(request.phone())));
        entity.setProfileEncrypted(crypto.encryptMap(openid, request.profile()));
        entity.setLastLoginAt(Instant.now());
        return toResponse(repository.save(entity));
    }

    private WxAppUserEntity newUser(String openid) {
        WxAppUserEntity entity = new WxAppUserEntity();
        entity.setOpenid(openid);
        entity.setStatus(WxAppUserStatus.ACTIVE);
        entity.setLastLoginAt(Instant.now());
        return entity;
    }

    private WxAppUserConfigResponse toResponse(WxAppUserEntity entity) {
        String openid = entity.getOpenid();
        return new WxAppUserConfigResponse(
                entity.getId(),
                openid,
                entity.getUnionid(),
                crypto.decrypt(openid, entity.getNicknameEncrypted()),
                crypto.decrypt(openid, entity.getAvatarUrlEncrypted()),
                crypto.decrypt(openid, entity.getPhoneEncrypted()),
                crypto.decryptMap(openid, entity.getProfileEncrypted()),
                entity.getStatus(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String blankToNull(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return StringUtils.trim(value);
    }

    private void requireOpenid(String openid) {
        if (StringUtils.isBlank(openid)) {
            throw new IllegalArgumentException("openid is required");
        }
    }
}
