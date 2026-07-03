package com.givemefive.gmfcontroller.platformsecret;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record PlatformSecretRequest(
        @NotBlank @Size(max = 128) String platformName,
        @NotBlank @Size(max = 128) String label,
        @Size(max = 512) String account,
        @Size(max = 512) String password,
        @Size(max = 2048) String secretKey,
        Map<String, String> extraSecrets,
        @Size(max = 2048) String remark
) {
}
