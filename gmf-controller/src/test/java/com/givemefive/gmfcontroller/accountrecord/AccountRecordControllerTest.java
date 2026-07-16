package com.givemefive.gmfcontroller.accountrecord;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.givemefive.gmfcontroller.config.SecurityConfig;
import com.givemefive.gmfcontroller.config.WxAppOpenidAuthenticationFilter;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountRecordController.class)
@Import({SecurityConfig.class, WxAppOpenidAuthenticationFilter.class})
class AccountRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRecordService service;

    @Test
    void listAcceptsWxOpenidHeaderAsAppAuthentication() throws Exception {
        when(service.list("openid-001")).thenReturn(new AccountRecordListResponse(
                List.of(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        ));

        mockMvc.perform(get("/api/app/account-records")
                        .header("X-Wx-Openid", "openid-001"))
                .andExpect(status().isOk());

        verify(service).list("openid-001");
    }
}
