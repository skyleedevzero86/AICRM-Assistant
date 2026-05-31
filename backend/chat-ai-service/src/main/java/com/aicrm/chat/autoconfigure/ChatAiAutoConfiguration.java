package com.aicrm.chat.autoconfigure;

import com.aicrm.chat.api.CopilotController;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(CopilotController.class)
public class ChatAiAutoConfiguration {
}
