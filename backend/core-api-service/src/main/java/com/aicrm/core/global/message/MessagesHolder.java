package com.aicrm.core.global.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class MessagesHolder {

    private static AppMessages messages;

    public MessagesHolder(AppMessages appMessages) {
        messages = appMessages;
    }

    public static AppMessages get() {
        if (messages == null) {
            messages = new AppMessages(new ObjectMapper());
        }
        return messages;
    }
}
