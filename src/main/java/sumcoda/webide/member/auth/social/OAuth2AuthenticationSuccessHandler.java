package sumcoda.webide.member.auth.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sumcoda.webide.member.auth.general.LoginResponseDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        log.info("OAuth2 success handler is working");
        CustomOAuth2User user =  (CustomOAuth2User) authentication.getPrincipal();
        log.info("login user name : " + user.getUsername());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        LoginResponseDTO responseLoginDTO =
                LoginResponseDTO.builder()
                        .username(user.getUsername())
                        .build();
        responseData.put("userInfo", responseLoginDTO);
        responseData.put("message", "로그인에 성공하였습니다.");

        redirectStrategy.sendRedirect(request, response , "http://localhost:3000/oauth-login-handler?message=success");
        objectMapper.writeValue(response.getWriter(), responseData);
    }
}
