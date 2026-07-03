package com.givemefive.gmfcontroller.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * 
 * @author guosk
 *
 */
public final class FileUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Runs a regular expression on a file, and returns the first match.
   *
   * @param pattern The regular expression to use.
   * @param filename The path of the filename to match against. Should be absolute.
   * @return The first match found. Null if no matches.
   */
  public static String runRegexOnFile(Pattern pattern, String filename) {
    try {
      final String file = slurp(filename);
      Matcher matcher = pattern.matcher(file);
      matcher.find();
      final String firstMatch = matcher.group(1);
      if (firstMatch != null && firstMatch.length() > 0) {
        return firstMatch;
      }
    } catch (IOException e) {
      LOGGER.warn("run regex failed.", e);
    }
    return null;
  }

  /**
   * Runs a regular expression on a String, and returns the first match.
   *
   * @param pattern The regular expression to use.
   * @param fileContent file String content.
   * @return The first match found. Null if no matches.
   */
  public static String runRegexOnFileContent(Pattern pattern, String fileContent) {
    try {
      Matcher matcher = pattern.matcher(fileContent);
      matcher.find();
      final String firstMatch = matcher.group(1);
      if (firstMatch != null && firstMatch.length() > 0) {
        return firstMatch;
      }
    } catch (Exception e) {
      LOGGER.warn("run regex failed.", e);
    }
    return null;
  }

  /**
   * Given a filename, reads the entire file into a string.
   *
   * @param fileName The path of the filename to read. Should be absolute.
   * @return A string containing the entire contents of the file
   * @throws IOException If there's an IO exception while trying to read the file
   */
  public static String slurp(String fileName) throws IOException {
    StringWriter sw = new StringWriter();
    String line;
    try (InputStream stream = java.nio.file.Files.newInputStream(Paths.get(fileName));
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(stream, StandardCharsets.UTF_8))) {
      while ((line = reader.readLine()) != null) {
        sw.write(line);
        sw.write('\n');
      }
    }
    return sw.toString();
  }

  /**
   * Given a filename, reads the entire file into a byte array.
   *
   * @param fileName The path of the filename to read. Should be absolute.
   * @return A byte array containing the entire contents of the file
   * @throws IOException If there's an IO exception while trying to read the file
   */
  public static byte[] slurpToByteArray(String fileName) throws IOException {
    File fileToRead = new File(fileName);
    byte[] contents = new byte[(int) fileToRead.length()];

    try (InputStream inputStream = java.nio.file.Files.newInputStream(Paths.get(fileName))) {

      int count = 0;
      while ((count = inputStream.read(contents)) > 0) {
        LOGGER.debug("Read {} bytes from inputstream {}.", count, fileName);
      }
      return contents;
    }
  }

  /**
   * 查找文件
   * @return
   */
  public static String findFile(String fileName) {
    Process process = null;
    String fileAbsolutePath = null;

    try {
      ProcessBuilder builder = new ProcessBuilder("sh", "-c",
          "find /* -name \"" + fileName + "\" ");
      builder.redirectErrorStream(true);

      process = builder.start();
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

      // 只取匹配到的最新的文件
      String line = "";
      Map<Long, String> fileMap = Maps.newTreeMap(new MapKeyComparator());
      while ((line = br.readLine()) != null) {
        File file = new File(line);
        fileMap.put(file.lastModified(), line);
      }

      for (Entry<Long, String> entry : fileMap.entrySet()) {
        fileAbsolutePath = entry.getValue();
        break;
      }

    } catch (IOException e) {
      LOGGER.warn("find file {} failed.", fileName, e);
    } finally {
      if (process != null) {
        process.destroy();
      }
    }
    return fileAbsolutePath;
  }

  public static int countLines(String filePath) throws IOException {
    if (!SystemUtils.IS_OS_LINUX) {
      return 0;
    }

    if (!Paths.get(filePath).toFile().exists()) {
      return 0;
    }

    Process process = Runtime.getRuntime().exec(new String[]{"wc", "-l", filePath});
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
      String output = reader.readLine();
      if (output != null) {
        String[] parts = output.trim().split("\\s+");
        return Integer.parseInt(parts[0]);
      }
    }
    throw new IOException("Failed to get line count");
  }

  static class MapKeyComparator implements Comparator<Long> {

    @Override
    public int compare(Long k1, Long k2) {

      return k2.compareTo(k1);
    }
  }

}
