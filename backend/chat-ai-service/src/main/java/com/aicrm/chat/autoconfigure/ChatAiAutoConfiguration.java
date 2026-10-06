package com.aicrm.chat.autoconfigure;

import com.aicrm.chat.api.CopilotController;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(name = "spring.ai.openai.api-key")
@Import(CopilotController.class)
public class ChatAiAutoConfiguration {
}
