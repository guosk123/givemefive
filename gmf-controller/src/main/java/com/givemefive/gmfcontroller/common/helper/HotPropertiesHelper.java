package com.givemefive.gmfcontroller.common.helper;

import org.apache.commons.lang3.StringUtils;

public final class HotPropertiesHelper {

    private HotPropertiesHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String getProperty(String key) {
        String systemValue = System.getProperty(key);
        if (StringUtils.isNotBlank(systemValue)) {
            return systemValue;
        }

        String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
        return StringUtils.defaultString(System.getenv(envKey));
    }
}
