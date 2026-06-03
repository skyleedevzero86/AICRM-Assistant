package com.aicrm.core.attachment.domain;

import java.io.InputStream;

public interface ObjectStorage {

    void upload(String bucket, String objectKey, InputStream inputStream, long size, String contentType);

    String createPresignedDownloadUrl(String bucket, String objectKey, int expirySeconds);
}
