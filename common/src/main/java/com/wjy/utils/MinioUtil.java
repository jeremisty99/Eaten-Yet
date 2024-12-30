package com.wjy.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
@AllArgsConstructor
@Slf4j
public class MinioUtil {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    public String upload(MultipartFile file, String objectName) throws IOException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 实例化客户端
        MinioClient client = MinioClient.builder()
                .endpoint(this.endpoint)
                .credentials(this.accessKey, this.secretKey)
                .build();

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(this.bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (ServerException e) {
            throw new RuntimeException(e);
        }

        return this.endpoint + this.bucketName + "/" + objectName;
    }

}
