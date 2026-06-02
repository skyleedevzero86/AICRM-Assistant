package com.aicrm.core.global.message;

import com.aicrm.core.global.exception.ErrorCode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Component;

@Component
public class AppMessages implements MessageSource {

    private final Map<String, Object> catalog;

    public AppMessages(ObjectMapper objectMapper) {
        try (InputStream inputStream = openCatalog()) {
            if (inputStream == null) {
                throw new IllegalStateException("messages/ko.json not found");
            }
            this.catalog = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load messages/ko.json", exception);
        }
    }

    private InputStream openCatalog() throws IOException {
        InputStream classpathStream = getClass().getResourceAsStream("/messages/ko.json");
        if (classpathStream != null) {
            return classpathStream;
        }

        Path workingDirectory = Path.of("").toAbsolutePath();
        Path[] fallbackPaths = {
                workingDirectory.resolve("shared/messages/ko.json"),
                workingDirectory.resolve("../shared/messages/ko.json"),
                workingDirectory.resolve("../../shared/messages/ko.json")
        };
        for (Path path : fallbackPaths) {
            if (Files.exists(path)) {
                return Files.newInputStream(path);
            }
        }
        return null;
    }

    public String error(ErrorCode errorCode) {
        return require("errors", errorCode.getCode());
    }

    public String error(String code) {
        return require("errors", code);
    }

    public String formatError(String code, Object... args) {
        return MessageFormat.format(error(code), args);
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        String resolved = resolve(code);
        if (resolved == null) {
            return defaultMessage != null ? defaultMessage : code;
        }
        if (args == null || args.length == 0) {
            return resolved;
        }
        return MessageFormat.format(resolved, args);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) {
        return getMessage(code, args, code, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        String[] codes = resolvable.getCodes();
        if (codes == null) {
            return resolvable.getDefaultMessage();
        }
        for (String code : codes) {
            String resolved = resolve(code);
            if (resolved != null) {
                return MessageFormat.format(resolved, resolvable.getArguments());
            }
        }
        return resolvable.getDefaultMessage();
    }

    private String require(String section, String key) {
        String resolved = resolve(section + "." + key);
        if (resolved == null) {
            throw new IllegalArgumentException("Missing message key: " + section + "." + key);
        }
        return resolved;
    }

    private String resolve(String dottedKey) {
        String[] parts = dottedKey.split("\\.");
        Object node = catalog;
        for (String part : parts) {
            if (!(node instanceof Map<?, ?> map) || !map.containsKey(part)) {
                return null;
            }
            node = map.get(part);
        }
        return node instanceof String text ? text : null;
    }
}
