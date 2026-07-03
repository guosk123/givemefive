package com.givemefive.gmfcontroller.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ClosedByInterruptException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelGenerateException;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.givemefive.gmfcontroller.common.Constants;
import com.givemefive.gmfcontroller.common.exception.BusinessException;
import com.givemefive.gmfcontroller.common.exception.ErrorCode;

import jakarta.annotation.Nullable;

/**
 * @author guosk
 *
 */
public final class ExportUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExportUtils.class);

  private ExportUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 数据导出
   * @param titles 标题
   * @param file 目标文件
   * @throws IOException
   */
  public static int export(List<String> titles, FetchData fetchData, File file, String fileType,
      @Nullable OutputStream out) throws IOException {
    // 判断导出格式是否合法
    if (!StringUtils.equalsAnyIgnoreCase(fileType, Constants.EXPORT_FILE_TYPE_CSV,
        Constants.EXPORT_FILE_TYPE_EXCEL)) {
      LOGGER.warn("export, Unsupported export file style: {}", fileType);
      throw new UnsupportedOperationException("涓嶆敮鎸佺殑瀵煎嚭鏍峰紡");
    }
    ExcelTypeEnum excelType = StringUtils.equals(fileType, Constants.EXPORT_FILE_TYPE_EXCEL)
        ? ExcelTypeEnum.XLSX
        : ExcelTypeEnum.CSV;

    int totalExport = 0;

    List<List<String>> header = titles.stream().map(Lists::newArrayList)
        .collect(Collectors.toList());
    try (ExcelWriter excelWriter = EasyExcel.write(file).head(header).excelType(excelType)
        .writeExcelOnException(true).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet("dataset").build();

      while (fetchData.hasNext()) {
        if (Thread.interrupted()) {
          // 线程中断处理
          excelWriter.finish();
          if (out != null) {
            FileUtils.copyFile(file, out);
            FileUtils.deleteQuietly(file);
          }
          throw new ClosedByInterruptException();
        }

        List<List<String>> dataset = fetchData.next();
        if (CollectionUtils.isEmpty(dataset)) {
          excelWriter.write(dataset, writeSheet);
          break;
        }

        if (excelType == ExcelTypeEnum.CSV) {
          dataset = formatCsvDataset(dataset);
        }

        boolean needTruncate = dataset.stream()
            .anyMatch(row -> row.stream().anyMatch(cell -> StringUtils.isNotBlank(cell)
                && cell.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()));
        if (needTruncate) {
          dataset = dataset.stream()
              .map(innerDataset -> innerDataset.stream()
                  .map(cell -> StringUtils.substring(StringUtils.defaultIfBlank(cell, ""), 0,
                      SpreadsheetVersion.EXCEL2007.getMaxTextLength()))
                  .toList())
              .toList();
        }

        excelWriter.write(dataset, writeSheet);
        totalExport += dataset.size();
      }
    } catch (ExcelGenerateException e) {
      LOGGER.warn("export failed, delete tmp file: {}", file.getAbsolutePath(), e);
      FileUtils.deleteQuietly(file);
      throw new BusinessException(ErrorCode.COMMON_BASE_COMMAND_RUN_ERROR, "鏈嶅姟鍣ㄨ繍琛屽紓甯革紝瀵煎嚭澶辫触");
    }

    if (out != null) {
      FileUtils.copyFile(file, out);
      FileUtils.deleteQuietly(file);
    }

    return totalExport;
  }

  public static void exportByDataset(List<String> titles, List<List<String>> dataset, File file,
      String fileType, @Nullable OutputStream out) throws IOException {
    // 判断导出格式是否合法
    if (!StringUtils.equalsAnyIgnoreCase(fileType, Constants.EXPORT_FILE_TYPE_CSV,
        Constants.EXPORT_FILE_TYPE_EXCEL)) {
      LOGGER.warn("exportByDataset, Unsupported export file style: {}", fileType);
      throw new UnsupportedOperationException("涓嶆敮鎸佺殑瀵煎嚭鏍峰紡");
    }
    ExcelTypeEnum excelType = StringUtils.equals(fileType, Constants.EXPORT_FILE_TYPE_EXCEL)
        ? ExcelTypeEnum.XLSX
        : ExcelTypeEnum.CSV;

    List<List<String>> header = titles.stream().map(Lists::newArrayList)
        .collect(Collectors.toList());
    try (ExcelWriter excelWriter = EasyExcel.write(file).head(header).excelType(excelType)
        .writeExcelOnException(true).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet("dataset").build();

      if (CollectionUtils.isNotEmpty(dataset) && excelType == ExcelTypeEnum.CSV) {
        dataset = formatCsvDataset(dataset);
      }
      excelWriter.write(dataset, writeSheet);
    } catch (ExcelGenerateException e) {
      if (e.getCause().getCause() != null
          && e.getCause().getCause() instanceof ClosedByInterruptException) {
        LOGGER.warn("export interrupt");
        FileUtils.deleteQuietly(file);
        throw new ClosedByInterruptException();
      } else {
        LOGGER.warn("export failed, delete tmp file: {}", file.getAbsolutePath(), e);
        FileUtils.deleteQuietly(file);
        throw new BusinessException(ErrorCode.COMMON_BASE_COMMAND_RUN_ERROR, "鏈嶅姟鍣ㄨ繍琛屽紓甯革紝瀵煎嚭澶辫触");
      }
    }

    if (out != null) {
      FileUtils.copyFile(file, out);
      FileUtils.deleteQuietly(file);
    }
  }

  private static List<List<String>> formatCsvDataset(List<List<String>> dataset) {
    return dataset.stream().map(row -> row.stream()
        .map(column -> "`"
            + StringUtils.replace(StringUtils.replace(column, "`", "\\`"), "\r\n", "\\r\\n") + "`")
        .toList()).toList();
  }

  /**
   * 导入数据
   * @param file web上传文件
   * @throws IOException 异常
   */
  public static void importData(MultipartFile file, List<String> titles,
      NoModelDataListener listener) throws IOException {
    importData(file.getInputStream(), titles, listener);
  }

  /**
   * 导入数据
   */
  public static void importData(File file, List<String> titles, NoModelDataListener listener) {
    EasyExcel.read(file, listener).sheet()
        .head(titles.stream().map(Lists::newArrayList).collect(Collectors.toList())).doRead();
  }

  /**
   * 导入数据
   */
  public static void importData(InputStream inputStream, List<String> titles,
      NoModelDataListener listener) {
    EasyExcel.read(inputStream, listener).sheet()
        .head(titles.stream().map(Lists::newArrayList).collect(Collectors.toList())).doRead();
  }

  /**
   */
  public interface FetchData extends Iterator<List<List<String>>> {
  }

  /**
   * 数据迭代器demo
   */
  public final class FetchDataImpl implements FetchData {

    private int offset = 0;
    // 单次读取数据最大量
    private int batchSize = 100;

    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return offset % batchSize == 0;
    }

    /**
     * @see java.util.Iterator#next()
     */
    @Override
    public List<List<String>> next() {
      // limit 100 offset 0
      int currentSize = 80;
      // 定位到下次的游标
      offset += currentSize;

      return null;
    }
  }

  /**
   * 数据监听器，用于解析xlsx文件
   */
  public abstract static class NoModelDataListener
      extends AnalysisEventListener<Map<Integer, String>> {

    private static final int BATCH_COUNT = 1000;
    private List<List<String>> cachedDataList = Lists.newArrayListWithExpectedSize(BATCH_COUNT);

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("parse one row: {}", JsonHelper.serialize(data));
      }

      cachedDataList.add(Lists.newArrayList(data.values()));
      if (cachedDataList.size() >= BATCH_COUNT) {
        processData(cachedDataList);
        cachedDataList = Lists.newArrayListWithExpectedSize(BATCH_COUNT);
      }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
      if (CollectionUtils.isNotEmpty(cachedDataList)) {
        processData(cachedDataList);
        cachedDataList = null;
      }
    }

    /**
     * 批量处理多行数据
     */
    public abstract void processData(List<List<String>> rows);
  }

}
