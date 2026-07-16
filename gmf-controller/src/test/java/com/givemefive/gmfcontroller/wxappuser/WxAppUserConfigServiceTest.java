package com.givemefive.gmfcontroller.wxappuser;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({WxAppUserConfigService.class, WxAppUserConfigCrypto.class, ObjectMapper.class})
class WxAppUserConfigServiceTest {

    @Autowired
    private WxAppUserConfigService service;

    @Autowired
    private WxAppUserRepository repository;

    @Test
    void saveEncryptsProfileAndUpdatesByOpenid() {
        String openid = "wx-openid-001";
        WxAppUserConfigRequest firstRequest = new WxAppUserConfigRequest(
                "unionid-001",
                "tester",
                "https://example.com/avatar.png",
                "13800000000",
                Map.of("defaultBook", "daily", "currency", "CNY")
        );

        WxAppUserConfigResponse saved = service.save(openid, firstRequest);

        WxAppUserEntity entity = repository.findById(saved.id()).orElseThrow();
        assertThat(entity.getNicknameEncrypted()).isNotEqualTo("tester");
        assertThat(entity.getAvatarUrlEncrypted()).doesNotContain("avatar.png");
        assertThat(entity.getPhoneEncrypted()).isNotEqualTo("13800000000");
        assertThat(entity.getProfileEncrypted()).doesNotContain("daily");

        WxAppUserConfigResponse loaded = service.getOrCreate(openid);

        assertThat(loaded.nickname()).isEqualTo("tester");
        assertThat(loaded.avatarUrl()).isEqualTo("https://example.com/avatar.png");
        assertThat(loaded.phone()).isEqualTo("13800000000");
        assertThat(loaded.profile()).containsEntry("defaultBook", "daily");

        WxAppUserConfigResponse updated = service.save(openid, new WxAppUserConfigRequest(
                "unionid-001",
                "updated",
                "https://example.com/updated.png",
                "13900000000",
                Map.of("defaultBook", "business")
        ));

        assertThat(updated.id()).isEqualTo(saved.id());
        assertThat(repository.findAll()).hasSize(1);
        assertThat(updated.nickname()).isEqualTo("updated");
        assertThat(updated.profile()).containsEntry("defaultBook", "business");
    }
}
