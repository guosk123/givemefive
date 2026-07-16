package com.givemefive.gmfcontroller.merchantproduct;

import com.givemefive.gmfcontroller.common.util.KeyEncUtils;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MerchantProductCrypto {

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

    String encryptAmount(String openid, BigDecimal value) {
        return encrypt(openid, value.stripTrailingZeros().toPlainString());
    }

    BigDecimal decryptAmount(String openid, String value) {
        return new BigDecimal(decrypt(openid, value));
    }

    String encryptInteger(String openid, Integer value) {
        return encrypt(openid, value.toString());
    }

    Integer decryptInteger(String openid, String value) {
        return Integer.valueOf(decrypt(openid, value));
    }
}
