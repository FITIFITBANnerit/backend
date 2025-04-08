package com.BANnerIt.server.api.s3.dto;

import java.util.List;
public record PresignedUrlResponse(List<PresignedUrlDto> key_urls) {
}
