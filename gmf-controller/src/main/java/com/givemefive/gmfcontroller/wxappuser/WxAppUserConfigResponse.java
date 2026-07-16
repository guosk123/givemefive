package com.givemefive.gmfcontroller.wxappuser;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record WxAppUserConfigResponse(
        UUID id,
        String openid,
        String unionid,
        String nickname,
        String avatarUrl,
        String phone,
        Map<String, String> profile,
        WxAppUserStatus status,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
) {
}
