package com.givemefive.gmfcontroller.accountrecord;

import com.givemefive.gmfcontroller.common.util.KeyEncUtils;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountRecordCrypto {

    String encrypt(String openid, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return KeyEncUtils.encrypt(openid, value);
    }

    String decrypt(String openid, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return KeyEncUtils.decrypt(openid, value);
    }

    String encryptAmount(String openid, BigDecimal amount) {
        return encrypt(openid, amount.stripTrailingZeros().toPlainString());
    }

    BigDecimal decryptAmount(String openid, String value) {
        return new BigDecimal(decrypt(openid, value));
    }
}
