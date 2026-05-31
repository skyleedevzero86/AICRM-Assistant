package com.aicrm.core.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@ComponentScan(basePackages = {
        "com.aicrm.core.global",
        "com.aicrm.core.auth"
})
@EntityScan(basePackages = "com.aicrm.core")
@EnableJpaRepositories(basePackages = "com.aicrm.core")
public class CoreApiAutoConfiguration {
}
