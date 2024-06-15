package sumcoda.webide.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploadFiles/**")
                .addResourceLocations("file:src/main/resources/static/uploadFiles/")
                .setCachePeriod(60 * 60 * 24 * 365);

        // s3 활용시에 해당 코드 적용하기
//        registry.addResourceHandler("/uploadFiles/**")
//                .addResourceLocations("classpath:/static/uploadFiles/")
//                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
    }
}
