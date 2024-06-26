//package sumcoda.webide.common.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsMVCConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("https://3ever.vercel.app")
//                .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP method
//                .allowedHeaders("Origin", "Content-Type", "Accept", "Cache-Control", "Authorization", "X-AUTH-TOKEN", "Authorization_Refresh", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
//                .exposedHeaders("Content-Type", "X-AUTH-TOKEN", "Authorization", "Authorization_Refresh", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
//                .allowCredentials(true) // 쿠키 인증 요청 허용
//                .maxAge(3000); // pre-flight 요청을 캐싱하는 시간
//    }
//}
