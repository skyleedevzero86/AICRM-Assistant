package com.aicrm.core.support;

import com.aicrm.core.global.message.AppMessages;
import com.aicrm.core.global.message.MessagesHolder;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class MessagesInitializer {

    private MessagesInitializer() {
    }

    public static void init() {
        new MessagesHolder(new AppMessages(new ObjectMapper()));
    }
}
