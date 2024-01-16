package com.chenxian.language_platform.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@Service
public class ImageService {

    // 声明 rootLocation
    private final Path rootLocation;

    // 在构造函数中初始化 rootLocation
    public ImageService() {
        this.rootLocation = Paths.get("C:\\images");
        // 您也可以添加代码来验证路径是否存在或创建它
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public Resource loadImage(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
