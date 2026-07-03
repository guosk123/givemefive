package com.givemefive.gmfcontroller.common.util;

import org.apache.commons.lang3.StringUtils;
import reactor.util.function.Tuple2;

import java.util.List;

public class MacUtils {
  /**
   * 支持三种格式
   * 01:23:45:67:89:ab
   * 0x0000000000FF
   * 01-23-45-67-89-ab
   * @param param1
   * @param param2
   * @return
   */
  public static String andOperation(String param1, String param2) {
    if (param1.startsWith("0x")) {
      param1 = param1.substring(2, param2.length());
    }
    if (param2.startsWith("0x")) {
      param2 = param2.substring(2, param2.length());
    }
    if (StringUtils.contains(param1, ":")) {
      param1 = StringUtils.replace(param1, ":", "");
    }
    if (StringUtils.contains(param2, ":")) {
      param2 = StringUtils.replace(param2, ":", "");
    }
    if (StringUtils.contains(param1, "-")) {
      param1 = StringUtils.replace(param1, "-", "");
    }
    if (StringUtils.contains(param2, "-")) {
      param2 = StringUtils.replace(param2, "-", "");
    }
    String result = "";
    for (int i = 0; i < param1.length(); i++) {
      String hexString = char2HexString(param1.charAt(i));
      String hexString2 = char2HexString(param2.charAt(i));
      String hexResult = "";
      for (int j = 0; j < hexString.length(); j++) {
        hexResult += andOperation(hexString.charAt(j), hexString2.charAt(j));
      }
      result += hexString2Char(hexResult);
    }
    return result;
  }

  /**
   *  判断mac掩码范围内是否有重复地址 支持掩码在前在后在中
   *     list.add(Tuples.of("01:23:45:67:89:ab", "0x0000000000FF"));
   *     list.add(Tuples.of("01:23:45:67:89:a2", "0x00000000FFFF"));
   * @param list
   * @return 重复true 不重复false
   */
  public static boolean isRepeat(List<Tuple2<String, String>> list) {
    outer: for (int i = 0; i < list.size(); i++) {
      Tuple2<String, String> left = list.get(i);
      for (int j = i + 1; j < list.size(); j++) {
        Tuple2<String, String> right = list.get(j);
        String commonMask = andOperation(left.getT2(), right.getT2());
//        System.out.println("commonMask" + commonMask);
        String leftRange = andOperation(left.getT1(), commonMask);
        String rightRange = andOperation(right.getT1(), commonMask);
//        System.out.println("leftRange" + leftRange);
//        System.out.println("rightRange" + rightRange);
        if (StringUtils.equals(leftRange, rightRange)) {
          return true;
          // break outer;
        }
      }
    }
    return false;
  }

  public static String char2HexString(char param) {
    String result = "";
    switch (param) {
      case 'F':
        result = "1111";
        break;
      case '0':
        result = "0000";
        break;
      case '1':
        result = "0001";
        break;
      case '2':
        result = "0010";
        break;
      case '3':
        result = "0011";
        break;
      case '4':
        result = "0100";
        break;
      case '5':
        result = "0101";
        break;
      case '6':
        result = "0110";
        break;
      case '7':
        result = "0111";
        break;
      case '8':
        result = "1000";
        break;
      case '9':
        result = "1001";
        break;
      case 'a':
        result = "1010";
        break;
      case 'b':
        result = "1011";
        break;
      case 'c':
        result = "1100";
        break;
      case 'd':
        result = "1101";
        break;
      case 'e':
        result = "1110";
        break;
      case 'f':
        result = "1111";
        break;
    }
    return result;
  }

  public static char hexString2Char(String param) {
    char result = '0';
    switch (param) {
      case "0000":
        result = '0';
        break;
      case "0001":
        result = '1';
        break;
      case "0010":
        result = '2';
        break;
      case "0011":
        result = '3';
        break;
      case "0100":
        result = '4';
        break;
      case "0101":
        result = '5';
        break;
      case "0110":
        result = '6';
        break;
      case "0111":
        result = '7';
        break;
      case "1000":
        result = '8';
        break;
      case "1001":
        result = '9';
        break;
      case "1010":
        result = 'a';
        break;
      case "1011":
        result = 'b';
        break;
      case "1100":
        result = 'c';
        break;
      case "1101":
        result = 'd';
        break;
      case "1110":
        result = 'e';
        break;
      case "1111":
        result = 'f';
        break;
    }
    return result;
  }

  /**
   *  0 & 0 -> 0
   *  1 & 0 -> 0
   *  1 & 1 -> 1
   * @param param1
   * @param param2
   * @return
   */
  public static char andOperation(char param1, char param2) {
    int i = param1 - '0';
    int j = param2 - '0';
    if (i == j && i == 1) {
      return '1';
    } else {
      return '0';
    }
  }
}
