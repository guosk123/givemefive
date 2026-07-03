package com.givemefive.gmfcontroller.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author guosk
 *
 */
public class HMACSHA256Utils {

  /**
   * sha256_HMAC加密
   * @param message 消息
   * @param secret  绉橀挜
   * @return 加密后字符串
   */
  public static String sha256_HMAC(String message, String secret) {
    String hash = "";
    try {
      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
      sha256_HMAC.init(secret_key);
      byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
      hash = byteArrayToHexString(bytes);
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    }

    return hash;
  }

  /**
   * sha1_HMAC加密
   * @param message 消息
   * @param secret  绉橀挜
   * @return 加密后字符串
   */
  public static String sha1_HMAC(String message, String secret) {
    String hash = "";
    try {
      Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
      SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
      sha1_HMAC.init(secret_key);
      byte[] bytes = sha1_HMAC.doFinal(message.getBytes());
      hash = byteArrayToHexString(bytes);
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    }

    return hash;
  }

  /**
   *
   * @param bytes 字节数组
   */
  public static String byteArrayToHexString(byte[] bytes) {
    StringBuilder hs = new StringBuilder();
    String stmp;
    for (int n = 0; bytes != null && n < bytes.length; n++) {
      stmp = Integer.toHexString(bytes[n] & 0XFF);
      if (stmp.length() == 1) hs.append('0');
      hs.append(stmp);
    }
    return hs.toString().toLowerCase();
  }

}
