package sumcoda.webide.member.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.member.auth.util.JWTUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReissueService {

    private final JWTUtil jwtUtil;

    /**
     * 새로운 JWT 생성
     *
     * @param category 생성할 JWT 종류
     * @param username JWT 생성시 암호화할 유저 아이디
     * @param role JWT 생성시 암호화할 유저 권한
     * @param expiredMs 생성할 JWT 유효시간
     **/
    public String createJwt(String category, String username, String role, Long expiredMs) {
        return jwtUtil.createJwt(category, username, role, expiredMs);
    }

    /**
     * Refresh Token 에서 유저 아이디를 추출
     *
     * @param refreshToken Refresh Token 정보
     **/
    public String getTokenUsername(String refreshToken) {
        return jwtUtil.getUsername(refreshToken);
    }

    /**
     * Refresh Token 에서 유저 권한을 추출
     *
     * @param refreshToken Refresh Token 정보
     **/
    public String getTokenRole(String refreshToken) {
        return jwtUtil.getRole(refreshToken);
    }

    /**
     * Refresh Token 에서 토큰 종류를 추출
     *
     * @param refreshToken Refresh Token 정보
     **/
    public String getTokenCategory(String refreshToken) {
        return jwtUtil.getCategory(refreshToken);
    }

    /**
     * Refresh Token 에서 토큰 만료 시간을 추출
     *
     * @param refreshToken Refresh Token 정보
     **/
    public Boolean isTokenExpired(String refreshToken) {
        return jwtUtil.isExpired(refreshToken);
    }


}
