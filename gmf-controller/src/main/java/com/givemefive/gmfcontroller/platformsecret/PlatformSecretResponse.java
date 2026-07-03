package com.givemefive.gmfcontroller.platformsecret;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PlatformSecretResponse(
        UUID id,
        String platformName,
        String label,
        String account,
        String password,
        String secretKey,
        Map<String, String> extraSecrets,
        String remark,
        Instant createdAt,
        Instant updatedAt
) {
}
