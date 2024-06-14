package sumcoda.webide.member.auth.social;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("OAuth2 authentication failure handler is working");


        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        String errorMessage = "";

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        if (exception instanceof OAuth2AuthenticationException) {
            errorMessage = getOAuth2ErrorMessage(exception.getMessage());
        }

        else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        }
        else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        redirectStrategy.sendRedirect(request, response, "http://localhost:3000/login/oauth/callback?isSuccess=false?error=" + errorMessage);
    }

    private String getOAuth2ErrorMessage(String errorMessage) {

        String oAuth2ErrorMessage;

        switch (errorMessage) {
            case "The authorization request or token request is missing a required parameter":
                oAuth2ErrorMessage = "인증 요청 또는 토큰 요청에 필요한 매개 변수가 누락되었습니다.";
                break;
            case "Missing or invalid client identifier":
                oAuth2ErrorMessage = "누락되었거나 유효하지 않은 클라이언트 식별자";
                break;
            case "Invalid or mismatching redirection URI":
                oAuth2ErrorMessage = "유효하지 않거나 일치하지 않는 리디렉션 URI";
                break;
            case "The requested scope is invalid, unknown, or malformed":
                oAuth2ErrorMessage = "요청된 범위가 유효하지 않거나, 알 수 없거나, 형식이 잘못되었습니다.";
                break;
            case "The resource owner or authorization server denied the access request":
                oAuth2ErrorMessage = "자원 소유자 또는 인증 서버가 액세스 요청을 거부했습니다.";
                break;
            case "Client authentication failed":
                oAuth2ErrorMessage = "클라이언트 인증 실패";
                break;
            default:
                oAuth2ErrorMessage = "제공된 권한 부여(승인 코드, 리소스 소유자 자격 증명)는 유효하지 않거나, 만료되거나, 취소되었습니다.";
                break;
        }
        return oAuth2ErrorMessage;
    }

    //        if (exception instanceof UsernameNotFoundException) {
//            errorMessage = "아이디가 존재하지 않습니다.";
//        } else if(exception instanceof BadCredentialsException) {
//            errorMessage = "비밀번호가 올바르지 않습니다.";
//        }

//        else if(exception instanceof DisabledException) {
//            errorMessage = "Locked";
//        }
//        else if(exception instanceof CredentialsExpiredException) {
//            errorMessage = "Expired password";
//        }
}

