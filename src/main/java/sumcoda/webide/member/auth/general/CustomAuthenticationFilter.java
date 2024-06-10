package sumcoda.webide.member.auth.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import sumcoda.webide.member.auth.RefreshTokenService;
import sumcoda.webide.member.auth.util.CookieUtil;
import sumcoda.webide.member.auth.util.JWTUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Log4j2
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JWTUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    // 생성자
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        setFilterProcessesUrl("/api/auth/login");
    }

    // 요청으로 받은 유저 정보를 바탕으로 로그인 인증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 만약 POST 요청이 아니라면
        if (!isPost(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        LoginRequestDTO loginRequestDTO;

        try {
            // 요청으로 받은 유저 정보 값을 LoginRequestDTO에 매핑
            loginRequestDTO = objectMapper.readValue(request.getReader(), LoginRequestDTO.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        // ID, PASSWORD 가 있는지 확인
        if(!StringUtils.hasLength(loginRequestDTO.getUsername())
                || !StringUtils.hasLength(loginRequestDTO.getPassword())) {
            throw new IllegalArgumentException("username or password is empty");
        }

        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();
        log.info(username);
        log.info(password);

        // 로그인을 요청한 유저 정보를 바탕으로 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // authenticationManager ㅇ
        return authenticationManager.authenticate(authenticationToken);
    }

    // 로그인 성공시 JWT 발급 및 프론트로 응답
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();

        log.info("success handler is working");
        UserDetails user =  (UserDetails) authResult.getPrincipal();
        log.info("login user name : " + user.getUsername());

        // 유저 정보
        String username = authResult.getName();

        // role 값을 가져오기 위해서 authResult 에서 Authority 객체를 가져온다.
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        // 로그인에 성공한 유저의 정보를 바탕으로 토큰 생성
        String accessToken = jwtUtil.createJwt("access", username, role, 600000L); // 10분
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L); // 24시간

        // refreshToken 저장
        refreshTokenService.saveRefreshToken(username, refreshToken, 86400000L);

        // 응답의 헤더에다가 accessToken을 access 키에 저장
        response.setHeader("Authorization", "Bearer " + accessToken);

        // 응답 쿠키에대가 refreshToken을 refresh 키에 저장
        response.addCookie(CookieUtil.createCookie("Refresh-Token", refreshToken, false));

        // 응답 상태 코드로 200 상태 코드로 설정 OK
        response.setStatus(HttpStatus.OK.value());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.setCharacterEncoding("UTF-8");

        LoginResponseDTO responseLoginDTO =
                LoginResponseDTO.builder()
                        .username(user.getUsername())
                        .build();
        responseData.put("userInfo", responseLoginDTO.getUsername());
        responseData.put("message", "로그인에 성공하였습니다.");

        objectMapper.writeValue(response.getWriter(), responseData);
    }


    // 로그인 실패시 해당하는 예외처리 및 프론트로 응답
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        final ObjectMapper objectMapper = new ObjectMapper();

        log.info("authentication failure handler is working");
        Map<String, Object> responseData = new HashMap<>();

        String errorMessage = "";

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");


        if (failed instanceof UsernameNotFoundException) {
            errorMessage = "아이디가 존재하지 않습니다.";
        } else if(failed instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 올바르지 않습니다.";
        }
        else if (failed instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        responseData.put("result", "error");
        responseData.put("message", errorMessage);

        objectMapper.writeValue(response.getWriter(), responseData);
    }

    // 해당 요청이 POST 요청인지 검증
    private boolean isPost(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }
}

//        else if(exception instanceof DisabledException) {
//            errorMessage = "Locked";
//        }
//        else if(exception instanceof CredentialsExpiredException) {
//            errorMessage = "Expired password";
//        }

//        else if (exception instanceof AuthenticationCredentialsNotFoundException) {
//            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
//        }