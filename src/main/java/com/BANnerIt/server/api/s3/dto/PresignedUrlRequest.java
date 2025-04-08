package com.BANnerIt.server.api.s3.dto;

import java.util.List;
public record PresignedUrlRequest(List<String> files) {
}
