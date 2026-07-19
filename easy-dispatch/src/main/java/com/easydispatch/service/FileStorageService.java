package com.easydispatch.service;

import com.easydispatch.config.AppProperties;
import com.easydispatch.exception.FileTooLargeException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件存储服务：负责附件的保存、读取、删除、存在性检查
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final AppProperties props;
    private Path storageRoot;

    public FileStorageService(AppProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() throws IOException {
        this.storageRoot = Paths.get(props.getStorage().getRoot()).toAbsolutePath().normalize();
        Files.createDirectories(this.storageRoot);
        log.info("附件存储根目录: {}", storageRoot);
    }

    /**
     * 保存上传文件，返回相对存储路径（用于持久化）
     */
    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("上传文件为空");
        }
        long size = file.getSize();
        if (size > props.getUpload().getMaxFileSize()) {
            throw new FileTooLargeException(size, props.getUpload().getMaxFileSize());
        }

        String dateDir = LocalDate.now().format(DATE_DIR);
        String fileName = UUID.randomUUID() + "_" + sanitize(file.getOriginalFilename());
        Path targetDir = storageRoot.resolve(dateDir);
        Files.createDirectories(targetDir);

        Path target = targetDir.resolve(fileName);
        file.transferTo(target.toFile());

        String relative = storageRoot.relativize(target).toString().replace('\\', '/');
        log.info("文件已保存: {} ({} bytes)", relative, size);
        return relative;
    }

    /**
     * 将存储路径转换为绝对路径
     */
    public Path resolve(String storedPath) {
        return storageRoot.resolve(storedPath).normalize();
    }

    /**
     * 校验文件是否存在
     */
    public boolean exists(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return false;
        Path p = resolve(storedPath);
        return Files.isRegularFile(p);
    }

    /**
     * 删除文件
     */
    public void delete(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return;
        try {
            Path p = resolve(storedPath);
            Files.deleteIfExists(p);
        } catch (IOException e) {
            log.warn("删除文件失败 {}: {}", storedPath, e.getMessage());
        }
    }

    /**
     * 提取文件扩展名
     */
    public static String extractType(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx + 1).toLowerCase() : "";
    }

    private static String sanitize(String filename) {
        if (filename == null) return "unnamed";
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
