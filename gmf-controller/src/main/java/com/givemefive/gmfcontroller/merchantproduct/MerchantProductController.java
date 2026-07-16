package com.givemefive.gmfcontroller.merchantproduct;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/merchant-products")
public class MerchantProductController {

    private static final String OPENID_HEADER = "X-Wx-Openid";

    private final MerchantProductService service;

    public MerchantProductController(MerchantProductService service) {
        this.service = service;
    }

    @GetMapping
    public MerchantProductListResponse list(@RequestHeader(OPENID_HEADER) String openid) {
        return service.list(openid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantProductResponse create(
            @RequestHeader(OPENID_HEADER) String openid,
            @Valid @RequestBody MerchantProductRequest request) {
        return service.create(openid, request);
    }

    @PutMapping("/{id}")
    public MerchantProductResponse update(
            @RequestHeader(OPENID_HEADER) String openid,
            @PathVariable UUID id,
            @Valid @RequestBody MerchantProductRequest request) {
        return service.update(openid, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(OPENID_HEADER) String openid, @PathVariable UUID id) {
        service.delete(openid, id);
    }
}
