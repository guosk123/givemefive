package com.givemefive.gmfcontroller.platformsecret;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({PlatformSecretService.class, PlatformSecretCrypto.class, ObjectMapper.class})
class PlatformSecretServiceTest {

    @Autowired
    private PlatformSecretService service;

    @Autowired
    private PlatformSecretRepository repository;

    @Test
    void saveEncryptsSensitiveFieldsAndListDecryptsByOpenid() {
        String openid = "wx-openid-001";
        PlatformSecretRequest request = new PlatformSecretRequest(
                "GitHub",
                "main",
                "user@example.com",
                "plain-password",
                "ghp_secret",
                Map.of("recoveryCode", "backup-code"),
                "private note"
        );

        PlatformSecretResponse saved = service.save(openid, request);

        PlatformSecretEntity entity = repository.findById(saved.id()).orElseThrow();
        assertThat(entity.getAccountEncrypted()).isNotEqualTo("user@example.com");
        assertThat(entity.getPasswordEncrypted()).isNotEqualTo("plain-password");
        assertThat(entity.getSecretKeyEncrypted()).isNotEqualTo("ghp_secret");
        assertThat(entity.getExtraSecretsEncrypted()).doesNotContain("backup-code");

        List<PlatformSecretGroupResponse> grouped = service.listGrouped(openid);

        assertThat(grouped).hasSize(1);
        assertThat(grouped.get(0).platformName()).isEqualTo("GitHub");
        assertThat(grouped.get(0).items()).singleElement().satisfies(item -> {
            assertThat(item.account()).isEqualTo("user@example.com");
            assertThat(item.password()).isEqualTo("plain-password");
            assertThat(item.secretKey()).isEqualTo("ghp_secret");
            assertThat(item.extraSecrets()).containsEntry("recoveryCode", "backup-code");
            assertThat(item.remark()).isEqualTo("private note");
        });
    }
}
