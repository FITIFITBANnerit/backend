package com.BANnerIt.server.api.s3.dto;

public record PresignedUrlResponse(String key, String url) {
}
