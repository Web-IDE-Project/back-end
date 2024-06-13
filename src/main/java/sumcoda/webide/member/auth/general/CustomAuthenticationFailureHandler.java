package sumcoda.webide.member.auth.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("authentication failure handler is working");
        Map<String, Object> responseData = new HashMap<>();

        String errorMessage = "";

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");


        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "아이디가 존재하지 않습니다.";
        } else if(exception instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 올바르지 않습니다.";
        }
        else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

//        else if(exception instanceof DisabledException) {
//            errorMessage = "Locked";
//        }
//        else if(exception instanceof CredentialsExpiredException) {
//            errorMessage = "Expired password";
//        }

//        else if (exception instanceof AuthenticationCredentialsNotFoundException) {
//            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
//        }

        responseData.put("result", "error");
        responseData.put("message", errorMessage);

        objectMapper.writeValue(response.getWriter(), responseData);

    }
}
