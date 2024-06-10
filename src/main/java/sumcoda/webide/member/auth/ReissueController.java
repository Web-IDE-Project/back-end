package sumcoda.webide.member.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sumcoda.webide.member.auth.util.CookieUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    private final RefreshTokenService refreshTokenService;

    // Refresh Token을 바탕으로 새로운 Access Token, Refresh Token 발급
    @PostMapping("/api/auth/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> responseData = new HashMap<>();

        //해당 컨트롤러로온 프론트측의 요청으로 부터 쿠키를 가져온다
        // 쿠키에서 Refresh Token 추출
        String refreshToken = CookieUtil.getRefreshTokenFromRequest(request);

        // 쿠키에서 refreshToken 토큰을 가져왔음에도 null일수도 있다.
        if (refreshToken == null) {

            //프론트측으로 해당 메시지와 약속된 응답을 보낸다.
            responseData.put("message", "리프레시 토큰이 유효하지 않습니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        // null이 아니라면 해당 refreshToken토큰이 만료되었는지 확인한다.
        try {
            reissueService.isTokenExpired(refreshToken);

            //만약 해당 토큰이 만료되었다면 예외가 던져지는데 해당 예외를 캐치하여 해당 메시지와 약속된 응답을 보낸다.
        } catch (ExpiredJwtException e) {

            responseData.put("message", "리프레시 토큰이 만료되었습니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        //해당 토큰의 category를 가져온다.
        String category = reissueService.getTokenCategory(refreshToken);

        //만약 refreshToken이 아니라면
        if (!category.equals("refresh")) {

            //해당 메시지와 약속된 응답을 보낸다.
            responseData.put("message", "유효하지 않은 리프레시 토큰입니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenService.existsByRefresh(refreshToken);
        if (Boolean.FALSE.equals(isExist)) {

            //해당 메시지와 약속된 응답을 보낸다.
            responseData.put("message", "존재하지 않는 리프레시 토큰입니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        //정상적인 refreshToken 이므로 새로운 Access Token발급

        // refreshToken 토큰에서 유저정보를 가져온다.
        String username = reissueService.getTokenUsername(refreshToken);
        String role = reissueService.getTokenRole(refreshToken);

        //해당 정보를 바탕으로 새로운 accessToken발급
        String newAccessToken = reissueService.createJwt("access", username, role, 600000L);

        // refreshToken도 accessToken을 새로 발급할때 새로운 refreshToken도 발급한다.
        String newRefreshToken = reissueService.createJwt("refresh", username, role, 86400000L);


        //Refresh토큰 저장 DB에 기존의 Refresh토큰 삭제
        refreshTokenService.deleteByRefresh(refreshToken);

        //새로운 Refresh토큰 저장
        refreshTokenService.saveRefreshToken(username, newRefreshToken, 86400000L);

        //새로 발급한 accessToken을 프론트측으로 보낼 응답의 헤더에 저장
        response.setHeader("Authorization","Bearer " + newAccessToken);

        //새로 발급한 refreshToken을 cookie에 추가한다.
        response.addCookie(CookieUtil.createCookie("Refresh-Token", newRefreshToken, false));

        //해당 메시지와 약속된 응답을 보낸다.
        responseData.put("message", "새로운 토큰을 성공적으로 발급했습니다.");

        //성공적으로 새로운 accessToken을 발급했으므로 200 OK 응답을 보냄.
        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }
}
