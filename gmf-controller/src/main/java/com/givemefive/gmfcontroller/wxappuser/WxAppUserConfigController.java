package com.givemefive.gmfcontroller.wxappuser;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/user-config")
public class WxAppUserConfigController {

    private static final String OPENID_HEADER = "X-Wx-Openid";

    private final WxAppUserConfigService service;

    public WxAppUserConfigController(WxAppUserConfigService service) {
        this.service = service;
    }

    @GetMapping
    public WxAppUserConfigResponse get(@RequestHeader(OPENID_HEADER) String openid) {
        return service.getOrCreate(openid);
    }

    @PostMapping
    public WxAppUserConfigResponse save(
            @RequestHeader(OPENID_HEADER) String openid,
            @Valid @RequestBody WxAppUserConfigRequest request) {
        return service.save(openid, request);
    }
}
