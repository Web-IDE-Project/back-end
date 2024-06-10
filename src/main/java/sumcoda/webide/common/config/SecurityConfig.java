package sumcoda.webide.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sumcoda.webide.member.auth.RefreshTokenService;
import sumcoda.webide.member.auth.filter.JWTFilter;
import sumcoda.webide.member.auth.general.*;
import sumcoda.webide.member.auth.util.JWTUtil;
import sumcoda.webide.member.repository.MemberRepository;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationManager 를 반환받기위해 필요한 AuthenticationConfiguration 인스턴스 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    private final JWTUtil jwtUtil;

    private final MemberRepository memberRepository;

    private final RefreshTokenService refreshTokenService;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;





    // AuthenticationManager 를 반환하는 메서드 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Authorization_Refresh", "Refresh-Token", "Cache-Control", "Content-Type"));
        configuration.addAllowedHeader("Access-Control-Allow-Origin");
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization_Refresh", "Refresh-Token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenService);
        customAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");


        // 새로추가
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors((corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource())))
                // 보안 컨텍스트의 저장 방식을 제어하는 설정
                // 보안 컨텍스트가 명시적으로 저장될 때만 저장되도록 한다.
                // 보안 컨텍스트가 실수로 변경되거나 저장되는 것을 방지하여 보안성을 높인다.
                .securityContext(securityContext -> securityContext
                        .requireExplicitSave(true))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated());
        http.
                addFilterBefore(new JWTFilter(jwtUtil, memberRepository), CustomAuthenticationFilter.class);

        http
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(config -> config
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                // HTTP 응답 헤더를 설정한다.
                // frameOptions 설정을 sameOrigin 으로 설정하여,
                // 현재 페이지가 동일한 출처의 페이지에서만 포함될 수 있도록 한다.
                .headers(
                        headersConfigurer -> headersConfigurer
                                .frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
                );



        // 스프링 시큐리티에서 기본적으로 등록되어있는 LogoutFilter 앞에 위치시켜 CustomLogoutFilter가 먼저 동작되도록 등록한다.
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenService), LogoutFilter.class);

                // jwt를 활용하는 방식은 세션을 STATELESS 방식을 활용하기 때문에 반드시 해당 코드를 작성
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}
