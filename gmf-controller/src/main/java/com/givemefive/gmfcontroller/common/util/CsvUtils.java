package com.givemefive.gmfcontroller.common.util;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.givemefive.gmfcontroller.common.Constants;

/**
 * 
 * @author guosk
 *
 */
public final class CsvUtils {

  private static final Pattern SPLIT_PATTERN = Pattern.compile("`((?:[^`]|((?<=\\\\)`))+)`|``",
      Pattern.MULTILINE);

  private static final Pattern SPLIT_SIMPLIFY_PATTERN = Pattern.compile("`([^`]+)`|``",
      Pattern.MULTILINE);

  private CsvUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 将CSV（Comma-seprate Value）转换为List
   * 
   */
  public static List<String> convertCSVToList(String valueCSV) {

    if (StringUtils.isBlank(valueCSV)) {
      return Lists.newArrayListWithCapacity(0);
    }

    String[] valueStrAry = valueCSV.split(",");
    List<String> valueList = Lists.newArrayListWithCapacity(valueStrAry.length);
    for (String valueStr : valueStrAry) {
      if (StringUtils.isBlank(valueCSV)) {
        continue;
      }

      valueList.add(valueStr);
    }

    return valueList;
  }

  public static <T> List<T> convertCSVToList(String valueCSV, Class<T> clazz) {

    if (StringUtils.isBlank(valueCSV)) {
      return Lists.newArrayListWithCapacity(0);
    }

    if (clazz == null) {
      throw new IllegalArgumentException("clazz can not be null");
    }

    String[] valueStrAry = valueCSV.split(",");
    List<T> valueList = Lists.newArrayListWithCapacity(valueStrAry.length);
    for (String valueStr : valueStrAry) {
      if (StringUtils.isBlank(valueStr)) {
        continue;
      }

      String value = StringUtils.trim(valueStr);
      Object parsed = switch (clazz.getSimpleName()) {
        case "Long" -> Long.valueOf(value);
        case "Integer" -> Integer.valueOf(value);
        case "Double" -> Double.valueOf(value);
        case "Float" -> Float.valueOf(value);
        case "Short" -> Short.valueOf(value);
        case "Byte" -> Byte.valueOf(value);
        case "Boolean" -> Boolean.valueOf(value);
        default -> value;
      };

      valueList.add(clazz.cast(parsed));
    }

    return valueList;
  }

  public static List<Long> convertCSVToLongList(String valueCSV) {

    if (StringUtils.isBlank(valueCSV)) {
      return Lists.newArrayListWithCapacity(0);
    }

    String[] valueStrAry = valueCSV.split(",");
    List<Long> valueList = Lists.newArrayListWithCapacity(valueStrAry.length);
    for (String valueStr : valueStrAry) {
      if (StringUtils.isBlank(valueCSV)) {
        continue;
      }

      valueList.add(Long.parseLong(valueStr));
    }

    return valueList;
  }

  public static List<Integer> convertCSVToIntList(String valueCSV) {

    if (StringUtils.isBlank(valueCSV)) {
      return Lists.newArrayListWithCapacity(0);
    }

    String[] valueStrAry = valueCSV.split(",");
    List<Integer> valueList = Lists.newArrayListWithCapacity(valueStrAry.length);
    for (String valueStr : valueStrAry) {
      if (StringUtils.isBlank(valueCSV)) {
        continue;
      }

      valueList.add(Integer.parseInt(valueStr));
    }

    return valueList;
  }

  /**
   * 将CSV（Comma-seprate Value）转换为Set
   * 
   */
  public static Set<String> convertCSVToSet(String valueCSV) {

    if (StringUtils.isBlank(valueCSV)) {
      return Sets.newHashSetWithExpectedSize(0);
    }

    String[] valueStrAry = valueCSV.split(",");
    Set<String> valueList = Sets.newHashSetWithExpectedSize(valueStrAry.length);
    for (String valueStr : valueStrAry) {
      if (StringUtils.isBlank(valueCSV)) {
        continue;
      }

      valueList.add(valueStr);
    }

    return valueList;
  }

  public static <T> Set<T> convertCSVToSet(String valueCSV, Class<T> clazz) {

    if (StringUtils.isBlank(valueCSV)) {
      return Sets.newHashSetWithExpectedSize(0);
    }

    if (clazz == null) {
      throw new IllegalArgumentException("clazz can not be null");
    }

    String[] valueStrAry = valueCSV.split(",");
    Set<T> valueList = Sets.newHashSetWithExpectedSize(valueStrAry.length);
    for (String valueStr : valueStrAry) {
      if (StringUtils.isBlank(valueStr)) {
        continue;
      }

      String value = StringUtils.trim(valueStr);
      Object parsed = switch (clazz.getSimpleName()) {
        case "Long" -> Long.valueOf(value);
        case "Integer" -> Integer.valueOf(value);
        case "Double" -> Double.valueOf(value);
        case "Float" -> Float.valueOf(value);
        case "Short" -> Short.valueOf(value);
        case "Byte" -> Byte.valueOf(value);
        case "Boolean" -> Boolean.valueOf(value);
        default -> value;
      };

      valueList.add(clazz.cast(parsed));
    }

    return valueList;
  }

  public static String convertCollectionToCSV(Iterable<String> valueCollection) {

    StringBuilder valueCSV = new StringBuilder();

    for (String value : valueCollection) {
      valueCSV.append(value).append(',');
    }
    if (!valueCSV.isEmpty()) {
      valueCSV.deleteCharAt(valueCSV.length() - 1);
    }
    return valueCSV.toString();
  }

  public static String convertCollectionToCSVWithSuffix(Iterable<String> valueCollection,
      String suffix) {

    StringBuilder valueCSV = new StringBuilder();

    for (String value : valueCollection) {
      valueCSV.append(value).append(suffix).append(',');
    }
    if (!valueCSV.isEmpty()) {
      valueCSV.deleteCharAt(valueCSV.length() - 1);
    }
    return valueCSV.toString();
  }

  /**
   * 将一个泛型转化为CSV
   * @param valueCollection 需要转化的泛型
   * @return 返回CSV
   */
  public static String convertGenericCollectionToCSV(Iterable<?> valueCollection) {

    StringBuilder valueCSV = new StringBuilder();

    for (Object value : valueCollection) {
      valueCSV.append(value).append(',');
    }
    if (!valueCSV.isEmpty()) {
      valueCSV.deleteCharAt(valueCSV.length() - 1);
    }
    return valueCSV.toString();
  }

  public static String convertLongCollectionToCSV(Iterable<Long> valueCollection) {

    StringBuilder valueCSV = new StringBuilder();

    for (Long value : valueCollection) {
      valueCSV.append(value).append(',');
    }
    if (!valueCSV.isEmpty()) {
      valueCSV.deleteCharAt(valueCSV.length() - 1);
    }
    return valueCSV.toString();
  }

  /**
   * 导出CSV时，字段内容由`包裹，如果字段内容有` 将`转义为\`，有\r\n转义为\\r\\n
   * @param fields 字段集合
   */
  public static String spliceRowData(String... fields) {
    StringBuilder valueStr = new StringBuilder();

    for (String field : fields) {
      if (field == null) {
        field = "";
      }

      valueStr.append("`").append(StringUtils.replaceEach(field, new String[]{"`", "\r", "\n"},
          new String[]{"\\`", "\\r", "\\n"})).append("`,");
    }

    if (!valueStr.isEmpty()) {
      valueStr.setCharAt(valueStr.length() - 1, '\n');
    }

    return valueStr.toString();
  }

  /**
   */
  public static List<String> splitRowData(String line) {
    return splitRowData(line, SPLIT_PATTERN);
  }

  /**
   * 导入CSV时，行转义（避免行数据较复杂时，栈内存不足问题）
   */
  public static List<String> splitRowDataBySimplify(String line) {
    return splitRowData(line, SPLIT_SIMPLIFY_PATTERN);
  }

  /**
   * @param pattern 分隔正则语法
   */
  public static List<String> splitRowData(String line, Pattern pattern) {
    List<String> fieldList = Lists.newArrayListWithCapacity(Constants.COL_DEFAULT_SIZE);

    if (StringUtils.isBlank(line)) {
      return fieldList;
    }

    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      String fieldContext = StringUtils
          .substringBeforeLast(StringUtils.substringAfter(matcher.group(), "`"), "`");
      fieldList.add(StringUtils.replaceEach(fieldContext, new String[]{"\\`", "\\r", "\\n"},
          new String[]{"`", "\r", "\n"}));
    }

    return fieldList;
  }
}
