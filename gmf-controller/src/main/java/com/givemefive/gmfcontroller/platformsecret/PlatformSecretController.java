package com.givemefive.gmfcontroller.platformsecret;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/platform-secrets")
public class PlatformSecretController {

    private static final String OPENID_HEADER = "X-Wx-Openid";

    private final PlatformSecretService service;

    public PlatformSecretController(PlatformSecretService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlatformSecretResponse save(
            @RequestHeader(OPENID_HEADER) String openid,
            @Valid @RequestBody PlatformSecretRequest request) {
        return service.save(openid, request);
    }

    @GetMapping
    public List<PlatformSecretGroupResponse> list(
            @RequestHeader(OPENID_HEADER) String openid,
            @RequestParam(required = false) String platformName) {
        return service.listGrouped(openid, platformName);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(OPENID_HEADER) String openid, @PathVariable UUID id) {
        service.delete(openid, id);
    }
}
