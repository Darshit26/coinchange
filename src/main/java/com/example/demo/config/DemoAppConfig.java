package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class DemoAppConfig {

    private static final Logger logger = LoggerFactory.getLogger(DemoAppConfig.class);

    public DemoAppConfig() {
        logger.info("|||||||||||||||||||| DemoAppConfig Config Message||||||||||||||||||||||||||");
    }
}
