package sumcoda.webide.member.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sumcoda.webide.member.auth.general.CustomUserDetails;
import sumcoda.webide.member.auth.util.JWTUtil;
import sumcoda.webide.member.dto.MemberResponseDTO;
import sumcoda.webide.member.repository.MemberRepository;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT Filter is working");

        // 서버측에서 access 토큰을 헤더에 담아서 클라이언트로 보냈기 때문에 클라이언트 측에서 서버로 어떤 요청을 할때도 header에 access 토큰을 담아서 요청을 보낸다.
        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = getJwtFromRequest(request);

        // 토큰이 없다면 다음 필터로 넘김
        // 토큰이 없더라도 토큰을 활용한 권한이 필요없는 페이지도 있기 때문에 다음 플터를 진행
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }
        // 만약 토큰이 있다면,
        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
            // 만약 만료가 되었으면 해당 예외가 던져지는데 던져진 예외를 캐치한다.
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            // 응답 코드에 토큰이 완료되었다고 표시
            writer.print("access token expired");

            //response status code
            // 특정한 상태코드를 응답으로 보낸다.
            // 프론트 측에서 만료된 토큰을 가지고 리프레스 토큰 서버로 전송하여 서버로부터 JWT를 재발급 받을수있도록
            // 따라서 프론트측과 약속된 상태코드를 보내야한다.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        // 토큰이 만료되지 않았다면,
        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        // access 토큰이 아니면,
        // 다음 필터를 실행하지 않는다.
        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            // access 토큰이 아니라고 표시
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰 검증이 완료되었으므로 토큰에서 username, role 값을 꺼내서 일시적인 세션 생성
        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);

        MemberResponseDTO findMember = memberRepository.findOneByUsername(username).orElse(null);

        // 먄약 findMember가 null 이라면,
        if (findMember == null) {
            // 바로 다음 필터 실행
            filterChain.doFilter(request, response);
        }

        // 그게 아니라면 일시적인 세션을 생성시키기 위한 로직 처리
        // CustomUserDetails에 유저 정보를 저장
        CustomUserDetails customUserDetails = new CustomUserDetails(findMember);

        // UsernamePasswordAuthenticationToken에 customUserDetails을 넘겨주어 로그인을 진행한다.
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // SecurityContextHolder에 유저 정보를 등록시키면, 일시적인 세션을 생성한다.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터를 실행
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
