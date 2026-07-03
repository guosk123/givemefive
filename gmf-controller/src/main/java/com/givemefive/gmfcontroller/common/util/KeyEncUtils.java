package com.givemefive.gmfcontroller.common.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author guosk
 *
 */
public final class KeyEncUtils {

  // 初始向量（偏移）
  public static final String VIPARA = "aabbccddeeffgghh";
  public static final int KEY_LENGTH = 16;

  private KeyEncUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String encrypt(String key, String content) {
    return encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, content);
  }

  public static String decrypt(String key, String content) {
    return encryptOrDecrypt(key, Cipher.DECRYPT_MODE, content);
  }

  public static String encryptOrDecrypt(String key, int mode, String content) {
    if (StringUtils.isBlank(content)) {
      return "";
    }

    try {
      SecretKeySpec sks = new SecretKeySpec(paddingKey(key), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      if (mode == Cipher.ENCRYPT_MODE) {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, sks, zeroIv);
        byte[] result = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        byte[] base64Data = Base64.getEncoder().encode(result);
        return new String(base64Data, StandardCharsets.UTF_8);
      } else if (mode == Cipher.DECRYPT_MODE) {
        byte[] encryptedBase64Bytes = content.getBytes(StandardCharsets.UTF_8);
        byte[] byteMi = Base64.getDecoder().decode(encryptedBase64Bytes);
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, sks, zeroIv);
        byte[] result = cipher.doFinal(byteMi);
        return new String(result, StandardCharsets.UTF_8);
      } else {
        throw new UnsupportedOperationException("parameter mode is invalid.");
      }
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
        | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
        | IllegalArgumentException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static String encryptOrDecryptByECB(String key, int mode, String content) {
    if (StringUtils.isBlank(content)) {
      return "";
    }

    try {
      SecretKeySpec sks = new SecretKeySpec(paddingKey(key), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      if (mode == Cipher.ENCRYPT_MODE) {
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        byte[] result = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        byte[] base64Data = Base64.getEncoder().encode(result);
        return new String(base64Data, StandardCharsets.UTF_8);
      } else if (mode == Cipher.DECRYPT_MODE) {
        byte[] encryptedBase64Bytes = content.getBytes(StandardCharsets.UTF_8);
        byte[] byteMi = Base64.getDecoder().decode(encryptedBase64Bytes);
        cipher.init(Cipher.DECRYPT_MODE, sks);
        byte[] result = cipher.doFinal(byteMi);
        return new String(result, StandardCharsets.UTF_8);
      } else {
        throw new UnsupportedOperationException("parameter mode is invalid.");
      }
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
        | IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  private static byte[] paddingKey(String key) {
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length % KEY_LENGTH != 0) {
      int groups = keyBytes.length / KEY_LENGTH + 1;
      byte[] temp = new byte[groups * KEY_LENGTH];
      Arrays.fill(temp, (byte) 0);
      System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
      keyBytes = temp;
    }

    return keyBytes;
  }
}
