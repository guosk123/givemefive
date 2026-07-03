package com.givemefive.gmfcontroller.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.IllegalFormatException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.givemefive.gmfcontroller.common.Constants;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class UnitConversionUtils {

  private static final int DEFAULT_DECIMAL_PLACES = 2;

  private static final String[] CAPACITY_UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "PB"};
  private static final String[] SPEED_UNITS = new String[]{"bps", "Kbps", "Mbps", "Gbps", "Tbps",
      "Pbps"};

  /**
   * 将大小格式化，取合适的单位
   * @param byteSize 字节大小
   * @return 格式化后的大小，如：10.11KB
   */
  public static String formatSizeWithUnit(long byteSize) {
    return formatSizeWithUnit(byteSize, Constants.BLOCK_DEFAULT_SIZE);
  }

  public static String formatSizeWithUnit(long byteSize, int blockSize) {
    return formatSizeWithUnit(byteSize, blockSize, DEFAULT_DECIMAL_PLACES);
  }

  public static String formatSizeWithUnit(long byteSize, int blockSize, int decimalPlaces) {
    Tuple2<BigDecimal, String> formatResult = formatSize(byteSize, blockSize, decimalPlaces);
    return formatResult.getT1() + formatResult.getT2();
  }

  public static Tuple2<BigDecimal, String> formatSize(long byteSize, int blockSize,
      int decimalPlaces) {
    if (byteSize <= 0) {
      BigDecimal zero = new BigDecimal("0").setScale(decimalPlaces, RoundingMode.HALF_UP);
      return Tuples.of(zero, CAPACITY_UNITS[0]);
    }

    int digitGroups = (int) (Math.log10(byteSize) / Math.log10(blockSize));
    BigDecimal result = new BigDecimal(byteSize / Math.pow(blockSize, digitGroups))
        .setScale(decimalPlaces, RoundingMode.HALF_UP);
    return Tuples.of(result, CAPACITY_UNITS[digitGroups]);
  }

  /**
   * 将速率格式化，取合适的单位
   * @param byteps 字节速率
   */
  public static String formatSpeedWithUnit(double byteps) {
    return formatSpeedWithUnit(byteps, Constants.BLOCK_DEFAULT_SIZE);
  }

  public static String formatSpeedWithUnit(double byteps, int blockSize) {
    return formatSpeedWithUnit(byteps, blockSize, DEFAULT_DECIMAL_PLACES);
  }

  public static String formatSpeedWithUnit(double byteps, int blockSize, int decimalPlaces) {
    Tuple2<BigDecimal, String> formatResult = formatSpeed(byteps, blockSize, decimalPlaces);
    return formatResult.getT1() + formatResult.getT2();
  }

  public static Tuple2<BigDecimal, String> formatSpeed(double byteps, int blockSize,
      int decimalPlaces) {
    if (byteps <= 0) {
      BigDecimal zero = new BigDecimal("0").setScale(decimalPlaces, RoundingMode.HALF_UP);
      return Tuples.of(zero, SPEED_UNITS[0]);
    }

    double bitSize = byteps * Constants.BYTE_BITS;
    int digitGroups = (int) (Math.log10(bitSize) / Math.log10(blockSize));
    BigDecimal result = new BigDecimal(bitSize / Math.pow(blockSize, digitGroups))
        .setScale(decimalPlaces, RoundingMode.HALF_UP);

    return Tuples.of(result, SPEED_UNITS[digitGroups]);
  }

  /**
   * 根据传入的总字节数和时间间隔，计算速率，并取合适的单位
   * @param byteSize 总字节数
   * @param interval 默认单位：秒
   */
  public static String formatSpeedWithUnit(long byteSize, long interval) {
    return formatSpeedWithUnit(byteSize, interval, Constants.BLOCK_DEFAULT_SIZE);
  }

  public static String formatSpeedWithUnit(long byteSize, long interval, int blockSize) {
    return formatSpeedWithUnit(byteSize, interval, TimeUnit.SECONDS, blockSize,
        DEFAULT_DECIMAL_PLACES);
  }

  public static String formatSpeedWithUnit(long byteSize, long interval, TimeUnit timeUnit,
      int blockSize, int decimalPlaces) {
    Tuple2<BigDecimal,
        String> formatResult = formatSpeed(byteSize, interval, timeUnit, blockSize, decimalPlaces);
    return formatResult.getT1() + formatResult.getT2();
  }

  public static Tuple2<BigDecimal, String> formatSpeed(long byteSize, long interval,
      TimeUnit timeUnit, int blockSize, int decimalPlaces) {
    if (byteSize <= 0) {
      BigDecimal zero = new BigDecimal("0").setScale(decimalPlaces, RoundingMode.HALF_UP);
      return Tuples.of(zero, SPEED_UNITS[0]);
    }

    double bitSize = byteSize * Constants.BYTE_BITS;
    double totalInterval = timeUnit.toSeconds(interval);
    int digitGroups = (int) (Math.log10(bitSize) / Math.log10(blockSize));
    BigDecimal result = new BigDecimal(bitSize / totalInterval / Math.pow(blockSize, digitGroups))
        .setScale(decimalPlaces, RoundingMode.HALF_UP);

    return Tuples.of(result, SPEED_UNITS[digitGroups]);
  }

  /**
   * 将毫秒数格式化，取合适的单位
   * @param time 时长，单位：毫秒
   */
  public static String formatTime(long time) {
    Duration duration = Duration.of(time, TimeUnit.MILLISECONDS.toChronoUnit());

    StringBuilder timeStr = new StringBuilder();
    if (duration.toDaysPart() > 0) {
      timeStr.append(duration.toDaysPart()).append("d ");
    }
    if (duration.toHoursPart() > 0) {
      timeStr.append(duration.toHoursPart()).append("h ");
    }
    if (duration.toMinutesPart() > 0) {
      timeStr.append(duration.toMinutesPart()).append("m ");
    }
    if (duration.toSecondsPart() > 0) {
      timeStr.append(duration.toSecondsPart()).append("s ");
    }
    if (duration.toMillisPart() > 0) {
      timeStr.append(duration.toMillisPart()).append("ms ");
    } else if (timeStr.isEmpty()) {
      timeStr.append("0ms");
    }

    return timeStr.toString();
  }

  public static String converseCapacity(String capacityStr) {
    String valueStr;
    String unit;
    if (StringUtils.containsIgnoreCase(capacityStr, "PB")) {
      valueStr = StringUtils.remove(capacityStr, "PB");
      unit = "PB";
    } else if (StringUtils.containsIgnoreCase(capacityStr, "TB")) {
      valueStr = StringUtils.remove(capacityStr, "TB");
      unit = "TB";
    } else if (StringUtils.containsIgnoreCase(capacityStr, "GB")) {
      valueStr = StringUtils.remove(capacityStr, "GB");
      unit = "GB";
    } else if (StringUtils.containsIgnoreCase(capacityStr, "MB")) {
      valueStr = StringUtils.remove(capacityStr, "MB");
      unit = "MB";
    } else {
      return "";
    }
    try {
      double valueDouble = Double.parseDouble(StringUtils.trim(valueStr));
      double resultDouble = converseCapacity(unit, valueDouble);
      return String.format("%.3f %s", resultDouble, unit);
    } catch (NullPointerException | NumberFormatException | IllegalFormatException e) {
      return "";
    }
  }

  private static double converseCapacity(String unit, double value) {
    return switch (unit) {
      case ("PB") -> value * 1024 * 1024 * 1024 * 1024 * 1024 / 1000 / 1000 / 1000 / 1000 / 1000;
      case ("TB") -> value * 1024 * 1024 * 1024 * 1024 / 1000 / 1000 / 1000 / 1000;
      case ("GB") -> value * 1024 * 1024 * 1024 / 1000 / 1000 / 1000;
      case ("MB") -> value * 1024 * 1024 / 1000 / 1000;
      default -> 0;
    };
  }
}
