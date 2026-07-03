package com.givemefive.gmfcontroller.common.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author guosk
 * @Date: 2024/08/14
 */
public class PcapUtils {

  private static final int PCAP_FILE_HEADER_LENGTH = 24;
  private static final int MAGIC_NUMBER = 0xa1b2c3d4; // PCAP文件格式魔数
  private static final short PCAP_VERSION_MAJOR = 2; // 主要版本
  private static final short PCAP_VERSION_MINOR = 4; // 次要版本
  private static final int THISZONE = 0;
  private static final int SIGFIGS = 0;
  private static final int MAX_LEN = 65535;
  private static final int LINK_TYPE = 1;


  private static final int PCAPNG_HEADER_BLOCK_LENGTH = 24;
  private static final int PCAPNG_MAGIC_NUMBER = 0x1A2B3C4D; // PCAPNG文件格式魔数
  private static final short PCAPNG_VERSION_MAJOR = 1; // 主要版本
  private static final short PCAPNG_VERSION_MINOR = 0; // 次要版本

  private static final int IDB_BLOCK_TYPE = 0x00000001;
  private static final int IDB_BLOCK_LENGTH = 20;


  /**
   * @param filePath
   */
  public static void createEmptyPcapFile(String filePath) {
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      ByteBuffer headerBuffer = ByteBuffer.allocate(24);
      headerBuffer.putInt(0xa1b2c3d4);
      headerBuffer.putShort(PCAP_VERSION_MAJOR);
      headerBuffer.putShort(PCAP_VERSION_MINOR);
      headerBuffer.putInt(THISZONE);
      headerBuffer.putInt(SIGFIGS);
      headerBuffer.putInt(MAX_LEN);
      headerBuffer.putInt(LINK_TYPE);

      // 写入文件头到PCAP文件
      fos.write(headerBuffer.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * @param filePath
   */
  public static void createEmptyPcapngFile(String filePath) {
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      ByteBuffer headerBlock = ByteBuffer.allocate(PCAPNG_HEADER_BLOCK_LENGTH);
      headerBlock.putInt(PCAPNG_MAGIC_NUMBER);
      headerBlock.putShort(PCAPNG_VERSION_MAJOR);
      headerBlock.putShort(PCAPNG_VERSION_MINOR);
      headerBlock.putInt(0);

      // 写入文件头块到PCAP-NG文件
      fos.write(headerBlock.array());

      ByteBuffer idbBlock = ByteBuffer.allocate(IDB_BLOCK_LENGTH + 12); // 附加12个字节的块头
      idbBlock.putInt(IDB_BLOCK_TYPE);
      idbBlock.putInt(IDB_BLOCK_LENGTH);
      idbBlock.putShort((short) 0); // 具体接口的链接类型（以太网）
      idbBlock.putShort((short) LINK_TYPE); // 数据链路类型
      idbBlock.putInt(0);
      idbBlock.putInt(0);

      // 写入接口描述块到PCAP-NG文件
      fos.write(idbBlock.array());

      // 最后，结束PCAP-NG文件的块
      ByteBuffer endBlock = ByteBuffer.wrap(new byte[]{0, 0, 0, 0});
      fos.write(endBlock.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static final int MAGIC_NUMBERA = 0x1A0A0D0A;

  public static void createPcapngFile(String filePath) {
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      writeSectionHeader(fos);
      writeInterfaceDescription(fos);
      // 写入节结束块
      writeSectionEnd(fos);
      System.out.println("鎴愬姛鍒涘缓鏈夋晥鐨勭┖PCAPNG鏂囦欢: " + filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void writeSectionHeader(FileOutputStream fos) throws IOException {
    ByteBuffer sectionHeader = ByteBuffer.allocate(32);
    sectionHeader.putInt(0x0a0d0d0a);
    sectionHeader.putInt(24); // 块总长度（包括自身的长度）
    sectionHeader.putShort((short) 0x0001); // 主要版本
    sectionHeader.putShort((short) 0x0000); // 次要版本
    sectionHeader.putInt(0x00000000); // 最后更新时间（UNIX时间戳）
    sectionHeader.putInt(0x00000000); // 其他字段

    fos.write(sectionHeader.array());
  }

  private static void writeInterfaceDescription(FileOutputStream fos) throws IOException {
    ByteBuffer interfaceDesc = ByteBuffer.allocate(32);
    interfaceDesc.putInt(0x00000001); // 块类型（接口描述块）
    interfaceDesc.putInt(0x0000001C);
    interfaceDesc.putShort((short) 0x0001);
    interfaceDesc.putShort((short) 0x0000); // 保留字段
    interfaceDesc.putInt(65535);
    interfaceDesc.putInt(0);
    interfaceDesc.putInt(0); // 保留（填充）

    fos.write(interfaceDesc.array());
  }

  private static void writeSectionEnd(FileOutputStream fos) throws IOException {
    ByteBuffer sectionEnd = ByteBuffer.allocate(8);
    sectionEnd.putInt(0x0000000A); // 块类型（节结束）
    sectionEnd.putInt(0x00000000);

    fos.write(sectionEnd.array());
  }
}

