package sumcoda.webide.member.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public class CookieUtil {

    //쿠키 생성 메서드
    public static Cookie createCookie(String key, String value, Boolean isLogout) {

        Cookie cookie = new Cookie(key, value);
        if (Boolean.TRUE.equals(isLogout)) {
            cookie.setMaxAge(0);
        } else {
            cookie.setMaxAge(24 * 60 * 60);
        }

        //쿠키가 적용될 범위 설정
        // 로그아웃일때만 아래와 같이 설정
        if (Boolean.TRUE.equals(isLogout)) {
            cookie.setPath("/");
        }
        // login, reissue는 아래와 같이 설정
        //cookie.setPath("/");

        // https통신을 할때 setSecure()메서드 활용
        //cookie.setSecure(true);

        //프론트엔드 클라이언트에서 javascript로 쿠키에 접근하지 못하도록 설정
        cookie.setHttpOnly(true);

        return cookie;
    }

    // 요청으로 부터 쿠키를 추출하기 위한 메섣,
    public static String getRefreshTokenFromRequest(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "Refresh-Token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
