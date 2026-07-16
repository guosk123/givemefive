package com.givemefive.gmfcontroller.wxappuser;

import jakarta.validation.constraints.Size;
import java.util.Map;

public record WxAppUserConfigRequest(
        @Size(max = 128) String unionid,
        @Size(max = 128) String nickname,
        @Size(max = 2048) String avatarUrl,
        @Size(max = 64) String phone,
        Map<String, String> profile
) {
}
