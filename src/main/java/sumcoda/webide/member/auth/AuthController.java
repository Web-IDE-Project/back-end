package sumcoda.webide.member.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sumcoda.webide.member.dto.AuthResponseDTO;
import sumcoda.webide.member.auth.social.CustomOAuth2User;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/api/auth/status")
    public ResponseEntity<?> isAuthenticated(Authentication authentication) {

        Map<String, Object> responseData = new HashMap<>();

        String username = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        AuthResponseDTO authResponseDTO = authService.findOneByUsername(username);

        if (authentication.isAuthenticated()) {
            responseData.put("userInfo", authResponseDTO);
            responseData.put("message", "세션이 유효합니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseData);

        } else {
            responseData.put("message", "세션이 만료되었습니다.");
            responseData.put("status", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
        }
    }
}
