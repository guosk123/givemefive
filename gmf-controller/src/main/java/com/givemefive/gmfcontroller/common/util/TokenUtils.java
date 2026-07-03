package com.givemefive.gmfcontroller.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * 
 * @author guosk
 *
 */
public final class TokenUtils {

  private TokenUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String makeSignature(String token, String... param) {

    // 将token与其他参数拼接为一个array
    String[] array = new String[1 + param.length];
    array[0] = token;
    System.arraycopy(param, 0, array, 1, param.length);

    // 进行字典排序
    Arrays.sort(array);

    StringBuilder content = new StringBuilder();
    for (int i = 0; i < array.length; i++) {
      content.append(array[i]);
    }

    // 摘要
    return Hashing.sha512().hashString(content, StandardCharsets.UTF_8).toString();
  }

  /**
   * 对接一所X平台专用
   * @param key
   * @param secret
   * @param timestamp
   * @param version
   * @return
   */
  public static String makeXSignature(String key, String secret, String timestamp, String version) {
    StringBuilder content = new StringBuilder();
    content.append("access_key=").append(StringUtils.trim(key));
    content.append("||access_secret=").append(StringUtils.trim(secret));
    content.append("||timestamp=").append(StringUtils.trim(timestamp));
    content.append("||version=").append(StringUtils.trim(version));

    // 摘要
    HashFunction hashFunction = Hashing.hmacMd5(secret.getBytes(StandardCharsets.UTF_8));
    HashCode hashCode = hashFunction.hashString(content.toString(), StandardCharsets.UTF_8);
    return hashCode.toString();
  }
}
