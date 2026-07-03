package com.givemefive.gmfcontroller;

import com.givemefive.gmfcontroller.config.CryptoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CryptoProperties.class)
public class GmfControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmfControllerApplication.class, args);
    }
}
