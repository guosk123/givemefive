package com.givemefive.gmfcontroller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gmf.crypto")
public record CryptoProperties(String dataKey) {
}
