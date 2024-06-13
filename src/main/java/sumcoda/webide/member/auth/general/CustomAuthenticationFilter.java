package sumcoda.webide.member.auth.general;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;


@Log4j2
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public CustomAuthenticationFilter() {
        // url과 일치할 경우 해당 필터가 동작합니다.
        super(new AntPathRequestMatcher("/api/auth/login"));
    }

    // 요청으로 받은 유저 정보를 바탕으로 로그인 인증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();

        // 해당 요청이 POST 인지 확인
        if(!isPost(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        // POST 이면 body 를 AccountDto( 로그인 정보 DTO ) 에 매핑
        LoginRequestDTO loginRequestDTO;
        try {
            loginRequestDTO = objectMapper.readValue(request.getReader(), LoginRequestDTO.class);
        } catch (IOException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        // ID, PASSWORD 가 있는지 확인
        if(!StringUtils.hasLength(loginRequestDTO.getUsername())
                || !StringUtils.hasLength(loginRequestDTO.getPassword())) {
            throw new IllegalArgumentException("username or password is empty");
        }

        // 처음에는 인증 되지 않은 토큰 생성
        CustomAuthenticationToken token = new CustomAuthenticationToken(
                loginRequestDTO.getUsername(),
                loginRequestDTO.getPassword()
        );

        // Manager 에게 인증 처리
        return getAuthenticationManager().authenticate(token);
    }

    // 해당 요청이 POST 요청인지 검증
    private boolean isPost(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }
}