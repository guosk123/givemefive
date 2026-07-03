package com.givemefive.gmfcontroller.common.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.givemefive.gmfcontroller.common.Constants;

/**
 * 
 * @author guosk
 *
 */
public final class TextUtils {

  private static final Pattern ILLEGAL_CHARACTER_PATTERN = Pattern.compile("[*?><;&!/'\"`(){}|]");

  private TextUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean toBoolean(String text) {

    if (text.length() == 1) {
      char ch0 = text.charAt(0);
      if (ch0 == '0') {
        return false;
      }
      if (ch0 == '1') {
        return true;
      }
    }
    return BooleanUtils.toBoolean(text);
  }

  public static String toUtf8String(String text) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c >= 0 && c <= 127) {
        builder.append(c);
      } else {
        byte[] b = Character.toString(c).getBytes(StandardCharsets.UTF_8);

        for (byte value : b) {
          // x & 0xFF, this operation converts byte to int, dropping the sign.
          builder.append('%').append(Integer.toHexString(value & 255).toUpperCase(Locale.US));
        }
      }
    }
    return builder.toString();
  }

  public static String byte2Hex(byte[] paramArrayOfByte) {
    StringBuilder builder = new StringBuilder();
    String str = "";
    for (byte b : paramArrayOfByte) {
      str = Integer.toHexString(b & 0xFF);
      if (str.length() == 1) {
        builder.append('0');
      }
      builder.append(str);
    }
    return builder.toString().toUpperCase(Locale.US);
  }

  public static String underLineToCamel(String text) {
    if (text == null) {
      return null;
    }

    int length = text.length();
    if (length == 0) {
      return text;
    }

    StringBuilder result = new StringBuilder(length);
    boolean shouldUpperCase = false;
    for (int i = 0; i < length; i++) {
      char c = text.charAt(i);

      if (c == '_') {
        if (i + 1 < length) {
          char nextChar = text.charAt(i + 1);
          if (Character.isLetter(nextChar)) {
            // 下划线后是字母，跳过下划线，下一个字符转大写
            shouldUpperCase = true;
          } else {
            // 下划线后不是字母，保留下划线
            result.append(c);
          }
        } else {
          result.append(c);
        }
        continue;
      }

      if (shouldUpperCase) {
        c = Character.toUpperCase(c);
        shouldUpperCase = false;
      }
      result.append(c);
    }

    return result.toString();
  }

  public static String camelToUnderLine(String text) {
    if (text == null) {
      return text;
    }

    String regex = "([A-Z])";
    Matcher matcher = Pattern.compile(regex).matcher(text);
    while (matcher.find()) {
      String target = matcher.group();
      text = text.replaceAll(target, "_" + target.toLowerCase());
    }
    return text;
  }

  public static String matchingIllegalCharacters(String text) {
    Set<String> illegalCharacters = Sets.newHashSetWithExpectedSize(Constants.COL_DEFAULT_SIZE);

    Matcher matcher = ILLEGAL_CHARACTER_PATTERN.matcher(text);
    while (matcher.find()) {
      illegalCharacters.add(matcher.group());
    }

    return StringUtils.join(illegalCharacters, ",");
  }

  public static boolean isBlank(Long param) {
    return param == null || (param == Constants.ZERO_LONG);
  }

  public static boolean isBlank(Integer param) {
    return param == null || (param == Constants.ZERO_INTEGER);
  }

  public static boolean isNotBlank(Long param) {
    return param != null && (param != Constants.ZERO_LONG);
  }

  public static boolean isNotBlank(Integer param) {
    return param != null && (param != Constants.ZERO_INTEGER);
  }

  public static boolean isAnyBlank(Long param1, Long param2) {
    return isBlank(param1) || isBlank(param2);
  }

  public static boolean isAnyBlank(Long... param1) {
    for (Long aLong : param1) {
      if (isBlank(aLong)) {
        return true;
      }
    }
    return false;
  }

  public static boolean equals(Long param1, Long param2) {
    if (param1 == null && param2 == null) {
      return true;
    }
    if (param1 == null || param2 == null) {
      return false;
    }
    return param1.longValue() == param2.longValue();
  }

  /**
   * TextUtils.defaultIfBlank(null, 1L)  = 1L
   * TextUtils.defaultIfBlank(0L, 1L)    = 1L
   * TextUtils.defaultIfBlank(1L, 2L)   = 1L
   * @return if param equals null or 0L, return defaultLong.
   */
  public static Long defaultIfBlank(Long param, Long defaultLong) {
    return isBlank(param) ? defaultLong : param;
  }

  /**
   *  数值型包装类转化为Long类型
   */
  public static Long numberToLong(Object object) {

    if (object == null) {
      return 0L;
    }

    try {
      return Long.parseLong(object.toString());
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  public static Integer numberToInt(Object object) {

    if (object == null) {
      return 0;
    }
    try {
      return Integer.parseInt(object.toString());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * 判断是否为基础数据类型集合
   */
  public static boolean isArray(Object object) {
    return object.getClass().getSimpleName().endsWith("[]");
  }

  public static boolean isMap(Object object) {
    return object.getClass().getSimpleName().endsWith("Map");
  }

  /**
   *  转化整型基础数据类型集合为字符串集合
   */
  public static List<String> arrayToString(Object object) {
    if (object == null) {
      return Lists.newArrayListWithCapacity(0);
    }

    try {
      if (object instanceof byte[] byteArray) {
        List<String> result = Lists.newArrayListWithCapacity(byteArray.length);
        for (byte b : byteArray) {
          result.add(String.valueOf(b));
        }
        return result;
      } else {
        return JsonHelper.deserialize(JsonHelper.serialize(object),
            new TypeReference<List<String>>() {
            });
      }
    } catch (UnsupportedOperationException e) {
      return Lists.newArrayListWithCapacity(0);
    }
  }

  public static String objectToString(Object object) {
    if (object == null) {
      return "";
    }

    return switch (object.getClass().getSimpleName()) {
      case "Inet4Address" -> ((Inet4Address) object).getHostAddress();
      case "Inet6Address" -> ((Inet6Address) object).getHostAddress();
      default -> object.toString();
    };
  }

}
