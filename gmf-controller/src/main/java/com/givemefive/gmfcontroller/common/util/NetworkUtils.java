package com.givemefive.gmfcontroller.common.util;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.hc.core5.net.InetAddressUtils;

import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;
import com.givemefive.gmfcontroller.common.Constants;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * 
 * @author guosk
 *
 */
public final class NetworkUtils {

  private static final String IPV4_ZERO = "0.0.0.0";

  private static final String IPV4_TO_IPV6_PREFIX = "::ffff:";

  private static final Pattern IPV4_PATTERN = Pattern
      .compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)"
          + "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){2}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

  private static final Pattern DOMAIN_PATTERN = Pattern.compile(
      "^(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]$",
      Pattern.CASE_INSENSITIVE);

  private static final int MAX_MASK_IPV4 = 32;
  private static final int MAX_MASK_IPV6 = 128;
  private static final int IPV4_IPV6_DIGIT_DIFF = 128 - 32;

  private NetworkUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 判断是否为IP地址
   * @return 验证结果
   */
  public static boolean isInetAddress(String ipAddress) {
    return isInetAddress(ipAddress, IpVersion.ALL);
  }

  public static boolean isDomain(String domain) {
    return DOMAIN_PATTERN.matcher(domain).matches();
  }

  /**
   * 判断是否为IP地址
   * @param version IP版本
   * @return 验证结果
   */
  public static boolean isInetAddress(String ipAddress, IpVersion version) {
    if (StringUtils.isBlank(ipAddress)) {
      return false;
    }

    if (version == IpVersion.ALL) {
      return InetAddresses.isInetAddress(ipAddress);
    } else if (version == IpVersion.V4) {
      return IPV4_PATTERN.matcher(ipAddress).matches();
    } else if (version == IpVersion.V6) {
      return InetAddresses.isInetAddress(ipAddress) && !(InetAddressUtils.isIPv4Address(ipAddress)
          || StringUtils.equals(ipAddress, IPV4_ZERO));
    }
    return false;
  }

  /**
   * 判断是否为端口号
   * @return 验证结果
   */
  public static boolean isInetAddressPort(String port) {
    boolean result = false;
    try {
      int portNum = Integer.parseInt(port);
      result = portNum >= 0 && portNum <= 65535;
    } catch (NumberFormatException ignored) {
    }

    return result;
  }

  /**
   * @param ipAddress ipv4地址
   */
  public static long ip2Long(String ipAddress) {

    long result = 0;

    if (StringUtils.isBlank(ipAddress)) {
      return result;
    }

    try {
      String[] ipAddressInArray = ipAddress.split("\\.");
      for (int i = Constants.IPV4_ADDRESS_BYTES; i > 0; i--) {
        long ip = Long.parseLong(ipAddressInArray[Constants.IPV4_ADDRESS_BYTES - i]);
        result |= ip << ((i - 1) * Constants.BYTE_BITS);
      }
    } catch (NumberFormatException e) {
      result = 0;
    }

    return result;
  }

  /**
   * 数值转换为ipv4地址
   */
  public static String long2Ip(long i) {
    return ((i >> 3 * Constants.BYTE_BITS) & 0xFF) + "." + ((i >> 2 * Constants.BYTE_BITS) & 0xFF)
        + "." + ((i >> Constants.BYTE_BITS) & 0xFF) + "." + (i & 0xFF);
  }

  /**
   * @param ipAddress 单IP/CIDR/IP范围
   */
  public static Tuple2<Long, Long> ip2Range(String ipAddress) {
    if (StringUtils.contains(ipAddress, "-")) {
      String[] ipRange = StringUtils.split(ipAddress, "-");
      return Tuples.of(ip2Long(ipRange[0]), ip2Long(ipRange[1]));
    } else if (StringUtils.contains(ipAddress, "/")) {
      SubnetUtils utils = new SubnetUtils(ipAddress);
      utils.setInclusiveHostCount(true);
      return Tuples.of(ip2Long(utils.getInfo().getLowAddress()),
          ip2Long(utils.getInfo().getHighAddress()));
    } else {
      return Tuples.of(ip2Long(ipAddress), ip2Long(ipAddress));
    }
  }

  /**
   * @param ipAddress IP地址范围
   * @return 验证结果
   */
  public static boolean isIpRange(String ipAddress) {
    return isIpRange(ipAddress, IpVersion.ALL);
  }

  /**
   * @param ipAddress IP地址范围
   * @param version IP版本
   * @return 验证结果
   */
  public static boolean isIpRange(String ipAddress, IpVersion version) {
    if (StringUtils.contains(ipAddress, "-")) {
      String[] ipRange = StringUtils.split(ipAddress, "-");
      if (ipRange.length != 2) {
        return false;
      }

      String leftIp = ipRange[0];
      String rightIp = ipRange[1];

      boolean validIpAddress;
      if (version == IpVersion.ALL) {
        validIpAddress = (isInetAddress(leftIp, IpVersion.V4)
            && isInetAddress(rightIp, IpVersion.V4))
            || (isInetAddress(leftIp, IpVersion.V6) && isInetAddress(rightIp, IpVersion.V6));
      } else {
        validIpAddress = isInetAddress(leftIp, version) && isInetAddress(rightIp, version);
      }
      if (!validIpAddress) {
        return false;
      }

      try {
        IPAddress leftIpAddress = new IPAddressString(leftIp).toAddress();
        IPAddress rightIpAddress = new IPAddressString(rightIp).toAddress();
        return leftIpAddress.compareTo(rightIpAddress) < 0;
      } catch (AddressStringException e) {
        return false;
      }
    }

    return false;
  }

  /**
   * 判断是否为CIDR
   * @param cidr CIDR地址
   * @return 验证结果
   */
  public static boolean isCidr(String cidr) {
    return isCidr(cidr, IpVersion.ALL);
  }

  /**
   * 判断是否为CIDR
   * @param cidr CIDR地址
   * @param version IP版本
   * @return 验证结果
   */
  public static boolean isCidr(String cidr, IpVersion version) {
    boolean result = false;
    if (StringUtils.contains(cidr, "/")) {
      int index = cidr.indexOf("/");
      String addressPart = cidr.substring(0, index);
      int networkPart;
      try {
        networkPart = Integer.parseInt(cidr.substring(index + 1));
      } catch (NumberFormatException e) {
        return result;
      }

      if (isInetAddress(addressPart)) {
        if (InetAddressUtils.isIPv4Address(addressPart)
            || StringUtils.equals(addressPart, IPV4_ZERO)) {
          if (networkPart >= 0 && networkPart <= 32) {
            result = (version == IpVersion.V4 || version == IpVersion.ALL);
          }
        } else {
          if (networkPart >= 0 && networkPart <= 128) {
            result = (version == IpVersion.V6 || version == IpVersion.ALL);
          }
        }
      }
    }

    return result;
  }

  /**
   * 根据掩码位数获得掩码
   * 
   * @param depth 掩码位数
   */
  public static int getMaskByDepth(int depth) {
    return 0x80000000 >> (depth - 1);
  }

  /**
   */
  public static int getNetMask(String netmarks) {
    int count = 0;
    String[] ipList = netmarks.split("\\.");
    for (String partMask : ipList) {
      String binary = Integer.toBinaryString(Integer.parseInt(partMask));
      String effective = StringUtils.substringBefore(binary, "0");
      count += effective.length();
      if (!StringUtils.equals(binary, effective)) {
        break;
      }
    }
    return count;
  }

  /**
   * ipv6转ipv4
   * @param ip ipv6地址
   * @return 转换结果
   */
  public static String ipv6ToIpv4(String ip) {
    if (StringUtils.isBlank(ip)) {
      return "";
    }

    return StringUtils.startsWith(ip, IPV4_TO_IPV6_PREFIX)
        ? StringUtils.substringAfter(ip, IPV4_TO_IPV6_PREFIX)
        : ip;
  }

  /**
   * ipv4转ipv6
   * @param ip ipv4地址
   * @return 转换结果
   */
  public static String ipv4ToIpv6(String ip) {
    if (StringUtils.isBlank(ip)) {
      return "";
    }
    if (!isInetAddress(ip, IpVersion.V4)) {
      return ip;
    }

    return IPV4_TO_IPV6_PREFIX + ip;
  }

  /**
   * ipv4掩码转换为ipv6掩码
   * @param depth ipv4掩码位数
   * @return ipv6掩码位数
   */
  public static int ipv4MaskToIpv6Mask(int depth) {
    if (depth >= 32 || depth <= 0) {
      return depth;
    }

    return depth + IPV4_IPV6_DIGIT_DIFF;
  }

  /**
   * ipv4Cidr转换为ipv6Cidr
   * @param cidr ipv4Cidr
   * @return ipv6Cidr
   */
  public static String ipv4CidrToIpv6Cidr(String cidr) {
    if (!isCidr(cidr) || isCidr(cidr, IpVersion.V6)) {
      return cidr;
    }

    int index = cidr.indexOf("/");
    return ipv4ToIpv6(cidr.substring(0, index)) + "/"
        + ipv4MaskToIpv6Mask(Integer.parseInt(cidr.substring(index + 1)));
  }

  /**
   * @param ipStr ipv6地址
   * @return 展开的ipv6地址
   */
  public static String[] expandIpv6(String ipStr) {
    String[] parts = ipStr.split(":");

    String[] expanded = new String[8];
    int ptr = 0;
    for (int i = 0; i < 8; i++) {
      if (ptr < parts.length && !parts[ptr].isEmpty()) {
        expanded[i] = parts[ptr++];
      } else {
        expanded[i] = "0";
      }
    }
    return expanded;
  }

  public static String ipUriFormat(String ip) {
    if (StringUtils.isBlank(ip)) {
      return "";
    }

    return isInetAddress(ip, IpVersion.V6) ? "[" + ip + "]" : ip;
  }

  /**
   * 获取IP类型
   * @param ip ip地址
   * @return 类型，非IP类型则为null
   */
  public static IpType getIpType(String ip) {
    if (StringUtils.isBlank(ip)) {
      return null;
    }

    return isInetAddress(ip) ? IpType.IP
        : isCidr(ip) ? IpType.CIDR : isIpRange(ip) ? IpType.RANGE : null;
  }

  /**
   * 判断IP是否包含在网段内
   * @return 验证结果，true表示包含，false表示不包含
   */
  public static boolean isContains(String ip, String ipRange) {
    if (StringUtils.isAnyBlank(ip, ipRange)) {
      return false;
    }

    IpType ipType = getIpType(ip);
    IpType ipRangeType = getIpType(ipRange);
    if (ipType == null || ipRangeType == null) {
      return false;
    }

    IPAddressSeqRange ipRangeSeqRange = null;
    IPAddress ipAddress = null;
    if (ipRangeType == IpType.IP || ipRangeType == IpType.CIDR) {
      ipAddress = new IPAddressString(ipRange).getAddress();
    } else {
      String[] ipRanges = StringUtils.split(ipRange, "-");
      IPAddress leftIp = new IPAddressString(ipRanges[0]).getAddress();
      IPAddress rightIp = new IPAddressString(ipRanges[1]).getAddress();
      ipRangeSeqRange = leftIp.spanWithRange(rightIp);
    }

    if (ipRangeSeqRange == null && ipAddress == null) {
      return false;
    }

    if (ipType == IpType.IP || ipType == IpType.CIDR) {
      IPAddress sourceIp = new IPAddressString(ip).getAddress();
      return ipRangeSeqRange != null ? ipRangeSeqRange.overlaps(sourceIp)
          : ipAddress.prefixContains(sourceIp) || sourceIp.prefixContains(ipAddress);
    } else {
      String[] sourceIpRanges = StringUtils.split(ip, "-");
      IPAddress leftIp = new IPAddressString(sourceIpRanges[0]).getAddress();
      IPAddress rightIp = new IPAddressString(sourceIpRanges[1]).getAddress();
      IPAddressSeqRange sourceIpRangeSeqRange = leftIp.spanWithRange(rightIp);
      return ipRangeSeqRange != null ? ipRangeSeqRange.overlaps(sourceIpRangeSeqRange)
          : ipAddress.contains(sourceIpRangeSeqRange) || sourceIpRangeSeqRange.contains(ipAddress);
    }
  }

  /**
   * 判断IP是否包含在一组网段内
   * @return 验证结果，true表示包含，false表示不包含
   */
  public static boolean isContainsAny(String ip, List<String> ipRanges) {
    if (StringUtils.isBlank(ip) || CollectionUtils.isEmpty(ipRanges)) {
      return false;
    }

    NavigableSet<Interval> intervals = new TreeSet<>();
    ipRanges.forEach(ipRange -> intervals.add(ip2Interval(ipRange)));

    return StringUtils.isNotBlank(hasOverlap(ip2Interval(ip), intervals));
  }

  /**
   * 判断一组IP是否包含在一组网段内
   * @return 验证结果，true表示包含，false表示不包含
   */
  public static boolean isContainsAny(List<String> ipList, List<String> ipRanges) {
    if (CollectionUtils.isEmpty(ipRanges) || CollectionUtils.isEmpty(ipList)) {
      return false;
    }

    NavigableSet<Interval> intervals = new TreeSet<>();
    ipRanges.forEach(ipRange -> intervals.add(ip2Interval(ipRange)));

    if (CollectionUtils.isEmpty(intervals)) {
      return false;
    }

    for (String ipItem : ipList) {
      if (StringUtils.isNotBlank(hasOverlap(ip2Interval(ipItem), intervals))) {
        return true;
      }
    }

    return false;
  }

  /**
   * @return 命中的IP范围
   */
  public static String getContainsIpRange(String ip, List<String> ipRanges) {
    if (StringUtils.isBlank(ip) || CollectionUtils.isEmpty(ipRanges)) {
      return "";
    }

    NavigableSet<Interval> intervals = new TreeSet<>();
    ipRanges.forEach(ipRange -> intervals.add(ip2Interval(ipRange)));

    return hasOverlap(ip2Interval(ip), intervals);
  }

  /**
   * @return 命中的IP范围
   */
  public static Map<String, String> getContainsIpRange(List<String> ipList, List<String> ipRanges) {
    if (CollectionUtils.isEmpty(ipRanges) || CollectionUtils.isEmpty(ipList)) {
      return Maps.newHashMapWithExpectedSize(0);
    }

    // 待匹配IP
    List<Interval> ipSegments = ipList.stream().map(NetworkUtils::ip2Interval).toList();

    // 匹配范围
    NavigableSet<Interval> intervals = new TreeSet<>();
    ipRanges.forEach(ipRange -> intervals.add(ip2Interval(ipRange)));
    return getContainsIpRange(ipSegments, intervals);
  }

  /**
   * 获取包含目标IP的网段（该方法适用于调用方的匹配IP范围不断增大，避免每次都重新将旧数据转换为Interval所带来的浪费）
   * @return 命中的IP范围
   */
  public static Map<String, String> getContainsIpRange(List<Interval> ipList,
      NavigableSet<Interval> ipRanges) {
    if (CollectionUtils.isEmpty(ipRanges) || CollectionUtils.isEmpty(ipList)) {
      return Maps.newHashMapWithExpectedSize(0);
    }

    Map<String, String> result = Maps.newHashMapWithExpectedSize(ipList.size());
    for (Interval ipItem : ipList) {
      result.put(ipItem.originalIp(), hasOverlap(ipItem, ipRanges));

      ipRanges.add(ipItem);
    }

    return result;
  }

  /**
   * ip字符串转区间
   * @return Interval(startIpNumber, endIpNumber, ipStr)
   */
  public static Interval ip2Interval(String ipStr) {
    IpType ipType = getIpType(ipStr);
    if (ipType == null) {
      return new Interval(BigInteger.ZERO, BigInteger.ZERO, ipStr);
    }

    if (ipType == IpType.CIDR) {
      String[] cidr = ipStr.split("/");

      boolean isIpv4 = isInetAddress(cidr[0], IpVersion.V4);
      BigInteger base = ip2BigInteger(cidr[0], isIpv4);
      int prefixLen = Integer.parseInt(cidr[1]);

      // 计算网络地址和广播地址
      BigInteger mask = BigInteger.ONE.shiftLeft(prefixLen).subtract(BigInteger.ONE)
          .shiftLeft((isIpv4 ? MAX_MASK_IPV4 : MAX_MASK_IPV6) - prefixLen);
      BigInteger start = base.and(mask);

      BigInteger hostMask = BigInteger.ONE
          .shiftLeft((isIpv4 ? MAX_MASK_IPV4 : MAX_MASK_IPV6) - prefixLen).subtract(BigInteger.ONE);
      BigInteger end = start.or(hostMask);
      return new Interval(start, end, ipStr);
    } else if (ipType == IpType.RANGE) {
      String[] range = ipStr.split("-");

      boolean isIpv4 = isInetAddress(range[0], IpVersion.V4);
      BigInteger start = ip2BigInteger(range[0], isIpv4);
      BigInteger end = ip2BigInteger(range[1], isIpv4);
      return new Interval(start.min(start), start.max(end), ipStr);
    } else {
      BigInteger ipInterval = ip2BigInteger(ipStr, isInetAddress(ipStr, IpVersion.V4));
      return new Interval(ipInterval, ipInterval, ipStr);
    }
  }

  private static BigInteger ip2BigInteger(String ipStr, boolean isIpv4) {
    BigInteger num = BigInteger.ZERO;

    if (isIpv4) {
      for (String part : ipStr.split("\\.")) {
        long val = Long.parseLong(part);
        num = num.shiftLeft(8).add(BigInteger.valueOf(val));
      }
    } else {
      for (String part : expandIpv6(ipStr)) {
        long val = Long.parseLong(part, 16);
        num = num.shiftLeft(16).add(BigInteger.valueOf(val));
      }
    }

    return num;
  }

  private static String hasOverlap(Interval newInterval, NavigableSet<Interval> intervals) {
    if (intervals.isEmpty()) {
      return "";
    }

    // Check intervals that start before newInterval
    Interval floor = intervals.floor(newInterval);
    while (floor != null && floor.end().compareTo(newInterval.start()) >= 0) {
      if (floor.start().compareTo(newInterval.end()) <= 0) {
        return floor.originalIp();
      }
      floor = intervals.lower(floor);
    }

    // Check intervals that start after newInterval
    Interval ceiling = intervals.ceiling(newInterval);
    while (ceiling != null && ceiling.start().compareTo(newInterval.end()) <= 0) {
      if (ceiling.end().compareTo(newInterval.start()) >= 0) {
        return ceiling.originalIp();
      }
      ceiling = intervals.higher(ceiling);
    }

    return "";
  }

  public enum IpType {
    IP,
    CIDR,
    RANGE
  }

  public enum IpVersion {
    V4,
    V6,
    ALL
  }

  public record Interval(BigInteger start, BigInteger end,
      String originalIp) implements Comparable<Interval> {

    @Override
    public int compareTo(Interval o) {

      return this.start.compareTo(o.start);
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Interval interval = (Interval) o;
      return Objects.equals(originalIp, interval.originalIp);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(originalIp);
    }
  }

}
