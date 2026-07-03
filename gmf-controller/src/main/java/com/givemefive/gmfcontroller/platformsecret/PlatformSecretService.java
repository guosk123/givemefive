package com.givemefive.gmfcontroller.platformsecret;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformSecretService {

    private final PlatformSecretRepository repository;
    private final PlatformSecretCrypto crypto;

    public PlatformSecretService(PlatformSecretRepository repository, PlatformSecretCrypto crypto) {
        this.repository = repository;
        this.crypto = crypto;
    }

    @Transactional
    public PlatformSecretResponse save(String openid, PlatformSecretRequest request) {
        requireOpenid(openid);

        PlatformSecretEntity entity = new PlatformSecretEntity();
        entity.setUserOpenid(openid);
        entity.setPlatformName(StringUtils.trim(request.platformName()));
        entity.setLabel(StringUtils.trim(request.label()));
        entity.setAccountEncrypted(crypto.encrypt(openid, request.account()));
        entity.setPasswordEncrypted(crypto.encrypt(openid, request.password()));
        entity.setSecretKeyEncrypted(crypto.encrypt(openid, request.secretKey()));
        entity.setExtraSecretsEncrypted(crypto.encryptMap(openid, request.extraSecrets()));
        entity.setRemarkEncrypted(crypto.encrypt(openid, request.remark()));

        return toResponse(repository.save(entity), openid);
    }

    @Transactional(readOnly = true)
    public List<PlatformSecretGroupResponse> listGrouped(String openid) {
        requireOpenid(openid);

        List<PlatformSecretEntity> entities =
                repository.findByUserOpenidOrderByPlatformNameAscLabelAsc(openid);
        return group(entities, openid);
    }

    @Transactional(readOnly = true)
    public List<PlatformSecretGroupResponse> listGrouped(String openid, String platformName) {
        requireOpenid(openid);
        if (StringUtils.isBlank(platformName)) {
            return listGrouped(openid);
        }

        List<PlatformSecretEntity> entities =
                repository.findByUserOpenidAndPlatformNameOrderByLabelAsc(
                        openid, StringUtils.trim(platformName));
        return group(entities, openid);
    }

    @Transactional
    public void delete(String openid, UUID id) {
        requireOpenid(openid);
        repository.findByIdAndUserOpenid(id, openid).ifPresent(repository::delete);
    }

    private List<PlatformSecretGroupResponse> group(
            List<PlatformSecretEntity> entities, String openid) {
        Map<String, List<PlatformSecretResponse>> grouped = new LinkedHashMap<>();
        for (PlatformSecretEntity entity : entities) {
            grouped.computeIfAbsent(entity.getPlatformName(), ignored -> new ArrayList<>())
                    .add(toResponse(entity, openid));
        }
        return grouped.entrySet().stream()
                .map(entry -> new PlatformSecretGroupResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private PlatformSecretResponse toResponse(PlatformSecretEntity entity, String openid) {
        return new PlatformSecretResponse(
                entity.getId(),
                entity.getPlatformName(),
                entity.getLabel(),
                crypto.decrypt(openid, entity.getAccountEncrypted()),
                crypto.decrypt(openid, entity.getPasswordEncrypted()),
                crypto.decrypt(openid, entity.getSecretKeyEncrypted()),
                crypto.decryptMap(openid, entity.getExtraSecretsEncrypted()),
                crypto.decrypt(openid, entity.getRemarkEncrypted()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void requireOpenid(String openid) {
        if (StringUtils.isBlank(openid)) {
            throw new IllegalArgumentException("openid is required");
        }
    }
}
