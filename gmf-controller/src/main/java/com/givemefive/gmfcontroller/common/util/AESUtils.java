package com.givemefive.gmfcontroller.common.util;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author guosk
 * @Date: 2024/02/01
 */
public final class AESUtils {

  private static final String AES_ECB = "AES/ECB/PKCS5Padding";
  private static final String ALGORITHM = "AES";

  /**
   * AES之ECB模式解码
   * @param content   被解码字符串
   * @param key       绉橀挜
   * @return
   */
  public static String decrypt(String content, String key) {

    try {
      byte[] textBytes = Base64.getDecoder().decode(content);

      Cipher cipher = Cipher.getInstance(AES_ECB);

      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

      // 解密字节数组
      byte[] decryptedBytes = cipher.doFinal(textBytes);

      return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String decrypt2(String sSrc, String sKey) throws Exception {
    try {
      if (StringUtils.isBlank(sKey) || sKey.length() != 16) {
        return "";
      }
      byte[] raw = sKey.getBytes("utf-8");
      SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
      Cipher cipher = Cipher.getInstance(AES_ECB);
      cipher.init(Cipher.DECRYPT_MODE, skeySpec);
      byte[] encrypted1 = Base64.getDecoder().decode(sSrc); // 先用base64解密
      try {
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original, "utf-8");
        return originalString;
      } catch (Exception e) {
        return null;
      }
    } catch (Exception ex) {
      return null;
    }
  }


  /**
   * 加密
   * @return
   * @throws Exception
   */
  public static String encrypt(String sSrc, String sKey) throws Exception {

    if (sKey.length() != 16) {
      return null;
    }
    byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
    SecretKeySpec keySpec = new SecretKeySpec(raw, ALGORITHM);
    Cipher cipher = Cipher.getInstance(AES_ECB);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encrypted);
  }


  public static void main(String[] args) throws Exception {
    /*
     */
    String cKey = "4621d373cade4e83";
    // 需要加密的字串
    String cSrc = "123456";
    System.out.println(cSrc);
    // 加密
    String enString = encrypt(cSrc, cKey);
    System.out.println("鍔犲瘑鍚庣殑瀛椾覆鏄細" + enString);

    // 解密
    String DeString = decrypt(
        "OFcaujnStwhpO2RIyMLncNviVyvEfOuisjcJBbQ7X9U4BNuGsCy3vLTgQLfNsmrXw1bzub9Ix/yklPeF76KSMOK4S+C8+/cKa+0vBIUv3kgSqMJ41Dhz+lo4+Zoj+TJEp/AS/P6rB8VLl+rsodtgMYNGLhT6sjgbdTHgcq3E6wPBSaOaJCvZJb/IdiSIuMgn/xSYt0fx1So1CLw6tQlBPkY6GHm7O1EIeydOutC75M4z7/t5uWq7l9zlro26CH1CVmiRKWhiQeI/cDf3EKB8SSusnE4zPd7HeKbdwEE9MFWIsJqX2FhvDBvTr8YAnaoB2v4F2c06gHCGi6uo3fCrgIC+I+ITZ2mcdlpnIAR3G1I=",
        "qwertyuiopasdfgh");
    String qwertyuiopasdfgh = decrypt2("NYdQ5qo3a30we4elBRBEkQ==", "qwertyuiopasdfgh");
    System.out.println("瑙ｅ瘑鍚庣殑瀛椾覆鏄細" + DeString);
    System.out.println("瑙ｅ瘑鍚庣殑瀛椾覆鏄細" + qwertyuiopasdfgh);
  }


}
