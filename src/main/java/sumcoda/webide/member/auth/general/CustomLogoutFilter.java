package sumcoda.webide.member.auth.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;
import sumcoda.webide.member.auth.RefreshTokenService;
import sumcoda.webide.member.auth.util.CookieUtil;
import sumcoda.webide.member.auth.util.JWTUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);

    }

    // 모든 요청에 대해서 해당 필터를 거치게된다.
    // 그중에서 로그아웃 요청만 캐치할 것이다.
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();

        // 클라이언트측 요청에서 path 값을 꺼내서 해당 요청이 logout 요청인지 확인한다.
        if (!isLogoutRequest(request)) {

            // 로그아웃 요청이 아니면 다음 위치의 필터를 동작하게 한다.
            filterChain.doFilter(request, response);
            return;
        }
        // 로그아웃 요청이더라도 post 요청이 아니면 다음 필터를 동작하게 한다.
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 로그아웃 요청이라면 쿠키에서 refresh 토큰을 가져온다.
        // 쿠키에서 Refresh Token 추출
        String refreshToken = CookieUtil.getRefreshTokenFromRequest(request);

        // 만약 refresh 토큰이 비어있다면,
        if (refreshToken == null) {

            // 400 응답을 프론트측으로 전달한다.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            jwtUtil.isExpired(refreshToken);
            // 만약 refresh 토큰이 만료되었다면 예외가 던져지게 되는데 해당 예외를 캐치한다.
        } catch (ExpiredJwtException e) {

            // 그리고 해당 경우는 로그아웃이 이미 된 상태이기 때문에 400 응답을 프론트측으로 전달한다.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenService.existsByRefresh(refreshToken);
        // 만약 DB에 해당 refresh 토큰이 저장되어있지 않다면,
        if (Boolean.FALSE.equals(isExist)) {
            // 이미 로그아웃이 된 상태이므로 프론트측에 400 응답을 전달한다.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        // 로그아웃시에 DB에서 제거해줘야 Reissue 요청이 들어와도 토큰이 재발급되지 않는다.
        refreshTokenService.deleteByRefresh(refreshToken);

        //Refresh 토큰 Cookie 값 세팅
        // 프론트측의 쿠키에 이미 저장되어있는 쿠키를 비어있는값으로 셋팅하기 위해서
        // 이미 저장되어있는 쿠키의 키값과 동일한 비어있는 쿠키를 생성
        Cookie cookie = CookieUtil.createCookie("Refresh-Token", null, true);


        response.addCookie(cookie);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.setCharacterEncoding("UTF-8");

        // 로그아웃이 완료되었다고 200응답을 프론트측에 전달한다.
        response.setStatus(HttpServletResponse.SC_OK);

        responseData.put("message", "로그아웃에 성공하였습니다.");

        objectMapper.writeValue(response.getWriter(), responseData);
    }

    public boolean isLogoutRequest(HttpServletRequest request) {
        String url = request.getRequestURI();
        String regex = "^/api/auth/logout$";
        return url.matches(regex);
    }
}
