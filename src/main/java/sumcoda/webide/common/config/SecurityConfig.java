package sumcoda.webide.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsUtils;
import sumcoda.webide.member.auth.RefreshTokenService;
import sumcoda.webide.member.auth.filter.JWTFilter;
import sumcoda.webide.member.auth.general.*;
import sumcoda.webide.member.auth.util.JWTUtil;
import sumcoda.webide.member.repository.MemberRepository;

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenService);
        customAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");


        // 새로추가
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
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
                        .accessDeniedHandler(customAccessDeniedHandler));

        // 스프링 시큐리티에서 기본적으로 등록되어있는 LogoutFilter 앞에 위치시켜 CustomLogoutFilter가 먼저 동작되도록 등록한다.
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenService), LogoutFilter.class);


                //세션 설정
                // jwt를 활용하는 방식은 세션을 STATELESS 방식을 활용하기 때문에 반드시 해당 코드를 작성
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}
