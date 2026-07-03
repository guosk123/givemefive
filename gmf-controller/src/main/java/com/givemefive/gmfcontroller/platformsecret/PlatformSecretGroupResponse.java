package com.givemefive.gmfcontroller.platformsecret;

import java.util.List;

public record PlatformSecretGroupResponse(
        String platformName,
        List<PlatformSecretResponse> items
) {
}
