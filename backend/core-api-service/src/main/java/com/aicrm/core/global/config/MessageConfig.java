package com.aicrm.core.global.config;

import com.aicrm.core.global.message.AppMessages;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {

    @Bean
    MessageSource messageSource(AppMessages appMessages) {
        return appMessages;
    }
}
