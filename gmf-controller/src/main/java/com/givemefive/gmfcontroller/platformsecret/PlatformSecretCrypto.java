package com.givemefive.gmfcontroller.platformsecret;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemefive.gmfcontroller.common.util.KeyEncUtils;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PlatformSecretCrypto {

    private static final TypeReference<Map<String, String>> STRING_MAP =
            new TypeReference<>() {
            };

    private final ObjectMapper objectMapper;

    public PlatformSecretCrypto(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    String encrypt(String openid, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return KeyEncUtils.encrypt(openid, value);
    }

    String decrypt(String openid, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return KeyEncUtils.decrypt(openid, value);
    }

    String encryptMap(String openid, Map<String, String> value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        try {
            return encrypt(openid, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("extraSecrets is invalid", e);
        }
    }

    Map<String, String> decryptMap(String openid, String value) {
        if (StringUtils.isBlank(value)) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(decrypt(openid, value), STRING_MAP);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("extraSecrets cannot be decoded", e);
        }
    }
}
