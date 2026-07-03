package com.givemefive.gmfcontroller.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public final class ZipUtils {

  private ZipUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * @throws IOException 
   */
  public static void zipFiles(File[] sourceFiles, File targetZipFile) throws IOException {
    try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(targetZipFile))) {
      for (File file : sourceFiles) {
        if (!file.exists()) {
          throw new FileNotFoundException(file.getAbsolutePath());
        }

        try {
          addEntry("", file, outputStream);
        } catch (Exception ignored) {
        }
      }
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  /**
   * @param zipFiles zip文件集合
   * @param tile 是否平铺压缩包内文件
   * @throws IOException
   */
  public static void unZipFiles(File[] zipFiles, String rootDirectoryPath, boolean tile)
      throws IOException {
    File rootDirectory = Paths.get(rootDirectoryPath).toFile();
    if (rootDirectory.exists()) {
      FileUtils.deleteQuietly(rootDirectory);
    }
    FileUtils.forceMkdir(rootDirectory);

    for (File zipFile : zipFiles) {
      if (!zipFile.exists()) {
        throw new FileNotFoundException(zipFile.getAbsolutePath());
      }

      try (InputStream inputStream = new FileInputStream(zipFile);
          ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {

        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
          File entryFile = Paths.get(rootDirectoryPath, zipEntry.getName()).toFile();

          if (zipEntry.isDirectory()) {
            if (!tile && !entryFile.exists()) {
              FileUtils.forceMkdir(entryFile);
            }

            continue;
          }

          String targetPath = entryFile.getAbsolutePath();
          if (tile) {
            targetPath = Paths.get(rootDirectoryPath, entryFile.getName()).toString();
          }

          FileUtils.copyToFile(zipInputStream, Paths.get(targetPath).toFile());
          zipInputStream.closeEntry();
        }
      }
    }
  }

  /**
   * @param source
   * @param outputstream
   * @throws IOException
   */
  private static void addEntry(String base, File source, ZipOutputStream outputstream)
      throws IOException {
    FileInputStream is = null;
    try {
      String entry = base + source.getName();
      if (source.isDirectory()) {
        for (File file : Objects.requireNonNull(source.listFiles())) {
          addEntry(entry + File.separator, file, outputstream);
        }
      } else {
        is = FileUtils.openInputStream(source);
        outputstream.putNextEntry(new ZipEntry(entry));

        int len;
        byte[] buffer = new byte[10 * 1024];
        while ((len = is.read(buffer)) > 0) {
          outputstream.write(buffer, 0, len);
          outputstream.flush();
        }
        outputstream.closeEntry();
      }

    } finally {
      if (is != null) {
        is.close();
      }
    }
  }

}
