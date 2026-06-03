package com.aicrm.core.attachment.infrastructure;

import com.aicrm.core.attachment.domain.ObjectStorage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "aicrm.storage.type", havingValue = "memory")
public class InMemoryObjectStorage implements ObjectStorage {

    private final Map<String, byte[]> objects = new ConcurrentHashMap<>();

    @Override
    public void upload(String bucket, String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            objects.put(storageKey(bucket, objectKey), inputStream.readAllBytes());
        } catch (Exception exception) {
            throw new IllegalStateException("파일 저장에 실패했습니다", exception);
        }
    }

    @Override
    public String createPresignedDownloadUrl(String bucket, String objectKey, int expirySeconds) {
        String key = storageKey(bucket, objectKey);
        if (!objects.containsKey(key)) {
            throw new IllegalStateException("파일을 찾을 수 없습니다");
        }
        return "memory://" + key;
    }

    public byte[] read(String bucket, String objectKey) {
        return objects.get(storageKey(bucket, objectKey));
    }

    private String storageKey(String bucket, String objectKey) {
        return bucket + "/" + objectKey;
    }
}
