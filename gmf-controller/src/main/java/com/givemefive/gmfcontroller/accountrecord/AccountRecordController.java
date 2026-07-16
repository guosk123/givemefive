package com.givemefive.gmfcontroller.accountrecord;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/account-records")
public class AccountRecordController {

    private static final String OPENID_HEADER = "X-Wx-Openid";

    private final AccountRecordService service;

    public AccountRecordController(AccountRecordService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountRecordResponse create(
            @RequestHeader(OPENID_HEADER) String openid,
            @Valid @RequestBody AccountRecordRequest request) {
        return service.create(openid, request);
    }

    @GetMapping
    public AccountRecordListResponse list(@RequestHeader(OPENID_HEADER) String openid) {
        return service.list(openid);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(OPENID_HEADER) String openid, @PathVariable UUID id) {
        service.delete(openid, id);
    }
}
