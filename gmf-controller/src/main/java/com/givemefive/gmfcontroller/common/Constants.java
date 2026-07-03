package com.givemefive.gmfcontroller.common;

/**
 * 
 * @author guosk
 *
 */
public final class Constants {

  public static final int BYTE_BITS = 8;
  public static final int IPV4_ADDRESS_BYTES = 4;
  public static final int MAC_ADDRESS_BYTES = 6;

  public static final int ONE_SECOND = 1;
  public static final int FIVE_SECONDS = 5;
  public static final int HALF_MINUTE_SECONDS = 30;
  public static final int ONE_MINUTE_SECONDS = 60;
  public static final int FIVE_MINUTE_SECONDS = 300;
  public static final int TEN_MINUTE_SECONDS = 600;
  public static final int ONE_HOUR_SECONDS = 3600;
  public static final int ONE_DAY_SECONDS = 3600 * 24;

  public static final int ONE_DAYS = 1;
  public static final int ONE_WEEK_DAYS = 7;

  public static final String RES_OK = "0";
  public static final String RES_NOK = "1";

  public static final String BOOL_NO = "0";
  public static final String BOOL_YES = "1";
  public static final String FILE_NOT_FOUND = "2";

  public static final String UNLOCKED = "0";
  public static final String LOCKED = "1";

  public static final String TYPE_DPDK = "0";
  public static final String TYPE_NETFLOW = "1";

  public static final String TYPE_DEVICE = "0";
  public static final String TYPE_NETIF = "1";

  public static final String STATE_UP = "0";
                                             // status code
  public static final String STATE_DOWN = "1";

  public static final int NUM_1000 = 1000;
  public static final int BLOCK_DEFAULT_SIZE = 1024;
  public static final int BUFFER_DEFAULT_SIZE = 128;
  public static final int COL_DEFAULT_SIZE = 16;
  public static final int MAP_DEFAULT_SIZE = 16;

  public static final int PAGE_DEFAULT_SIZE = 10;
  public static final String PAGE_DEFAULT_SIZE_STRING = "10";

  public static final String ENCODING_FORMAT_ISO88591 = "ISO-8859-1";

  public static final String EXPORT_FILE_TYPE_CSV = "csv";
  public static final String EXPORT_FILE_TYPE_EXCEL = "excel";

  public static final long ZERO_LONG = 0l;
  public static final long ZERO_INTEGER = 0;

  /************************************************************
   * 
   *************************************************************/

  public static final String PROP_APPLICATION_KEY = "machloop.application.key";
  public static final String PROP_APPLICATION_TOKEN = "machloop.application.token";
  public static final String PROP_APPLICATION_COMPONENT = "machloop.application.component";
  public static final String PROP_APPLICATION_SECRET = "machloop.application.secret";

  public static final String DICT_SYSTEM_COMPONENT = "system_component";

  public static final long TIMESTAMP_MAX_GAP_MILLSEC = 30 * 60 * 1000L;

  public static final long DOWNLOAD_MAX_GAP_MILLSEC = 24 * 60 * 60 * 1000L;

  public static final long ONE_HOUR_SECONDS_LONG = 60 * 60L;
  public static final CharSequence OAUTH_SUCCESS = "0000";

  private Constants() {
    throw new IllegalStateException("Utility class");
  }
}
