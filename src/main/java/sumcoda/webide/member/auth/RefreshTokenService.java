package sumcoda.webide.member.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public Boolean existsByRefresh(String token) {
        return refreshTokenRepository.existsByToken(token);
    }

    /**
     * Refresh Token 저장
     *
     * @param username Refresh Token을 저장하는 유저 아이디
     * @param token Refresh Token에서 토큰 정보
     * @param expiredMs Refresh Token의 만료 시간
     **/
    @Transactional
    public void saveRefreshToken(String username, String token, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(token)
                .expiration(date.toString())
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Refresh Token 삭제
     *
     * @param token 해당하는 Refresh Token을 삭제하기위한 토큰 정보
     **/
    @Transactional
    public void deleteByRefresh(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
