package com.aicrm.chat.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/copilot")
public class CopilotController {

    private final ChatClient chatClient;

    public CopilotController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a call center copilot. Draft concise, policy-safe answers for a human agent.
                        Never claim that an action was completed unless a tool or system confirms it.
                        """)
                .build();
    }

    @PostMapping(path = "/drafts/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamDraft(@Valid @RequestBody DraftRequest request) {
        SseEmitter emitter = new SseEmitter(0L);
        chatClient.prompt()
                .user("""
                        Customer context:
                        %s

                        Current conversation:
                        %s

                        Agent request:
                        %s
                        """.formatted(request.customerContext(), request.conversation(), request.instruction()))
                .stream()
                .content()
                .subscribe(
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (Exception ex) {
                                emitter.completeWithError(ex);
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete
                );
        return emitter;
    }

    public record DraftRequest(
            @NotBlank String customerContext,
            @NotBlank String conversation,
            @NotBlank String instruction
    ) {
    }
}
