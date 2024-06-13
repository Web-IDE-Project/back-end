package sumcoda.webide.member.auth.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();

        log.info("success handler is working");
        UserDetails user =  (UserDetails) authentication.getPrincipal();
        log.info("login user name : " + user.getUsername());


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
}
