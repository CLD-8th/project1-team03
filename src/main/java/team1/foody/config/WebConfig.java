package team1.foody.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        File uploadDirectory =
                new File(uploadDir).getAbsoluteFile();

        System.out.println(
                "업로드 폴더 경로 = "
                        + uploadDirectory.getAbsolutePath()
        );

        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations(
                        uploadDirectory.toURI().toString()
                );
    }
}