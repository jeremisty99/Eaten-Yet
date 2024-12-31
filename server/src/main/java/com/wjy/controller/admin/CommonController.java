package com.wjy.controller.admin;

import com.wjy.result.Result;
import com.wjy.utils.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 通用接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
public class CommonController {
    @Autowired
    private MinioUtil minioUtil;

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}", file);
        String originalFilename = file.getOriginalFilename();
        String extension = null;
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = UUID.randomUUID() + extension;
        String filePath = null;
        try {
            filePath = minioUtil.upload(file, objectName);
            log.info("文件上传成功:{}", filePath);
            return Result.success(filePath);
        } catch (Exception e) {
            log.error("文件上传失败:{}", e.getMessage());
            return Result.error("文件上传失败");
        }
    }
}
