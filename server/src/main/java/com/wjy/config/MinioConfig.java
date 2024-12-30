package com.wjy.config;

import com.wjy.properties.MinioProperties;
import com.wjy.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类 用于创建MinioUtils对象
 */
@Configuration
public class MinioConfig {

    @Autowired
    private MinioProperties minIOProperties;

    @Bean
    @ConditionalOnMissingBean //保证容器中只会出现一个MinioClient对象
    public MinioUtil minioUtil() {
        return new MinioUtil(
                minIOProperties.getEndpoint(),
                minIOProperties.getAccessKey(),
                minIOProperties.getSecretKey(),
                minIOProperties.getBucketName()
        );
    }
}
