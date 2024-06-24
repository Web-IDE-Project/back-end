package sumcoda.webide.member.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.member.dto.AuthResponseDTO;
import sumcoda.webide.member.auth.social.CustomOAuth2User;
import sumcoda.webide.member.dto.ValidatePasswordRequestDTO;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/api/auth/status")
    public ResponseEntity<?> isAuthenticated() {

        Map<String, Object> responseData = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication information found");
        }
        String username;
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        if (authentication.isAuthenticated()) {
            AuthResponseDTO authResponseDTO = authService.findOneByUsername(username);

            responseData.put("message", "세션이 유효합니다.");
            responseData.put("userInfo", authResponseDTO);
            return ResponseEntity.status(HttpStatus.OK).body(responseData);

        } else {
            responseData.put("message", "세션이 만료되었습니다.");
            responseData.put("status", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }
    }

    @PostMapping("/api/auth/password")
    public ResponseEntity<?> validatePassword(
            @RequestBody ValidatePasswordRequestDTO validatePasswordRequestDTO,
            Authentication authentication) {

        Map<String, Object> responseData = new HashMap<>();

        String username;
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        try {
            Boolean validateResult = authService.validatePassword(username, validatePasswordRequestDTO);
            responseData.put("result", validateResult);
            responseData.put("message", "비밀번호 검증이 완료되었습니다.");

        } catch(Exception e) {
            responseData.put("result", false);
            responseData.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseData);

    }


}
