package cn.people.cms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * Created by lml on 2018/4/2.
 */
@Configuration
public class MultipartConfig {

    @Value("${theone.multipart.maxFileSize}")
    private String fileSize;
    @Value("${theone.multipart.maxRequestSize}")
    private String maxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(fileSize);
        factory.setMaxRequestSize(maxRequestSize);
        return   factory.createMultipartConfig();
    }
}
