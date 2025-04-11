package com.BANnerIt.server.api.s3.repository;

import com.BANnerIt.server.api.s3.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
